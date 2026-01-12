package com.scrap2025.scrap2025.data.remote.api

import com.scrap2025.scrap2025.data.remote.dto.BaseResponse
import com.scrap2025.scrap2025.data.remote.dto.CreateScrapRequest
import com.scrap2025.scrap2025.data.remote.dto.CreateScrapResponse
import com.scrap2025.scrap2025.data.remote.dto.DeleteSCrapBulkRequest
import com.scrap2025.scrap2025.data.remote.dto.FavoriteListResponse
import com.scrap2025.scrap2025.data.remote.dto.FavoriteListToggleRequest
import com.scrap2025.scrap2025.data.remote.dto.MoveScrapBulkRequest
import com.scrap2025.scrap2025.data.remote.dto.MoveScrapRequest
import com.scrap2025.scrap2025.data.remote.dto.ScrapDetailResponse
import com.scrap2025.scrap2025.data.remote.dto.ScrapListResponse
import com.scrap2025.scrap2025.data.remote.dto.ScrapMemoDto
import com.scrap2025.scrap2025.data.remote.dto.SearchFavoriteResponse
import com.scrap2025.scrap2025.data.remote.dto.SearchListResponse
import com.scrap2025.scrap2025.data.remote.dto.SearchRequest
import com.scrap2025.scrap2025.data.remote.dto.SearchScrapResponse
import kotlinx.serialization.json.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ScrapService {
    @GET("/auth/scraps")
    suspend fun getAllScrapsByCategoryId(
        @Query("category") categoryId: Long,
        @Query("sort") sort: String? = null,
        @Query("direction") direction: String? = null,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): Response<BaseResponse<ScrapListResponse>>

    @GET("/auth/scraps/favorite")
    suspend fun getFavoriteScraps(
        @Query("sort") sort: String? = null,
        @Query("direction") direction: String? = null,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): Response<BaseResponse<FavoriteListResponse>>

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
        @Path("scrap-id") scrapId: Long
    ): Response<BaseResponse<ScrapDetailResponse>>

    @POST("/auth/scraps/{category-id}")
    suspend fun createScrap(
        @Path("category-id") categoryId: Long,
        @Body body: CreateScrapRequest
    ): Response<BaseResponse<CreateScrapResponse>>

    @PATCH("/auth/scraps/{scrap-id}/memo")
    suspend fun updateScrapMemo(
        @Path("scrap-id") scrapId: Long,
        @Body body: ScrapMemoDto
    ): Response<BaseResponse<ScrapMemoDto>>

    @PATCH("/auth/scraps/{scrap-id}/move")
    suspend fun moveScrap(
        @Path("scrap-id") scrapId: Long,
        @Body body: MoveScrapRequest
    ): Response<BaseResponse<JsonElement>>

    @PATCH("/auth/scraps/move")
    suspend fun moveScrapBulk(
        @Body body: MoveScrapBulkRequest
    ): Response<BaseResponse<JsonElement?>>

    @PATCH("/auth/scraps/{scrap-id}/trash")
    suspend fun deleteScrap(
        @Path("scrap-id") scrapId: Long
    ): Response<BaseResponse<JsonElement?>>

    @PATCH("/auth/scraps/trash")
    suspend fun deleteScrapBulk(
        @Body body: DeleteSCrapBulkRequest // scrapIds
    ): Response<BaseResponse<JsonElement?>>

    @POST("/auth/search")
    suspend fun searchScraps(
        @Query("q") query: String,
        @Query("sort") sort: String? = null,
        @Query("direction") direction: String? = null,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
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
        @Path("category-id") categoryId: Long,
        @Query("q") query: String,
        @Query("sort") sort: String? = null,
        @Query("direction") direction: String? = null
    ): Response<BaseResponse<SearchScrapResponse>>
}
