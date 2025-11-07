package com.scrap2025.scrap2025.repository

import com.scrap2025.scrap2025.data.local.CategoryDummyData
import com.scrap2025.scrap2025.model.CategoryItem
import com.scrap2025.scrap2025.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * CategoryRepositoryImpl - CategoryRepository 구현체
 * 더미 데이터를 MutableStateFlow로 관리하며, Result 래퍼로 에러 처리
 */
@Singleton
class CategoryRepositoryImpl @Inject constructor() : CategoryRepository {

    // 더미 데이터를 기반으로 MutableStateFlow 생성
    private val _categories = MutableStateFlow<List<CategoryItem>>(
        CategoryDummyData.dummyCategories
    )

    override fun getCategories(): Flow<Result<List<CategoryItem>>> {
        return _categories.map { items ->
            try {
                Result.Success(items)
            } catch (e: Exception) {
                Result.Error(e, "카테고리 목록 조회 실패")
            }
        }
    }

    override suspend fun getCategoryById(id: String): Result<CategoryItem> {
        return try {
            val item = _categories.value.find { it.id == id }
            if (item != null) {
                Result.Success(item)
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
            val currentItems = _categories.value.toMutableList()
            currentItems.add(item)
            _categories.value = currentItems
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "카테고리 추가 실패")
        }
    }

    override suspend fun deleteCategory(id: String): Result<Unit> {
        return try {
            // "분류되지 않음" 카테고리는 삭제 불가
            if (id == "1") {
                return Result.Error(
                    IllegalArgumentException("기본 카테고리는 삭제할 수 없습니다."),
                    "기본 카테고리는 삭제할 수 없습니다"
                )
            }

            val currentItems = _categories.value.toMutableList()
            val removed = currentItems.removeIf { it.id == id }
            if (removed) {
                _categories.value = currentItems
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
            if (id == "1") {
                return Result.Error(
                    IllegalArgumentException("기본 카테고리는 수정할 수 없습니다."),
                    "기본 카테고리는 수정할 수 없습니다"
                )
            }

            val currentItems = _categories.value.toMutableList()
            val index = currentItems.indexOfFirst { it.id == id }
            if (index != -1) {
                currentItems[index] = currentItems[index].copy(name = name)
                _categories.value = currentItems
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
}
