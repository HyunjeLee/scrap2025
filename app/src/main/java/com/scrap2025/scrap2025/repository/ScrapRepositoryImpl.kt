package com.scrap2025.scrap2025.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.scrap2025.scrap2025.data.remote.datasource.ScrapRemoteDataSource
import com.scrap2025.scrap2025.data.remote.dto.CreateScrapRequest
import com.scrap2025.scrap2025.data.remote.dto.SearchRequest
import com.scrap2025.scrap2025.data.remote.paging.FavoritePagingSource
import com.scrap2025.scrap2025.data.remote.paging.ScrapPagingSource
import com.scrap2025.scrap2025.data.remote.paging.SearchPagingSource
import com.scrap2025.scrap2025.model.ScrapItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/** ScrapRepository의 구현체. [ScrapRemoteDataSource]를 통해 원격 서버와 통신하며 스크랩 데이터를 관리합니다. */
@Singleton
class ScrapRepositoryImpl
@Inject
constructor(
    private val scrapRemoteDataSource: ScrapRemoteDataSource,
) : ScrapRepository {

    // 데이터 변경 사항을 UI에 알리기 위한 스트림
    private val _refreshEvent = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }
    override val refreshEvent: SharedFlow<Unit> = _refreshEvent.asSharedFlow()

    override fun getScrapPagingFlow(
        categoryId: Long,
        sort: String?,
        direction: String?,
        pageSize: Int
    ): Flow<PagingData<ScrapItem>> {
        return Pager(
            config =
                PagingConfig(
                    pageSize = pageSize,
                    enablePlaceholders = false,
                    prefetchDistance = 5,
                    initialLoadSize = pageSize,
                ),
            pagingSourceFactory = {
                ScrapPagingSource(
                    scrapRemoteDataSource = scrapRemoteDataSource,
                    categoryId = categoryId,
                    sort = sort,
                    direction = direction
                )
            }
        )
            .flow
    }

    override fun getFavoriteScrapPagingFlow(
        sort: String?,
        direction: String?,
        pageSize: Int
    ): Flow<PagingData<ScrapItem>> {
        return Pager(
            config =
                PagingConfig(
                    pageSize = pageSize,
                    enablePlaceholders = false,
                    prefetchDistance = 5,
                    initialLoadSize = pageSize
                ),
            pagingSourceFactory = {
                FavoritePagingSource(
                    scrapRemoteDataSource = scrapRemoteDataSource,
                    sort = sort,
                    direction = direction
                )
            }
        )
            .flow
    }

    override fun getSearchScrapPagingFlow(
        query: String,
        searchScope: List<String>,
        categoryRemoteIds: List<Long>,
        startDate: String,
        endDate: String,
        sortType: String,
        sortDirection: String,
        pageSize: Int
    ): Flow<PagingData<ScrapItem>> {
        val searchRequest =
            SearchRequest(
                searchScope = searchScope,
                categoryScope = categoryRemoteIds,
                startDate = startDate,
                endDate = endDate
            )
        return Pager(
            config =
                PagingConfig(
                    pageSize = pageSize,
                    enablePlaceholders = false,
                    prefetchDistance = 5,
                    initialLoadSize = pageSize
                ),
            pagingSourceFactory = {
                SearchPagingSource(
                    scrapRemoteDataSource = scrapRemoteDataSource,
                    query = query,
                    sort = sortType,
                    direction = sortDirection,
                    searchRequest = searchRequest
                )
            }
        )
            .flow
    }

    override suspend fun getScrapById(id: Long): Result<ScrapItem> {
        return try {
            val scrap = scrapRemoteDataSource.getScrapById(id)
            Result.success(scrap.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createScrap(item: ScrapItem): Result<Unit> {
        return try {
            scrapRemoteDataSource.createScrap(
                categoryId = item.categoryId ?: 0L,
                request =
                    CreateScrapRequest(
                        scrapURL = item.url,
                        imageURL = item.imageUrl,
                        title = item.title,
                        description = item.description,
                        memo = item.memo,
                        isFavorite = item.isFavorite
                    )
            )
            _refreshEvent.emit(Unit)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteScrapItem(id: Long): Result<Unit> {
        return try {
            scrapRemoteDataSource.deleteScrap(id)
            _refreshEvent.emit(Unit)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteScrapBulk(idBulk: List<Long>): Result<Unit> {
        return try {
            scrapRemoteDataSource.deleteScrapBulk(idBulk)
            _refreshEvent.emit(Unit)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateScrapMemo(id: Long, memo: String?): Result<Unit> {
        return try {
            if (memo != null) {
                scrapRemoteDataSource.updateScrapMemo(id, memo)
                _refreshEvent.emit(Unit)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun moveScrap(scrapId: Long, categoryId: Long): Result<Unit> {
        return try {
            scrapRemoteDataSource.moveScrap(scrapId, categoryId)
            _refreshEvent.emit(Unit)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun moveScrapBulk(scrapIds: List<Long>, categoryId: Long): Result<Unit> {
        return try {
            scrapRemoteDataSource.moveScrapBulk(scrapIds, categoryId)
            _refreshEvent.emit(Unit)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleFavorite(scrapId: Long): Result<Unit> {
        return try {
            scrapRemoteDataSource.updateScrapFavorite(scrapId)
            _refreshEvent.emit(Unit)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleFavoriteBulk(scrapIdBulk: List<Long>): Result<Unit> {
        return try {
            scrapRemoteDataSource.updateScrapListFavorite(scrapIdBulk)
            _refreshEvent.emit(Unit)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchFavoriteScraps(
        query: String,
        sortType: String?,
        sortDirection: String?,
    ): Result<List<ScrapItem>> =
        try {
            val remoteData =
                scrapRemoteDataSource.favoriteSearch(
                    query = query,
                    sort = sortType,
                    direction = sortDirection,
                )

            val domainItems = remoteData.scraps.map { it.toDomainModel() }
            Result.success(domainItems)
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun searchScrapsByCategory(
        categoryRemoteId: Long,
        query: String,
        sortType: String?,
        sortDirection: String?
    ): Result<List<ScrapItem>> =
        try {
            val remoteData =
                scrapRemoteDataSource.searchScrapsByCategory(
                    categoryId = categoryRemoteId,
                    query = query,
                    sort = sortType,
                    direction = sortDirection
                )

            val domainItems = remoteData.scraps.map { it.toDomainModel() }
            Result.success(domainItems)
        } catch (e: Exception) {
            Result.failure(e)
        }
}
