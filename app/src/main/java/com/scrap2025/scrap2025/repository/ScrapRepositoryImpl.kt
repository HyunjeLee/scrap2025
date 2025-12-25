package com.scrap2025.scrap2025.repository

import androidx.room.withTransaction
import com.scrap2025.scrap2025.data.local.AppDatabase
import com.scrap2025.scrap2025.data.local.dao.CategoryDao
import com.scrap2025.scrap2025.data.local.dao.ScrapDao
import com.scrap2025.scrap2025.data.local.entity.ScrapEntity
import com.scrap2025.scrap2025.data.model.ScrapCreateRequest
import com.scrap2025.scrap2025.data.model.ScrapCreateResult
import com.scrap2025.scrap2025.data.model.SyncStatus
import com.scrap2025.scrap2025.data.remote.AuthService
import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.model.ScrapItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
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
    private val authService: AuthService
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

    override suspend fun getScrapItemById(id: String): Result<ScrapItem> {
        return try {
            val entity = scrapDao.getScrapById(id)
            if (entity != null) {
                Result.Success(entity.toDomainModel())
            } else {
                Result.Error(NoSuchElementException("ID가 $id 인 스크랩을 찾을 수 없습니다."), "스크랩을 찾을 수 없습니다")
            }
        } catch (e: Exception) {
            Result.Error(e, "스크랩 조회 실패")
        }
    }

    override fun getScrapItemByIdAsFlow(id: String): Flow<Result<ScrapItem>> {
        return scrapDao.getScrapByIdFlow(id).map { entity ->
            if (entity != null) {
                Result.Success(entity.toDomainModel())
            } else {
                Result.Error(NoSuchElementException(), "스크랩을 찾을 수 없습니다")
            }
        }.catch { e -> // 예외 처리도 Flow 흐름 안에서 처리
            emit(Result.Error(e, "스크랩 연동 오류"))
        }
    }

    override suspend fun createScrap(item: ScrapItem, token: String?): Result<Unit> {
        return try {
            appDatabase.withTransaction {
                // 1. Local Insert (PENDING)
                scrapDao.insertScrap(ScrapEntity.fromDomainModel(item))
                // 카테고리 카운트 증가
                categoryDao.incrementScrapCount(item.categoryId)
            }

            // 2. Remote Sync (if token exists)
            if (token != null) {
                val remoteResult = createScrapRemote(token, item)
                if (remoteResult is Result.Success) {
                    val category = categoryDao.getCategoryById(item.categoryId)
                    val categoryRemoteId = category?.remoteId

                    // 3. Trigger Sync to get the remoteId
                    // Since the creation API doesn't return the ID, we must fetch the list
                    // and match by name (handled in syncScrapsByCategoryId)
                    if (categoryRemoteId != null) {
                        syncScrapsByCategoryId(
                            token = token,
                            categoryId = item.categoryId,
                            categoryRemoteId = categoryRemoteId
                        )
                    }

                }
                // If remote fails, it stays PENDING
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "스크랩 추가 실패")
        }
    }

    override suspend fun deleteScrapItem(id: String): Result<Unit> {
        return try {
            val existing = scrapDao.getScrapById(id)
            if (existing != null) {
                scrapDao.deleteScrap(id)
                // 카테고리 카운트 감소
                categoryDao.decrementScrapCount(existing.categoryId)

                Result.Success(Unit)
            } else {
                Result.Error(NoSuchElementException("ID가 $id 인 스크랩을 찾을 수 없습니다."), "스크랩을 찾을 수 없습니다")
            }
        } catch (e: Exception) {
            Result.Error(e, "스크랩 삭제 실패")
        }
    }

    override suspend fun updateScrapItem(id: String, memo: String?): Result<Unit> {
        return try {
            val existing = scrapDao.getScrapById(id)
            if (existing != null) {
                scrapDao.updateScrapMemo(id, memo)
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
                scrapDao.moveScrap(scrapId, categoryId)
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
        return try {
            val existing = scrapDao.getScrapById(scrapId)
            if (existing != null) {
                scrapDao.updateIsFavorite(scrapId, !existing.isFavorite)
                Result.Success(Unit)
            } else {
                Result.Error(
                    NoSuchElementException("ID가 $scrapId 인 스크랩을 찾을 수 없습니다."),
                    "스크랩을 찾을 수 없습니다"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "즐겨찾기 토글 실패")
        }
    }

    override suspend fun moveScrapsToCategory(fromId: String, toId: String): Result<Unit> {
        return try {
            val movedCount = scrapDao.moveScraps(fromId, toId)
            if (movedCount > 0) {
                categoryDao.updateScrapCount(fromId, -movedCount)
                categoryDao.updateScrapCount(toId, movedCount)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "스크랩 이동 실패")
        }
    }

    override fun getScrapCount(): Flow<Int> = scrapDao.getScrapCount()

    override suspend fun syncScrapsByCategoryId(token: String, categoryId: String, categoryRemoteId: Int): Result<Unit> {
        return try {
            val response = authService.getAllScrapsByCategoryId(token, categoryRemoteId)
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

    private suspend fun createScrapRemote(
        token: String,
        item: ScrapItem
    ): Result<ScrapCreateResult> {
        return try {
            // 1. 카테고리 정보 가져오기
            val category = categoryDao.getCategoryById(item.categoryId)
            val categoryRemoteId = category?.remoteId
            // 2. 서버 연동에 필수인 remoteId가 없다면 실패 처리 -> 상위에서 PENDING 처리
            if (categoryRemoteId == null) {
                return Result.Error(Exception("Remote Category ID missing"), "해당 카테고리의 서버 정보를 찾을 수 없습니다.")
            }

            val request = ScrapCreateRequest(
                scrapURL = item.url,
                imageURL = item.imageUrl,
                title = item.title,
                description = item.description,
                memo = item.memo,
                isFavorite = item.isFavorite
            )

            // 3. remoteId가 확실히 있을 때만 호출
            val response = authService.createScrap(token, categoryRemoteId, request)
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
}
