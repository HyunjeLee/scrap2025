package com.scrap2025.scrap2025.repository

import androidx.room.withTransaction
import com.scrap2025.scrap2025.data.local.AppDatabase
import com.scrap2025.scrap2025.data.local.dao.CategoryDao
import com.scrap2025.scrap2025.data.local.dao.ScrapDao
import com.scrap2025.scrap2025.data.local.entity.CategoryEntity
import com.scrap2025.scrap2025.data.model.SyncStatus
import com.scrap2025.scrap2025.data.remote.api.CategoryService
import com.scrap2025.scrap2025.data.remote.dto.CreateCategoryRequest
import com.scrap2025.scrap2025.data.remote.dto.CreateCategoryResponse
import com.scrap2025.scrap2025.data.remote.dto.RenameCategoryRequest
import com.scrap2025.scrap2025.data.remote.dto.RenameCategoryResponse
import com.scrap2025.scrap2025.data.remote.dto.SequenceCategoryRequest
import com.scrap2025.scrap2025.data.remote.dto.SequenceCategoryResponse
import com.scrap2025.scrap2025.model.CategoryItem
import com.scrap2025.scrap2025.model.Result
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** CategoryRepositoryImpl - CategoryRepository 구현체 Room DB(CategoryDao)를 사용하여 데이터 관리 */
@Singleton
class CategoryRepositoryImpl
@Inject
constructor(
    private val categoryDao: CategoryDao,
    private val scrapDao: ScrapDao,
    private val db: AppDatabase,
    private val categoryService: CategoryService
) : CategoryRepository {

    override fun getCategoryCount(): Flow<Int> = categoryDao.getCategoryCount()

    override fun getAllCategories(): Flow<Result<List<CategoryItem>>> {
        return categoryDao.getAllCategories().map { entities ->
            try {
                Result.Success(entities.map { it.toDomainModel() })
            } catch (e: Exception) {
                Result.Error(e, "카테고리 목록 조회 실패")
            }
        }
    }

    override suspend fun getCategoryById(id: String): Result<CategoryItem> {
        return try {
            val entity = categoryDao.getCategoryById(id)
            if (entity != null) {
                // 단일 조회 시 카운트가 필요하다면 DAO를 수정해야 하지만,
                // 현재 요구사항에서는 목록에서만 카운트가 중요하므로 0으로 처리하거나 별도 쿼리 필요.
                // 일단 0으로 매핑 (상세 화면에서 카운트가 필요한지 확인 필요하지만 지금은 안전하게 0)
                Result.Success(entity.toDomainModel())
            } else {
                Result.Error(
                    NoSuchElementException("ID가 $id 인 카테고리를 찾을 수 없습니다."),
                    "카테고리를 찾을 수 없습니다"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "카테고리 조회 실패")
        }
    }

    override suspend fun createCategory(item: CategoryItem): Result<Unit> {
        return try {
            // 1. Local Insert (PENDING)
            val entity = CategoryEntity.fromDomainModel(item)
            categoryDao.insertCategory(entity)

            // 2. Remote Sync
            val remoteResult = createCategoryRemote(item.name)
            if (remoteResult is Result.Success) {
                // 3. Trigger Sync to get the remoteId
                // Since the creation API doesn't return the ID, we must fetch the list
                // and match by name (handled in syncCategories)
                syncCategories()
            }
            // If remote fails, it stays PENDING

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "카테고리 추가 실패")
        }
    }

    override suspend fun deleteCategory(id: String): Result<Unit> {
        return try {
            val existing =
                categoryDao.getCategoryById(id)
                    ?: return Result.Error(
                        NoSuchElementException("ID가 $id 인 카테고리를 찾을 수 없습니다."),
                        "카테고리를 찾을 수 없습니다"
                    )

            // "분류되지 않음" (기본) 카테고리는 삭제 불가
            if (existing.isDefault) {
                return Result.Error(
                    IllegalArgumentException("기본 카테고리는 삭제할 수 없습니다."),
                    "기본 카테고리는 삭제할 수 없습니다"
                )
            }

            // 기본 카테고리 찾기
            val defaultCategory = categoryDao.getDefaultCategory()
            if (defaultCategory == null) {
                return Result.Error(IllegalStateException("기본 카테고리를 찾을 수 없습니다."), "기본 카테고리 오류")
            }

            db.withTransaction {
                // 1. 해당 카테고리의 모든 스크랩을 기본 카테고리로 이동
                val count = scrapDao.moveScraps(id, defaultCategory.id)
                // 2. 기본 카테고리의 스크랩 count 업데이트
                if (count != 0) {
                    categoryDao.updateScrapCount(CategoryItem.DEFAULT_ID, count)
                }
                // 3. 카테고리 삭제
                categoryDao.deleteCategory(id)
            }

            // 3. Remote Delete (if remoteId exists)
            val remoteId = existing.remoteId
            if (remoteId != null) {
                // Remote failure shouldn't rollback local deletion (Local First)
                // But we might want to log it or schedule a retry
                val remoteResult = deleteCategoryRemote(remoteId)
                if (remoteResult is Result.Error) {
                    // TODO: Handle sync failure (e.g., job scheduler)
                    // For now, we consider the operation successful if local part is done
                }
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "카테고리 삭제 실패")
        }
    }

    override suspend fun updateCategory(id: String, name: String): Result<Unit> {
        return try {
            val existing =
                categoryDao.getCategoryById(id)
                    ?: return Result.Error(
                        NoSuchElementException("ID가 $id 인 카테고리를 찾을 수 없습니다."),
                        "카테고리를 찾을 수 없습니다"
                    )

            // "분류되지 않음" (기본) 카테고리는 수정 불가
            if (existing.isDefault) {
                return Result.Error(
                    IllegalArgumentException("기본 카테고리는 수정할 수 없습니다."),
                    "기본 카테고리는 수정할 수 없습니다"
                )
            }

            // 1. Local Update
            categoryDao.updateCategoryName(id, name)

            // 2. Remote Sync (if remoteId is valid)
            val remoteId = existing.remoteId
            if (remoteId != null && remoteId != 0 && remoteId != -1) {
                // If remoteId is 0 or -1, it means it's not synced yet.
                updateCategoryRemote(remoteId, name)
                // We ignore remote failure for now, following local-first principle
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "카테고리 업데이트 실패")
        }
    }

    override suspend fun syncCategories(): Result<Unit> {
        return try {
            val response = categoryService.getCategories()
            if (response.isSuccessful) {
                val remoteCategories = response.body()?.result?.categories ?: emptyList()
                val localCategories = categoryDao.getAllCategoriesSnapshot()

                // Map local categories by name for easy lookup
                // Using Map<String, CategoryEntity>
                // Note: If multiple local categories have same name, this logic picks one. ideally
                // names should be unique.
                val localCategoryMap = localCategories.associateBy { it.name }

                val toInsert = mutableListOf<CategoryEntity>()

                for (remoteCategory in remoteCategories) {
                    val existingLocal = localCategoryMap[remoteCategory.categoryTitle]

                    if (existingLocal != null) {
                        // Match found! Update remoteId of existing local category
                        // Preserve local ID (UUID)
                        // Update remoteId and syncStatus
                        if (existingLocal.remoteId != remoteCategory.categoryRemoteId) {
                            categoryDao.updateCategoryRemoteId(
                                existingLocal.id,
                                remoteCategory.categoryRemoteId,
                                remoteCategory.scrapCount,
                                remoteCategory.orderIndex - 1,
                                SyncStatus.SYNCED
                            )
                        }
                    } else {
                        // No match -> Insert new category (Use server ID based entity from
                        // toEntity())
                        toInsert.add(remoteCategory.toEntity())
                    }
                }

                if (toInsert.isNotEmpty()) {
                    categoryDao.upsertCategories(toInsert)
                }

                Result.Success(Unit)
            } else {
                Result.Error(Exception("Sync failed code: ${response.code()}"), "카테고리 동기화 실패")
            }
        } catch (e: Exception) {
            Result.Error(e, "카테고리 동기화 중 오류 발생")
        }
    }

    override suspend fun reorderCategories(
        categoryItems: List<CategoryItem>,
    ): Result<Unit> {
        return try {
            db.withTransaction {
                // 각 아이템의 orderIndex를 리스트의 인덱스로 업데이트
                categoryItems.forEachIndexed { index, categoryItem ->
                    categoryDao.updateCategoryOrder(id = categoryItem.id, orderIndex = index)
                }
            }

            // Remote Sync

            val categoryRemoteIds = categoryItems.mapNotNull { it.remoteId }
            if (categoryRemoteIds.isNotEmpty()) {
                updateCategorySequenceRemote(categoryRemoteIds)
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "카테고리 순서 변경 실패")
        }
    }

    private suspend fun createCategoryRemote(title: String): Result<CreateCategoryResponse> {
        return try {
            val request = CreateCategoryRequest(categoryTitle = title)
            val response = categoryService.createCategory(request)
            if (response.isSuccessful) {
                val result = response.body()?.result
                if (result != null) {
                    Result.Success(result)
                } else {
                    Result.Error(Exception("Response body is null"), "카테고리 생성 응답 오류")
                }
            } else {
                Result.Error(Exception("Create failed code: ${response.code()}"), "카테고리 생성 실패")
            }
        } catch (e: Exception) {
            Result.Error(e, "카테고리 생성 중 오류 발생")
        }
    }

    private suspend fun updateCategoryRemote(
        categoryId: Int,
        newTitle: String
    ): Result<RenameCategoryResponse> {
        return try {
            val request = RenameCategoryRequest(newCategoryTitle = newTitle)
            val response = categoryService.renameCategory(categoryId, request)
            if (response.isSuccessful) {
                val result = response.body()?.result
                if (result != null) {
                    Result.Success(result)
                } else {
                    Result.Error(Exception("Response body is null"), "카테고리 이름 수정 응답 오류")
                }
            } else {
                Result.Error(Exception("Rename failed code: ${response.code()}"), "카테고리 이름 수정 실패")
            }
        } catch (e: Exception) {
            Result.Error(e, "카테고리 이름 수정 중 오류 발생")
        }
    }

    private suspend fun deleteCategoryRemote(categoryId: Int): Result<Unit> {
        return try {
            val response = categoryService.deleteCategory(categoryId)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Delete failed code: ${response.code()}"), "카테고리 삭제 실패")
            }
        } catch (e: Exception) {
            Result.Error(e, "카테고리 삭제 중 오류 발생")
        }
    }

    private suspend fun updateCategorySequenceRemote(
        categoryRemoteIds: List<Int>
    ): Result<SequenceCategoryResponse> {
        return try {
            val request = SequenceCategoryRequest(categoryIdList = categoryRemoteIds)
            val response = categoryService.updateCategorySequence(request)
            if (response.isSuccessful) {
                val result = response.body()?.result
                if (result != null) {
                    Result.Success(result)
                } else {
                    Result.Error(Exception("Response body is null"), "카테고리 순서 변경 응답 오류")
                }
            } else {
                Result.Error(Exception("Reorder failed code: ${response.code()}"), "카테고리 순서 변경 실패")
            }
        } catch (e: Exception) {
            Result.Error(e, "카테고리 순서 변경 중 오류 발생")
        }
    }
}
