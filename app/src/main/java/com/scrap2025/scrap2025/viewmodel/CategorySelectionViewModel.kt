package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.scrap2025.scrap2025.model.GlobalUiState
import com.scrap2025.scrap2025.navigation.CategorySelection
import com.scrap2025.scrap2025.repository.CategoryRepository
import com.scrap2025.scrap2025.repository.ScrapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategorySelectionViewModel
@Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val scrapRepository: ScrapRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val mode = savedStateHandle.toRoute<CategorySelection>().mode
    val scrapId = savedStateHandle.toRoute<CategorySelection>().scrapId
    val initialSelectedIds = savedStateHandle.toRoute<CategorySelection>().initialSelectedIds

    private val _categoryUiState =
        MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val categoryUiState: StateFlow<CategoryUiState> = _categoryUiState.asStateFlow()

    private val _selectedCategoryId =
        MutableStateFlow<String>(GlobalUiState.selectedCategoryId.value)
    val selectedCategoryId: StateFlow<String> = _selectedCategoryId.asStateFlow()

    private val _selectedCategoryName =
        MutableStateFlow<String>(GlobalUiState.selectedCategoryName.value)
    val selectedCategoryName: StateFlow<String> = _selectedCategoryName.asStateFlow()

    // For SEARCH mode (Multi-Selection)
    private val _selectedCategoryIds = MutableStateFlow<Set<String>>(initialSelectedIds.toSet())
    val selectedCategoryIds: StateFlow<Set<String>> = _selectedCategoryIds.asStateFlow()

    init {
        fetchCategories()
    }

    fun updateSelectedCategory(id: String, name: String) {
        _selectedCategoryId.value = id
        _selectedCategoryName.value = name
    }

    fun toggleCategorySelection(id: String) {
        if (_selectedCategoryIds.value.contains(id)) {
            _selectedCategoryIds.value -= id
        } else {
            _selectedCategoryIds.value += id
        }
    }

    fun moveScrap(categoryId: String, onSuccess: () -> Unit) {
        if (scrapId != null) {
            viewModelScope.launch {
                val result = scrapRepository.moveScrapItem(scrapId, categoryId)
                result.onSuccess { onSuccess() }
            }
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { result ->
                result.fold(
                    onSuccess = { _categoryUiState.value = CategoryUiState.Success(it) },
                    onFailure = {
                        _categoryUiState.value = CategoryUiState.Error(it.message)
                    })
            }
        }

        // Trigger Sync
        viewModelScope.launch { categoryRepository.syncCategories() }
    }
}
