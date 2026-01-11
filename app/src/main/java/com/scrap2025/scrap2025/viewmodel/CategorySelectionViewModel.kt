package com.scrap2025.scrap2025.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.scrap2025.scrap2025.navigation.destinations.CategorySelection
import com.scrap2025.scrap2025.repository.CategoryRepository
import com.scrap2025.scrap2025.repository.ScrapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CategorySelectionViewModel"

@HiltViewModel
class CategorySelectionViewModel
@Inject
constructor(
    private val categoryRepository: CategoryRepository,
    private val scrapRepository: ScrapRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val mode = savedStateHandle.toRoute<CategorySelection>().mode
    val scrapId = savedStateHandle.toRoute<CategorySelection>().scrapId ?: -1L
    val scrapIds = savedStateHandle.toRoute<CategorySelection>().scrapIds ?: emptyList()
    val initialCategoryId = savedStateHandle.toRoute<CategorySelection>().initialCategoryId
    val initialCategoryTitle = savedStateHandle.toRoute<CategorySelection>().initialCategoryTitle
    val initialSelectedIds = savedStateHandle.toRoute<CategorySelection>().initialSelectedIds

    val categoryUiState: StateFlow<CategoryUiState> = categoryRepository.allCategories
        .map { result ->
            result.fold(
                onSuccess = { CategoryUiState.Success(it) },
                onFailure = { CategoryUiState.Error(it.message) }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CategoryUiState.Loading // 처음엔 로딩
        )

    private val _selectedCategoryId = MutableStateFlow<Long>(initialCategoryId ?: 0L)
    val selectedCategoryId: StateFlow<Long> = _selectedCategoryId.asStateFlow()

    private val _selectedCategoryName = MutableStateFlow<String>(initialCategoryTitle ?: "")
    val selectedCategoryName: StateFlow<String> = _selectedCategoryName.asStateFlow()

    // For SEARCH mode (Multi-Selection)
    private val _selectedCategoryIds = MutableStateFlow<Set<Long>>(initialSelectedIds.toSet())
    val selectedCategoryIds: StateFlow<Set<Long>> = _selectedCategoryIds.asStateFlow()

    init {
        fetchCategories()
    }

    fun updateSelectedCategory(id: Long, name: String) {
        _selectedCategoryId.value = id
        _selectedCategoryName.value = name
    }

    fun toggleCategorySelection(id: Long) {
        if (_selectedCategoryIds.value.contains(id)) {
            _selectedCategoryIds.value -= id
        } else {
            _selectedCategoryIds.value += id
        }
    }

    fun moveScrap(categoryId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = scrapRepository.moveScrap(scrapId, categoryId)
            result.onSuccess {
                categoryRepository.refreshCategories()
                onSuccess()
            }
        }

    }

    // 선택된 아이템 이동
    fun moveScrapBulk(
        categoryId: Long,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        viewModelScope.launch {
            scrapRepository.moveScrapBulk(scrapIds, categoryId)
                .onSuccess {
                    categoryRepository.refreshCategories()
                    onSuccess()
                }
                .onFailure {
                    onFailure()
                    Log.e(TAG, "Error moving selected items", it)
                }

        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            categoryRepository.refreshCategories()
        }
    }
}
