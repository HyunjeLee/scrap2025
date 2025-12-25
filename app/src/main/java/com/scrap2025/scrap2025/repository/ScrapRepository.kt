package com.scrap2025.scrap2025.repository

import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.model.ScrapItem
import kotlinx.coroutines.flow.Flow

interface ScrapRepository {
    /** 모든 스크랩 아이템 목록을 Flow로 반환 */
    /** 모든 스크랩 아이템 목록을 Flow로 반환 categoryId가 null이면 전체 목록 반환 */
    fun getScrapItems(categoryId: String? = null): Flow<Result<List<ScrapItem>>>

    /** 특정 ID의 스크랩 아이템 조회 */
    suspend fun getScrapItemById(id: String): Result<ScrapItem>
    fun getScrapItemByIdAsFlow(id: String): Flow<Result<ScrapItem>> // Flow 반환으로 변경

    /** 새로운 스크랩 아이템 추가 */
    suspend fun addScrapItem(item: ScrapItem): Result<Unit>

    /** 스크랩 아이템 삭제 */
    suspend fun deleteScrapItem(id: String): Result<Unit>

    /** 스크랩 아이템 업데이트 */
    suspend fun updateScrapItem(id: String, memo: String?): Result<Unit>

    /** 스크랩 아이템을 다른 카테고리로 이동 */
    suspend fun moveScrapItem(scrapId: String, categoryId: String): Result<Unit>

    /** 스크랩 아이템의 즐겨찾기 상태 토글 */
    suspend fun toggleFavorite(scrapId: String): Result<Unit>

    /** 특정 카테고리의 모든 스크랩을 다른 카테고리로 이동 */
    suspend fun moveScrapsToCategory(fromId: String, toId: String): Result<Unit>

    /** 전체 스크랩 개수 조회 */
    fun getScrapCount(): Flow<Int>

    /**
     * 스크랩 동기화 (Remote -> Local)
     * @param token API Access Token
     * @return Result<Unit>
     */
    suspend fun syncScrapsByCategoryId(token: String, categoryId: String, categoryRemoteId: Int): Result<Unit>
}
