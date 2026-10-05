package com.scrap2025.scrap2025.data.remote.datasource

import com.scrap2025.scrap2025.data.remote.dto.NoticeDetailResponse
import com.scrap2025.scrap2025.data.remote.dto.NoticeListResponse

interface NoticeRemoteDataSource {
    suspend fun getNotices(direction: String?, page: Int?, size: Int?): NoticeListResponse

    suspend fun getNoticeById(noticeId: Long): NoticeDetailResponse
}
