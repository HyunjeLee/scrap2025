package com.scrap2025.scrap2025.data.remote.api

import com.scrap2025.scrap2025.data.remote.dto.BaseResponse
import com.scrap2025.scrap2025.data.remote.dto.LoginResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {
    @Headers("accept: application/json")
    @POST("/oauth/login")
    suspend fun login(
        @Query("sns") sns: String,
        @Header("Authorization") token: String
    ): Response<BaseResponse<LoginResponse>>

    @Headers("accept: application/json")
    @POST("/token")
    suspend fun refreshToken(
        @Query("refresh_token") refreshToken: String
    ): Response<BaseResponse<LoginResponse>>

    @PATCH("/auth/logout")
    suspend fun logout(): Response<BaseResponse<Unit?>>

    @DELETE("/auth/signout")
    suspend fun withdraw(): Response<BaseResponse<Unit?>>
}
