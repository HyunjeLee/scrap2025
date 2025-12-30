package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.scrap2025.scrap2025.model.CategoryItem
import com.scrap2025.scrap2025.model.GlobalUiState
import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.navigation.CategorySelection
import com.scrap2025.scrap2025.repository.CategoryRepository
import com.scrap2025.scrap2025.repository.ScrapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CategorySelectionViewModel
@Inject
constructor(
    private val categoryRepository: CategoryRepository,
    private val scrapRepository: ScrapRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val mode = savedStateHandle.toRoute<CategorySelection>().mode
    val scrapId = savedStateHandle.toRoute<CategorySelection>().scrapId

    private val _categoryUiState = MutableStateFlow<Result<List<CategoryItem>>>(Result.Loading)
    val categoryUiState: StateFlow<Result<List<CategoryItem>>> = _categoryUiState.asStateFlow()

    private val _selectedCategoryId =
        MutableStateFlow<String>(GlobalUiState.selectedCategoryId.value)
    val selectedCategoryId: StateFlow<String> = _selectedCategoryId.asStateFlow()

    private val _selectedCategoryName =
        MutableStateFlow<String>(GlobalUiState.selectedCategoryName.value)
    val selectedCategoryName: StateFlow<String> = _selectedCategoryName.asStateFlow()

    // For SEARCH mode (Multi-Selection)
    private val _selectedCategoryIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedCategoryIds: StateFlow<Set<String>> = _selectedCategoryIds.asStateFlow()

    private val _selectedCategoryNames = MutableStateFlow<Map<String, String>>(emptyMap())
    val selectedCategoryNames: StateFlow<Map<String, String>> = _selectedCategoryNames.asStateFlow()

    init {
        fetchCategories()
    }

    fun updateSelectedCategory(id: String, name: String) {
        _selectedCategoryId.value = id
        _selectedCategoryName.value = name
    }

    fun toggleCategorySelection(id: String, name: String) {
        if (_selectedCategoryIds.value.contains(id)) {
            _selectedCategoryIds.value -= id
            _selectedCategoryNames.value -= id
        } else {
            _selectedCategoryIds.value += id
            _selectedCategoryNames.value += (id to name)
        }
    }

    fun moveScrap(categoryId: String, onSuccess: () -> Unit) {
        if (scrapId != null) {
            viewModelScope.launch {
                val result = scrapRepository.moveScrapItem(scrapId, categoryId)

                if (result is Result.Success) {
                    onSuccess()
                }
            }
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { _categoryUiState.value = it }
        }

        // Trigger Sync
        viewModelScope.launch { categoryRepository.syncCategories() }
    }
}
