package com.aibrowser.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.aibrowser.app.data.local.database.AIBrowserDatabase
import com.aibrowser.app.data.local.preferences.UserPreferences
import com.aibrowser.app.data.remote.api.AIBrowserApi
import com.aibrowser.app.data.remote.api.AIApi
import com.aibrowser.app.data.remote.api.VPNApi
import com.aibrowser.app.data.repository.*
import com.aibrowser.app.domain.repository.*
import com.aibrowser.app.domain.usecase.*
import com.aibrowser.app.util.SecurityHelper
import com.aibrowser.app.util.CryptoHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ai_browser_preferences")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        securityHelper: SecurityHelper
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val original = chain.request()
                val token = securityHelper.getAuthToken()
                val request = if (token != null) {
                    original.newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                } else {
                    original
                }
                chain.proceed(request)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.aibrowser.app/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(json.asConverterFactory(kotlinx.serialization.json.JSON))
            .build()
    }

    @Provides
    @Singleton
    fun provideAIBrowserApi(retrofit: Retrofit): AIBrowserApi {
        return retrofit.create(AIBrowserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAIApi(retrofit: Retrofit): AIApi {
        return retrofit.create(AIApi::class.java)
    }

    @Provides
    @Singleton
    fun provideVPNApi(retrofit: Retrofit): VPNApi {
        return retrofit.create(VPNApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AIBrowserDatabase {
        return Room.databaseBuilder(
            context,
            AIBrowserDatabase::class.java,
            "ai_browser_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideSecurityHelper(@ApplicationContext context: Context): SecurityHelper {
        return SecurityHelper(context)
    }

    @Provides
    @Singleton
    fun provideCryptoHelper(): CryptoHelper {
        return CryptoHelper()
    }

    @Provides
    @Singleton
    fun provideUserPreferences(dataStore: DataStore<Preferences>): UserPreferences {
        return UserPreferences(dataStore)
    }

    // Repositories
    @Provides
    @Singleton
    fun provideAuthRepository(
        api: AIBrowserApi,
        securityHelper: SecurityHelper,
        userPreferences: UserPreferences
    ): AuthRepository {
        return AuthRepositoryImpl(api, securityHelper, userPreferences)
    }

    @Provides
    @Singleton
    fun provideBookmarkRepository(database: AIBrowserDatabase): BookmarkRepository {
        return BookmarkRepositoryImpl(database.bookmarkDao())
    }

    @Provides
    @Singleton
    fun provideHistoryRepository(database: AIBrowserDatabase): HistoryRepository {
        return HistoryRepositoryImpl(database.historyDao())
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(userPreferences: UserPreferences): SettingsRepository {
        return SettingsRepositoryImpl(userPreferences)
    }

    @Provides
    @Singleton
    fun provideVPNRepository(
        api: VPNApi,
        userPreferences: UserPreferences,
        @ApplicationContext context: Context
    ): VPNRepository {
        return VPNRepositoryImpl(api, userPreferences, context)
    }

    @Provides
    @Singleton
    fun provideAIRepository(
        api: AIApi,
        userPreferences: UserPreferences
    ): AIRepository {
        return AIRepositoryImpl(api, userPreferences)
    }

    @Provides
    @Singleton
    fun provideTabRepository(database: AIBrowserDatabase): TabRepository {
        return TabRepositoryImpl(database.tabDao())
    }

    @Provides
    @Singleton
    fun provideDownloadRepository(database: AIBrowserDatabase): DownloadRepository {
        return DownloadRepositoryImpl(database.downloadDao())
    }

    // Use Cases
    @Provides
    @Singleton
    fun provideLoginUseCase(authRepository: AuthRepository): LoginUseCase {
        return LoginUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideRegisterUseCase(authRepository: AuthRepository): RegisterUseCase {
        return RegisterUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideSyncDataUseCase(
        authRepository: AuthRepository,
        bookmarkRepository: BookmarkRepository,
        historyRepository: HistoryRepository,
        tabRepository: TabRepository
    ): SyncDataUseCase {
        return SyncDataUseCase(authRepository, bookmarkRepository, historyRepository, tabRepository)
    }
}
