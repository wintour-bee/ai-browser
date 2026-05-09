package com.aibrowser.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val email: String,
    val username: String,
    val avatarUrl: String? = null,
    val isVip: Boolean = false,
    val vipExpireTime: Long? = null,
    val vipLevel: Int = 0,
    val points: Long = 0,
    val inviteCode: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val username: String,
    val inviteCode: String? = null
)

@Serializable
data class GoogleAuthRequest(
    val idToken: String
)

@Serializable
data class AuthResponse(
    val user: User,
    val token: AuthToken
)

data class Bookmark(
    val id: String,
    val title: String,
    val url: String,
    val iconUrl: String? = null,
    val folderId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

data class BookmarkFolder(
    val id: String,
    val name: String,
    val parentId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class HistoryItem(
    val id: String,
    val title: String,
    val url: String,
    val iconUrl: String? = null,
    val visitedAt: Long = System.currentTimeMillis(),
    val visitCount: Int = 1,
    val isSynced: Boolean = false
)

data class Tab(
    val id: String,
    val url: String,
    val title: String,
    val iconUrl: String? = null,
    val isIncognito: Boolean = false,
    val position: Int = 0,
    val scrollPosition: Int = 0,
    val lastAccessedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)

data class DownloadItem(
    val id: String,
    val url: String,
    val fileName: String,
    val filePath: String? = null,
    val mimeType: String? = null,
    val totalBytes: Long = 0,
    val downloadedBytes: Long = 0,
    val status: DownloadStatus = DownloadStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)

enum class DownloadStatus {
    PENDING,
    DOWNLOADING,
    PAUSED,
    COMPLETED,
    FAILED
}

data class VPNServer(
    val id: String,
    val name: String,
    val country: String,
    val city: String,
    val host: String,
    val port: Int,
    val protocol: VPNProtocol = VPNProtocol.VLESS,
    val config: String,
    val pingLatency: Long? = null,
    val isPremium: Boolean = false,
    val load: Float = 0f
)

enum class VPNProtocol {
    VLESS,
    VMess,
    Trojan,
    WireGuard,
    Shadowsocks
}

data class VPNConnection(
    val server: VPNServer,
    val connectedAt: Long = System.currentTimeMillis(),
    val bytesReceived: Long = 0,
    val bytesSent: Long = 0,
    val isConnected: Boolean = false
)

@Serializable
data class VPNSubscription(
    val id: String,
    val url: String,
    val name: String,
    val expiresAt: Long,
    val isActive: Boolean = true
)

data class AISession(
    val id: String,
    val provider: AIProvider,
    val messages: List<AIMessage> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val title: String = "New Chat"
)

data class AIMessage(
    val id: String,
    val role: AIRole,
    val content: String,
    val imageUrls: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val isStreaming: Boolean = false
)

enum class AIRole {
    USER,
    ASSISTANT,
    SYSTEM
}

enum class AIProvider {
    CHATGPT,
    CLAUDE,
    GEMINI,
    DEEPSEEK
}

@Serializable
data class AIRequest(
    val provider: AIProvider,
    val model: String,
    val messages: List<AIMessage>,
    val temperature: Float = 0.7f,
    val maxTokens: Int = 4096,
    val stream: Boolean = true
)

@Serializable
data class AISummarizeRequest(
    val provider: AIProvider,
    val url: String,
    val content: String,
    val language: String = "auto"
)

@Serializable
data class AITranslateRequest(
    val provider: AIProvider,
    val text: String,
    val sourceLanguage: String = "auto",
    val targetLanguage: String = "zh"
)

data class AppSettings(
    val isDarkMode: Boolean = true,
    val isAutoVPN: Boolean = false,
    val isKillSwitch: Boolean = true,
    val isDNSLeakProtection: Boolean = true,
    val defaultSearchEngine: SearchEngine = SearchEngine.GOOGLE,
    val defaultAIProvider: AIProvider = AIProvider.CHATGPT,
    val isAdBlockEnabled: Boolean = true,
    val isIncognitoDefault: Boolean = false,
    val userAgent: String = "",
    val language: String = "auto",
    val isBiometricEnabled: Boolean = false
)

enum class SearchEngine(val displayName: String, val searchUrl: String, val iconUrl: String) {
    GOOGLE("Google", "https://www.google.com/search?q=%s", "google"),
    BING("Bing", "https://www.bing.com/search?q=%s", "bing"),
    DUCKDUCKGO("DuckDuckGo", "https://duckduckgo.com/?q=%s", "duckduckgo"),
    BAIDU("Baidu", "https://www.baidu.com/s?wd=%s", "baidu"),
    YANDEX("Yandex", "https://yandex.com/search/?text=%s", "yandex")
}

data class AdBlockRule(
    val id: String,
    val pattern: String,
    val type: AdBlockRuleType,
    val isEnabled: Boolean = true
)

enum class AdBlockRuleType {
    DOMAIN,
    URL,
    CSS,
    SCRIPT
}
