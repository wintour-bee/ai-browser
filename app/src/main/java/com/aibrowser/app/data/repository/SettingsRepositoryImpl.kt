package com.aibrowser.app.data.repository

import com.aibrowser.app.data.local.preferences.UserPreferences
import com.aibrowser.app.domain.model.AIProvider
import com.aibrowser.app.domain.model.AppSettings
import com.aibrowser.app.domain.model.SearchEngine
import com.aibrowser.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val userPreferences: UserPreferences
) : SettingsRepository {

    override fun getSettings(): Flow<AppSettings> {
        return userPreferences.userData.map { userData ->
            AppSettings(
                isDarkMode = userData[UserPreferences.PREF_DARK_MODE]?.toBoolean() ?: true,
                isAutoVPN = userData[UserPreferences.PREF_AUTO_VPN]?.toBoolean() ?: false,
                isKillSwitch = userData[UserPreferences.PREF_KILL_SWITCH]?.toBoolean() ?: true,
                isDNSLeakProtection = userData[UserPreferences.PREF_DNS_LEAK_PROTECTION]?.toBoolean() ?: true,
                defaultSearchEngine = SearchEngine.valueOf(
                    userData[UserPreferences.PREF_SEARCH_ENGINE] ?: SearchEngine.GOOGLE.name
                ),
                defaultAIProvider = AIProvider.valueOf(
                    userData[UserPreferences.PREF_AI_PROVIDER] ?: AIProvider.CHATGPT.name
                ),
                isAdBlockEnabled = userData[UserPreferences.PREF_AD_BLOCK]?.toBoolean() ?: true,
                isIncognitoDefault = userData[UserPreferences.PREF_INCOGNITO_DEFAULT]?.toBoolean() ?: false,
                userAgent = userData[UserPreferences.PREF_USER_AGENT] ?: "",
                language = userData[UserPreferences.PREF_LANGUAGE] ?: "auto",
                isBiometricEnabled = userData[UserPreferences.PREF_BIOMETRIC]?.toBoolean() ?: false
            )
        }
    }

    override suspend fun updateSettings(settings: AppSettings) {
        userPreferences.updateUserData(
            mapOf(
                UserPreferences.PREF_DARK_MODE to settings.isDarkMode.toString(),
                UserPreferences.PREF_AUTO_VPN to settings.isAutoVPN.toString(),
                UserPreferences.PREF_KILL_SWITCH to settings.isKillSwitch.toString(),
                UserPreferences.PREF_DNS_LEAK_PROTECTION to settings.isDNSLeakProtection.toString(),
                UserPreferences.PREF_SEARCH_ENGINE to settings.defaultSearchEngine.name,
                UserPreferences.PREF_AI_PROVIDER to settings.defaultAIProvider.name,
                UserPreferences.PREF_AD_BLOCK to settings.isAdBlockEnabled.toString(),
                UserPreferences.PREF_INCOGNITO_DEFAULT to settings.isIncognitoDefault.toString(),
                UserPreferences.PREF_USER_AGENT to settings.userAgent,
                UserPreferences.PREF_LANGUAGE to settings.language,
                UserPreferences.PREF_BIOMETRIC to settings.isBiometricEnabled.toString()
            )
        )
    }

    override suspend fun updateDarkMode(isDarkMode: Boolean) {
        userPreferences.updateUserData(mapOf(UserPreferences.PREF_DARK_MODE to isDarkMode.toString()))
    }

    override suspend fun updateAutoVPN(isAutoVPN: Boolean) {
        userPreferences.updateUserData(mapOf(UserPreferences.PREF_AUTO_VPN to isAutoVPN.toString()))
    }

    override suspend fun updateKillSwitch(isKillSwitch: Boolean) {
        userPreferences.updateUserData(mapOf(UserPreferences.PREF_KILL_SWITCH to isKillSwitch.toString()))
    }

    override suspend fun updateDNSLeakProtection(isEnabled: Boolean) {
        userPreferences.updateUserData(mapOf(UserPreferences.PREF_DNS_LEAK_PROTECTION to isEnabled.toString()))
    }

    override suspend fun updateDefaultSearchEngine(searchEngine: SearchEngine) {
        userPreferences.updateUserData(mapOf(UserPreferences.PREF_SEARCH_ENGINE to searchEngine.name))
    }

    override suspend fun updateDefaultAIProvider(provider: AIProvider) {
        userPreferences.updateUserData(mapOf(UserPreferences.PREF_AI_PROVIDER to provider.name))
    }

    override suspend fun updateAdBlockEnabled(isEnabled: Boolean) {
        userPreferences.updateUserData(mapOf(UserPreferences.PREF_AD_BLOCK to isEnabled.toString()))
    }

    override suspend fun updateIncognitoDefault(isIncognito: Boolean) {
        userPreferences.updateUserData(mapOf(UserPreferences.PREF_INCOGNITO_DEFAULT to isIncognito.toString()))
    }

    override suspend fun updateUserAgent(userAgent: String) {
        userPreferences.updateUserData(mapOf(UserPreferences.PREF_USER_AGENT to userAgent))
    }

    override suspend fun updateLanguage(language: String) {
        userPreferences.updateUserData(mapOf(UserPreferences.PREF_LANGUAGE to language))
    }
}
