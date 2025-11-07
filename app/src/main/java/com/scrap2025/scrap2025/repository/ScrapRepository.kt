package com.scrap2025.scrap2025.repository

import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.model.ScrapItem
import kotlinx.coroutines.flow.Flow

interface ScrapRepository {
    /**
     * 모든 스크랩 아이템 목록을 Flow로 반환
     */
    fun getScrapItems(): Flow<Result<List<ScrapItem>>>

    /**
     * 특정 ID의 스크랩 아이템 조회
     */
    suspend fun getScrapItemById(id: String): Result<ScrapItem>

    /**
     * 새로운 스크랩 아이템 추가
     */
    suspend fun addScrapItem(item: ScrapItem): Result<Unit>

    /**
     * 스크랩 아이템 삭제
     */
    suspend fun deleteScrapItem(id: String): Result<Unit>

    /**
     * 스크랩 아이템 업데이트
     */
    suspend fun updateScrapItem(id: String, memo: String?): Result<Unit>
}
