package com.aibrowser.app.data.local.database

import androidx.room.*
import com.aibrowser.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Database(
    entities = [
        BookmarkEntity::class,
        HistoryEntity::class,
        TabEntity::class,
        DownloadEntity::class,
        VPNServerEntity::class,
        AISessionEntity::class,
        AIMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AIBrowserDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun historyDao(): HistoryDao
    abstract fun tabDao(): TabDao
    abstract fun downloadDao(): DownloadDao
    abstract fun vpnDao(): VPNDao
    abstract fun aiDao(): AIDao
}

// Bookmark DAO
@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE folderId IS NULL ORDER BY createdAt DESC")
    fun getRootBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE folderId = :folderId ORDER BY createdAt DESC")
    fun getBookmarksByFolder(folderId: String): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE id = :id")
    suspend fun getBookmarkById(id: String): BookmarkEntity?

    @Query("SELECT * FROM bookmarks WHERE url = :url")
    suspend fun getBookmarkByUrl(url: String): BookmarkEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmarks(bookmarks: List<BookmarkEntity>)

    @Update
    suspend fun updateBookmark(bookmark: BookmarkEntity)

    @Delete
    suspend fun deleteBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmarkById(id: String)

    @Query("DELETE FROM bookmarks")
    suspend fun deleteAllBookmarks()

    @Query("SELECT COUNT(*) FROM bookmarks")
    suspend fun getBookmarkCount(): Int

    @Query("SELECT * FROM bookmarks WHERE isSynced = 0")
    suspend fun getUnsyncedBookmarks(): List<BookmarkEntity>
}

// History DAO
@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY visitedAt DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history ORDER BY visitedAt DESC LIMIT :limit")
    fun getRecentHistory(limit: Int): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history WHERE visitedAt > :since ORDER BY visitedAt DESC")
    fun getHistorySince(since: Long): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history WHERE id = :id")
    suspend fun getHistoryById(id: String): HistoryEntity?

    @Query("SELECT * FROM history WHERE url = :url")
    suspend fun getHistoryByUrl(url: String): HistoryEntity?

    @Query("SELECT * FROM history WHERE title LIKE '%' || :query || '%' OR url LIKE '%' || :query || '%' ORDER BY visitedAt DESC")
    fun searchHistory(query: String): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity)

    @Update
    suspend fun updateHistory(history: HistoryEntity)

    @Delete
    suspend fun deleteHistory(history: HistoryEntity)

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteHistoryById(id: String)

    @Query("DELETE FROM history WHERE visitedAt < :timestamp")
    suspend fun deleteHistoryOlderThan(timestamp: Long)

    @Query("DELETE FROM history")
    suspend fun deleteAllHistory()

    @Query("SELECT COUNT(*) FROM history")
    suspend fun getHistoryCount(): Int

    @Query("SELECT * FROM history WHERE isSynced = 0")
    suspend fun getUnsyncedHistory(): List<HistoryEntity>
}

// Tab DAO
@Dao
interface TabDao {
    @Query("SELECT * FROM tabs ORDER BY position ASC")
    fun getAllTabs(): Flow<List<TabEntity>>

    @Query("SELECT * FROM tabs WHERE isIncognito = 0 ORDER BY position ASC")
    fun getNormalTabs(): Flow<List<TabEntity>>

    @Query("SELECT * FROM tabs WHERE isIncognito = 1 ORDER BY position ASC")
    fun getIncognitoTabs(): Flow<List<TabEntity>>

    @Query("SELECT * FROM tabs WHERE id = :id")
    suspend fun getTabById(id: String): TabEntity?

    @Query("SELECT * FROM tabs WHERE id = :id")
    fun getTabByIdFlow(id: String): Flow<TabEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTab(tab: TabEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTabs(tabs: List<TabEntity>)

    @Update
    suspend fun updateTab(tab: TabEntity)

    @Delete
    suspend fun deleteTab(tab: TabEntity)

    @Query("DELETE FROM tabs WHERE id = :id")
    suspend fun deleteTabById(id: String)

    @Query("DELETE FROM tabs")
    suspend fun deleteAllTabs()

    @Query("SELECT COUNT(*) FROM tabs")
    suspend fun getTabCount(): Int

    @Query("SELECT MAX(position) FROM tabs")
    suspend fun getMaxPosition(): Int?
}

// Download DAO
@Dao
interface DownloadDao {
    @Query("SELECT * FROM downloads ORDER BY createdAt DESC")
    fun getAllDownloads(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE status = :status ORDER BY createdAt DESC")
    fun getDownloadsByStatus(status: String): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE id = :id")
    suspend fun getDownloadById(id: String): DownloadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: DownloadEntity)

    @Update
    suspend fun updateDownload(download: DownloadEntity)

    @Delete
    suspend fun deleteDownload(download: DownloadEntity)

    @Query("DELETE FROM downloads WHERE id = :id")
    suspend fun deleteDownloadById(id: String)

    @Query("DELETE FROM downloads")
    suspend fun deleteAllDownloads()
}

// VPN DAO
@Dao
interface VPNDao {
    @Query("SELECT * FROM vpn_servers ORDER BY name ASC")
    fun getAllServers(): Flow<List<VPNServerEntity>>

    @Query("SELECT * FROM vpn_servers WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteServers(): Flow<List<VPNServerEntity>>

    @Query("SELECT * FROM vpn_servers WHERE id = :id")
    suspend fun getServerById(id: String): VPNServerEntity?

    @Query("SELECT * FROM vpn_servers WHERE country = :country ORDER BY load ASC")
    fun getServersByCountry(country: String): Flow<List<VPNServerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServer(server: VPNServerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServers(servers: List<VPNServerEntity>)

    @Update
    suspend fun updateServer(server: VPNServerEntity)

    @Delete
    suspend fun deleteServer(server: VPNServerEntity)

    @Query("DELETE FROM vpn_servers")
    suspend fun deleteAllServers()
}

// AI DAO
@Dao
interface AIDao {
    @Query("SELECT * FROM ai_sessions ORDER BY createdAt DESC")
    fun getAllSessions(): Flow<List<AISessionEntity>>

    @Query("SELECT * FROM ai_sessions WHERE id = :id")
    suspend fun getSessionById(id: String): AISessionEntity?

    @Query("SELECT * FROM ai_sessions WHERE id = :id")
    fun getSessionByIdFlow(id: String): Flow<AISessionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: AISessionEntity)

    @Update
    suspend fun updateSession(session: AISessionEntity)

    @Delete
    suspend fun deleteSession(session: AISessionEntity)

    @Query("DELETE FROM sessions WHERE id = :id")
    suspend fun deleteSessionById(id: String)

    @Query("SELECT * FROM ai_messages WHERE sessionId = :sessionId ORDER BY createdAt ASC")
    fun getMessagesBySession(sessionId: String): Flow<List<AIMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: AIMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<AIMessageEntity>)

    @Delete
    suspend fun deleteMessage(message: AIMessageEntity)

    @Query("DELETE FROM ai_messages WHERE sessionId = :sessionId")
    suspend fun deleteMessagesBySession(sessionId: String)
}
