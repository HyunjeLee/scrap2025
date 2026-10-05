package com.scrap2025.scrap2025.data.remote.api

import com.scrap2025.scrap2025.data.remote.dto.BaseResponse
import com.scrap2025.scrap2025.data.remote.dto.NoticeDetailResponse
import com.scrap2025.scrap2025.data.remote.dto.NoticeListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NoticeService {
    @GET("/auth/notices")
    suspend fun getNotices(
        @Query("direction") direction: String? = null,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): Response<BaseResponse<NoticeListResponse>>

    @GET("/auth/notices/{notice-id}")
    suspend fun getNoticeById(
        @Path("notice-id") noticeId: Long
    ): Response<BaseResponse<NoticeDetailResponse>>
}
