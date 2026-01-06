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
import com.scrap2025.scrap2025.model.Result
import kotlinx.serialization.json.JsonElement

interface ScrapRemoteDataSource {
    suspend fun getAllScrapsByCategoryId(categoryRemoteId: Int): Result<ScrapListResponse>
    suspend fun getFavoriteScraps(): Result<FavoriteListResponse>
    suspend fun updateScrapFavorite(scrapId: Long): Result<JsonElement>
    suspend fun updateScrapBulkFavorite(scrapIdList: List<Long>): Result<JsonElement>
    suspend fun getScrapById(scrapRemoteId: Int): Result<ScrapDetailResponse>
    suspend fun createScrap(
        categoryRemoteId: Int, request: CreateScrapRequest
    ): Result<CreateScrapResponse>

    suspend fun updateScrapMemo(scrapId: Int, memo: String): Result<ScrapMemoDto>
    suspend fun moveScrap(scrapId: Long, categoryRemoteId: Long): Result<JsonElement>
    suspend fun moveScrapList(scrapIds: List<Long>, moveCategoryId: Long): Result<JsonElement?>
    suspend fun deleteScrap(scrapId: Long): Result<JsonElement?>
    suspend fun deleteScrapBulk(scrapIds: List<Long>): Result<JsonElement?>
    suspend fun searchScraps(
        query: String,
        sort: String?,
        direction: String?,
        page: Int?,
        size: Int?,
        request: SearchRequest
    ): Result<SearchListResponse>

    suspend fun favoriteSearch(
        query: String, sort: String?, direction: String?
    ): Result<SearchFavoriteResponse>

    suspend fun searchScrapsByCategory(
        categoryRemoteId: Long, query: String, sort: String?, direction: String?
    ): Result<SearchScrapResponse>
}
