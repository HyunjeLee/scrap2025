package com.scrap2025.scrap2025.repository

import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.model.ScrapItem
import kotlinx.coroutines.flow.Flow

interface ScrapRepository {
    /** 모든 스크랩 아이템 목록을 Flow로 반환 */
    /** 모든 스크랩 아이템 목록을 Flow로 반환 categoryId가 null이면 전체 목록 반환 */
    fun getScrapItems(categoryId: String? = null): Flow<Result<List<ScrapItem>>>

    /** 특정 ID의 스크랩 아이템 조회 */
    fun getScrapItemByIdAsFlow(id: String): Flow<Result<ScrapItem>> // Flow 반환으로 변경

    /** 새로운 스크랩 아이템 추가 */
    suspend fun createScrap(item: ScrapItem): Result<Unit>

    /** 스크랩 아이템 삭제 */
    suspend fun deleteScrapItem(id: String): Result<Unit>

    /** 스크랩 벌크 삭제 */
    suspend fun deleteScrapBulk(idBulk: List<Long>): Result<Unit>

    /** 스크랩 아이템 업데이트 */
    suspend fun updateScrapItem(id: String, memo: String?): Result<Unit>

    /** 스크랩 아이템을 다른 카테고리로 이동 */
    suspend fun moveScrapItem(scrapId: String, categoryId: String): Result<Unit>

    /** 스크랩 아이템의 즐겨찾기 상태 토글 */
    suspend fun toggleFavorite(scrapId: String): Result<Unit>

    /** 특정 카테고리의 모든 스크랩을 다른 카테고리로 이동 */
    suspend fun moveScrapsToCategory(fromId: String, toId: String): Result<Unit>  // todo: 실제 스크랩 ID 리스트로 인자 추가

    /** 전체 스크랩 개수 조회 */
    fun getScrapCount(): Flow<Int>

    /**
     * 스크랩 동기화 (Remote -> Local)
     * @return Result<Unit>
     */
    suspend fun syncScrapsByCategoryId(
            categoryId: String,
            categoryRemoteId: Int
    ): Result<Unit>

    /** 특정 스크랩 상세 정보 동기화 (Remote -> Local) */
    suspend fun syncScrapById(id: String): Result<Unit>
}
