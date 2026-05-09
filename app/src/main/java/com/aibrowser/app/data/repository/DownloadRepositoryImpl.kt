package com.aibrowser.app.data.repository

import com.aibrowser.app.data.local.database.DownloadDao
import com.aibrowser.app.data.local.entity.DownloadEntity
import com.aibrowser.app.domain.model.DownloadItem
import com.aibrowser.app.domain.model.DownloadStatus
import com.aibrowser.app.domain.repository.DownloadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DownloadRepositoryImpl @Inject constructor(
    private val downloadDao: DownloadDao
) : DownloadRepository {

    override fun getAllDownloads(): Flow<List<DownloadItem>> {
        return downloadDao.getAllDownloads().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getDownloadsByStatus(status: DownloadStatus): Flow<List<DownloadItem>> {
        return downloadDao.getDownloadsByStatus(status.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getDownloadById(id: String): DownloadItem? {
        return downloadDao.getDownloadById(id)?.toDomain()
    }

    override suspend fun addDownload(download: DownloadItem) {
        downloadDao.insertDownload(download.toEntity())
    }

    override suspend fun updateDownload(download: DownloadItem) {
        downloadDao.updateDownload(download.toEntity())
    }

    override suspend fun deleteDownload(id: String) {
        downloadDao.deleteDownloadById(id)
    }

    override suspend fun deleteAllDownloads() {
        downloadDao.deleteAllDownloads()
    }
}

fun DownloadEntity.toDomain(): DownloadItem {
    return DownloadItem(
        id = id,
        url = url,
        fileName = fileName,
        filePath = filePath,
        mimeType = mimeType,
        totalBytes = totalBytes,
        downloadedBytes = downloadedBytes,
        status = DownloadStatus.valueOf(status),
        createdAt = createdAt
    )
}

fun DownloadItem.toEntity(): DownloadEntity {
    return DownloadEntity(
        id = id,
        url = url,
        fileName = fileName,
        filePath = filePath,
        mimeType = mimeType,
        totalBytes = totalBytes,
        downloadedBytes = downloadedBytes,
        status = status.name,
        createdAt = createdAt
    )
}
