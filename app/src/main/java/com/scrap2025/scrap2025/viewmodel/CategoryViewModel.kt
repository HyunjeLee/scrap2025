package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrap2025.scrap2025.data.local.TokenManager
import com.scrap2025.scrap2025.model.CategoryItem
import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** CategoryViewModel - 카테고리 목록 화면의 상태 관리 CategoryRepository를 통해 카테고리 목록을 조회하고 관리 */
@HiltViewModel
class CategoryViewModel
@Inject
constructor(
        private val categoryRepository: CategoryRepository,
        private val tokenManager: TokenManager
) : ViewModel() {

    private val _categoryUiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val categoryUiState: StateFlow<CategoryUiState> = _categoryUiState.asStateFlow()

    init {
        fetchCategories()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            _categoryUiState.value = CategoryUiState.Loading
            categoryRepository.getAllCategories().collect { result ->
                _categoryUiState.update {
                    when (result) {
                        is Result.Loading -> CategoryUiState.Loading
                        is Result.Success -> CategoryUiState.Success(result.data)
                        is Result.Error -> CategoryUiState.Error(result.message)
                    }
                }
            }
        }

        // Trigger Sync
        viewModelScope.launch {
            val token = tokenManager.accessToken.firstOrNull()
            if (!token.isNullOrBlank()) {
                categoryRepository.syncCategories(token)
            }
        }
    }

    /**
     * 카테고리 순서 변경 (UI 반영 전용)
     * 드래그 중 실시간으로 리스트의 위치를 바꿉니다.
     * @param fromIndex 원래 위치
     * @param toIndex 새로운 위치
     */
    fun moveCategory(fromIndex: Int, toIndex: Int) {
        val currentState = _categoryUiState.value
        if (currentState !is CategoryUiState.Success) return

        val currentCategories = currentState.categories

        // 0번 자리가 DEFAULT_ID인 경우, 출발지가 0번이거나 목적지가 0번이면 이동 불가
        if (currentCategories[0].id == CategoryItem.DEFAULT_ID && (fromIndex == 0 || toIndex == 0)) return

        val updatedList = currentCategories.toMutableList().apply { add(toIndex, removeAt(fromIndex)) }

        // 낙관적 업데이트 (UI 즉시 반영)
        _categoryUiState.update { CategoryUiState.Success(updatedList) }
    }

    /**
     * 카테고리 순서 확정 및 저장 (DB & Server Sync)
     * 드래그가 끝난 시점(Drop)에 호출하여 현재 리스트의 순서를 영구적으로 저장합니다.
     */
    fun updateCategoryOrder() {
        val currentState = _categoryUiState.value
        if (currentState !is CategoryUiState.Success) return

        val updatedList = currentState.categories

        // DB 업데이트 및 서버 동기화
        viewModelScope.launch {
            val token = tokenManager.accessToken.firstOrNull()
            categoryRepository.reorderCategories(updatedList, token)
        }
    }
}

sealed interface CategoryUiState {
    data object Loading : CategoryUiState
    data class Success(val categories: List<CategoryItem>) : CategoryUiState
    data class Error(val message: String?) : CategoryUiState
}
