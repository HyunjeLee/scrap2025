package com.scrap2025.scrap2025.repository

import com.scrap2025.scrap2025.model.ScrapItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface ScrapRepository {
    /** 전역 스크랩 데이터 갱신 신호 (상세화면 수정 시 목록화면 자동 갱신용) */
    val refreshEvent: SharedFlow<Unit>

    /** 모든 스크랩 아이템 목록을 Flow로 반환 categoryId가 null이면 전체 목록 반환 */
    fun getAllScrapsByCategory(categoryId: Long): Flow<Result<List<ScrapItem>>>

    /** 즐겨찾기된 스크랩 아이템 목록을 Flow로 반환 */
    fun getAllFavoriteScraps(): Flow<Result<List<ScrapItem>>>

    /** 특정 ID의 스크랩 아이템 조회 */
    suspend fun getScrapById(id: Long): Result<ScrapItem>

    /** 새로운 스크랩 아이템 추가 */
    suspend fun createScrap(item: ScrapItem): Result<Unit>

    /** 스크랩 아이템 삭제 */
    suspend fun deleteScrapItem(id: Long): Result<Unit>

    /** 스크랩 벌크 삭제 */
    suspend fun deleteScrapBulk(idBulk: List<Long>): Result<Unit>

    /** 스크랩 아이템 업데이트 */
    suspend fun updateScrapMemo(id: Long, memo: String?): Result<Unit>

    /** 스크랩 아이템을 다른 카테고리로 이동 */
    suspend fun moveScrap(scrapId: Long, categoryId: Long): Result<Unit>

    /** 스크랩 아이템의 즐겨찾기 상태 토글 */
    suspend fun toggleFavorite(scrapId: Long): Result<Unit>

    suspend fun toggleFavoriteBulk(scrapIdBulk: List<Long>): Result<Unit>

    /** 특정 카테고리열의 모든 스크랩을 다른 카테고리로 이동 */
    suspend fun moveScrapBulk(scrapIds: List<Long>, categoryId: Long): Result<Unit>

    suspend fun searchScraps(
        query: String,
        searchScope: List<String>,
        categoryRemoteIds: List<Long>,
        startDate: String,
        endDate: String,
        sortType: String,
        sortDirection: String,
        page: Int,
        size: Int
    ): Result<List<ScrapItem>>

    suspend fun searchFavoriteScraps(
        query: String,
        sortType: String? = null,
        sortDirection: String? = null,
    ): Result<List<ScrapItem>>

    suspend fun searchScrapsByCategory(
        categoryRemoteId: Long,
        query: String,
        sortType: String? = null,
        sortDirection: String? = null,
    ): Result<List<ScrapItem>>
}
