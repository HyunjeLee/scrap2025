package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrap2025.scrap2025.model.CategoryItem
import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** CategoryViewModel - 카테고리 목록 화면의 상태 관리 CategoryRepository를 통해 카테고리 목록을 조회하고 관리 */
@HiltViewModel
class CategoryViewModel @Inject constructor(private val categoryRepository: CategoryRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    init {
        fetchCategories()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            _uiState.value = CategoryUiState.Loading
            categoryRepository.getCategories().collect { result ->
                _uiState.update {
                    when (result) {
                        is Result.Loading -> CategoryUiState.Loading
                        is Result.Success -> CategoryUiState.Success(result.data)
                        is Result.Error -> CategoryUiState.Error(result.message)
                    }
                }
            }
        }
    }

    /**
     * 카테고리 삭제
     * @param id 삭제할 카테고리 ID
     */
    fun deleteCategory(id: String) {
        viewModelScope.launch {
            // Repository 내에서 트랜잭션으로 처리 (스크랩 이동 + 카테고리 삭제)
            categoryRepository.deleteCategory(id)
        }
    }

    /**
     * 카테고리명 업데이트
     * @param id 업데이트할 카테고리 ID
     * @param newTitle 새로운 카테고리명
     */
    fun updateCategoryTitle(id: String, newTitle: String) {
        viewModelScope.launch { categoryRepository.updateCategory(id, newTitle) }
    }

    /**
     * 카테고리 순서 변경
     * @param fromIndex 원래 위치
     * @param toIndex 새로운 위치
     */
    fun moveCategory(fromIndex: Int, toIndex: Int) {
        val currentState = _uiState.value
        if (currentState !is CategoryUiState.Success) return

        val currentList = currentState.categories
        // "분류되지 않음" (ID 1) 카테고리는 이동 불가 & 0번 인덱스 고정
        if (currentList[fromIndex].id == "1") return
        if (toIndex == 0 && currentList[0].id == "1") return

        val updatedList = currentList.toMutableList().apply { add(toIndex, removeAt(fromIndex)) }

        // 낙관적 업데이트 (UI 즉시 반영)
        _uiState.update { CategoryUiState.Success(updatedList) }

        // DB 업데이트
        viewModelScope.launch { categoryRepository.reorderCategories(updatedList) }
    }
}

sealed interface CategoryUiState {
    data object Loading : CategoryUiState
    data class Success(val categories: List<CategoryItem>) : CategoryUiState
    data class Error(val message: String?) : CategoryUiState
}
