package com.scrap2025.scrap2025.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.scrap2025.scrap2025.data.remote.datasource.NoticeRemoteDataSource
import com.scrap2025.scrap2025.data.remote.paging.NoticePagingSource
import com.scrap2025.scrap2025.model.Notice
import com.scrap2025.scrap2025.model.NoticeSummary
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ServerNoticeRepositoryImpl
@Inject
constructor(
    private val noticeRemoteDataSource: NoticeRemoteDataSource
) : ServerNoticeRepository {
    override fun getNoticePagingFlow(pageSize: Int): Flow<PagingData<NoticeSummary>> = Pager(
        config =
        PagingConfig(
            pageSize = pageSize,
            enablePlaceholders = false,
            prefetchDistance = 5,
            initialLoadSize = pageSize // page 번호 기반이므로 첫 로드 크기를 pageSize와 맞춘다
        ),
        pagingSourceFactory = { NoticePagingSource(noticeRemoteDataSource) }
    ).flow

    override suspend fun getLatestNoticeSummary(): Result<NoticeSummary?> = runCatching {
        noticeRemoteDataSource
            .getNotices(direction = "DESC", page = 0, size = 1)
            .notices
            .firstOrNull()
            ?.toDomainModel()
    }

    override suspend fun getNoticeById(noticeId: Long): Result<Notice> = runCatching {
        noticeRemoteDataSource.getNoticeById(noticeId).toDomainModel()
    }
}
