package com.scrap2025.scrap2025.repository

import androidx.room.withTransaction
import com.scrap2025.scrap2025.data.local.AppDatabase
import com.scrap2025.scrap2025.data.local.dao.CategoryDao
import com.scrap2025.scrap2025.data.local.dao.ScrapDao
import com.scrap2025.scrap2025.data.local.entity.ScrapEntity
import com.scrap2025.scrap2025.data.model.SyncStatus
import com.scrap2025.scrap2025.data.remote.datasource.ScrapRemoteDataSource
import com.scrap2025.scrap2025.data.remote.dto.CreateScrapRequest
import com.scrap2025.scrap2025.data.remote.dto.CreateScrapResponse
import com.scrap2025.scrap2025.data.remote.dto.SearchRequest
import com.scrap2025.scrap2025.model.ScrapItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScrapRepositoryImpl
@Inject constructor(
    private val appDatabase: AppDatabase,
    private val scrapDao: ScrapDao,
    private val categoryDao: CategoryDao,
    private val scrapRemoteDataSource: ScrapRemoteDataSource,
) : ScrapRepository {

    override fun getScrapItems(categoryId: String?): Flow<Result<List<ScrapItem>>> {
        val flow = if (categoryId != null) {
            scrapDao.getAllScrapsByCategoryId(categoryId)
        } else {
            scrapDao.getAllScraps()
        }

        return flow.map { entities ->
            try {
                Result.success(entities.map { it.toDomainModel() })
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override fun getFavoriteScrapItemsFromRemote(): Flow<Result<List<ScrapItem>>> = flow {
        try {
            val remoteData = scrapRemoteDataSource.getFavoriteScraps()
            val remoteScraps = remoteData.scraps

            val domainItems = remoteScraps.map { remoteScrap ->
                val localScrap = scrapDao.getScrapByRemoteId(remoteScrap.scrapRemoteId)

                remoteScrap.toDomainModel(
                    scrapLocalId = localScrap?.id ?: "NO_LOCAL",
                    categoryLocalId = localScrap?.categoryId ?: "NO_LOCAL"
                )
            }

            emit(Result.success(domainItems))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getScrapItemByIdAsFlow(id: String): Flow<Result<ScrapItem>> {
        return scrapDao.getScrapByIdFlow(id).map { entity ->
                if (entity != null) {
                    Result.success(entity.toDomainModel())
                } else {
                    Result.failure(NoSuchElementException())
                }
            }.catch { e -> emit(Result.failure(e)) }
    }

    override suspend fun createScrap(item: ScrapItem): Result<Unit> {
        return try {
            appDatabase.withTransaction {
                scrapDao.insertScrap(ScrapEntity.fromDomainModel(item))
                categoryDao.incrementScrapCount(item.categoryId)
            }

            try {
                createScrapRemote(item)
                val category = categoryDao.getCategoryById(item.categoryId)
                val categoryRemoteId = category?.remoteId

                if (categoryRemoteId != null) {
                    syncScrapsByCategoryId(
                        categoryId = item.categoryId, categoryRemoteId = categoryRemoteId
                    )
                }
            } catch (e: Exception) {
                // Stay PENDING
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteScrapItem(id: String): Result<Unit> {
        return try {
            val existing = scrapDao.getScrapById(id)
            if (existing != null) {
                appDatabase.withTransaction {
                    scrapDao.deleteScrap(id)
                    categoryDao.decrementScrapCount(existing.categoryId)
                }

                try {
                    scrapRemoteDataSource.deleteScrap(existing.remoteId!!.toLong())
                } catch (e: Exception) {
                    // Sync fail
                }

                Result.success(Unit)
            } else {
                Result.failure(NoSuchElementException("ID가 $id 인 스크랩을 찾을 수 없습니다."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteScrapBulk(scrapIdBulk: List<Long>): Result<Unit> {
        return try {
            scrapRemoteDataSource.deleteScrapBulk(scrapIdBulk)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateScrapItem(id: String, memo: String?): Result<Unit> {
        return try {
            val existing = scrapDao.getScrapById(id)
            if (existing != null) {
                val remoteId = existing.remoteId

                scrapDao.updateScrapMemo(id, memo)
                if (remoteId != null && memo != null) {
                    try {
                        scrapRemoteDataSource.updateScrapMemo(remoteId, memo)
                    } catch (e: Exception) {
                        // Sync fail
                    }
                }

                Result.success(Unit)
            } else {
                Result.failure(NoSuchElementException("ID가 $id 인 스크랩을 찾을 수 없습니다."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun moveScrapItem(scrapId: String, categoryId: String): Result<Unit> {
        return try {
            val existing = scrapDao.getScrapById(scrapId)
            if (existing != null) {
                val categoryRemoteId = categoryDao.getCategoryById(categoryId)!!.remoteId!!

                appDatabase.withTransaction {
                    categoryDao.decrementScrapCount(existing.categoryId)
                    scrapDao.moveScrap(scrapId, categoryId)
                    categoryDao.incrementScrapCount(categoryId)
                }

                try {
                    scrapRemoteDataSource.moveScrap(
                        existing.remoteId!!.toLong(), categoryRemoteId.toLong()
                    )
                } catch (e: Exception) {
                    // Sync fail
                }

                Result.success(Unit)
            } else {
                Result.failure(NoSuchElementException("ID가 $scrapId 인 스크랩을 찾을 수 없습니다."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleFavorite(scrapId: String): Result<Unit> {
        return try {
            val scrapLocal = scrapDao.getScrapById(scrapId) ?: throw NoSuchElementException()
            scrapDao.updateIsFavorite(scrapId, !scrapLocal.isFavorite)

            try {
                scrapRemoteDataSource.updateScrapFavorite(scrapLocal.remoteId!!.toLong())
            } catch (e: Exception) {
                // Sync fail
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleFavoriteBulk(scrapIdBulk: List<String>): Result<Unit> {
        return try {
            val scrapRemoteIdBulk = scrapIdBulk.map { scrapId ->
                scrapDao.getScrapById(scrapId)?.remoteId!!.toLong()
            }
            scrapRemoteDataSource.updateScrapBulkFavorite(scrapRemoteIdBulk)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun moveScrapsToCategory(fromId: String, toId: String): Result<Unit> {
        return try {
            val movedCount = scrapDao.moveScraps(fromId, toId)
            if (movedCount > 0) {
                categoryDao.updateScrapCount(fromId, -movedCount)
                categoryDao.updateScrapCount(toId, movedCount)
            }

            try {
                scrapRemoteDataSource.moveScrapList(
                    scrapIds = listOf(), // todo: actual scrap IDs
                    moveCategoryId = categoryDao.getCategoryById(toId)!!.remoteId!!.toLong()
                )
            } catch (e: Exception) {
                // Sync fail
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getScrapCount(): Flow<Int> = scrapDao.getScrapCount()

    override suspend fun syncScrapsByCategoryId(
        categoryId: String, categoryRemoteId: Int
    ): Result<Unit> {
        return try {
            val remoteData = scrapRemoteDataSource.getAllScrapsByCategoryId(categoryRemoteId)
            val remoteScraps = remoteData.scraps
            val localScraps = scrapDao.getAllScrapsByCategoryId(categoryId).first()

            val localScrapMap = localScraps.associateBy { it.remoteId }

            val toInsert = mutableListOf<ScrapEntity>()

            for (remoteScrap in remoteScraps) {
                val existingLocal = localScrapMap[remoteScrap.scrapRemoteId]

                if (existingLocal != null) {
                    if (existingLocal.remoteId != remoteScrap.scrapRemoteId) {
                        scrapDao.updateScrapRemoteId(
                            id = existingLocal.id,
                            remoteId = remoteScrap.scrapRemoteId,
                            isFavorite = remoteScrap.isFavorite,
                            status = SyncStatus.SYNCED
                        )
                    }
                } else {
                    toInsert.add(remoteScrap.toEntity(categoryId))
                }
            }

            if (toInsert.isNotEmpty()) {
                scrapDao.upsertScraps(toInsert)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncScrapById(id: String): Result<Unit> {
        return try {
            val existing =
                scrapDao.getScrapById(id) ?: return Result.failure(NoSuchElementException())
            val remoteId = existing.remoteId ?: return Result.success(Unit)

            val remoteScrap = scrapRemoteDataSource.getScrapById(remoteId)
            scrapDao.updateScrapDetails(
                id = id,
                description = remoteScrap.description,
                memo = remoteScrap.memo,
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchScraps(
        query: String,
        searchScope: List<String>,
        categoryRemoteIds: List<Int>,
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

        val remoteScraps = remoteData.scraps
        val domainItems = remoteScraps.map { remoteScrap ->
            val localScrap = scrapDao.getScrapByRemoteId(remoteScrap.scrapRemoteId)

            remoteScrap.toDomainModel(
                scrapLocalId = localScrap?.id ?: "NO_LOCAL",
                categoryLocalId = localScrap?.categoryId ?: "NO_LOCAL"
            )
        }
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

        val remoteScraps = remoteData.scraps
        val domainItems = remoteScraps.map { remoteScrap ->
            val localScrap = scrapDao.getScrapByRemoteId(remoteScrap.scrapRemoteId)
            val categoryTitle =
                categoryDao.getCategoryById(localScrap?.categoryId ?: "NO_LOCAL")?.name.orEmpty()

            remoteScrap.toDomainModel(
                scrapLocalId = localScrap?.id ?: "NO_LOCAL",
                categoryLocalId = localScrap?.categoryId ?: "NO_LOCAL",
                categoryTitle = categoryTitle,
            )
        }
        Result.success(domainItems)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun searchScrapsByCategory(
        categoryRemoteId: Long, query: String, sortType: String?, sortDirection: String?
    ): Result<List<ScrapItem>> = try {
        val remoteData = scrapRemoteDataSource.searchScrapsByCategory(
            categoryRemoteId = categoryRemoteId,
            query = query,
            sort = sortType,
            direction = sortDirection
        )

        val remoteScraps = remoteData.scraps
        val domainItems = remoteScraps.map { remoteScrap ->
            val localScrap = scrapDao.getScrapByRemoteId(remoteScrap.scrapRemoteId)

            remoteScrap.toDomainModel(
                scrapLocalId = localScrap?.id ?: "NO_LOCAL",
                categoryLocalId = localScrap?.categoryId ?: "NO_LOCAL"
            )
        }
        Result.success(domainItems)
    } catch (e: Exception) {
        Result.failure(e)
    }

    private suspend fun createScrapRemote(item: ScrapItem): CreateScrapResponse {
        val category = categoryDao.getCategoryById(item.categoryId)
        val categoryRemoteId = category?.remoteId
        if (categoryRemoteId == null) {
            throw Exception("Remote Category ID missing")
        }

        val request = CreateScrapRequest(
            scrapURL = item.url,
            imageURL = item.imageUrl,
            title = item.title,
            description = item.description,
            memo = item.memo,
            isFavorite = item.isFavorite
        )

        return scrapRemoteDataSource.createScrap(categoryRemoteId, request)
    }
}
