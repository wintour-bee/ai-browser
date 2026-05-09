package com.aibrowser.app.vpn.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import com.aibrowser.app.AIBrowserApp
import com.aibrowser.app.R
import com.aibrowser.app.domain.model.VPNServer
import com.aibrowser.app.presentation.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.InetAddress
import java.nio.ByteBuffer
import javax.inject.Inject

@AndroidEntryPoint
class VPNService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private var server: VPNServer? = null
    private var isRunning = false
    
    private var bytesReceived: Long = 0
    private var bytesSent: Long = 0

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, createNotification("Connecting..."))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_CONNECT -> {
                val serverJson = intent.getStringExtra(EXTRA_SERVER)
                if (serverJson != null) {
                    startVPN(serverJson)
                }
            }
            ACTION_DISCONNECT -> {
                stopVPN()
            }
            ACTION_SWITCH_SERVER -> {
                val serverJson = intent.getStringExtra(EXTRA_SERVER)
                if (serverJson != null) {
                    switchServer(serverJson)
                }
            }
        }
        return START_STICKY
    }

    private fun startVPN(serverJson: String) {
        try {
            // Parse server configuration
            server = parseServerConfig(serverJson)
            
            // Create VPN interface
            vpnInterface = createVPNInterface()
            
            if (vpnInterface != null) {
                isRunning = true
                updateNotification("Connected to ${server?.name ?: "VPN"}")
                
                // Start packet handling
                startPacketHandling()
                
                // Broadcast connection status
                sendBroadcast(createConnectionStatusIntent(true))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            stopVPN()
        }
    }

    private fun createVPNInterface(): ParcelFileDescriptor? {
        val builder = Builder()
            .setSession("AI Browser VPN")
            .addAddress("10.0.0.2", 32)
            .addRoute("0.0.0.0", 0)
            .addDnsServer("8.8.8.8")
            .addDnsServer("8.8.4.4")
            .setMtu(1500)
            .setBlocking(true)

        // Add included apps for split tunneling
        builder.addDisallowedApplication(packageName)

        // Configure for specific protocols
        when (server?.protocol) {
            com.aibrowser.app.domain.model.VPNProtocol.WireGuard -> {
                // WireGuard specific configuration
            }
            else -> {
                // V2Ray/VMess/Trojan/Shadowsocks
                builder.addAddress("10.8.0.2", 24)
            }
        }

        return builder.establish()
    }

    private fun startPacketHandling() {
        serviceScope.launch {
            val vpnFd = vpnInterface?.fileDescriptor ?: return@launch
            
            val inputStream = FileInputStream(vpnFd)
            val outputStream = FileOutputStream(vpnFd)
            val packet = ByteBuffer.allocate(32767)
            
            while (isRunning && vpnInterface != null) {
                try {
                    packet.clear()
                    val length = inputStream.read(packet.array())
                    
                    if (length > 0) {
                        packet.limit(length)
                        
                        // Process packet based on protocol
                        when (server?.protocol) {
                            com.aibrowser.app.domain.model.VPNProtocol.VLESS,
                            com.aibrowser.app.domain.model.VPNProtocol.VMess,
                            com.aibrowser.app.domain.model.VPNProtocol.Trojan -> {
                                processV2RayPacket(packet, outputStream)
                            }
                            com.aibrowser.app.domain.model.VPNProtocol.WireGuard -> {
                                processWireGuardPacket(packet, outputStream)
                            }
                            else -> {
                                // Default forwarding
                                forwardPacket(packet, outputStream)
                            }
                        }
                        
                        bytesReceived += length
                    }
                    
                    // Check for outgoing packets
                    processOutgoingPackets(outputStream)
                    
                } catch (e: Exception) {
                    if (isRunning) {
                        delay(100)
                    }
                }
            }
        }
    }

    private fun processV2RayPacket(packet: ByteBuffer, outputStream: FileOutputStream) {
        // V2Ray/Xray packet processing
        // This would integrate with the actual V2Ray core library
        try {
            // Get destination from IP header
            val destAddress = InetAddress.getByAddress(
                byteArrayOf(
                    packet.get(16),
                    packet.get(17),
                    packet.get(18),
                    packet.get(19)
                )
            )
            val destPort = ((packet.get(20).toInt() and 0xFF) shl 8) or (packet.get(21).toInt() and 0xFF)
            
            // Route through tunnel
            // Actual implementation would use GoLib or similar
            forwardPacket(packet, outputStream)
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun processWireGuardPacket(packet: ByteBuffer, outputStream: FileOutputStream) {
        // WireGuard protocol implementation
        // Would use WireGuard library
        forwardPacket(packet, outputStream)
    }

    private fun forwardPacket(packet: ByteBuffer, outputStream: FileOutputStream) {
        try {
            // Forward packet to tunnel
            outputStream.write(packet.array(), 0, packet.limit())
            outputStream.flush()
            bytesSent += packet.limit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun processOutgoingPackets(outputStream: FileOutputStream) {
        // Process any queued outgoing packets
        // This would be implemented based on the protocol
    }

    private fun stopVPN() {
        isRunning = false
        
        try {
            vpnInterface?.close()
            vpnInterface = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        sendBroadcast(createConnectionStatusIntent(false))
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun switchServer(serverJson: String) {
        val wasRunning = isRunning
        if (wasRunning) {
            stopVPN()
        }
        startVPN(serverJson)
    }

    private fun createNotification(status: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val disconnectIntent = Intent(this, VPNService::class.java).apply {
            action = ACTION_DISCONNECT
        }
        val disconnectPendingIntent = PendingIntent.getService(
            this,
            1,
            disconnectIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, AIBrowserApp.VPN_CHANNEL_ID)
            .setContentTitle("AI Browser VPN")
            .setContentText(status)
            .setSmallIcon(R.drawable.ic_vpn)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_stop, "Disconnect", disconnectPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun updateNotification(status: String) {
        val notification = createNotification(status)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createConnectionStatusIntent(isConnected: Boolean): Intent {
        return Intent(ACTION_CONNECTION_STATUS).apply {
            putExtra(EXTRA_IS_CONNECTED, isConnected)
            setPackage(packageName)
        }
    }

    private fun parseServerConfig(serverJson: String): VPNServer? {
        return try {
            kotlinx.serialization.json.Json.decodeFromString<VPNServer>(serverJson)
        } catch (e: Exception) {
            null
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        stopVPN()
        super.onDestroy()
    }

    override fun onRevoke() {
        stopVPN()
        super.onRevoke()
    }

    companion object {
        const val ACTION_CONNECT = "com.aibrowser.app.vpn.CONNECT"
        const val ACTION_DISCONNECT = "com.aibrowser.app.vpn.DISCONNECT"
        const val ACTION_SWITCH_SERVER = "com.aibrowser.app.vpn.SWITCH_SERVER"
        const val ACTION_CONNECTION_STATUS = "com.aibrowser.app.vpn.CONNECTION_STATUS"
        
        const val EXTRA_SERVER = "extra_server"
        const val EXTRA_IS_CONNECTED = "extra_is_connected"
        
        private const val NOTIFICATION_ID = 1001

        fun connect(context: Context, server: VPNServer) {
            val intent = Intent(context, VPNService::class.java).apply {
                action = ACTION_CONNECT
                putExtra(EXTRA_SERVER, kotlinx.serialization.json.Json.encodeToString(
                    kotlinx.serialization.serializer(), server
                ))
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun disconnect(context: Context) {
            val intent = Intent(context, VPNService::class.java).apply {
                action = ACTION_DISCONNECT
            }
            context.startService(intent)
        }

        fun switchServer(context: Context, server: VPNServer) {
            val intent = Intent(context, VPNService::class.java).apply {
                action = ACTION_SWITCH_SERVER
                putExtra(EXTRA_SERVER, kotlinx.serialization.json.Json.encodeToString(
                    kotlinx.serialization.serializer(), server
                ))
            }
            context.startService(intent)
        }
    }
}
