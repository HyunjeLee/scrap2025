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
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * AddCategoryViewModel - 카테고리 추가 화면의 상태 관리
 * CategoryRepository를 통해 카테고리를 추가
 */
@HiltViewModel
class AddCategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _addCategoryState = MutableStateFlow<Result<Unit>?>(null)
    val addCategoryState: StateFlow<Result<Unit>?> = _addCategoryState.asStateFlow()

    /**
     * 카테고리 추가
     * 유효성 검사 후 Repository를 통해 카테고리 추가
     */
    fun addCategory(categoryTitle: String) {

        // 유효성 검사: 빈 문자열 체크
        if (categoryTitle.isEmpty()) {
            _addCategoryState.value = Result.Error(
                IllegalArgumentException("카테고리명이 비어있습니다"),
                "카테고리명을 입력해주세요"
            )
            return
        }

        viewModelScope.launch {
            // Loading 상태 설정
            _addCategoryState.value = Result.Loading

            val newCategory = CategoryItem(
                id = UUID.randomUUID().toString(),
                name = categoryTitle,
                scrapCount = 0
            )

            // Repository를 통해 카테고리 추가
            val result = categoryRepository.addCategory(newCategory)
            _addCategoryState.value = result
        }
    }

    /**
     * 상태 초기화 (다음 추가를 위해)
     */
    fun resetState() {
        _addCategoryState.value = null
    }
}
