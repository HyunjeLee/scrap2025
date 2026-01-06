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
import com.scrap2025.scrap2025.model.Result
import kotlinx.serialization.json.JsonElement
import javax.inject.Inject

class ScrapRemoteDataSourceImpl @Inject constructor(private val scrapService: ScrapService) :
    ScrapRemoteDataSource {

    override suspend fun getAllScrapsByCategoryId(
        categoryRemoteId: Int
    ): Result<ScrapListResponse> {
        return try {
            val response = scrapService.getAllScrapsByCategoryId(categoryRemoteId)
            if (response.isSuccessful) {
                val body = response.body()?.result
                if (body != null) Result.Success(body)
                else Result.Error(Exception("Response body is null"), "스크랩 목록 응답 오류")
            } else {
                Result.Error(
                    Exception("Failed to get scraps code: ${response.code()}"), "스크랩 목록 조회 실패"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "스크랩 목록 조회 중 오류 발생")
        }
    }

    override suspend fun getFavoriteScraps(): Result<FavoriteListResponse> {
        return try {
            val response = scrapService.getFavoriteScraps()
            if (response.isSuccessful) {
                val body = response.body()?.result
                if (body != null) Result.Success(body)
                else Result.Error(Exception("Response body is null"), "즐겨찾기 목록 응답 오류")
            } else {
                Result.Error(
                    Exception("Failed to get favorite scraps code: ${response.code()}"),
                    "즐겨찾기 목록 조회 실패"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "즐겨찾기 목록 조회 중 오류 발생")
        }
    }

    override suspend fun updateScrapFavorite(scrapId: Long): Result<JsonElement> {
        return try {
            val response = scrapService.updateScrapFavorite(scrapId)
            if (response.isSuccessful) {
                val body = response.body()?.result
                if (body != null) Result.Success(body)
                else Result.Error(Exception("Response body is null"), "즐겨찾기 토글 응답 오류")
            } else {
                Result.Error(
                    Exception("Toggle favorite failed code: ${response.code()}"), "즐겨찾기 토글 실패"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "즐겨찾기 토글 중 오류 발생")
        }
    }

    override suspend fun updateScrapBulkFavorite(scrapIdList: List<Long>): Result<JsonElement> {
        return try {
            val request = FavoriteListToggleRequest(scrapIdList = scrapIdList)
            val response = scrapService.updateScrapBulkFavorite(request)
            if (response.isSuccessful) {
                val body = response.body()?.result
                if (body != null) Result.Success(body)
                else Result.Error(Exception("Response body is null"), "다중 즐겨찾기 토글 응답 오류")
            } else {
                Result.Error(
                    Exception("Bulk toggle favorite failed code: ${response.code()}"),
                    "다중 즐겨찾기 토글 실패"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "다중 즐겨찾기 토글 중 오류 발생")
        }
    }

    override suspend fun getScrapById(scrapRemoteId: Int): Result<ScrapDetailResponse> {
        return try {
            val response = scrapService.getScrapById(scrapRemoteId)
            if (response.isSuccessful) {
                val body = response.body()?.result
                if (body != null) Result.Success(body)
                else Result.Error(Exception("Response body is null"), "스크랩 상세 응답 오류")
            } else {
                Result.Error(
                    Exception("Failed to get scrap details code: ${response.code()}"),
                    "스크랩 상세 조회 실패"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "스크랩 상세 조회 중 오류 발생")
        }
    }

    override suspend fun createScrap(
        categoryRemoteId: Int, request: CreateScrapRequest
    ): Result<CreateScrapResponse> {
        return try {
            val response = scrapService.createScrap(categoryRemoteId, request)
            if (response.isSuccessful) {
                val body = response.body()?.result
                if (body != null) Result.Success(body)
                else Result.Error(Exception("Response body is null"), "스크랩 생성 응답 오류")
            } else {
                Result.Error(Exception("Create scrap failed code: ${response.code()}"), "스크랩 생성 실패")
            }
        } catch (e: Exception) {
            Result.Error(e, "스크랩 생성 중 오류 발생")
        }
    }

    override suspend fun updateScrapMemo(scrapId: Int, memo: String): Result<ScrapMemoDto> {
        return try {
            val request = ScrapMemoDto(memo = memo)
            val response = scrapService.updateScrapMemo(scrapId, request)
            if (response.isSuccessful) {
                val body = response.body()?.result
                if (body != null) Result.Success(body)
                else Result.Error(Exception("Response body is null"), "메모 업데이트 응답 오류")
            } else {
                Result.Error(Exception("Update memo failed code: ${response.code()}"), "메모 업데이트 실패")
            }
        } catch (e: Exception) {
            Result.Error(e, "메모 업데이트 중 오류 발생")
        }
    }

    override suspend fun moveScrap(scrapId: Long, categoryRemoteId: Long): Result<JsonElement> {
        return try {
            val request = MoveScrapRequest(moveCategoryId = categoryRemoteId)
            val response = scrapService.moveScrap(scrapId, request)
            if (response.isSuccessful) {
                val body = response.body()?.result
                if (body != null) Result.Success(body)
                else Result.Error(Exception("Response body is null"), "스크랩 이동 응답 오류")
            } else {
                Result.Error(Exception("Move scrap failed code: ${response.code()}"), "스크랩 이동 실패")
            }
        } catch (e: Exception) {
            Result.Error(e, "스크랩 이동 중 오류 발생")
        }
    }

    override suspend fun moveScrapList(
        scrapIds: List<Long>, moveCategoryId: Long
    ): Result<JsonElement?> {
        return try {
            val request = MoveScrapListRequest(scrapIds = scrapIds, moveCategoryId = moveCategoryId)
            val response = scrapService.moveScrapList(request)
            if (response.isSuccessful) {
                Result.Success(response.body()?.result)
            } else {
                Result.Error(
                    Exception("Move scraps failed code: ${response.code()}"), "스크랩 목록 이동 실패"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "스크랩 목록 이동 중 오류 발생")
        }
    }

    override suspend fun deleteScrap(scrapId: Long): Result<JsonElement?> {
        return try {
            val response = scrapService.deleteScrap(scrapId)
            if (response.isSuccessful) {
                Result.Success(response.body()?.result)
            } else {
                Result.Error(Exception("Delete scrap failed code: ${response.code()}"), "스크랩 삭제 실패")
            }
        } catch (e: Exception) {
            Result.Error(e, "스크랩 삭제 중 오류 발생")
        }
    }

    override suspend fun deleteScrapBulk(scrapIds: List<Long>): Result<JsonElement?> {
        return try {
            val response = scrapService.deleteScrapBulk(scrapIds)
            if (response.isSuccessful) {
                Result.Success(response.body()?.result)
            } else {
                Result.Error(
                    Exception("Bulk delete failed code: ${response.code()}"), "다중 스크랩 삭제 실패"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "다중 스크랩 삭제 중 오류 발생")
        }
    }

    override suspend fun searchScraps(
        query: String,
        sort: String?,
        direction: String?,
        page: Int?,
        size: Int?,
        request: SearchRequest
    ): Result<SearchListResponse> {
        return try {
            val response = scrapService.searchScraps(query, sort, direction, page, size, request)
            if (response.isSuccessful) {
                val body = response.body()?.result
                if (body != null) Result.Success(body)
                else Result.Error(Exception("Response body is null"), "검색 응답 오류")
            } else {
                Result.Error(Exception("Search failed code: ${response.code()}"), "검색 실패")
            }
        } catch (e: Exception) {
            Result.Error(e, "검색 중 오류 발생")
        }
    }

    override suspend fun favoriteSearch(
        query: String, sort: String?, direction: String?
    ): Result<SearchFavoriteResponse> {
        return try {
            val response = scrapService.favoriteSearch(query, sort, direction)
            if (response.isSuccessful) {
                val body = response.body()?.result
                if (body != null) Result.Success(body)
                else Result.Error(Exception("Response body is null"), "즐겨찾기 검색 응답 오류")
            } else {
                Result.Error(
                    Exception("Favorite search failed code: ${response.code()}"), "즐겨찾기 검색 실패"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "즐겨찾기 검색 중 오류 발생")
        }
    }

    override suspend fun searchScrapsByCategory(
        categoryRemoteId: Long, query: String, sort: String?, direction: String?
    ): Result<SearchScrapResponse> {
        return try {
            val response =
                scrapService.searchScrapsByCategory(categoryRemoteId, query, sort, direction)
            if (response.isSuccessful) {
                val body = response.body()?.result
                if (body != null) Result.Success(body)
                else Result.Error(Exception("Response body is null"), "카테고리별 검색 응답 오류")
            } else {
                Result.Error(
                    Exception("Category search failed code: ${response.code()}"), "카테고리별 검색 실패"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "카테고리별 검색 중 오류 발생")
        }
    }
}
