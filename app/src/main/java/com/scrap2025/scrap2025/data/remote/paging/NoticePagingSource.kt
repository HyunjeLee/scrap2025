package com.scrap2025.scrap2025.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.scrap2025.scrap2025.data.remote.datasource.NoticeRemoteDataSource
import com.scrap2025.scrap2025.model.NoticeSummary

/** 서버 공지사항 목록(최신순)을 페이징하여 가져오는 PagingSource */
class NoticePagingSource(private val noticeRemoteDataSource: NoticeRemoteDataSource) :
    PagingSource<Int, NoticeSummary>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NoticeSummary> {
        val page = params.key ?: 0
        return try {
            val response =
                noticeRemoteDataSource.getNotices(
                    direction = "DESC",
                    page = page,
                    size = params.loadSize
                )

            val notices = response.notices.map { it.toDomainModel() }

            LoadResult.Page(
                data = notices,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (response.meta.isEnd || notices.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, NoticeSummary>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
}
