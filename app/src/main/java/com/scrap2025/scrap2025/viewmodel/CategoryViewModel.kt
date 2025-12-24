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

    private val _uiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    init {
        fetchCategories()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            _uiState.value = CategoryUiState.Loading
            categoryRepository.getAllCategories().collect { result ->
                _uiState.update {
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
     * 카테고리 삭제
     * @param id 삭제할 카테고리 ID
     */
    fun deleteCategory(id: String) {
        viewModelScope.launch {
            val token = tokenManager.accessToken.firstOrNull()
            // Repository 내에서 트랜잭션으로 처리 (스크랩 이동 + 카테고리 삭제)
            categoryRepository.deleteCategory(id, token)
        }
    }

    /**
     * 카테고리명 업데이트
     * @param id 업데이트할 카테고리 ID
     * @param newTitle 새로운 카테고리명
     */
    fun updateCategoryTitle(id: String, newTitle: String) {
        viewModelScope.launch { categoryRepository.updateCategory(
            id = id,
            name = newTitle,
            token = tokenManager.accessToken.firstOrNull()
        ) }
    }
}

sealed interface CategoryUiState {
    data object Loading : CategoryUiState
    data class Success(val categories: List<CategoryItem>) : CategoryUiState
    data class Error(val message: String?) : CategoryUiState
}
