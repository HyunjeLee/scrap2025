package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrap2025.scrap2025.model.CategoryItem
import com.scrap2025.scrap2025.repository.CategoryRepository
import com.scrap2025.scrap2025.utils.move
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface CategoryUiState {
    data object Loading : CategoryUiState

    data class Success(val categories: List<CategoryItem>) : CategoryUiState

    data class Error(val message: String? = null) : CategoryUiState
}

/** CategoryViewModel - 카테고리 목록 화면의 상태 관리 CategoryRepository를 통해 카테고리 목록을 조회하고 관리 */
@HiltViewModel
class CategoryViewModel
@Inject
constructor(private val categoryRepository: CategoryRepository) :
    ViewModel() {
    // 드래그 중인 로컬 순서를 담을 변수 (null이면 서버 데이터를 따름)
    private val localCategoriesState = MutableStateFlow<List<CategoryItem>?>(null)

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val categoryUiState: StateFlow<CategoryUiState> =
        combine(
            categoryRepository.allCategories,
            localCategoriesState,
            categoryRepository.refreshEvent
        ) { remoteResult, localItems, _ ->
            remoteResult.fold(
                onSuccess = { categories ->
                    // 로컬 데이터(드래그 중인 순서)가 있으면 그걸 우선해서 보여줌
                    CategoryUiState.Success(localItems ?: categories)
                },
                onFailure = { CategoryUiState.Error(it.message) }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CategoryUiState.Loading // 처음엔 로딩
        )

    init {
        fetchCategories()
    }

    private fun fetchCategories() {
        viewModelScope.launch { categoryRepository.refreshCategories() }
    }

    /** 당겨서 새로고침 */
    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true

            try {
                coroutineScope {
                    launch { categoryRepository.refreshCategories() }
                    delay(1000L)
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    /**
     * 카테고리 순서 변경 (UI 반영 전용) **드래그 중 실시간**으로 리스트의 위치를 바꿉니다.
     *
     * @param fromIndex 원래 위치
     * @param toIndex 새로운 위치
     */
    fun moveCategory(fromIndex: Int, toIndex: Int) {
        val currentState = categoryUiState.value
        if (currentState !is CategoryUiState.Success) return

        // 현재 리스트(로컬에 이미 있으면 로컬, 없으면 서버 데이터)를 가져와서 순서 변경
        val updatedList = currentState.categories.move(fromIndex, toIndex)

        // 로컬 상태 업데이트 -> UI가 즉시 반응함 (낙관적 업데이트)
        localCategoriesState.value = updatedList
    }

    /** 카테고리 순서 확정 및 저장 드래그가 끝난 시점(Drop)에 호출하여 현재 리스트의 순서를 영구적으로 저장합니다. */
    fun updateCategoryOrder() {
        val currentState = localCategoriesState.value ?: return // 변경된 게 없으면 종료
        val updatedCategoryIds = currentState.map { it.id }

        viewModelScope.launch {
            val result = categoryRepository.reorderCategories(updatedCategoryIds)
            result.onSuccess {
                // 저장 성공 시 로컬 오버라이드를 비워서 다시 서버 데이터를 바라보게 함
                localCategoriesState.value = null
            }
        }
    }
}
