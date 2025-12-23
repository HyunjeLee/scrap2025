package com.scrap2025.scrap2025.data.remote

import com.scrap2025.scrap2025.data.model.BaseResponse
import com.scrap2025.scrap2025.data.model.CategoryCreateRequest
import com.scrap2025.scrap2025.data.model.CategoryCreateResult
import com.scrap2025.scrap2025.data.model.CategoryListResponse
import com.scrap2025.scrap2025.data.model.LoginResult
import com.scrap2025.scrap2025.data.model.MyPageResult
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
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
    ): Response<BaseResponse<LoginResult>>

    @Headers("accept: application/json")
    @POST("/token")
    suspend fun refreshToken(
        @Query("refresh_token") refreshToken: String
    ): Response<BaseResponse<LoginResult>>

    @Headers("accept: application/json")
    @GET("/auth/mypage")
    suspend fun getMyPage(
        @Header("Authorization") token: String
    ): Response<BaseResponse<MyPageResult>>

    @Headers("accept: application/json")
    @PATCH("/auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<BaseResponse<Unit?>>

    @Headers("accept: application/json")
    @PATCH("/auth/signout")
    suspend fun withdraw(@Header("Authorization") token: String): Response<BaseResponse<Unit?>>

    @Headers("accept: application/json")
    @GET("/auth/categories")
    suspend fun getCategories(
        @Header("Authorization") token: String
    ): Response<BaseResponse<CategoryListResponse>>

    @Headers("accept: application/json")
    @POST("/auth/categories")
    suspend fun createCategory(
        @Header("Authorization") token: String,
        @Body body: CategoryCreateRequest
    ): Response<BaseResponse<CategoryCreateResult>>
}
