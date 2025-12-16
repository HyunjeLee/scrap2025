package com.scrap2025.scrap2025.data.remote

import com.scrap2025.scrap2025.data.model.BaseResponse
import com.scrap2025.scrap2025.data.model.LoginResult
import retrofit2.Response
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
}
