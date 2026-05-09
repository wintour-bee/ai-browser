package com.aibrowser.app.domain.repository

import com.aibrowser.app.domain.model.*
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, password: String, username: String, inviteCode: String?): Result<User>
    suspend fun googleAuth(idToken: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): Result<User>
    suspend fun refreshToken(): Result<Unit>
    fun isLoggedIn(): Boolean
    fun getAuthToken(): String?
}

interface BookmarkRepository {
    fun getAllBookmarks(): Flow<List<Bookmark>>
    fun getBookmarksByFolder(folderId: String?): Flow<List<Bookmark>>
    suspend fun getBookmarkById(id: String): Bookmark?
    suspend fun getBookmarkByUrl(url: String): Bookmark?
    suspend fun addBookmark(bookmark: Bookmark)
    suspend fun updateBookmark(bookmark: Bookmark)
    suspend fun deleteBookmark(id: String)
    suspend fun deleteAllBookmarks()
    suspend fun syncBookmarks(): Result<Unit>
}

interface HistoryRepository {
    fun getAllHistory(): Flow<List<HistoryItem>>
    fun getRecentHistory(limit: Int): Flow<List<HistoryItem>>
    fun searchHistory(query: String): Flow<List<HistoryItem>>
    suspend fun getHistoryById(id: String): HistoryItem?
    suspend fun addHistory(history: HistoryItem)
    suspend fun deleteHistory(id: String)
    suspend fun deleteHistoryOlderThan(timestamp: Long)
    suspend fun deleteAllHistory()
    suspend fun syncHistory(): Result<Unit>
}

interface TabRepository {
    fun getAllTabs(): Flow<List<Tab>>
    fun getNormalTabs(): Flow<List<Tab>>
    fun getIncognitoTabs(): Flow<List<Tab>>
    suspend fun getTabById(id: String): Tab?
    fun getTabByIdFlow(id: String): Flow<Tab?>
    suspend fun addTab(tab: Tab)
    suspend fun updateTab(tab: Tab)
    suspend fun deleteTab(id: String)
    suspend fun deleteAllTabs()
    suspend fun syncTabs(): Result<Unit>
}

interface DownloadRepository {
    fun getAllDownloads(): Flow<List<DownloadItem>>
    fun getDownloadsByStatus(status: DownloadStatus): Flow<List<DownloadItem>>
    suspend fun getDownloadById(id: String): DownloadItem?
    suspend fun addDownload(download: DownloadItem)
    suspend fun updateDownload(download: DownloadItem)
    suspend fun deleteDownload(id: String)
    suspend fun deleteAllDownloads()
}

interface VPNRepository {
    fun getAllServers(): Flow<List<VPNServer>>
    fun getFavoriteServers(): Flow<List<VPNServer>>
    suspend fun getServerById(id: String): VPNServer?
    suspend fun refreshServers(): Result<List<VPNServer>>
    suspend fun pingServer(id: String): Result<Long>
    suspend fun connect(server: VPNServer): Result<Unit>
    suspend fun disconnect(): Result<Unit>
    fun isConnected(): Flow<Boolean>
    fun getConnectionInfo(): Flow<VPNConnection?>
    suspend fun addSubscription(url: String, name: String): Result<Unit>
    suspend fun removeSubscription(id: String): Result<Unit>
    suspend fun refreshSubscriptions(): Result<Unit>
    suspend fun speedTest(serverId: String): Result<Pair<Long, Long>>
}

interface AIRepository {
    suspend fun chat(
        provider: AIProvider,
        model: String,
        messages: List<AIMessage>,
        onChunk: (String) -> Unit
    ): Result<String>

    suspend fun summarize(
        provider: AIProvider,
        url: String?,
        content: String,
        language: String
    ): Result<Triple<String, List<String>, Int>>

    suspend fun translate(
        provider: AIProvider,
        text: String,
        sourceLanguage: String,
        targetLanguage: String
    ): Result<Pair<String, String?>>

    suspend fun ocr(
        provider: AIProvider,
        imageUrl: String?,
        imageBase64: String?
    ): Result<Pair<String, Float>>

    fun getAISessions(): Flow<List<AISession>>
    suspend fun getAISessionById(id: String): AISession?
    fun getAISessionByIdFlow(id: String): Flow<AISession?>
    suspend fun createAISession(session: AISession)
    suspend fun updateAISession(session: AISession)
    suspend fun deleteAISession(id: String)
    fun getMessagesBySession(sessionId: String): Flow<List<AIMessage>>
    suspend fun addMessage(message: AIMessage)
    suspend fun deleteMessagesBySession(sessionId: String)
}

interface SettingsRepository {
    fun getSettings(): Flow<AppSettings>
    suspend fun updateSettings(settings: AppSettings)
    suspend fun updateDarkMode(isDarkMode: Boolean)
    suspend fun updateAutoVPN(isAutoVPN: Boolean)
    suspend fun updateKillSwitch(isKillSwitch: Boolean)
    suspend fun updateDNSLeakProtection(isEnabled: Boolean)
    suspend fun updateDefaultSearchEngine(searchEngine: SearchEngine)
    suspend fun updateDefaultAIProvider(provider: AIProvider)
    suspend fun updateAdBlockEnabled(isEnabled: Boolean)
    suspend fun updateIncognitoDefault(isIncognito: Boolean)
    suspend fun updateUserAgent(userAgent: String)
    suspend fun updateLanguage(language: String)
}
