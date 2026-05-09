package com.aibrowser.app.data.repository

import com.aibrowser.app.data.local.database.TabDao
import com.aibrowser.app.data.local.entity.TabEntity
import com.aibrowser.app.domain.model.Tab
import com.aibrowser.app.domain.repository.TabRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TabRepositoryImpl @Inject constructor(
    private val tabDao: TabDao
) : TabRepository {

    override fun getAllTabs(): Flow<List<Tab>> {
        return tabDao.getAllTabs().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getNormalTabs(): Flow<List<Tab>> {
        return tabDao.getNormalTabs().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getIncognitoTabs(): Flow<List<Tab>> {
        return tabDao.getIncognitoTabs().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTabById(id: String): Tab? {
        return tabDao.getTabById(id)?.toDomain()
    }

    override fun getTabByIdFlow(id: String): Flow<Tab?> {
        return tabDao.getTabByIdFlow(id).map { it?.toDomain() }
    }

    override suspend fun addTab(tab: Tab) {
        val maxPosition = tabDao.getMaxPosition() ?: -1
        tabDao.insertTab(tab.toEntity().copy(position = maxPosition + 1))
    }

    override suspend fun updateTab(tab: Tab) {
        tabDao.updateTab(tab.toEntity())
    }

    override suspend fun deleteTab(id: String) {
        tabDao.deleteTabById(id)
    }

    override suspend fun deleteAllTabs() {
        tabDao.deleteAllTabs()
    }

    override suspend fun syncTabs(): Result<Unit> {
        return Result.success(Unit)
    }
}

fun TabEntity.toDomain(): Tab {
    return Tab(
        id = id,
        url = url,
        title = title,
        iconUrl = iconUrl,
        isIncognito = isIncognito,
        position = position,
        scrollPosition = scrollPosition,
        lastAccessedAt = lastAccessedAt,
        createdAt = createdAt
    )
}

fun Tab.toEntity(): TabEntity {
    return TabEntity(
        id = id,
        url = url,
        title = title,
        iconUrl = iconUrl,
        isIncognito = isIncognito,
        position = position,
        scrollPosition = scrollPosition,
        lastAccessedAt = lastAccessedAt,
        createdAt = createdAt
    )
}
