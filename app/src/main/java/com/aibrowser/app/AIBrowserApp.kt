package com.aibrowser.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AIBrowserApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)

            // VPN Channel
            val vpnChannel = NotificationChannel(
                VPN_CHANNEL_ID,
                "VPN Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "VPN connection status notifications"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(vpnChannel)

            // Download Channel
            val downloadChannel = NotificationChannel(
                DOWNLOAD_CHANNEL_ID,
                "Downloads",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Download progress notifications"
            }
            notificationManager.createNotificationChannel(downloadChannel)

            // AI Assistant Channel
            val aiChannel = NotificationChannel(
                AI_CHANNEL_ID,
                "AI Assistant",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "AI assistant notifications"
            }
            notificationManager.createNotificationChannel(aiChannel)
        }
    }

    companion object {
        const val VPN_CHANNEL_ID = "vpn_service_channel"
        const val DOWNLOAD_CHANNEL_ID = "download_channel"
        const val AI_CHANNEL_ID = "ai_assistant_channel"
    }
}
