package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrap2025.scrap2025.model.CategoryItem
import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.repository.CategoryRepository
import com.scrap2025.scrap2025.repository.ScrapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** CategoryViewModel - 카테고리 목록 화면의 상태 관리 CategoryRepository를 통해 카테고리 목록을 조회하고 관리 */
@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val scrapRepository: ScrapRepository
) : ViewModel() {

    /** 카테고리 목록 상태 Repository의 Flow를 StateFlow로 변환하여 UI에 노출 */
    val categories: StateFlow<Result<List<CategoryItem>>> =
        categoryRepository.getCategories().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.Loading
        )

    /**
     * 카테고리 삭제
     * @param id 삭제할 카테고리 ID
     */
    fun deleteCategory(id: String) {
        viewModelScope.launch {
            // 1. 해당 카테고리의 모든 스크랩을 기본 카테고리(1)로 이동
            scrapRepository.moveScrapsToCategory(id, "1")
            // 2. 카테고리 삭제
            categoryRepository.deleteCategory(id)
        }
    }

    /**
     * 카테고리명 업데이트
     * @param id 업데이트할 카테고리 ID
     * @param newTitle 새로운 카테고리명
     */
    fun updateCategoryTitle(id: String, newTitle: String) {
        viewModelScope.launch {
            categoryRepository.updateCategory(id, newTitle)
        }
    }
}
