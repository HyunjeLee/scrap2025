package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrap2025.scrap2025.model.CategoryItem
import com.scrap2025.scrap2025.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed interface AddCategoryUiState {
    data object Loading : AddCategoryUiState
    data object Success : AddCategoryUiState
    data class Error(val message: String? = null) : AddCategoryUiState
}

/** AddCategoryViewModel - 카테고리 추가 화면의 상태 관리 CategoryRepository를 통해 카테고리를 추가 */
@HiltViewModel
class AddCategoryViewModel
@Inject constructor(
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _categoryCount = categoryRepository.getCategoryCount()

    private val _addCategoryUiState = MutableStateFlow<AddCategoryUiState?>(null)
    val addCategoryUiState: StateFlow<AddCategoryUiState?> = _addCategoryUiState.asStateFlow()

    private val _categoryTitle = MutableStateFlow("")
    val categoryTitle: StateFlow<String> = _categoryTitle.asStateFlow()

    fun updateCategoryTitle(newTitle: String) {
        _categoryTitle.value = newTitle
    }

    /** 카테고리 추가 유효성 검사 후 Repository를 통해 카테고리 추가 */
    fun addCategory() {
        val currentTitle = _categoryTitle.value

        // 유효성 검사: 빈 문자열 체크
        if (currentTitle.isEmpty()) {
            _addCategoryUiState.value = AddCategoryUiState.Error("카테고리명을 입력해주세요")
            return
        }

        viewModelScope.launch {
            // Loading 상태 설정
            _addCategoryUiState.value = AddCategoryUiState.Loading

            val currentCategoryCount = _categoryCount.firstOrNull() ?: 0

            // 새로운 카테고리 생성
            val newCategory = CategoryItem(
                id = UUID.randomUUID().toString(),
                name = currentTitle,
                scrapCount = 0,
                orderIndex = currentCategoryCount // 기본 카테고리가 0번 index로 존재하므로 그대로 사용
            )

            // Repository를 통해 카테고리 추가
            val result = categoryRepository.createCategory(newCategory)
            result.fold(
                onSuccess = { _addCategoryUiState.value = AddCategoryUiState.Success },
                onFailure = { _addCategoryUiState.value = AddCategoryUiState.Error(it.message) })
        }
    }

    /** 상태 초기화 (다음 추가를 위해) */
    fun resetState() {
        _addCategoryUiState.value = null
        _categoryTitle.value = ""
    }
}
