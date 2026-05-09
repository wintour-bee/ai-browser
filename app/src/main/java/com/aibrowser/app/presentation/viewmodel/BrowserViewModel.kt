package com.aibrowser.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aibrowser.app.ai.AIChatManager
import com.aibrowser.app.browser.AIBrowserCore
import com.aibrowser.app.browser.BrowserState
import com.aibrowser.app.domain.model.*
import com.aibrowser.app.domain.repository.*
import com.aibrowser.app.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrowserViewModel @Inject constructor(
    private val browserCore: AIBrowserCore,
    private val addTabUseCase: AddTabUseCase,
    private val updateTabUseCase: UpdateTabUseCase,
    private val deleteTabUseCase: DeleteTabUseCase,
    private val getTabsUseCase: GetTabsUseCase,
    private val addHistoryUseCase: AddHistoryUseCase,
    private val addBookmarkUseCase: AddBookmarkUseCase,
    private val settingsRepository: SettingsRepository,
    private val aiRepository: AIRepository
) : ViewModel() {
    
    // Browser state
    val browserState = browserCore.browserState
    val currentUrl = browserCore.currentUrl
    val currentTitle = browserCore.currentTitle
    val isLoading = browserCore.isLoading
    val loadingProgress = browserCore.loadingProgress
    val canGoBack = browserCore.canGoBack
    val canGoForward = browserCore.canGoForward
    
    // Tab management
    private val _tabs = MutableStateFlow<List<Tab>>(emptyList())
    val tabs: StateFlow<List<Tab>> = _tabs.asStateFlow()
    
    private val _currentTabId = MutableStateFlow<String?>(null)
    val currentTabId: StateFlow<String?> = _currentTabId.asStateFlow()
    
    private val _isIncognito = MutableStateFlow(false)
    val isIncognito: StateFlow<Boolean> = _isIncognito.asStateFlow()
    
    // UI State
    private val _showUrlBar = MutableStateFlow(false)
    val showUrlBar: StateFlow<Boolean> = _showUrlBar.asStateFlow()
    
    private val _showTabSwitcher = MutableStateFlow(false)
    val showTabSwitcher: StateFlow<Boolean> = _showTabSwitcher.asStateFlow()
    
    private val _showMenu = MutableStateFlow(false)
    val showMenu: StateFlow<Boolean> = _showMenu.asStateFlow()
    
    // Settings
    val settings = settingsRepository.getSettings()
        .stateIn(viewModelScope, SharingStarted.Lazily, AppSettings())
    
    init {
        loadTabs()
        observeSettings()
    }
    
    private fun loadTabs() {
        viewModelScope.launch {
            getTabsUseCase().collect { tabList ->
                _tabs.value = tabList
                if (_currentTabId.value == null && tabList.isNotEmpty()) {
                    _currentTabId.value = tabList.firstOrNull()?.id
                }
            }
        }
    }
    
    private fun observeSettings() {
        viewModelScope.launch {
            settings.collect { appSettings ->
                browserCore.isAdBlockEnabled = appSettings.isAdBlockEnabled
                browserCore.customUserAgent = appSettings.userAgent
                _isIncognito.value = appSettings.isIncognitoDefault
            }
        }
    }
    
    fun loadUrl(url: String) {
        browserCore.loadUrl(url)
        viewModelScope.launch {
            addHistoryUseCase(currentTitle.value, url)
        }
        saveCurrentTab()
    }
    
    fun goBack() {
        browserCore.goBack()
        saveCurrentTab()
    }
    
    fun goForward() {
        browserCore.goForward()
        saveCurrentTab()
    }
    
    fun reload() {
        browserCore.reload()
    }
    
    fun stopLoading() {
        browserCore.stopLoading()
    }
    
    fun createNewTab(url: String = "about:blank") {
        viewModelScope.launch {
            addTabUseCase(url, "New Tab", _isIncognito.value)
                .onSuccess { tab ->
                    _currentTabId.value = tab.id
                    if (url != "about:blank") {
                        browserCore.loadUrl(url)
                    }
                }
        }
    }
    
    fun selectTab(tabId: String) {
        viewModelScope.launch {
            _currentTabId.value = tabId
            val tab = _tabs.value.find { it.id == tabId }
            tab?.let {
                browserCore.loadUrl(it.url)
            }
        }
        _showTabSwitcher.value = false
    }
    
    fun closeTab(tabId: String) {
        viewModelScope.launch {
            deleteTabUseCase(tabId)
            if (_currentTabId.value == tabId) {
                _currentTabId.value = _tabs.value.firstOrNull()?.id
            }
        }
    }
    
    fun toggleIncognito() {
        _isIncognito.value = !_isIncognito.value
        browserCore.isIncognito = _isIncognito.value
    }
    
    fun addBookmark() {
        viewModelScope.launch {
            addBookmarkUseCase(currentTitle.value, currentUrl.value)
        }
    }
    
    private fun saveCurrentTab() {
        viewModelScope.launch {
            val tabId = _currentTabId.value ?: return@launch
            val tab = _tabs.value.find { it.id == tabId } ?: return@launch
            updateTabUseCase(
                tab.copy(
                    url = currentUrl.value,
                    title = currentTitle.value,
                    lastAccessedAt = System.currentTimeMillis()
                )
            )
        }
    }
    
    fun toggleUrlBar() {
        _showUrlBar.value = !_showUrlBar.value
    }
    
    fun toggleTabSwitcher() {
        _showTabSwitcher.value = !_showTabSwitcher.value
    }
    
    fun toggleMenu() {
        _showMenu.value = !_showMenu.value
    }
    
    fun hideAllOverlays() {
        _showUrlBar.value = false
        _showTabSwitcher.value = false
        _showMenu.value = false
    }
    
    override fun onCleared() {
        super.onCleared()
        saveCurrentTab()
    }
}

@HiltViewModel
class AIChatViewModel @Inject constructor(
    private val aiChatManager: AIChatManager,
    private val aiRepository: AIRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    val chatState = aiChatManager.chatState
    val messages = aiChatManager.currentMessages
    
    private val _selectedProvider = MutableStateFlow(AIProvider.CHATGPT)
    val selectedProvider: StateFlow<AIProvider> = _selectedProvider.asStateFlow()
    
    private val _selectedModel = MutableStateFlow("gpt-4o")
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()
    
    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()
    
    val sessions = aiRepository.getAISessions()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    init {
        viewModelScope.launch {
            settingsRepository.getSettings().collect { settings ->
                _selectedProvider.value = settings.defaultAIProvider
            }
        }
    }
    
    fun sendMessage() {
        val text = _inputText.value.trim()
        if (text.isEmpty()) return
        
        _inputText.value = ""
        
        viewModelScope.launch {
            aiChatManager.sendMessage(
                content = text,
                provider = _selectedProvider.value,
                model = _selectedModel.value,
                repository = aiRepository
            )
        }
    }
    
    fun updateInputText(text: String) {
        _inputText.value = text
    }
    
    fun selectProvider(provider: AIProvider) {
        _selectedProvider.value = provider
        _selectedModel.value = com.aibrowser.app.ai.AIProviderConfig.getDefaultModel(provider)
    }
    
    fun selectModel(model: String) {
        _selectedModel.value = model
    }
    
    fun startNewChat() {
        aiChatManager.startNewChat(_selectedProvider.value)
    }
    
    fun clearChat() {
        aiChatManager.clearChat()
    }
    
    fun loadSession(sessionId: String) {
        aiChatManager.loadSession(sessionId)
    }
}

@HiltViewModel
class VPNViewModel @Inject constructor(
    private val vpnRepository: VPNRepository
) : ViewModel() {
    
    val servers = vpnRepository.getAllServers()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    val favoriteServers = vpnRepository.getFavoriteServers()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    val isConnected = vpnRepository.isConnected()
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    
    val connectionInfo = vpnRepository.getConnectionInfo()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
    
    private val _selectedServer = MutableStateFlow<VPNServer?>(null)
    val selectedServer: StateFlow<VPNServer?> = _selectedServer.asStateFlow()
    
    private val _isConnecting = MutableStateFlow(false)
    val isConnecting: StateFlow<Boolean> = _isConnecting.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        refreshServers()
    }
    
    fun refreshServers() {
        viewModelScope.launch {
            vpnRepository.refreshServers()
        }
    }
    
    fun connect(server: VPNServer) {
        viewModelScope.launch {
            _isConnecting.value = true
            _selectedServer.value = server
            
            vpnRepository.connect(server)
                .onSuccess {
                    _isConnecting.value = false
                }
                .onFailure { error ->
                    _errorMessage.value = error.message
                    _isConnecting.value = false
                }
        }
    }
    
    fun disconnect() {
        viewModelScope.launch {
            vpnRepository.disconnect()
        }
    }
    
    fun pingServer(server: VPNServer) {
        viewModelScope.launch {
            vpnRepository.pingServer(server.id)
        }
    }
    
    fun speedTest(server: VPNServer) {
        viewModelScope.launch {
            vpnRepository.speedTest(server.id)
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}
