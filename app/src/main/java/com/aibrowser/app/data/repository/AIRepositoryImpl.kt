package com.aibrowser.app.data.repository

import com.aibrowser.app.data.local.database.AIDao
import com.aibrowser.app.data.local.entity.AISessionEntity
import com.aibrowser.app.data.local.entity.AIMessageEntity
import com.aibrowser.app.data.remote.api.AIApi
import com.aibrowser.app.data.remote.dto.AIChatRequestDto
import com.aibrowser.app.data.remote.dto.AIMessageDto
import com.aibrowser.app.data.remote.dto.AIOcrRequestDto
import com.aibrowser.app.data.remote.dto.AISummarizeRequestDto
import com.aibrowser.app.data.remote.dto.AITranslateRequestDto
import com.aibrowser.app.domain.model.*
import com.aibrowser.app.domain.repository.AIRepository
import com.aibrowser.app.data.local.preferences.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class AIRepositoryImpl @Inject constructor(
    private val aiApi: AIApi,
    private val userPreferences: UserPreferences
) : AIRepository {

    override suspend fun chat(
        provider: AIProvider,
        model: String,
        messages: List<AIMessage>,
        onChunk: (String) -> Unit
    ): Result<String> {
        return try {
            val request = AIChatRequestDto(
                provider = provider.name,
                model = model,
                messages = messages.map { AIMessageDto(it.role.name.lowercase(), it.content, it.imageUrls) },
                temperature = 0.7f,
                maxTokens = 4096,
                stream = true
            )

            val response = aiApi.chat(request)
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                onChunk(result.content)
                Result.success(result.content)
            } else {
                Result.failure(Exception(response.message() ?: "Chat failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun summarize(
        provider: AIProvider,
        url: String?,
        content: String,
        language: String
    ): Result<Triple<String, List<String>, Int>> {
        return try {
            val request = AISummarizeRequestDto(
                provider = provider.name,
                url = url,
                content = content,
                language = language
            )

            val response = aiApi.summarize(request)
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                Result.success(Triple(result.summary, result.keyPoints, result.estimatedReadTime))
            } else {
                Result.failure(Exception(response.message() ?: "Summarize failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun translate(
        provider: AIProvider,
        text: String,
        sourceLanguage: String,
        targetLanguage: String
    ): Result<Pair<String, String?>> {
        return try {
            val request = AITranslateRequestDto(
                provider = provider.name,
                text = text,
                sourceLanguage = sourceLanguage,
                targetLanguage = targetLanguage
            )

            val response = aiApi.translate(request)
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                Result.success(Pair(result.translatedText, result.detectedLanguage))
            } else {
                Result.failure(Exception(response.message() ?: "Translate failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun ocr(
        provider: AIProvider,
        imageUrl: String?,
        imageBase64: String?
    ): Result<Pair<String, Float>> {
        return try {
            val request = AIOcrRequestDto(
                provider = provider.name,
                imageUrl = imageUrl,
                imageBase64 = imageBase64
            )

            val response = aiApi.ocr(request)
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                Result.success(Pair(result.text, result.confidence))
            } else {
                Result.failure(Exception(response.message() ?: "OCR failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAISessions(): Flow<List<AISession>> {
        return kotlinx.coroutines.flow.flowOf(emptyList())
    }

    override suspend fun getAISessionById(id: String): AISession? {
        return null
    }

    override fun getAISessionByIdFlow(id: String): Flow<AISession?> {
        return kotlinx.coroutines.flow.flowOf(null)
    }

    override suspend fun createAISession(session: AISession) {
        // Save to database
    }

    override suspend fun updateAISession(session: AISession) {
        // Update in database
    }

    override suspend fun deleteAISession(id: String) {
        // Delete from database
    }

    override fun getMessagesBySession(sessionId: String): Flow<List<AIMessage>> {
        return kotlinx.coroutines.flow.flowOf(emptyList())
    }

    override suspend fun addMessage(message: AIMessage) {
        // Save to database
    }

    override suspend fun deleteMessagesBySession(sessionId: String) {
        // Delete from database
    }
}

fun AISessionEntity.toDomain(messages: List<AIMessage> = emptyList()): AISession {
    return AISession(
        id = id,
        provider = AIProvider.valueOf(provider),
        messages = messages,
        createdAt = createdAt,
        title = title
    )
}

fun AISession.toEntity(): AISessionEntity {
    return AISessionEntity(
        id = id,
        provider = provider.name,
        title = title,
        createdAt = createdAt,
        updatedAt = System.currentTimeMillis()
    )
}

fun AIMessageEntityLocal.toDomain(): AIMessage {
    return AIMessage(
        id = id,
        role = AIRole.valueOf(role.uppercase()),
        content = content,
        imageUrls = try {
            Json.decodeFromString<List<String>>(imageUrls)
        } catch (e: Exception) {
            emptyList()
        },
        createdAt = createdAt,
        isStreaming = isStreaming
    )
}

fun AIMessage.toEntity(sessionId: String): AIMessageEntityLocal {
    return AIMessageEntityLocal(
        id = id,
        sessionId = sessionId,
        role = role.name.lowercase(),
        content = content,
        imageUrls = Json.encodeToString(imageUrls),
        createdAt = createdAt,
        isStreaming = isStreaming
    )
}
