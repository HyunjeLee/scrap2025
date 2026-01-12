package com.scrap2025.scrap2025.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.scrap2025.scrap2025.data.remote.datasource.ScrapRemoteDataSource
import com.scrap2025.scrap2025.model.ScrapItem

/** 즐겨찾기 스크랩 목록을 페이징하여 가져오는 PagingSource */
class FavoritePagingSource(
    private val scrapRemoteDataSource: ScrapRemoteDataSource,
    private val sort: String?,
    private val direction: String?
) : PagingSource<Int, ScrapItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ScrapItem> {
        val page = params.key ?: 0
        return try {
            val response =
                scrapRemoteDataSource.getFavoriteScraps(
                    sort = sort,
                    direction = direction,
                    page = page,
                    size = params.loadSize
                )

            val scraps = response.scraps.map { it.toDomainModel() }

            LoadResult.Page(
                data = scraps,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (response.meta.isEnd || scraps.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ScrapItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
