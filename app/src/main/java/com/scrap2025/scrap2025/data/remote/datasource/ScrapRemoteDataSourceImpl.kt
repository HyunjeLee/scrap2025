package com.scrap2025.scrap2025.data.remote.datasource

import com.scrap2025.scrap2025.data.remote.api.ScrapService
import com.scrap2025.scrap2025.data.remote.dto.CreateScrapRequest
import com.scrap2025.scrap2025.data.remote.dto.CreateScrapResponse
import com.scrap2025.scrap2025.data.remote.dto.FavoriteListResponse
import com.scrap2025.scrap2025.data.remote.dto.FavoriteListToggleRequest
import com.scrap2025.scrap2025.data.remote.dto.MoveScrapListRequest
import com.scrap2025.scrap2025.data.remote.dto.MoveScrapRequest
import com.scrap2025.scrap2025.data.remote.dto.ScrapDetailResponse
import com.scrap2025.scrap2025.data.remote.dto.ScrapListResponse
import com.scrap2025.scrap2025.data.remote.dto.ScrapMemoDto
import com.scrap2025.scrap2025.data.remote.dto.SearchFavoriteResponse
import com.scrap2025.scrap2025.data.remote.dto.SearchListResponse
import com.scrap2025.scrap2025.data.remote.dto.SearchRequest
import com.scrap2025.scrap2025.data.remote.dto.SearchScrapResponse
import kotlinx.serialization.json.JsonElement
import javax.inject.Inject

class ScrapRemoteDataSourceImpl @Inject constructor(private val scrapService: ScrapService) :
    ScrapRemoteDataSource {

    override suspend fun getAllScrapsByCategoryId(categoryRemoteId: Int): ScrapListResponse {
        val response = scrapService.getAllScrapsByCategoryId(categoryRemoteId)
        if (response.isSuccessful) {
            return response.body()?.result ?: throw Exception("Response body is null")
        } else {
            throw Exception("Failed to get scraps code: ${response.code()}")
        }
    }

    override suspend fun getFavoriteScraps(): FavoriteListResponse {
        val response = scrapService.getFavoriteScraps()
        if (response.isSuccessful) {
            return response.body()?.result ?: throw Exception("Response body is null")
        } else {
            throw Exception("Failed to get favorite scraps code: ${response.code()}")
        }
    }

    override suspend fun updateScrapFavorite(scrapId: Long): JsonElement {
        val response = scrapService.updateScrapFavorite(scrapId)
        if (response.isSuccessful) {
            return response.body()?.result ?: throw Exception("Response body is null")
        } else {
            throw Exception("Toggle favorite failed code: ${response.code()}")
        }
    }

    override suspend fun updateScrapBulkFavorite(scrapIdList: List<Long>): JsonElement {
        val request = FavoriteListToggleRequest(scrapIdList = scrapIdList)
        val response = scrapService.updateScrapBulkFavorite(request)
        if (response.isSuccessful) {
            return response.body()?.result ?: throw Exception("Response body is null")
        } else {
            throw Exception("Bulk toggle favorite failed code: ${response.code()}")
        }
    }

    override suspend fun getScrapById(scrapRemoteId: Int): ScrapDetailResponse {
        val response = scrapService.getScrapById(scrapRemoteId)
        if (response.isSuccessful) {
            return response.body()?.result ?: throw Exception("Response body is null")
        } else {
            throw Exception("Failed to get scrap details code: ${response.code()}")
        }
    }

    override suspend fun createScrap(
        categoryRemoteId: Int, request: CreateScrapRequest
    ): CreateScrapResponse {
        val response = scrapService.createScrap(categoryRemoteId, request)
        if (response.isSuccessful) {
            return response.body()?.result ?: throw Exception("Response body is null")
        } else {
            throw Exception("Create scrap failed code: ${response.code()}")
        }
    }

    override suspend fun updateScrapMemo(scrapId: Int, memo: String): ScrapMemoDto {
        val request = ScrapMemoDto(memo = memo)
        val response = scrapService.updateScrapMemo(scrapId, request)
        if (response.isSuccessful) {
            return response.body()?.result ?: throw Exception("Response body is null")
        } else {
            throw Exception("Update memo failed code: ${response.code()}")
        }
    }

    override suspend fun moveScrap(scrapId: Long, categoryRemoteId: Long): JsonElement {
        val request = MoveScrapRequest(moveCategoryId = categoryRemoteId)
        val response = scrapService.moveScrap(scrapId, request)
        if (response.isSuccessful) {
            return response.body()?.result ?: throw Exception("Response body is null")
        } else {
            throw Exception("Move scrap failed code: ${response.code()}")
        }
    }

    override suspend fun moveScrapList(scrapIds: List<Long>, moveCategoryId: Long): JsonElement? {
        val request = MoveScrapListRequest(scrapIds = scrapIds, moveCategoryId = moveCategoryId)
        val response = scrapService.moveScrapList(request)
        if (response.isSuccessful) {
            return response.body()?.result
        } else {
            throw Exception("Move scraps failed code: ${response.code()}")
        }
    }

    override suspend fun deleteScrap(scrapId: Long): JsonElement? {
        val response = scrapService.deleteScrap(scrapId)
        if (response.isSuccessful) {
            return response.body()?.result
        } else {
            throw Exception("Delete scrap failed code: ${response.code()}")
        }
    }

    override suspend fun deleteScrapBulk(scrapIds: List<Long>): JsonElement? {
        val response = scrapService.deleteScrapBulk(scrapIds)
        if (response.isSuccessful) {
            return response.body()?.result
        } else {
            throw Exception("Bulk delete failed code: ${response.code()}")
        }
    }

    override suspend fun searchScraps(
        query: String,
        sort: String?,
        direction: String?,
        page: Int?,
        size: Int?,
        request: SearchRequest
    ): SearchListResponse {
        val response = scrapService.searchScraps(query, sort, direction, page, size, request)
        if (response.isSuccessful) {
            return response.body()?.result ?: throw Exception("Response body is null")
        } else {
            throw Exception("Search failed code: ${response.code()}")
        }
    }

    override suspend fun favoriteSearch(
        query: String, sort: String?, direction: String?
    ): SearchFavoriteResponse {
        val response = scrapService.favoriteSearch(query, sort, direction)
        if (response.isSuccessful) {
            return response.body()?.result ?: throw Exception("Response body is null")
        } else {
            throw Exception("Favorite search failed code: ${response.code()}")
        }
    }

    override suspend fun searchScrapsByCategory(
        categoryRemoteId: Long, query: String, sort: String?, direction: String?
    ): SearchScrapResponse {
        val response = scrapService.searchScrapsByCategory(categoryRemoteId, query, sort, direction)
        if (response.isSuccessful) {
            return response.body()?.result ?: throw Exception("Response body is null")
        } else {
            throw Exception("Category search failed code: ${response.code()}")
        }
    }
}
