package com.scrap2025.scrap2025.repository

import androidx.room.withTransaction
import com.scrap2025.scrap2025.data.local.AppDatabase
import com.scrap2025.scrap2025.data.local.dao.CategoryDao
import com.scrap2025.scrap2025.data.local.dao.ScrapDao
import com.scrap2025.scrap2025.data.local.entity.CategoryEntity
import com.scrap2025.scrap2025.data.model.SyncStatus
import com.scrap2025.scrap2025.data.remote.datasource.CategoryRemoteDataSource
import com.scrap2025.scrap2025.model.CategoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/** CategoryRepositoryImpl - CategoryRepository 구현체 Room DB(CategoryDao)를 사용하여 데이터 관리 */
@Singleton
class CategoryRepositoryImpl
@Inject
constructor(
    private val categoryDao: CategoryDao,
    private val scrapDao: ScrapDao,
    private val db: AppDatabase,
    private val categoryRemoteDataSource: CategoryRemoteDataSource
) : CategoryRepository {

    override fun getCategoryCount(): Flow<Int> = categoryDao.getCategoryCount()

    override fun getAllCategories(): Flow<Result<List<CategoryItem>>> {
        return categoryDao.getAllCategories().map { entities ->
            try {
                Result.success(entities.map { it.toDomainModel() })
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getCategoryById(id: String): Result<CategoryItem> {
        return try {
            val entity = categoryDao.getCategoryById(id)
            if (entity != null) {
                Result.success(entity.toDomainModel())
            } else {
                Result.failure(NoSuchElementException("ID가 $id 인 카테고리를 찾을 수 없습니다."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createCategory(item: CategoryItem): Result<Unit> {
        return try {
            // 1. Local Insert (PENDING)
            val entity = CategoryEntity.fromDomainModel(item)
            categoryDao.insertCategory(entity)

            // 2. Remote Sync
            try {
                categoryRemoteDataSource.createCategory(item.name)
                // 3. Trigger Sync to get the remoteId
                syncCategories()
            } catch (e: Exception) {
                // If remote fails, it stays PENDING
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCategory(id: String): Result<Unit> {
        return try {
            val existing =
                categoryDao.getCategoryById(id)
                    ?: return Result.failure(
                        NoSuchElementException("ID가 $id 인 카테고리를 찾을 수 없습니다.")
                    )

            // "분류되지 않음" (기본) 카테고리는 삭제 불가
            if (existing.isDefault) {
                return Result.failure(IllegalArgumentException("기본 카테고리는 삭제할 수 없습니다."))
            }

            // 기본 카테고리 찾기
            val defaultCategory = categoryDao.getDefaultCategory()
            if (defaultCategory == null) {
                return Result.failure(IllegalStateException("기본 카테고리를 찾을 수 없습니다."))
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
                try {
                    categoryRemoteDataSource.deleteCategory(remoteId)
                } catch (e: Exception) {
                    // TODO: Handle sync failure (e.g., job scheduler)
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCategory(id: String, name: String): Result<Unit> {
        return try {
            val existing =
                categoryDao.getCategoryById(id)
                    ?: return Result.failure(
                        NoSuchElementException("ID가 $id 인 카테고리를 찾을 수 없습니다.")
                    )

            // "분류되지 않음" (기본) 카테고리는 수정 불가
            if (existing.isDefault) {
                return Result.failure(IllegalArgumentException("기본 카테고리는 수정할 수 없습니다."))
            }

            // 1. Local Update
            categoryDao.updateCategoryName(id, name)

            // 2. Remote Sync (if remoteId is valid)
            val remoteId = existing.remoteId
            if (remoteId != null && remoteId != 0 && remoteId != -1) {
                try {
                    categoryRemoteDataSource.renameCategory(remoteId, name)
                } catch (e: Exception) {
                    // We ignore remote failure for now, following local-first principle
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncCategories(): Result<Unit> {
        return try {
            val remoteData = categoryRemoteDataSource.getCategories()
            val remoteCategories = remoteData.categories
            val localCategories = categoryDao.getAllCategoriesSnapshot()

            val localCategoryMap = localCategories.associateBy { it.name }
            val toInsert = mutableListOf<CategoryEntity>()

            for (remoteCategory in remoteCategories) {
                val existingLocal = localCategoryMap[remoteCategory.categoryTitle]

                if (existingLocal != null) {
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
                    toInsert.add(remoteCategory.toEntity())
                }
            }

            if (toInsert.isNotEmpty()) {
                categoryDao.upsertCategories(toInsert)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun reorderCategories(
        categoryItems: List<CategoryItem>,
    ): Result<Unit> {
        return try {
            db.withTransaction {
                categoryItems.forEachIndexed { index, categoryItem ->
                    categoryDao.updateCategoryOrder(id = categoryItem.id, orderIndex = index)
                }
            }

            val categoryRemoteIds = categoryItems.mapNotNull { it.remoteId }
            if (categoryRemoteIds.isNotEmpty()) {
                try {
                    categoryRemoteDataSource.updateCategorySequence(categoryRemoteIds)
                } catch (e: Exception) {
                    // Ignore remote failure
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
