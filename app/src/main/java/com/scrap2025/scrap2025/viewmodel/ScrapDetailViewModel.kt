package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.scrap2025.scrap2025.model.ScrapItem
import com.scrap2025.scrap2025.navigation.ScrapDetail
import com.scrap2025.scrap2025.repository.ScrapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ScrapDetailUiState {
    data object Loading : ScrapDetailUiState
    data class Success(val scrapItem: ScrapItem) : ScrapDetailUiState
    data class Error(val message: String? = null) : ScrapDetailUiState
}

@HiltViewModel
class ScrapDetailViewModel
@Inject
constructor(
    private val scrapRepository: ScrapRepository,
    savedStateHandle: SavedStateHandle
) :
    ViewModel() {
    private val scrapId: Long = savedStateHandle.toRoute<ScrapDetail>().scrapId

    private val _isDeleteDialogVisible = MutableStateFlow(false)
    val isDeleteDialogVisible: StateFlow<Boolean> = _isDeleteDialogVisible.asStateFlow()

    private val _uiState = MutableStateFlow<ScrapDetailUiState>(ScrapDetailUiState.Loading)
    val uiState: StateFlow<ScrapDetailUiState> = _uiState.asStateFlow()

    init {
        fetchScrapDetail()
    }

    private fun fetchScrapDetail() {
        viewModelScope.launch {
            _uiState.value = ScrapDetailUiState.Loading

            scrapRepository.getScrapById(scrapId)
                .fold(
                    onSuccess = {
                        _uiState.value = ScrapDetailUiState.Success(it)
                    },
                    onFailure = {
                        _uiState.value = ScrapDetailUiState.Error(it.message)
                    }
                )
        }
    }

    fun showDeleteDialog() {
        _isDeleteDialogVisible.value = true
    }

    fun hideDeleteDialog() {
        _isDeleteDialogVisible.value = false
    }

    fun deleteScrap(onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            val result = scrapRepository.deleteScrapItem(scrapId)
            result.fold(onSuccess = { onSuccess() }, onFailure = { onFailure() })
        }
    }

    fun toggleFavorite(onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            scrapRepository.toggleFavorite(scrapId)
                .onSuccess {
                    val currentState = _uiState.value
                    if (currentState is ScrapDetailUiState.Success) {
                        val updated =
                            currentState.scrapItem.copy(isFavorite = !currentState.scrapItem.isFavorite)

                        _uiState.value = ScrapDetailUiState.Success(updated)
                    }

                    onSuccess()
                }
                .onFailure {
                    onFailure()
                }
        }
    }
}
