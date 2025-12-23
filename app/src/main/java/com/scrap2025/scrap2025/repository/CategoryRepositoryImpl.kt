package com.scrap2025.scrap2025.repository

import androidx.room.withTransaction
import com.scrap2025.scrap2025.data.local.AppDatabase
import com.scrap2025.scrap2025.data.local.dao.CategoryDao
import com.scrap2025.scrap2025.data.local.dao.ScrapDao
import com.scrap2025.scrap2025.data.local.entity.CategoryEntity
import com.scrap2025.scrap2025.data.model.CategoryCreateRequest
import com.scrap2025.scrap2025.data.model.CategoryCreateResult
import com.scrap2025.scrap2025.data.remote.AuthService
import com.scrap2025.scrap2025.model.CategoryItem
import com.scrap2025.scrap2025.model.Result
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
    private val authService: AuthService
) : CategoryRepository {

    override fun getCategories(): Flow<Result<List<CategoryItem>>> {
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

    override suspend fun addCategory(item: CategoryItem): Result<Unit> {
        return try {
            categoryDao.insertCategory(CategoryEntity.fromDomainModel(item))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "카테고리 추가 실패")
        }
    }

    override suspend fun deleteCategory(id: String): Result<Unit> {
        return try {
            // "분류되지 않음" 카테고리는 삭제 불가
            if (id == CategoryItem.DEFAULT_CATEGORY_ID) {
                return Result.Error(
                    IllegalArgumentException("기본 카테고리는 삭제할 수 없습니다."),
                    "기본 카테고리는 삭제할 수 없습니다"
                )
            }

            // 존재하는지 확인 후 삭제
            val existing = categoryDao.getCategoryById(id)
            if (existing != null) {
                db.withTransaction {
                    // 1. 해당 카테고리의 모든 스크랩을 기본 카테고리로 이동
                    scrapDao.moveScraps(id, CategoryItem.DEFAULT_CATEGORY_ID)
                    // 2. 카테고리 삭제
                    categoryDao.deleteCategory(id)
                }
                Result.Success(Unit)
            } else {
                Result.Error(
                    NoSuchElementException("ID가 $id 인 카테고리를 찾을 수 없습니다."),
                    "카테고리를 찾을 수 없습니다"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "카테고리 삭제 실패")
        }
    }

    override suspend fun updateCategory(id: String, name: String): Result<Unit> {
        return try {
            // "분류되지 않음" 카테고리는 이름 변경 불가
            if (id == CategoryItem.DEFAULT_CATEGORY_ID) {
                return Result.Error(
                    IllegalArgumentException("기본 카테고리는 수정할 수 없습니다."),
                    "기본 카테고리는 수정할 수 없습니다"
                )
            }

            val existing = categoryDao.getCategoryById(id)
            if (existing != null) {
                categoryDao.updateCategoryName(id, name)
                Result.Success(Unit)
            } else {
                Result.Error(
                    NoSuchElementException("ID가 $id 인 카테고리를 찾을 수 없습니다."),
                    "카테고리를 찾을 수 없습니다"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "카테고리 업데이트 실패")
        }
    }

    override fun getCategoryCount(): Flow<Int> = categoryDao.getCategoryCount()

    override suspend fun syncCategories(token: String): Result<Unit> {
        return try {
            val response = authService.getCategories(token)
            if (response.isSuccessful) {
                response.body()?.result?.let { categoryListResponse ->
                    val entities = categoryListResponse.categories.map { it.toEntity() }
                    categoryDao.upsertCategories(entities)
                }
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Sync failed code: ${response.code()}"), "카테고리 동기화 실패")
            }
        } catch (e: Exception) {
            Result.Error(e, "카테고리 동기화 중 오류 발생")
        }
    }

    override suspend fun createCategoryRemote(
        token: String,
        title: String
    ): Result<CategoryCreateResult> {
        return try {
            val request = CategoryCreateRequest(categoryTitle = title)
            val response = authService.createCategory(token, request)
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
}
