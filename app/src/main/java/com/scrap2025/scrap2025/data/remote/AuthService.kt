package com.scrap2025.scrap2025.data.remote

import com.scrap2025.scrap2025.data.model.BaseResponse
import com.scrap2025.scrap2025.data.model.LoginResult
import com.scrap2025.scrap2025.data.model.MyPageResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {
    @retrofit2.http.Headers("accept: application/json")
    @POST("/oauth/login")
    suspend fun login(
        @Query("sns") sns: String,
        @Header("Authorization") token: String
    ): Response<BaseResponse<LoginResult>>

    @retrofit2.http.Headers("accept: application/json")
    @POST("/token")
    suspend fun refreshToken(
        @Query("refresh_token") refreshToken: String
    ): Response<BaseResponse<LoginResult>>

    @retrofit2.http.Headers("accept: application/json")
    @GET("/auth/mypage")
    suspend fun getMyPage(
        @Header("Authorization") token: String
    ): Response<BaseResponse<MyPageResult>>

    @retrofit2.http.Headers("accept: application/json")
    @POST("/auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<BaseResponse<Unit?>>

    @retrofit2.http.Headers("accept: application/json")
    @POST("/auth/signout")
    suspend fun withdraw(@Header("Authorization") token: String): Response<BaseResponse<Unit?>>
}
