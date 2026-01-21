package com.scrap2025.scrap2025.data.remote.datasource

import com.scrap2025.scrap2025.data.remote.dto.CreateScrapRequest
import com.scrap2025.scrap2025.data.remote.dto.CreateScrapResponse
import com.scrap2025.scrap2025.data.remote.dto.FavoriteListResponse
import com.scrap2025.scrap2025.data.remote.dto.ScrapDetailResponse
import com.scrap2025.scrap2025.data.remote.dto.ScrapListResponse
import com.scrap2025.scrap2025.data.remote.dto.ScrapMemoDto
import com.scrap2025.scrap2025.data.remote.dto.SearchFavoriteResponse
import com.scrap2025.scrap2025.data.remote.dto.SearchListResponse
import com.scrap2025.scrap2025.data.remote.dto.SearchRequest
import com.scrap2025.scrap2025.data.remote.dto.SearchScrapResponse
import kotlinx.serialization.json.JsonElement

interface ScrapRemoteDataSource {
    suspend fun getAllScrapsByCategoryId(
        categoryId: Long,
        sort: String? = null,
        direction: String? = null,
        page: Int? = null,
        size: Int? = null
    ): ScrapListResponse

    suspend fun getFavoriteScraps(
        sort: String? = null,
        direction: String? = null,
        page: Int? = null,
        size: Int? = null
    ): FavoriteListResponse

    suspend fun updateScrapFavorite(scrapId: Long): JsonElement

    suspend fun updateScrapListFavorite(scrapIds: List<Long>): JsonElement

    suspend fun getScrapById(scrapId: Long): ScrapDetailResponse

    suspend fun createScrap(categoryId: Long, request: CreateScrapRequest): CreateScrapResponse

    suspend fun updateScrapMemo(scrapId: Long, memo: String): ScrapMemoDto

    suspend fun moveScrap(scrapId: Long, categoryId: Long): JsonElement

    suspend fun moveScrapBulk(scrapIds: List<Long>, categoryId: Long): JsonElement?

    suspend fun deleteScrap(scrapId: Long): JsonElement?

    suspend fun deleteScrapBulk(scrapIds: List<Long>): JsonElement?

    suspend fun searchScraps(
        query: String,
        sort: String?,
        direction: String?,
        page: Int?,
        size: Int?,
        request: SearchRequest
    ): SearchListResponse

    suspend fun favoriteSearch(
        query: String,
        sort: String?,
        direction: String?
    ): SearchFavoriteResponse

    suspend fun searchScrapsByCategory(
        categoryId: Long,
        query: String,
        sort: String?,
        direction: String?
    ): SearchScrapResponse
}
