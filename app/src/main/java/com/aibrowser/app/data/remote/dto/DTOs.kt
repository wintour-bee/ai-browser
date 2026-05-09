package com.aibrowser.app.data.remote.dto

import kotlinx.serialization.Serializable

// Auth DTOs
@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequestDto(
    val email: String,
    val password: String,
    val username: String,
    val inviteCode: String? = null
)

@Serializable
data class GoogleAuthRequestDto(
    val idToken: String
)

@Serializable
data class RefreshTokenRequestDto(
    val refreshToken: String
)

@Serializable
data class AuthResponseDto(
    val user: UserDto,
    val token: TokenDto
)

@Serializable
data class TokenDto(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val username: String,
    val avatarUrl: String? = null,
    val isVip: Boolean = false,
    val vipExpireTime: Long? = null,
    val vipLevel: Int = 0,
    val points: Long = 0,
    val inviteCode: String = "",
    val createdAt: Long = 0
)

@Serializable
data class UpdateProfileRequestDto(
    val username: String? = null,
    val avatarUrl: String? = null
)

@Serializable
data class ChangePasswordRequestDto(
    val oldPassword: String,
    val newPassword: String
)

// VIP DTOs
@Serializable
data class VipStatusDto(
    val isVip: Boolean,
    val level: Int,
    val expireTime: Long?,
    val features: List<String>
)

@Serializable
data class ActivateVipRequestDto(
    val purchaseToken: String,
    val productId: String
)

// Sync DTOs
@Serializable
data class BookmarkDto(
    val id: String,
    val title: String,
    val url: String,
    val iconUrl: String? = null,
    val folderId: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)

@Serializable
data class HistoryDto(
    val id: String,
    val title: String,
    val url: String,
    val iconUrl: String? = null,
    val visitedAt: Long,
    val visitCount: Int
)

@Serializable
data class TabDto(
    val id: String,
    val url: String,
    val title: String,
    val iconUrl: String? = null,
    val isIncognito: Boolean,
    val position: Int,
    val scrollPosition: Int,
    val createdAt: Long
)

// Invitation DTOs
@Serializable
data class InviteCodeDto(
    val code: String,
    val url: String,
    val totalUses: Int,
    val totalRewards: Long
)

@Serializable
data class UseInviteCodeRequestDto(
    val code: String
)

@Serializable
data class InviteRewardDto(
    val id: String,
    val inviterUsername: String,
    val rewardAmount: Long,
    val rewardedAt: Long
)

// Check-in DTOs
@Serializable
data class CheckInResponseDto(
    val success: Boolean,
    val points: Long,
    val streak: Int
)

@Serializable
data class CheckInStatusDto(
    val checkedInToday: Boolean,
    val currentStreak: Int,
    val totalPoints: Long,
    val nextReward: Long
)

// Settings DTOs
@Serializable
data class SettingsDto(
    val isDarkMode: Boolean = true,
    val isAutoVPN: Boolean = false,
    val isKillSwitch: Boolean = true,
    val isDNSLeakProtection: Boolean = true,
    val defaultSearchEngine: String = "GOOGLE",
    val defaultAIProvider: String = "CHATGPT",
    val isAdBlockEnabled: Boolean = true,
    val isIncognitoDefault: Boolean = false,
    val userAgent: String = "",
    val language: String = "auto"
)

// AI DTOs
@Serializable
data class AIChatRequestDto(
    val provider: String,
    val model: String,
    val messages: List<AIMessageDto>,
    val temperature: Float = 0.7f,
    val maxTokens: Int = 4096,
    val stream: Boolean = true
)

@Serializable
data class AIMessageDto(
    val role: String,
    val content: String,
    val imageUrls: List<String> = emptyList()
)

@Serializable
data class AIChatResponseDto(
    val id: String,
    val content: String,
    val model: String,
    val usage: AIUsageDto? = null
)

@Serializable
data class AIUsageDto(
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int
)

@Serializable
data class AISummarizeRequestDto(
    val provider: String,
    val url: String? = null,
    val content: String,
    val language: String = "auto",
    val maxLength: Int = 500
)

@Serializable
data class AISummarizeResponseDto(
    val summary: String,
    val keyPoints: List<String>,
    val estimatedReadTime: Int
)

@Serializable
data class AITranslateRequestDto(
    val provider: String,
    val text: String,
    val sourceLanguage: String = "auto",
    val targetLanguage: String = "zh"
)

@Serializable
data class AITranslateResponseDto(
    val translatedText: String,
    val detectedLanguage: String? = null
)

@Serializable
data class AIOcrRequestDto(
    val provider: String,
    val imageUrl: String? = null,
    val imageBase64: String? = null,
    val language: String = "auto"
)

@Serializable
data class AIOcrResponseDto(
    val text: String,
    val confidence: Float
)

@Serializable
data class AIModelDto(
    val id: String,
    val name: String,
    val provider: String,
    val maxTokens: Int,
    val isAvailable: Boolean = true
)

// VPN DTOs
@Serializable
data class VpnServerDto(
    val id: String,
    val name: String,
    val country: String,
    val city: String,
    val host: String,
    val port: Int,
    val protocol: String,
    val config: String,
    val pingLatency: Long? = null,
    val isPremium: Boolean = false,
    val load: Float = 0f
)

@Serializable
data class PingResponseDto(
    val latency: Long,
    val serverId: String
)

@Serializable
data class VpnSubscriptionDto(
    val id: String,
    val url: String,
    val name: String,
    val serverCount: Int,
    val expiresAt: Long,
    val isActive: Boolean = true
)

@Serializable
data class AddSubscriptionRequestDto(
    val url: String,
    val name: String
)

@Serializable
data class VpnConfigDto(
    val id: String,
    val config: String,
    val protocol: String
)

@Serializable
data class SpeedTestRequestDto(
    val serverId: String
)

@Serializable
data class SpeedTestResponseDto(
    val downloadSpeed: Long,
    val uploadSpeed: Long,
    val latency: Long
)
