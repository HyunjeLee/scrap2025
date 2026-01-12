package com.scrap2025.scrap2025.repository

import androidx.paging.PagingData
import com.scrap2025.scrap2025.model.ScrapItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

/** 스크랩 데이터 처리를 담당하는 리포지토리 인터페이스 */
interface ScrapRepository {
    /** 전역 스크랩 데이터 갱신 신호 (상세 화면 수정 시 목록 화면 자동 갱신 등 UI 동기화용) */
    val refreshEvent: SharedFlow<Unit>

    /** 특정 카테고리의 스크랩 목록을 PagingData Flow로 조회합니다. */
    fun getScrapPagingFlow(
        categoryId: Long,
        sort: String? = null,
        direction: String? = null,
        pageSize: Int = 10
    ): Flow<PagingData<ScrapItem>>

    /** 즐겨찾기 설정된 모든 스크랩 목록을 PagingData Flow로 조회합니다. */
    fun getFavoriteScrapPagingFlow(
        sort: String? = null,
        direction: String? = null,
        pageSize: Int = 10
    ): Flow<PagingData<ScrapItem>>

    /** 통합 검색 결과를 PagingData Flow로 조회합니다. */
    fun getSearchScrapPagingFlow(
        query: String,
        searchScope: List<String>,
        categoryRemoteIds: List<Long>,
        startDate: String,
        endDate: String,
        sortType: String,
        sortDirection: String,
        pageSize: Int = 10
    ): Flow<PagingData<ScrapItem>>

    /**
     * 특정 ID의 스크랩 상세 정보를 조회합니다.
     * @param id 스크랩 ID
     * @return 조회된 [ScrapItem] 결과
     */
    suspend fun getScrapById(id: Long): Result<ScrapItem>

    /**
     * 새로운 스크랩을 추가합니다.
     * @param item 추가할 [ScrapItem] 객체
     * @return 성공 여부
     */
    suspend fun createScrap(item: ScrapItem): Result<Unit>

    /**
     * 단일 스크랩을 삭제합니다.
     * @param id 삭제할 스크랩 ID
     * @return 성공 여부
     */
    suspend fun deleteScrapItem(id: Long): Result<Unit>

    /**
     * 여러 스크랩을 한꺼번에 삭제합니다.
     * @param idBulk 삭제할 스크랩 ID 리스트
     * @return 성공 여부
     */
    suspend fun deleteScrapBulk(idBulk: List<Long>): Result<Unit>

    /**
     * 스크랩의 메모 내용을 업데이트합니다.
     * @param id 스크랩 ID
     * @param memo 수정할 내용 (null 가능)
     * @return 성공 여부
     */
    suspend fun updateScrapMemo(id: Long, memo: String?): Result<Unit>

    /**
     * 스크랩 아이템을 다른 카테고리로 이동시킵니다.
     * @param scrapId 이동할 스크랩 ID
     * @param categoryId 대상 카테고리 ID
     * @return 성공 여부
     */
    suspend fun moveScrap(scrapId: Long, categoryId: Long): Result<Unit>

    /**
     * 특정 스크랩의 즐겨찾기 상태를 토글(설정/해제)합니다.
     * @param scrapId 스크랩 ID
     * @return 성공 여부
     */
    suspend fun toggleFavorite(scrapId: Long): Result<Unit>

    /**
     * 여러 스크랩의 즐겨찾기 상태를 일괄적으로 토글합니다.
     * @param scrapIdBulk 스크랩 ID 리스트
     * @return 성공 여부
     */
    suspend fun toggleFavoriteBulk(scrapIdBulk: List<Long>): Result<Unit>

    /**
     * 여러 스크랩 아이템을 특정 카테고리로 일괄 이동시킵니다.
     * @param scrapIds 이동할 스크랩 ID 리스트
     * @param categoryId 대상 카테고리 ID
     * @return 성공 여부
     */
    suspend fun moveScrapBulk(scrapIds: List<Long>, categoryId: Long): Result<Unit>

    /**
     * 즐겨찾기 목록 내에서 검색을 수행합니다.
     * @param query 검색어
     * @param sortType 정렬 기준
     * @param sortDirection 정렬 방향
     * @return 검색된 즐겨찾기 목록 결과
     */
    suspend fun searchFavoriteScraps(
        query: String,
        sortType: String? = null,
        sortDirection: String? = null,
    ): Result<List<ScrapItem>>

    /**
     * 특정 카테고리 내에서 검색을 수행합니다.
     * @param categoryRemoteId 카테고리 ID
     * @param query 검색어
     * @param sortType 정렬 기준
     * @param sortDirection 정렬 방향
     * @return 검색된 카테고리 내 스크랩 목록 결과
     */
    suspend fun searchScrapsByCategory(
        categoryRemoteId: Long,
        query: String,
        sortType: String? = null,
        sortDirection: String? = null,
    ): Result<List<ScrapItem>>
}
