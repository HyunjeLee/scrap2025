package com.scrap2025.scrap2025.repository

import androidx.room.withTransaction
import com.scrap2025.scrap2025.data.local.AppDatabase
import com.scrap2025.scrap2025.data.local.dao.CategoryDao
import com.scrap2025.scrap2025.data.local.dao.ScrapDao
import com.scrap2025.scrap2025.data.local.entity.ScrapEntity
import com.scrap2025.scrap2025.data.model.ScrapCreateRequest
import com.scrap2025.scrap2025.data.model.ScrapCreateResult
import com.scrap2025.scrap2025.data.model.ScrapMemoDto
import com.scrap2025.scrap2025.data.model.ScrapMoveDto
import com.scrap2025.scrap2025.data.model.SearchRequest
import com.scrap2025.scrap2025.data.model.SyncStatus
import com.scrap2025.scrap2025.data.remote.AuthService
import com.scrap2025.scrap2025.data.remote.dto.FavoriteBulkDTO
import com.scrap2025.scrap2025.data.remote.dto.ScrapBulkRequest
import com.scrap2025.scrap2025.model.Result
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
@Inject
constructor(
    private val appDatabase: AppDatabase,
    private val scrapDao: ScrapDao,
    private val categoryDao: CategoryDao,
    private val authService: AuthService,
) : ScrapRepository {

    override fun getScrapItems(categoryId: String?): Flow<Result<List<ScrapItem>>> {
        val flow =
            if (categoryId != null) {
                scrapDao.getAllScrapsByCategoryId(categoryId)
            } else {
                scrapDao.getAllScraps()
            }

        return flow.map { entities ->
            try {
                Result.Success(entities.map { it.toDomainModel() })
            } catch (e: Exception) {
                Result.Error(e, "스크랩 목록 조회 실패")
            }
        }
    }

    override fun getFavoriteScrapItemsFromRemote(): Flow<Result<List<ScrapItem>>> = flow {
        emit(Result.Loading)

        try {
            val response = authService.getFavoriteScraps()

            if (response.isSuccessful) {
                val remoteScraps = response.body()?.result?.scraps ?: emptyList()

                val domainItems = remoteScraps.map { it.toEntity("NO_LOCAL").toDomainModel() }

                emit(Result.Success(domainItems))
            } else {
                Result.Error(Exception("error code: ${response.code()}"), "즐겨찾기 조회 실패")
            }
        } catch (e: Exception) {
            emit(Result.Error(e, "네트워크 오류 발생 (즐겨찾기 조회)"))
        }
    }

    override fun getScrapItemByIdAsFlow(id: String): Flow<Result<ScrapItem>> {
        return scrapDao.getScrapByIdFlow(id)
            .map { entity ->
                if (entity != null) {
                    Result.Success(entity.toDomainModel())
                } else {
                    Result.Error(NoSuchElementException(), "스크랩을 찾을 수 없습니다")
                }
            }
            .catch { e -> // 예외 처리도 Flow 흐름 안에서 처리
                emit(Result.Error(e, "스크랩 연동 오류"))
            }
    }

    override suspend fun createScrap(item: ScrapItem): Result<Unit> {
        return try {
            appDatabase.withTransaction {
                // 1. Local Insert (PENDING)
                scrapDao.insertScrap(ScrapEntity.fromDomainModel(item))
                // 카테고리 카운트 증가
                categoryDao.incrementScrapCount(item.categoryId)
            }

            // 2. Remote Sync (if token exists)
            val remoteResult = createScrapRemote(item)
            if (remoteResult is Result.Success) {
                val category = categoryDao.getCategoryById(item.categoryId)
                val categoryRemoteId = category?.remoteId

                // 3. Trigger Sync to get the remoteId
                // Since the creation API doesn't return the ID, we must fetch the list
                // and match by name (handled in syncScrapsByCategoryId)
                if (categoryRemoteId != null) {
                    syncScrapsByCategoryId(
                        categoryId = item.categoryId,
                        categoryRemoteId = categoryRemoteId
                    )
                }
            }
            // If remote fails, it stays PENDING

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "스크랩 추가 실패")
        }
    }

    override suspend fun deleteScrapItem(id: String): Result<Unit> {
        return try {
            val existing = scrapDao.getScrapById(id)
            if (existing != null) {
                appDatabase.withTransaction {
                    scrapDao.deleteScrap(id)
                    categoryDao.decrementScrapCount(existing.categoryId) // 카테고리 카운트 감소
                }

                authService.deleteScrap(existing.remoteId!!.toLong())

                Result.Success(Unit)
            } else {
                Result.Error(NoSuchElementException("ID가 $id 인 스크랩을 찾을 수 없습니다."), "스크랩을 찾을 수 없습니다")
            }
        } catch (e: Exception) {
            Result.Error(e, "스크랩 삭제 실패")
        }
    }

    override suspend fun deleteScrapBulk(idBulk: List<Long>): Result<Unit> {
        return try {
            authService.deleteScrapBulk(idBulk)

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "다중 스크랩 삭제 실패")
        }
    }

    override suspend fun updateScrapItem(id: String, memo: String?): Result<Unit> {
        return try {
            val existing = scrapDao.getScrapById(id)
            if (existing != null) {
                val remoteId = existing.remoteId

                scrapDao.updateScrapMemo(id, memo)
                editMemo(remoteId!!, memo!!)

                Result.Success(Unit)
            } else {
                Result.Error(NoSuchElementException("ID가 $id 인 스크랩을 찾을 수 없습니다."), "스크랩을 찾을 수 없습니다")
            }
        } catch (e: Exception) {
            Result.Error(e, "스크랩 업데이트 실패")
        }
    }

    override suspend fun moveScrapItem(scrapId: String, categoryId: String): Result<Unit> {
        return try {
            val existing = scrapDao.getScrapById(scrapId)
            if (existing != null) {
                val categoryRemoteId = categoryDao.getCategoryById(categoryId)!!.remoteId!!

                appDatabase.withTransaction {
                    // 이전 카테고리 카운트 감소
                    categoryDao.decrementScrapCount(existing.categoryId)
                    // 카테고리 이동
                    scrapDao.moveScrap(scrapId, categoryId)
                    // 이동한 카테고리 카운트 증가
                    categoryDao.incrementScrapCount(categoryId)
                }

                authService.moveScrap(
                    existing.remoteId!!.toLong(),
                    ScrapMoveDto(categoryRemoteId.toLong())
                )

                Result.Success(Unit)
            } else {
                Result.Error(
                    NoSuchElementException("ID가 $scrapId 인 스크랩을 찾을 수 없습니다."),
                    "스크랩을 찾을 수 없습니다"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "스크랩 이동 실패")
        }
    }

    override suspend fun toggleFavorite(scrapId: String): Result<Unit> {
//        return try {
//            val existing = scrapDao.getScrapById(scrapId)
//            if (existing != null) {
//                scrapDao.updateIsFavorite(scrapId, !existing.isFavorite)
//                Result.Success(Unit)
//            } else {
//                Result.Error(
//                    NoSuchElementException("ID가 $scrapId 인 스크랩을 찾을 수 없습니다."),
//                    "스크랩을 찾을 수 없습니다"
//                )
//            }
//        } catch (e: Exception) {
//            Result.Error(e, "즐겨찾기 토글 실패")
//        }
        try {
            val scrapRemoteId = scrapDao.getScrapById(scrapId)?.remoteId
            val response = authService.updateScrapFavorite(scrapRemoteId!!.toLong())

            return if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Toggle Favorite failed code: ${response.code()}"), "즐겨찾기 토글 실패")
            }
        } catch (e: Exception) {
            return Result.Error(e, "즐겨찾기 토글 실패")
        }
    }

    override suspend fun toggleFavoriteBulk(scrapIdBulk: List<String>): Result<Unit> {
        try {
            val scrapRemoteIdBulk = scrapIdBulk.map { scrapId -> scrapDao.getScrapById(scrapId)?.remoteId!!.toLong() }
            val response = authService.updateScrapBulkFavorite(FavoriteBulkDTO(scrapIdList = scrapRemoteIdBulk))

            return if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Toggle Favorite Bulk failed code: ${response.code()}"), "즐겨찾기 목록 토글 실패")
            }

        } catch (e: Exception) {
            return Result.Error(e, "즐겨찾기 목록 토글 실패")
        }
    }

    override suspend fun moveScrapsToCategory(
        fromId: String,
        toId: String
    ): Result<Unit> { // todo: 실제 스크랩 ID 리스트로 인자 추가
        return try {
            val movedCount = scrapDao.moveScraps(fromId, toId)
            if (movedCount > 0) {
                categoryDao.updateScrapCount(fromId, -movedCount)
                categoryDao.updateScrapCount(toId, movedCount)
            }

            authService.moveScrapBulk(
                ScrapBulkRequest(
                    scrapIds = listOf(), // todo: 실제 스크랩 ID 리스트로 대체
                    moveCategoryId = categoryDao.getCategoryById(toId)!!.remoteId!!.toLong()
                )
            )

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "스크랩 이동 실패")
        }
    }

    override fun getScrapCount(): Flow<Int> = scrapDao.getScrapCount()

    override suspend fun syncScrapsByCategoryId(
        categoryId: String,
        categoryRemoteId: Int
    ): Result<Unit> {
        return try {
            val response = authService.getAllScrapsByCategoryId(categoryRemoteId)
            if (response.isSuccessful) {
                val remoteScraps = response.body()?.result?.scraps ?: emptyList()
                val localScraps = scrapDao.getAllScrapsByCategoryId(categoryId).first()

                val localScrapMap = localScraps.associateBy { it.title }

                val toInsert = mutableListOf<ScrapEntity>()

                for (remoteScrap in remoteScraps) {
                    val existingLocal = localScrapMap[remoteScrap.title]

                    if (existingLocal != null) {
                        // Match found! Update remoteId of existing local scrap
                        // Preserve local ID (UUID)
                        // Update remoteId and syncStatus
                        if (existingLocal.remoteId != remoteScrap.id) {
                            scrapDao.updateScrapRemoteId(
                                existingLocal.id,
                                remoteScrap.id,
                                SyncStatus.SYNCED
                            )
                        }
                    } else {
                        // No match -> Insert new Scrap (Use server ID based entity from
                        // toEntity())
                        toInsert.add(remoteScrap.toEntity(categoryId))
                    }
                }

                if (toInsert.isNotEmpty()) {
                    scrapDao.upsertScraps(toInsert)
                }

                Result.Success(Unit)
            } else {
                Result.Error(Exception("Sync failed code: ${response.code()}"), "스크랩 동기화 실패")
            }
        } catch (e: Exception) {
            Result.Error(e, "스크랩 동기화 중 오류 발생")
        }
    }

    override suspend fun syncScrapById(id: String): Result<Unit> {
        return try {
            val existing =
                scrapDao.getScrapById(id)
                    ?: return Result.Error(NoSuchElementException(), "스크랩을 찾을 수 없습니다")
            val remoteId = existing.remoteId ?: return Result.Success(Unit)

            val response = authService.getScrapById(remoteId)
            if (response.isSuccessful) {
                val remoteScrap =
                    response.body()?.result
                        ?: return Result.Error(Exception("Body is null"), "응답 데이터 오류")

                scrapDao.updateScrapDetails(
                    id = id,
                    description = remoteScrap.description,
                    memo = remoteScrap.memo,
                )
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Fetch failed code: ${response.code()}"), "스크랩 최신화 실패")
            }
        } catch (e: Exception) {
            Result.Error(e, "스크랩 동기화 중 오류 발생")
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
    ): Result<List<ScrapItem>> =
        try {
            val request = SearchRequest(searchScope, categoryRemoteIds, startDate, endDate)

            val response = authService.searchScraps(
                query = query,
                sort = sortType,
                direction = sortDirection,
                page = page,
                size = size,
                body = request
            )

            if (response.isSuccessful) {
                val remoteScraps = response.body()?.result?.scraps ?: emptyList()
                // 도메인 모델로 변환 (categoryId가 서버에서 이름으로 오므로 적절한 처리 필요)
                val domainItems = remoteScraps.map { it.toDomainModel() }
                Result.Success(domainItems)
            } else {
                Result.Error(Exception("Search failed"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }

    private suspend fun createScrapRemote(item: ScrapItem): Result<ScrapCreateResult> {
        return try {
            // 1. 카테고리 정보 가져오기
            val category = categoryDao.getCategoryById(item.categoryId)
            val categoryRemoteId = category?.remoteId
            // 2. 서버 연동에 필수인 remoteId가 없다면 실패 처리 -> 상위에서 PENDING 처리
            if (categoryRemoteId == null) {
                return Result.Error(
                    Exception("Remote Category ID missing"),
                    "해당 카테고리의 서버 정보를 찾을 수 없습니다."
                )
            }

            val request =
                ScrapCreateRequest(
                    scrapURL = item.url,
                    imageURL = item.imageUrl,
                    title = item.title,
                    description = item.description,
                    memo = item.memo,
                    isFavorite = item.isFavorite
                )

            // 3. remoteId가 확실히 있을 때만 호출
            val response = authService.createScrap(categoryRemoteId, request)
            if (response.isSuccessful) {
                val result = response.body()?.result
                if (result != null) {
                    Result.Success(result)
                } else {
                    Result.Error(Exception("Response body is null"), "스크랩 생성 응답 오류")
                }
            } else {
                Result.Error(Exception("Create failed code: ${response.code()}"), "스크랩 생성 실패")
            }
        } catch (e: Exception) {
            Result.Error(e, "스크랩 생성 중 오류 발생")
        }
    }

    private suspend fun editMemo(remoteId: Int, memo: String): Result<ScrapMemoDto> {
        return try {
            val request = ScrapMemoDto(memo = memo)

            val response = authService.updateScrapMemo(remoteId, request)
            if (response.isSuccessful) {
                val result = response.body()?.result
                if (result != null) {
                    Result.Success(result)
                } else {
                    Result.Error(Exception("Response body is null"), "메모 수정 응답 오류")
                }
            } else {
                Result.Error(Exception("Edit Memo failed code: ${response.code()}"), "메모 수정 실패")
            }
        } catch (e: Exception) {
            Result.Error(e, "메모 수정 중 오류 발생")
        }
    }
}
