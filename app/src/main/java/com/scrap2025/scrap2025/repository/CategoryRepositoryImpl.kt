package com.scrap2025.scrap2025.repository

import com.scrap2025.scrap2025.data.remote.datasource.CategoryRemoteDataSource
import com.scrap2025.scrap2025.model.CategoryItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/** CategoryRepositoryImpl - CategoryRepository 구현체 Room DB(CategoryDao)를 사용하여 데이터 관리 */
@Singleton
class CategoryRepositoryImpl
@Inject
constructor(
    private val categoryRemoteDataSource: CategoryRemoteDataSource
) : CategoryRepository {
    private val _refreshEvent = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }
    override val refreshEvent: SharedFlow<Unit> = _refreshEvent.asSharedFlow()

    override var defaultCategory: CategoryItem? = null
        private set  // set은 외부에서 할 수 없도록

    private val _allCategories =
        MutableStateFlow<Result<List<CategoryItem>>>(Result.success(emptyList()))
    override val allCategories: StateFlow<Result<List<CategoryItem>>> = _allCategories.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    override val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId.asStateFlow()

    private val _selectedCategoryTitle = MutableStateFlow<String?>(null)
    override val selectedCategoryTitle: StateFlow<String?> = _selectedCategoryTitle.asStateFlow()

    override suspend fun refreshCategories() {
        try {
            val response = categoryRemoteDataSource.getCategories()
            val categories = response.categories.map { it.toDomainModel() }
            _allCategories.value = Result.success(categories)

            if (defaultCategory == null) {
                defaultCategory = categories.find { it.isDefault }
            }

            // 초기 선택값이 없는 경우 기본 카테고리로 설정
            val currentDefault = defaultCategory
            if (_selectedCategoryId.value == null && currentDefault != null) {
                _selectedCategoryId.value = currentDefault.id
                _selectedCategoryTitle.value = currentDefault.title
            }
        } catch (e: Exception) {
            _allCategories.value = Result.failure(e)
        }
    }

    override fun setGlobalCategory(id: Long, title: String) {
        _selectedCategoryId.value = id
        _selectedCategoryTitle.value = title
    }

    override suspend fun createCategory(title: String): Result<Unit> {
        return try {
            categoryRemoteDataSource.createCategory(title)

            _refreshEvent.emit(Unit)
            refreshCategories()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCategory(id: Long): Result<Unit> {
        return try {
            categoryRemoteDataSource.deleteCategory(id)

            _refreshEvent.emit(Unit)
            refreshCategories()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCategory(id: Long, newTitle: String): Result<Unit> {
        return try {
            categoryRemoteDataSource.renameCategory(id, newTitle)

            refreshCategories()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun reorderCategories(
        ids: List<Long>,
    ): Result<Unit> {
        return try {
            categoryRemoteDataSource.updateCategorySequence(ids)

            refreshCategories()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
