package com.aibrowser.app.data.remote.api

import com.aibrowser.app.data.remote.dto.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface AIBrowserApi {
    // Auth
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<AuthResponseDto>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): Response<AuthResponseDto>

    @POST("auth/google")
    suspend fun googleAuth(@Body request: GoogleAuthRequestDto): Response<AuthResponseDto>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequestDto): Response<AuthResponseDto>

    @POST("auth/logout")
    suspend fun logout(): Response<Unit>

    @GET("auth/me")
    suspend fun getCurrentUser(): Response<UserDto>

    // User
    @GET("user/profile")
    suspend fun getUserProfile(): Response<UserDto>

    @PUT("user/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequestDto): Response<UserDto>

    @PUT("user/password")
    suspend fun changePassword(@Body request: ChangePasswordRequestDto): Response<Unit>

    // VIP
    @GET("vip/status")
    suspend fun getVipStatus(): Response<VipStatusDto>

    @POST("vip/activate")
    suspend fun activateVip(@Body request: ActivateVipRequestDto): Response<VipStatusDto>

    // Sync
    @GET("sync/bookmarks")
    suspend fun getSyncBookmarks(@Query("since") since: Long?): Response<List<BookmarkDto>>

    @POST("sync/bookmarks")
    suspend fun syncBookmarks(@Body bookmarks: List<BookmarkDto>): Response<Unit>

    @GET("sync/history")
    suspend fun getSyncHistory(@Query("since") since: Long?): Response<List<HistoryDto>>

    @POST("sync/history")
    suspend fun syncHistory(@Body history: List<HistoryDto>): Response<Unit>

    @GET("sync/tabs")
    suspend fun getSyncTabs(): Response<List<TabDto>>

    @POST("sync/tabs")
    suspend fun syncTabs(@Body tabs: List<TabDto>): Response<Unit>

    // Invitation
    @GET("invite/code")
    suspend fun getInviteCode(): Response<InviteCodeDto>

    @POST("invite/use")
    suspend fun useInviteCode(@Body request: UseInviteCodeRequestDto): Response<Unit>

    @GET("invite/rewards")
    suspend fun getInviteRewards(): Response<List<InviteRewardDto>>

    // Check-in
    @POST("checkin/daily")
    suspend fun dailyCheckIn(): Response<CheckInResponseDto>

    @GET("checkin/status")
    suspend fun getCheckInStatus(): Response<CheckInStatusDto>

    // Settings
    @GET("settings")
    suspend fun getSettings(): Response<SettingsDto>

    @PUT("settings")
    suspend fun updateSettings(@Body settings: SettingsDto): Response<SettingsDto>
}

interface AIApi {
    @POST("ai/chat")
    suspend fun chat(@Body request: AIChatRequestDto): Response<AIChatResponseDto>

    @POST("ai/chat/stream")
    suspend fun chatStream(@Body request: AIChatRequestDto): Response<ResponseBody>

    @POST("ai/summarize")
    suspend fun summarize(@Body request: AISummarizeRequestDto): Response<AISummarizeResponseDto>

    @POST("ai/translate")
    suspend fun translate(@Body request: AITranslateRequestDto): Response<AITranslateResponseDto>

    @POST("ai/ocr")
    suspend fun ocr(@Body request: AIOcrRequestDto): Response<AIOcrResponseDto>

    @GET("ai/models")
    suspend fun getModels(@Query("provider") provider: String): Response<List<AIModelDto>>
}

interface VPNApi {
    @GET("vpn/servers")
    suspend fun getServers(): Response<List<VpnServerDto>>

    @GET("vpn/servers/{id}")
    suspend fun getServerById(@Path("id") id: String): Response<VpnServerDto>

    @POST("vpn/servers/{id}/ping")
    suspend fun pingServer(@Path("id") id: String): Response<PingResponseDto>

    @GET("vpn/subscriptions")
    suspend fun getSubscriptions(): Response<List<VpnSubscriptionDto>>

    @POST("vpn/subscriptions")
    suspend fun addSubscription(@Body request: AddSubscriptionRequestDto): Response<VpnSubscriptionDto>

    @DELETE("vpn/subscriptions/{id}")
    suspend fun removeSubscription(@Path("id") id: String): Response<Unit>

    @POST("vpn/subscriptions/{id}/refresh")
    suspend fun refreshSubscription(@Path("id") id: String): Response<Unit>

    @GET("vpn/config/{id}")
    suspend fun getServerConfig(@Path("id") id: String): Response<VpnConfigDto>

    @POST("vpn/speedtest")
    suspend fun speedTest(@Body request: SpeedTestRequestDto): Response<SpeedTestResponseDto>
}
