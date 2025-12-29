package com.scrap2025.scrap2025.data.remote

import com.scrap2025.scrap2025.data.model.BaseResponse
import com.scrap2025.scrap2025.data.model.CategoryCreateRequest
import com.scrap2025.scrap2025.data.model.CategoryCreateResult
import com.scrap2025.scrap2025.data.model.CategoryListResponse
import com.scrap2025.scrap2025.data.model.CategoryRenameRequest
import com.scrap2025.scrap2025.data.model.CategoryRenameResult
import com.scrap2025.scrap2025.data.model.CategorySequenceRequest
import com.scrap2025.scrap2025.data.model.CategorySequenceResult
import com.scrap2025.scrap2025.data.model.LoginResult
import com.scrap2025.scrap2025.data.model.MyPageResult
import com.scrap2025.scrap2025.data.model.ScrapCreateRequest
import com.scrap2025.scrap2025.data.model.ScrapCreateResult
import com.scrap2025.scrap2025.data.model.ScrapListResponse
import com.scrap2025.scrap2025.data.model.ScrapMemoDto
import com.scrap2025.scrap2025.data.model.ScrapMoveDto
import com.scrap2025.scrap2025.data.model.ScrapResponse
import com.scrap2025.scrap2025.data.remote.dto.ScrapBulkRequest
import kotlinx.serialization.json.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
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

    @Headers("accept: application/json")
    @PATCH("/auth/categories/{categoryId}/title")
    suspend fun renameCategory(
        @Header("Authorization") token: String,
        @Path("categoryId") categoryId: Int,
        @Body body: CategoryRenameRequest
    ): Response<BaseResponse<CategoryRenameResult>>

    @Headers("accept: application/json")
    @DELETE("/auth/categories/{category-id}")
    suspend fun deleteCategory(
        @Header("Authorization") token: String,
        @Path("category-id") categoryId: Int
    ): Response<BaseResponse<Unit?>>

    @Headers("accept: application/json")
    @PATCH("/auth/categories/sequence")
    suspend fun updateCategorySequence(
        @Header("Authorization") token: String,
        @Body body: CategorySequenceRequest
    ): Response<BaseResponse<CategorySequenceResult>>

    @Headers("accept: application/json")
    @GET("/auth/scraps")
    suspend fun getAllScrapsByCategoryId(
        @Header("Authorization") token: String,
        @Query("category") categoryId: Int
    ): Response<BaseResponse<ScrapListResponse>>

    @GET("/auth/scraps/{scrap-id}")
    suspend fun getScrapById(
        @Header("Authorization") token: String,
        @Path("scrap-id") scrapId: Int
    ): Response<BaseResponse<ScrapResponse>>

    @Headers("accept: application/json")
    @POST("/auth/scraps/{category-id}")
    suspend fun createScrap(
        @Header("Authorization") token: String,
        @Path("category-id") categoryId: Int,
        @Body body: ScrapCreateRequest
    ): Response<BaseResponse<ScrapCreateResult>>

    @Headers("accept: application/json")
    @PATCH("/auth/scraps/{scrap-id}/memo")
    suspend fun updateScrapMemo(
        @Header("Authorization") token: String,
        @Path("scrap-id") scrapId: Int,
        @Body body: ScrapMemoDto
    ): Response<BaseResponse<ScrapMemoDto>>

    @Headers("accept: application/json")
    @PATCH("/auth/scraps/{scrap-id}/move")
    suspend fun moveScrap(
        @Header("Authorization") token: String,
        @Path("scrap-id") scrapId: Long,
        @Body body: ScrapMoveDto
    ): Response<BaseResponse<JsonElement>>

    @Headers("accept: application/json")
    @PATCH("/auth/scraps/move")
    suspend fun moveScrapBulk(
        @Header("Authorization") token: String,
        @Body body: ScrapBulkRequest
    ): Response<BaseResponse<JsonElement?>>

    @Headers("accept: application/json")
    @PATCH("/auth/scraps/{scrap-id}/trash")
    suspend fun deleteScrap(
        @Header("Authorization") token: String,
        @Path("scrap-id") scrapId: Long
    ): Response<BaseResponse<JsonElement?>>

    @Headers("accept: application/json")
    @PATCH("/auth/scraps/trash")
    suspend fun deleteScrapBulk(
        @Header("Authorization") token: String,
        @Body body: List<Long> // scrapIds
    ): Response<BaseResponse<JsonElement?>>
}
