package com.scrap2025.scrap2025.data.remote

import com.scrap2025.scrap2025.data.model.BaseResponse
import com.scrap2025.scrap2025.data.model.CategoryCreateRequest
import com.scrap2025.scrap2025.data.model.CategoryCreateResult
import com.scrap2025.scrap2025.data.model.CategoryListResponse
import com.scrap2025.scrap2025.data.model.CategoryRenameRequest
import com.scrap2025.scrap2025.data.model.CategoryRenameResult
import com.scrap2025.scrap2025.data.model.CategorySequenceRequest
import com.scrap2025.scrap2025.data.model.CategorySequenceResult
import com.scrap2025.scrap2025.data.model.FavoriteSearchResult
import com.scrap2025.scrap2025.data.model.LoginResult
import com.scrap2025.scrap2025.data.model.MyPageResult
import com.scrap2025.scrap2025.data.model.ScrapCreateRequest
import com.scrap2025.scrap2025.data.model.ScrapCreateResult
import com.scrap2025.scrap2025.data.model.ScrapListResponse
import com.scrap2025.scrap2025.data.model.ScrapMemoDto
import com.scrap2025.scrap2025.data.model.ScrapMoveDto
import com.scrap2025.scrap2025.data.model.ScrapResponse
import com.scrap2025.scrap2025.data.model.SearchRequest
import com.scrap2025.scrap2025.data.model.SearchResult
import com.scrap2025.scrap2025.data.remote.dto.FavoriteBulkDTO
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

    @GET("/auth/mypage")
    suspend fun getMyPage(): Response<BaseResponse<MyPageResult>>

    @PATCH("/auth/logout")
    suspend fun logout(): Response<BaseResponse<Unit?>>

    @PATCH("/auth/signout")
    suspend fun withdraw(): Response<BaseResponse<Unit?>>

    @GET("/auth/categories")
    suspend fun getCategories(): Response<BaseResponse<CategoryListResponse>>

    @POST("/auth/categories")
    suspend fun createCategory(
        @Body body: CategoryCreateRequest
    ): Response<BaseResponse<CategoryCreateResult>>

    @PATCH("/auth/categories/{categoryId}/title")
    suspend fun renameCategory(
        @Path("categoryId") categoryId: Int,
        @Body body: CategoryRenameRequest
    ): Response<BaseResponse<CategoryRenameResult>>

    @DELETE("/auth/categories/{category-id}")
    suspend fun deleteCategory(
        @Path("category-id") categoryId: Int
    ): Response<BaseResponse<Unit?>>

    @PATCH("/auth/categories/sequence")
    suspend fun updateCategorySequence(
        @Body body: CategorySequenceRequest
    ): Response<BaseResponse<CategorySequenceResult>>

    @GET("/auth/scraps")
    suspend fun getAllScrapsByCategoryId(
        @Query("category") categoryId: Int
    ): Response<BaseResponse<ScrapListResponse>>

    @GET("/auth/scraps/favorite")
    suspend fun getFavoriteScraps(): Response<BaseResponse<ScrapListResponse>>

    @PATCH("/auth/scraps/{scrap-id}/favorite")
    suspend fun updateScrapFavorite(
        @Path("scrap-id") scrapId: Long
    ): Response<BaseResponse<JsonElement>>

    @PATCH("/auth/scraps/favorite")
    suspend fun updateScrapBulkFavorite(
        @Body body: FavoriteBulkDTO
    ): Response<BaseResponse<JsonElement>>

    @GET("/auth/scraps/{scrap-id}")
    suspend fun getScrapById(
        @Path("scrap-id") scrapId: Int
    ): Response<BaseResponse<ScrapResponse>>

    @POST("/auth/scraps/{category-id}")
    suspend fun createScrap(
        @Path("category-id") categoryId: Int,
        @Body body: ScrapCreateRequest
    ): Response<BaseResponse<ScrapCreateResult>>

    @PATCH("/auth/scraps/{scrap-id}/memo")
    suspend fun updateScrapMemo(
        @Path("scrap-id") scrapId: Int,
        @Body body: ScrapMemoDto
    ): Response<BaseResponse<ScrapMemoDto>>

    @PATCH("/auth/scraps/{scrap-id}/move")
    suspend fun moveScrap(
        @Path("scrap-id") scrapId: Long,
        @Body body: ScrapMoveDto
    ): Response<BaseResponse<JsonElement>>

    @PATCH("/auth/scraps/move")
    suspend fun moveScrapBulk(
        @Body body: ScrapBulkRequest
    ): Response<BaseResponse<JsonElement?>>

    @PATCH("/auth/scraps/{scrap-id}/trash")
    suspend fun deleteScrap(
        @Path("scrap-id") scrapId: Long
    ): Response<BaseResponse<JsonElement?>>

    @PATCH("/auth/scraps/trash")
    suspend fun deleteScrapBulk(
        @Body body: List<Long> // scrapIds
    ): Response<BaseResponse<JsonElement?>>

    @POST("/auth/search")
    suspend fun searchScraps(
        @Query("q") query: String,
        @Query("sort") sort: String?,
        @Query("direction") direction: String?,
        @Query("page") page: Int?,
        @Query("size") size: Int?,
        @Body body: SearchRequest
    ): Response<BaseResponse<SearchResult>>

    @GET("/auth/scraps/search/favorite")
    suspend fun favoriteSearch(
        @Query("q") query: String,
        @Query("sort") sort: String? = null,
        @Query("direction") direction: String? = null,
    ): Response<BaseResponse<FavoriteSearchResult>>
}
