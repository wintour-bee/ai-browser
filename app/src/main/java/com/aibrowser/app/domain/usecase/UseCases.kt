package com.aibrowser.app.domain.usecase

import com.aibrowser.app.domain.model.*
import com.aibrowser.app.domain.repository.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (email.isBlank()) {
            return Result.failure(Exception("Email cannot be empty"))
        }
        if (password.isBlank()) {
            return Result.failure(Exception("Password cannot be empty"))
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("Invalid email format"))
        }
        if (password.length < 6) {
            return Result.failure(Exception("Password must be at least 6 characters"))
        }
        return authRepository.login(email, password)
    }
}

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        username: String,
        inviteCode: String? = null
    ): Result<User> {
        if (email.isBlank()) {
            return Result.failure(Exception("Email cannot be empty"))
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("Invalid email format"))
        }
        if (password.length < 6) {
            return Result.failure(Exception("Password must be at least 6 characters"))
        }
        if (username.isBlank()) {
            return Result.failure(Exception("Username cannot be empty"))
        }
        if (username.length < 2) {
            return Result.failure(Exception("Username must be at least 2 characters"))
        }
        return authRepository.register(email, password, username, inviteCode)
    }
}

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logout()
    }
}

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<User> {
        return authRepository.getCurrentUser()
    }

    fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }
}

class SyncDataUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val historyRepository: HistoryRepository,
    private val tabRepository: TabRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        if (!authRepository.isLoggedIn()) {
            return Result.failure(Exception("Not logged in"))
        }

        return try {
            // Sync bookmarks
            bookmarkRepository.syncBookmarks()
            
            // Sync history
            historyRepository.syncHistory()
            
            // Sync tabs
            tabRepository.syncTabs()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class GetBookmarksUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    operator fun invoke(): Flow<List<Bookmark>> {
        return bookmarkRepository.getAllBookmarks()
    }

    fun getByFolder(folderId: String?): Flow<List<Bookmark>> {
        return bookmarkRepository.getBookmarksByFolder(folderId)
    }
}

class AddBookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(title: String, url: String, folderId: String? = null): Result<Bookmark> {
        if (url.isBlank()) {
            return Result.failure(Exception("URL cannot be empty"))
        }
        if (title.isBlank()) {
            return Result.failure(Exception("Title cannot be empty"))
        }

        val bookmark = Bookmark(
            id = java.util.UUID.randomUUID().toString(),
            title = title,
            url = url,
            folderId = folderId,
            createdAt = System.currentTimeMillis()
        )
        
        bookmarkRepository.addBookmark(bookmark)
        return Result.success(bookmark)
    }
}

class DeleteBookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        bookmarkRepository.deleteBookmark(id)
        return Result.success(Unit)
    }
}

class GetHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    operator fun invoke(): Flow<List<HistoryItem>> {
        return historyRepository.getAllHistory()
    }

    fun getRecent(limit: Int = 50): Flow<List<HistoryItem>> {
        return historyRepository.getRecentHistory(limit)
    }

    fun search(query: String): Flow<List<HistoryItem>> {
        return historyRepository.searchHistory(query)
    }
}

class AddHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(title: String, url: String): Result<HistoryItem> {
        if (url.isBlank()) {
            return Result.failure(Exception("URL cannot be empty"))
        }

        val history = HistoryItem(
            id = java.util.UUID.randomUUID().toString(),
            title = title.ifBlank { url },
            url = url,
            visitedAt = System.currentTimeMillis()
        )

        historyRepository.addHistory(history)
        return Result.success(history)
    }
}

class DeleteHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        historyRepository.deleteHistory(id)
        return Result.success(Unit)
    }

    suspend fun deleteAll(): Result<Unit> {
        historyRepository.deleteAllHistory()
        return Result.success(Unit)
    }
}

class GetTabsUseCase @Inject constructor(
    private val tabRepository: TabRepository
) {
    operator fun invoke(): Flow<List<Tab>> {
        return tabRepository.getAllTabs()
    }

    fun getNormal(): Flow<List<Tab>> {
        return tabRepository.getNormalTabs()
    }

    fun getIncognito(): Flow<List<Tab>> {
        return tabRepository.getIncognitoTabs()
    }
}

class AddTabUseCase @Inject constructor(
    private val tabRepository: TabRepository
) {
    suspend operator fun invoke(
        url: String = "about:blank",
        title: String = "New Tab",
        isIncognito: Boolean = false
    ): Result<Tab> {
        val tab = Tab(
            id = java.util.UUID.randomUUID().toString(),
            url = url,
            title = title,
            isIncognito = isIncognito,
            createdAt = System.currentTimeMillis(),
            lastAccessedAt = System.currentTimeMillis()
        )
        
        tabRepository.addTab(tab)
        return Result.success(tab)
    }
}

class UpdateTabUseCase @Inject constructor(
    private val tabRepository: TabRepository
) {
    suspend operator fun invoke(tab: Tab): Result<Unit> {
        tabRepository.updateTab(tab.copy(lastAccessedAt = System.currentTimeMillis()))
        return Result.success(Unit)
    }
}

class DeleteTabUseCase @Inject constructor(
    private val tabRepository: TabRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        tabRepository.deleteTab(id)
        return Result.success(Unit)
    }
}
