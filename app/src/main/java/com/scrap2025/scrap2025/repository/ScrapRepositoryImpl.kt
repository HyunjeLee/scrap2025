package com.scrap2025.scrap2025.repository

import com.scrap2025.scrap2025.data.remote.datasource.ScrapRemoteDataSource
import com.scrap2025.scrap2025.data.remote.dto.CreateScrapRequest
import com.scrap2025.scrap2025.data.remote.dto.SearchRequest
import com.scrap2025.scrap2025.model.ScrapItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScrapRepositoryImpl
@Inject constructor(
    private val scrapRemoteDataSource: ScrapRemoteDataSource,
) : ScrapRepository {

    private val _refreshEvent = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }
    override val refreshEvent: SharedFlow<Unit> = _refreshEvent.asSharedFlow()

    override fun getAllScrapsByCategory(categoryId: Long): Flow<Result<List<ScrapItem>>> = flow {
        try {
            val response = scrapRemoteDataSource.getAllScrapsByCategoryId(categoryId)
            val scraps = response.scraps.map { it.toDomainModel() }

            emit(Result.success(scraps))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getAllFavoriteScraps(): Flow<Result<List<ScrapItem>>> = flow {
        try {
            val response = scrapRemoteDataSource.getFavoriteScraps()
            val scraps = response.scraps.map { it.toDomainModel() }

            emit(Result.success(scraps))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
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
                categoryId = item.categoryId ?: 0L, request = CreateScrapRequest(
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

    override suspend fun searchScraps(
        query: String,
        searchScope: List<String>,
        categoryRemoteIds: List<Long>,
        startDate: String,
        endDate: String,
        sortType: String,
        sortDirection: String,
        page: Int,
        size: Int
    ): Result<List<ScrapItem>> = try {
        val request = SearchRequest(searchScope, categoryRemoteIds, startDate, endDate)

        val remoteData = scrapRemoteDataSource.searchScraps(
            query = query,
            sort = sortType,
            direction = sortDirection,
            page = page,
            size = size,
            request = request
        )

        val domainItems = remoteData.scraps.map { it.toDomainModel() }
        Result.success(domainItems)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun searchFavoriteScraps(
        query: String,
        sortType: String?,
        sortDirection: String?,
    ): Result<List<ScrapItem>> = try {
        val remoteData = scrapRemoteDataSource.favoriteSearch(
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
        categoryRemoteId: Long, query: String, sortType: String?, sortDirection: String?
    ): Result<List<ScrapItem>> = try {
        val remoteData = scrapRemoteDataSource.searchScrapsByCategory(
            categoryId = categoryRemoteId, query = query, sort = sortType, direction = sortDirection
        )

        val domainItems = remoteData.scraps.map { it.toDomainModel() }
        Result.success(domainItems)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
