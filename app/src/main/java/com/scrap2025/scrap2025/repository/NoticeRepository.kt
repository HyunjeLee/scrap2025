package com.scrap2025.scrap2025.repository

import com.scrap2025.scrap2025.model.NoticeConfig

/** Remote Config로 관리되는 공지사항 데이터를 가져오는 리포지토리 인터페이스 */
interface NoticeRepository {
    /** 활성화된(enabled = true) 공지사항을 가져옵니다. 없거나 파싱에 실패하면 null을 반환합니다. */
    suspend fun getNotice(): NoticeConfig?
}
