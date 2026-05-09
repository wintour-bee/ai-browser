package com.aibrowser.app.ai

import com.aibrowser.app.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIChatManager @Inject constructor() {
    
    private val _chatState = MutableStateFlow<ChatState>(ChatState.Idle)
    val chatState: StateFlow<ChatState> = _chatState.asStateFlow()
    
    private val _currentMessages = MutableStateFlow<List<AIMessage>>(emptyList())
    val currentMessages: StateFlow<List<AIMessage>> = _currentMessages.asStateFlow()
    
    private val sessions = mutableMapOf<String, List<AIMessage>>()
    
    fun startNewChat(provider: AIProvider, systemPrompt: String? = null) {
        val messages = mutableListOf<AIMessage>()
        if (systemPrompt != null) {
            messages.add(
                AIMessage(
                    id = generateId(),
                    role = AIRole.SYSTEM,
                    content = systemPrompt
                )
            )
        }
        sessions.clear()
        _currentMessages.value = messages
        _chatState.value = ChatState.Ready
    }
    
    fun addUserMessage(content: String, imageUrls: List<String> = emptyList()) {
        val message = AIMessage(
            id = generateId(),
            role = AIRole.USER,
            content = content,
            imageUrls = imageUrls
        )
        _currentMessages.value = _currentMessages.value + message
        _chatState.value = ChatState.Sending
    }
    
    suspend fun sendMessage(
        content: String,
        provider: AIProvider,
        model: String,
        repository: com.aibrowser.app.domain.repository.AIRepository
    ) {
        addUserMessage(content)
        
        val assistantMessage = AIMessage(
            id = generateId(),
            role = AIRole.ASSISTANT,
            content = "",
            isStreaming = true
        )
        _currentMessages.value = _currentMessages.value + assistantMessage
        
        val result = repository.chat(
            provider = provider,
            model = model,
            messages = _currentMessages.value.dropLast(1),
            onChunk = { chunk ->
                val currentList = _currentMessages.value.toMutableList()
                val lastIndex = currentList.lastIndex
                if (lastIndex >= 0) {
                    currentList[lastIndex] = currentList[lastIndex].copy(
                        content = currentList[lastIndex].content + chunk
                    )
                    _currentMessages.value = currentList
                }
            }
        )
        
        val finalMessage = _currentMessages.value.lastOrNull()
        if (finalMessage != null) {
            _currentMessages.value = _currentMessages.value.dropLast(1) + 
                finalMessage.copy(isStreaming = false)
        }
        
        _chatState.value = if (result.isSuccess) {
            ChatState.Success
        } else {
            ChatState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
        }
    }
    
    fun clearChat() {
        _currentMessages.value = emptyList()
        sessions.clear()
        _chatState.value = ChatState.Idle
    }
    
    fun saveSession(sessionId: String) {
        sessions[sessionId] = _currentMessages.value
    }
    
    fun loadSession(sessionId: String) {
        _currentMessages.value = sessions[sessionId] ?: emptyList()
    }
    
    private fun generateId(): String = java.util.UUID.randomUUID().toString()
}

sealed class ChatState {
    object Idle : ChatState()
    object Ready : ChatState()
    object Sending : ChatState()
    object Success : ChatState()
    data class Error(val message: String) : ChatState()
}

// AI Provider implementations
object AIProviderConfig {
    val CHATGPT_MODELS = listOf("gpt-4o", "gpt-4-turbo", "gpt-4", "gpt-3.5-turbo")
    val CLAUDE_MODELS = listOf("claude-3-opus", "claude-3-sonnet", "claude-3-haiku")
    val GEMINI_MODELS = listOf("gemini-pro", "gemini-pro-vision", "gemini-1.5-pro")
    val DEEPSEEK_MODELS = listOf("deepseek-chat", "deepseek-coder")
    
    fun getModels(provider: AIProvider): List<String> {
        return when (provider) {
            AIProvider.CHATGPT -> CHATGPT_MODELS
            AIProvider.CLAUDE -> CLAUDE_MODELS
            AIProvider.GEMINI -> GEMINI_MODELS
            AIProvider.DEEPSEEK -> DEEPSEEK_MODELS
        }
    }
    
    fun getDefaultModel(provider: AIProvider): String {
        return getModels(provider).first()
    }
}
