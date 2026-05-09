package com.aibrowser.app.data.repository

import com.aibrowser.app.data.local.database.BookmarkDao
import com.aibrowser.app.data.local.entity.BookmarkEntity
import com.aibrowser.app.data.remote.api.AIBrowserApi
import com.aibrowser.app.data.remote.dto.BookmarkDto
import com.aibrowser.app.domain.model.Bookmark
import com.aibrowser.app.domain.model.User
import com.aibrowser.app.domain.repository.AuthRepository
import com.aibrowser.app.domain.repository.BookmarkRepository
import com.aibrowser.app.domain.repository.HistoryRepository
import com.aibrowser.app.domain.repository.TabRepository
import com.aibrowser.app.util.SecurityHelper
import com.aibrowser.app.data.local.preferences.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AIBrowserApi,
    private val securityHelper: SecurityHelper,
    private val userPreferences: UserPreferences
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = api.login(
                com.aibrowser.app.data.remote.dto.LoginRequestDto(email, password)
            )
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                securityHelper.saveAuthToken(authResponse.token.accessToken)
                securityHelper.saveRefreshToken(authResponse.token.refreshToken)
                userPreferences.saveUser(authResponse.user)
                Result.success(authResponse.user.toDomain())
            } else {
                Result.failure(Exception(response.message() ?: "Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        username: String,
        inviteCode: String?
    ): Result<User> {
        return try {
            val response = api.register(
                com.aibrowser.app.data.remote.dto.RegisterRequestDto(email, password, username, inviteCode)
            )
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                securityHelper.saveAuthToken(authResponse.token.accessToken)
                securityHelper.saveRefreshToken(authResponse.token.refreshToken)
                userPreferences.saveUser(authResponse.user)
                Result.success(authResponse.user.toDomain())
            } else {
                Result.failure(Exception(response.message() ?: "Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun googleAuth(idToken: String): Result<User> {
        return try {
            val response = api.googleAuth(
                com.aibrowser.app.data.remote.dto.GoogleAuthRequestDto(idToken)
            )
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                securityHelper.saveAuthToken(authResponse.token.accessToken)
                securityHelper.saveRefreshToken(authResponse.token.refreshToken)
                userPreferences.saveUser(authResponse.user)
                Result.success(authResponse.user.toDomain())
            } else {
                Result.failure(Exception(response.message() ?: "Google auth failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            api.logout()
            securityHelper.clearAuthToken()
            securityHelper.clearRefreshToken()
            userPreferences.clearUser()
            Result.success(Unit)
        } catch (e: Exception) {
            securityHelper.clearAuthToken()
            securityHelper.clearRefreshToken()
            userPreferences.clearUser()
            Result.success(Unit)
        }
    }

    override suspend fun getCurrentUser(): Result<User> {
        return try {
            val response = api.getCurrentUser()
            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!.toDomain()
                userPreferences.saveUser(response.body()!!)
                Result.success(user)
            } else {
                Result.failure(Exception(response.message() ?: "Get user failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refreshToken(): Result<Unit> {
        return try {
            val refreshToken = securityHelper.getRefreshToken()
                ?: return Result.failure(Exception("No refresh token"))

            val response = api.refreshToken(
                com.aibrowser.app.data.remote.dto.RefreshTokenRequestDto(refreshToken)
            )
            if (response.isSuccessful && response.body() != null) {
                val tokenResponse = response.body()!!
                securityHelper.saveAuthToken(tokenResponse.accessToken)
                securityHelper.saveRefreshToken(tokenResponse.refreshToken)
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message() ?: "Refresh token failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isLoggedIn(): Boolean {
        return securityHelper.getAuthToken() != null
    }

    override fun getAuthToken(): String? {
        return securityHelper.getAuthToken()
    }
}

class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkDao: BookmarkDao
) : BookmarkRepository {

    override fun getAllBookmarks(): Flow<List<Bookmark>> {
        return bookmarkDao.getAllBookmarks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getBookmarksByFolder(folderId: String?): Flow<List<Bookmark>> {
        return if (folderId == null) {
            bookmarkDao.getRootBookmarks()
        } else {
            bookmarkDao.getBookmarksByFolder(folderId)
        }.map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getBookmarkById(id: String): Bookmark? {
        return bookmarkDao.getBookmarkById(id)?.toDomain()
    }

    override suspend fun getBookmarkByUrl(url: String): Bookmark? {
        return bookmarkDao.getBookmarkByUrl(url)?.toDomain()
    }

    override suspend fun addBookmark(bookmark: Bookmark) {
        bookmarkDao.insertBookmark(bookmark.toEntity())
    }

    override suspend fun updateBookmark(bookmark: Bookmark) {
        bookmarkDao.updateBookmark(bookmark.toEntity())
    }

    override suspend fun deleteBookmark(id: String) {
        bookmarkDao.deleteBookmarkById(id)
    }

    override suspend fun deleteAllBookmarks() {
        bookmarkDao.deleteAllBookmarks()
    }

    override suspend fun syncBookmarks(): Result<Unit> {
        return Result.success(Unit)
    }
}

// Extension functions for mapping
fun com.aibrowser.app.data.remote.dto.UserDto.toDomain(): User {
    return User(
        id = id,
        email = email,
        username = username,
        avatarUrl = avatarUrl,
        isVip = isVip,
        vipExpireTime = vipExpireTime,
        vipLevel = vipLevel,
        points = points,
        inviteCode = inviteCode,
        createdAt = createdAt
    )
}

fun BookmarkEntity.toDomain(): Bookmark {
    return Bookmark(
        id = id,
        title = title,
        url = url,
        iconUrl = iconUrl,
        folderId = folderId,
        createdAt = createdAt,
        isSynced = isSynced
    )
}

fun Bookmark.toEntity(): BookmarkEntity {
    return BookmarkEntity(
        id = id,
        title = title,
        url = url,
        iconUrl = iconUrl,
        folderId = folderId,
        createdAt = createdAt,
        isSynced = isSynced
    )
}
