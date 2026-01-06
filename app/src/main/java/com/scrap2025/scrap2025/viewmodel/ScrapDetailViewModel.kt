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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ScrapDetailUiState {
    data object Loading : ScrapDetailUiState
    data class Success(val scrapItem: ScrapItem) : ScrapDetailUiState
    data class Error(val message: String? = null) : ScrapDetailUiState
}

@HiltViewModel
class ScrapDetailViewModel
@Inject constructor(
    private val scrapRepository: ScrapRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val scrapId: String = savedStateHandle.toRoute<ScrapDetail>().scrapId

    private val _isDeleteDialogVisible = MutableStateFlow(false)
    val isDeleteDialogVisible: StateFlow<Boolean> = _isDeleteDialogVisible.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    val scrapDetailUiState: StateFlow<ScrapDetailUiState> =
        scrapRepository.getScrapItemByIdAsFlow(scrapId).map { result ->
                result.fold(
                    onSuccess = { ScrapDetailUiState.Success(it) },
                    onFailure = { ScrapDetailUiState.Error(it.message) })
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ScrapDetailUiState.Loading
            )

    init {
        fetchScrapDetail()
    }

    private fun fetchScrapDetail() {
        viewModelScope.launch {
            _isSyncing.value = true
            scrapRepository.syncScrapById(scrapId)
            _isSyncing.value = false
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
            val result = scrapRepository.toggleFavorite(scrapId)
            result.fold(onSuccess = { onSuccess() }, onFailure = { onFailure() })
        }
    }
}
