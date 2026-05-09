package com.aibrowser.app.data.repository

import com.aibrowser.app.data.local.database.HistoryDao
import com.aibrowser.app.data.local.entity.HistoryEntity
import com.aibrowser.app.domain.model.HistoryItem
import com.aibrowser.app.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val historyDao: HistoryDao
) : HistoryRepository {

    override fun getAllHistory(): Flow<List<HistoryItem>> {
        return historyDao.getAllHistory().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRecentHistory(imit: Int): Flow<List<HistoryItem>> {
        return historyDao.getRecentHistory(imit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchHistory(query: String): Flow<List<HistoryItem>> {
        return historyDao.searchHistory(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getHistoryById(id: String): HistoryItem? {
        return historyDao.getHistoryById(id)?.toDomain()
    }

    override suspend fun addHistory(history: HistoryItem) {
        val existing = historyDao.getHistoryByUrl(history.url)
        if (existing != null) {
            historyDao.updateHistory(
                existing.copy(
                    title = history.title,
                    visitedAt = System.currentTimeMillis(),
                    visitCount = existing.visitCount + 1
                )
            )
        } else {
            historyDao.insertHistory(history.toEntity())
        }
    }

    override suspend fun deleteHistory(id: String) {
        historyDao.deleteHistoryById(id)
    }

    override suspend fun deleteHistoryOlderThan(timestamp: Long) {
        historyDao.deleteHistoryOlderThan(timestamp)
    }

    override suspend fun deleteAllHistory() {
        historyDao.deleteAllHistory()
    }

    override suspend fun syncHistory(): Result<Unit> {
        return Result.success(Unit)
    }
}

fun HistoryEntity.toDomain(): HistoryItem {
    return HistoryItem(
        id = id,
        title = title,
        url = url,
        iconUrl = iconUrl,
        visitedAt = visitedAt,
        visitCount = visitCount,
        isSynced = isSynced
    )
}

fun HistoryItem.toEntity(): HistoryEntity {
    return HistoryEntity(
        id = id,
        title = title,
        url = url,
        iconUrl = iconUrl,
        visitedAt = visitedAt,
        visitCount = visitCount,
        isSynced = isSynced
    )
}
