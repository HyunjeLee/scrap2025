package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrap2025.scrap2025.data.local.TokenManager
import com.scrap2025.scrap2025.model.CategoryItem
import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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
}

sealed interface CategoryUiState {
    data object Loading : CategoryUiState
    data class Success(val categories: List<CategoryItem>) : CategoryUiState
    data class Error(val message: String?) : CategoryUiState
}
