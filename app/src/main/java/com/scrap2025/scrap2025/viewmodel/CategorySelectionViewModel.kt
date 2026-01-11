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
    val scrapIds = savedStateHandle.toRoute<CategorySelection>().scrapIds ?: emptyList()  // 해당 인자만 유지  // 단일은 1개의 값만
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

    fun moveScrap(
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val scrapId = scrapIds.firstOrNull() ?: return // 아이템이 없으면 바로 종료

        viewModelScope.launch {
            scrapRepository.moveScrap(scrapId, _selectedCategoryId.value)
                .onSuccess {
                    categoryRepository.setGlobalCategory(_selectedCategoryId.value, _selectedCategoryName.value)
                    categoryRepository.refreshCategories()
                    onSuccess()
                }
                .onFailure {
                    onFailure()
                    Log.e(TAG, " Error moveScrap", it)
                }
        }
    }

    // 선택된 아이템 이동
    fun moveScrapBulk(
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        viewModelScope.launch {
            scrapRepository.moveScrapBulk(scrapIds, _selectedCategoryId.value)
                .onSuccess {
                    categoryRepository.setGlobalCategory(_selectedCategoryId.value, _selectedCategoryName.value)
                    categoryRepository.refreshCategories()
                    onSuccess()
                }
                .onFailure {
                    onFailure()
                    Log.e(TAG, "Error moveScrapBulk", it)
                }

        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            categoryRepository.refreshCategories()
        }
    }
}
