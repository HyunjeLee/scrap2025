package com.scrap2025.scrap2025.data.remote

import com.scrap2025.scrap2025.data.remote.dto.BaseResponse
import com.scrap2025.scrap2025.data.remote.dto.CategoryListResponse
import com.scrap2025.scrap2025.data.remote.dto.CreateCategoryRequest
import com.scrap2025.scrap2025.data.remote.dto.CreateCategoryResponse
import com.scrap2025.scrap2025.data.remote.dto.CreateScrapRequest
import com.scrap2025.scrap2025.data.remote.dto.CreateScrapResponse
import com.scrap2025.scrap2025.data.remote.dto.FavoriteListToggleRequest
import com.scrap2025.scrap2025.data.remote.dto.FavoriteListResponse
import com.scrap2025.scrap2025.data.remote.dto.LoginResponse
import com.scrap2025.scrap2025.data.remote.dto.MoveScrapListRequest
import com.scrap2025.scrap2025.data.remote.dto.MoveScrapRequest
import com.scrap2025.scrap2025.data.remote.dto.MyPageResponse
import com.scrap2025.scrap2025.data.remote.dto.RenameCategoryRequest
import com.scrap2025.scrap2025.data.remote.dto.RenameCategoryResponse
import com.scrap2025.scrap2025.data.remote.dto.ScrapListResponse
import com.scrap2025.scrap2025.data.remote.dto.ScrapDetailResponse
import com.scrap2025.scrap2025.data.remote.dto.ScrapMemoDto
import com.scrap2025.scrap2025.data.remote.dto.SearchFavoriteResponse
import com.scrap2025.scrap2025.data.remote.dto.SearchListResponse
import com.scrap2025.scrap2025.data.remote.dto.SearchRequest
import com.scrap2025.scrap2025.data.remote.dto.SearchScrapResponse
import com.scrap2025.scrap2025.data.remote.dto.SequenceCategoryRequest
import com.scrap2025.scrap2025.data.remote.dto.SequenceCategoryResponse
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
    ): Response<BaseResponse<LoginResponse>>

    @Headers("accept: application/json")
    @POST("/token")
    suspend fun refreshToken(
        @Query("refresh_token") refreshToken: String
    ): Response<BaseResponse<LoginResponse>>

    @GET("/auth/mypage")
    suspend fun getMyPage(): Response<BaseResponse<MyPageResponse>>

    @PATCH("/auth/logout")
    suspend fun logout(): Response<BaseResponse<Unit?>>

    @PATCH("/auth/signout")
    suspend fun withdraw(): Response<BaseResponse<Unit?>>

    @GET("/auth/categories")
    suspend fun getCategories(): Response<BaseResponse<CategoryListResponse>>

    @POST("/auth/categories")
    suspend fun createCategory(
        @Body body: CreateCategoryRequest
    ): Response<BaseResponse<CreateCategoryResponse>>

    @PATCH("/auth/categories/{categoryId}/title")
    suspend fun renameCategory(
        @Path("categoryId") categoryId: Int,
        @Body body: RenameCategoryRequest
    ): Response<BaseResponse<RenameCategoryResponse>>

    @DELETE("/auth/categories/{category-id}")
    suspend fun deleteCategory(
        @Path("category-id") categoryId: Int
    ): Response<BaseResponse<Unit?>>

    @PATCH("/auth/categories/sequence")
    suspend fun updateCategorySequence(
        @Body body: SequenceCategoryRequest
    ): Response<BaseResponse<SequenceCategoryResponse>>

    @GET("/auth/scraps")
    suspend fun getAllScrapsByCategoryId(
        @Query("category") categoryRemoteId: Int
    ): Response<BaseResponse<ScrapListResponse>>

    @GET("/auth/scraps/favorite")
    suspend fun getFavoriteScraps(): Response<BaseResponse<FavoriteListResponse>>

    @PATCH("/auth/scraps/{scrap-id}/favorite")
    suspend fun updateScrapFavorite(
        @Path("scrap-id") scrapId: Long
    ): Response<BaseResponse<JsonElement>>

    @PATCH("/auth/scraps/favorite")
    suspend fun updateScrapBulkFavorite(
        @Body body: FavoriteListToggleRequest
    ): Response<BaseResponse<JsonElement>>

    @GET("/auth/scraps/{scrap-id}")
    suspend fun getScrapById(
        @Path("scrap-id") scrapRemoteId: Int
    ): Response<BaseResponse<ScrapDetailResponse>>

    @POST("/auth/scraps/{category-id}")
    suspend fun createScrap(
        @Path("category-id") categoryId: Int,
        @Body body: CreateScrapRequest
    ): Response<BaseResponse<CreateScrapResponse>>

    @PATCH("/auth/scraps/{scrap-id}/memo")
    suspend fun updateScrapMemo(
        @Path("scrap-id") scrapId: Int,
        @Body body: ScrapMemoDto
    ): Response<BaseResponse<ScrapMemoDto>>

    @PATCH("/auth/scraps/{scrap-id}/move")
    suspend fun moveScrap(
        @Path("scrap-id") scrapId: Long,
        @Body body: MoveScrapRequest
    ): Response<BaseResponse<JsonElement>>

    @PATCH("/auth/scraps/move")
    suspend fun moveScrapList(
        @Body body: MoveScrapListRequest
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
    ): Response<BaseResponse<SearchListResponse>>

    @GET("/auth/scraps/search/favorite")
    suspend fun favoriteSearch(
        @Query("q") query: String,
        @Query("sort") sort: String? = null,
        @Query("direction") direction: String? = null,
    ): Response<BaseResponse<SearchFavoriteResponse>>

    @GET("/auth/scraps/search/{category-id}")
    suspend fun searchScrapsByCategory(
        @Path("category-id") categoryRemoteId: Long,
        @Query("q") query: String,
        @Query("sort") sort: String? = null,
        @Query("direction") direction: String? = null
    ): Response<BaseResponse<SearchScrapResponse>>
}
