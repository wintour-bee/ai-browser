package com.aibrowser.app.vpn.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.aibrowser.app.vpn.service.VPNService

class VPNBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            VPNService.ACTION_CONNECTION_STATUS -> {
                val isConnected = intent.getBooleanExtra(VPNService.EXTRA_IS_CONNECTED, false)
                // Handle connection status change
                // Could broadcast to other components or update UI
            }
        }
    }
}
