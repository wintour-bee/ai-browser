package com.aibrowser.app.data.local.entity

import androidx.room.*
import com.aibrowser.app.domain.model.AIRole

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val url: String,
    val iconUrl: String?,
    val folderId: String?,
    val createdAt: Long,
    val isSynced: Boolean
)

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val url: String,
    val iconUrl: String?,
    val visitedAt: Long,
    val visitCount: Int,
    val isSynced: Boolean
)

@Entity(tableName = "tabs")
data class TabEntity(
    @PrimaryKey
    val id: String,
    val url: String,
    val title: String,
    val iconUrl: String?,
    val isIncognito: Boolean,
    val position: Int,
    val scrollPosition: Int,
    val lastAccessedAt: Long,
    val createdAt: Long
)

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey
    val id: String,
    val url: String,
    val fileName: String,
    val filePath: String?,
    val mimeType: String?,
    val totalBytes: Long,
    val downloadedBytes: Long,
    val status: String,
    val createdAt: Long
)

@Entity(tableName = "vpn_servers")
data class VPNServerEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val country: String,
    val city: String,
    val host: String,
    val port: Int,
    val protocol: String,
    val config: String,
    val pingLatency: Long?,
    val isPremium: Boolean,
    val load: Float,
    val isFavorite: Boolean = false
)

@Entity(tableName = "ai_sessions")
data class AISessionEntity(
    @PrimaryKey
    val id: String,
    val provider: String,
    val title: String,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    tableName = "ai_messages",
    foreignKeys = [
        ForeignKey(
            entity = AISessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId")]
)
data class AIMessageEntity(
    @PrimaryKey
    val id: String,
    val sessionId: String,
    val role: String,
    val content: String,
    val imageUrls: String, // JSON array stored as string
    val createdAt: Long,
    val isStreaming: Boolean
)
