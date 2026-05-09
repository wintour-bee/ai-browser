package com.aibrowser.app.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.aibrowser.app.data.remote.dto.UserDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    val userData: Flow<Map<String, String>> = dataStore.data.map { preferences ->
        preferences.asMap().mapKeys { it.key.name }.mapValues { it.value.toString() }
    }

    suspend fun saveUser(user: UserDto) {
        dataStore.edit { preferences ->
            preferences[KEY_USER_ID] = user.id
            preferences[KEY_USER_EMAIL] = user.email
            preferences[KEY_USER_NAME] = user.username
            preferences[KEY_USER_AVATAR] = user.avatarUrl ?: ""
            preferences[KEY_USER_IS_VIP] = user.isVip.toString()
            preferences[KEY_USER_VIP_LEVEL] = user.vipLevel.toString()
            preferences[KEY_USER_POINTS] = user.points.toString()
            preferences[KEY_USER_INVITE_CODE] = user.inviteCode
        }
    }

    suspend fun getUser(): UserDto? {
        val preferences = dataStore.data.first()
        val id = preferences[KEY_USER_ID] ?: return null
        return UserDto(
            id = id,
            email = preferences[KEY_USER_EMAIL] ?: "",
            username = preferences[KEY_USER_NAME] ?: "",
            avatarUrl = preferences[KEY_USER_AVATAR]?.takeIf { it.isNotEmpty() },
            isVip = preferences[KEY_USER_IS_VIP]?.toBoolean() ?: false,
            vipLevel = preferences[KEY_USER_VIP_LEVEL]?.toInt() ?: 0,
            points = preferences[KEY_USER_POINTS]?.toLong() ?: 0,
            inviteCode = preferences[KEY_USER_INVITE_CODE] ?: "",
            createdAt = System.currentTimeMillis()
        )
    }

    suspend fun clearUser() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_USER_ID)
            preferences.remove(KEY_USER_EMAIL)
            preferences.remove(KEY_USER_NAME)
            preferences.remove(KEY_USER_AVATAR)
            preferences.remove(KEY_USER_IS_VIP)
            preferences.remove(KEY_USER_VIP_LEVEL)
            preferences.remove(KEY_USER_POINTS)
            preferences.remove(KEY_USER_INVITE_CODE)
        }
    }

    suspend fun updateUserData(data: Map<String, String>) {
        dataStore.edit { preferences ->
            data.forEach { (key, value) ->
                preferences[stringPreferencesKey(key)] = value
            }
        }
    }

    suspend fun getString(key: String, default: String = ""): String {
        return dataStore.data.first()[stringPreferencesKey(key)] ?: default
    }

    suspend fun putString(key: String, value: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }

    suspend fun getBoolean(key: String, default: Boolean = false): Boolean {
        return dataStore.data.first()[booleanPreferencesKey(key)] ?: default
    }

    suspend fun putBoolean(key: String, value: Boolean) {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(key)] = value
        }
    }

    suspend fun getLong(key: String, default: Long = 0L): Long {
        return dataStore.data.first()[longPreferencesKey(key)] ?: default
    }

    suspend fun putLong(key: String, value: Long) {
        dataStore.edit { preferences ->
            preferences[longPreferencesKey(key)] = value
        }
    }

    suspend fun getInt(key: String, default: Int = 0): Int {
        return dataStore.data.first()[intPreferencesKey(key)] ?: default
    }

    suspend fun putInt(key: String, value: Int) {
        dataStore.edit { preferences ->
            preferences[intPreferencesKey(key)] = value
        }
    }

    companion object {
        // User keys
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_USER_AVATAR = stringPreferencesKey("user_avatar")
        private val KEY_USER_IS_VIP = booleanPreferencesKey("user_is_vip")
        private val KEY_USER_VIP_LEVEL = intPreferencesKey("user_vip_level")
        private val KEY_USER_POINTS = longPreferencesKey("user_points")
        private val KEY_USER_INVITE_CODE = stringPreferencesKey("user_invite_code")

        // Settings keys
        const val PREF_DARK_MODE = "dark_mode"
        const val PREF_AUTO_VPN = "auto_vpn"
        const val PREF_KILL_SWITCH = "kill_switch"
        const val PREF_DNS_LEAK_PROTECTION = "dns_leak_protection"
        const val PREF_SEARCH_ENGINE = "search_engine"
        const val PREF_AI_PROVIDER = "ai_provider"
        const val PREF_AD_BLOCK = "ad_block"
        const val PREF_INCOGNITO_DEFAULT = "incognito_default"
        const val PREF_USER_AGENT = "user_agent"
        const val PREF_LANGUAGE = "language"
        const val PREF_BIOMETRIC = "biometric"

        // Last sync
        const val PREF_LAST_SYNC = "last_sync"
    }
}
