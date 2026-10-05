package com.scrap2025.scrap2025.repository

import androidx.paging.PagingData
import com.scrap2025.scrap2025.model.Notice
import com.scrap2025.scrap2025.model.NoticeSummary
import kotlinx.coroutines.flow.Flow

/** 서버 API로 관리되는 공지사항 데이터를 가져오는 리포지토리 인터페이스 (Remote Config 공지는 [NoticeRepository]) */
interface ServerNoticeRepository {
    /** 전체 공지사항 목록을 최신순 PagingData Flow로 조회합니다. */
    fun getNoticePagingFlow(pageSize: Int = 20): Flow<PagingData<NoticeSummary>>

    /** 가장 최신 공지사항 1건을 조회합니다. 공지사항이 하나도 없으면 null을 담아 반환합니다. */
    suspend fun getLatestNoticeSummary(): Result<NoticeSummary?>

    /** 공지사항 상세(본문 포함)를 조회합니다. */
    suspend fun getNoticeById(noticeId: Long): Result<Notice>
}
