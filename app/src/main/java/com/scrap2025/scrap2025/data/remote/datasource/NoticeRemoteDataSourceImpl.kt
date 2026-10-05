package com.scrap2025.scrap2025.data.remote.datasource

import com.scrap2025.scrap2025.data.remote.api.NoticeService
import com.scrap2025.scrap2025.data.remote.dto.NoticeDetailResponse
import com.scrap2025.scrap2025.data.remote.dto.NoticeListResponse
import javax.inject.Inject

class NoticeRemoteDataSourceImpl
@Inject
constructor(private val noticeService: NoticeService) :
    NoticeRemoteDataSource {
    override suspend fun getNotices(
        direction: String?,
        page: Int?,
        size: Int?
    ): NoticeListResponse {
        val response = noticeService.getNotices(direction, page, size)
        if (response.isSuccessful) {
            return response.body()?.result ?: throw Exception("Response body is null")
        } else {
            throw Exception("Failed to get notices code: ${response.code()}")
        }
    }

    override suspend fun getNoticeById(noticeId: Long): NoticeDetailResponse {
        val response = noticeService.getNoticeById(noticeId)
        if (response.isSuccessful) {
            return response.body()?.result ?: throw Exception("Response body is null")
        } else {
            throw Exception("Failed to get notice code: ${response.code()}")
        }
    }
}
