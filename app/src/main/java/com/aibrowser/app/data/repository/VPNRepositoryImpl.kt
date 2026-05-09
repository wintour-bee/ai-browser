package com.aibrowser.app.data.repository

import android.content.Context
import com.aibrowser.app.data.local.database.VPNDao
import com.aibrowser.app.data.local.entity.VPNServerEntity
import com.aibrowser.app.data.remote.api.VPNApi
import com.aibrowser.app.data.remote.dto.AddSubscriptionRequestDto
import com.aibrowser.app.data.remote.dto.VpnServerDto
import com.aibrowser.app.domain.model.VPNConnection
import com.aibrowser.app.domain.model.VPNProtocol
import com.aibrowser.app.domain.model.VPNServer
import com.aibrowser.app.domain.repository.VPNRepository
import com.aibrowser.app.data.local.preferences.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VPNRepositoryImpl @Inject constructor(
    private val vpnApi: VPNApi,
    private val userPreferences: UserPreferences,
    @ApplicationContext private val context: Context
) : VPNRepository {

    private val _isConnected = MutableStateFlow(false)
    private val _connectionInfo = MutableStateFlow<VPNConnection?>(null)

    override fun getAllServers(): Flow<List<VPNServer>> {
        return MutableStateFlow<List<VPNServer>>(emptyList())
    }

    override fun getFavoriteServers(): Flow<List<VPNServer>> {
        return MutableStateFlow<List<VPNServer>>(emptyList())
    }

    override suspend fun getServerById(id: String): VPNServer? {
        return null
    }

    override suspend fun refreshServers(): Result<List<VPNServer>> {
        return try {
            val response = vpnApi.getServers()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toDomain() })
            } else {
                Result.failure(Exception(response.message() ?: "Failed to get servers"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun pingServer(id: String): Result<Long> {
        return try {
            val response = vpnApi.pingServer(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.latency)
            } else {
                Result.failure(Exception(response.message() ?: "Ping failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun connect(server: VPNServer): Result<Unit> {
        return try {
            val response = vpnApi.getServerConfig(server.id)
            if (response.isSuccessful && response.body() != null) {
                _isConnected.value = true
                _connectionInfo.value = VPNConnection(
                    server = server,
                    isConnected = true
                )
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message() ?: "Connection failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun disconnect(): Result<Unit> {
        _isConnected.value = false
        _connectionInfo.value = null
        return Result.success(Unit)
    }

    override fun isConnected(): Flow<Boolean> = _isConnected

    override fun getConnectionInfo(): Flow<VPNConnection?> = _connectionInfo

    override suspend fun addSubscription(url: String, name: String): Result<Unit> {
        return try {
            vpnApi.addSubscription(AddSubscriptionRequestDto(url, name))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeSubscription(id: String): Result<Unit> {
        return try {
            vpnApi.removeSubscription(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refreshSubscriptions(): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun speedTest(serverId: String): Result<Pair<Long, Long>> {
        return try {
            val response = vpnApi.speedTest(
                com.aibrowser.app.data.remote.dto.SpeedTestRequestDto(serverId)
            )
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!
                Result.success(Pair(result.downloadSpeed, result.uploadSpeed))
            } else {
                Result.failure(Exception(response.message() ?: "Speed test failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

fun VpnServerDto.toDomain(): VPNServer {
    return VPNServer(
        id = id,
        name = name,
        country = country,
        city = city,
        host = host,
        port = port,
        protocol = VPNProtocol.valueOf(protocol.uppercase()),
        config = config,
        pingLatency = pingLatency,
        isPremium = isPremium,
        load = load
    )
}
