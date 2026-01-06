package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.scrap2025.scrap2025.data.remote.dto.ScrapMemoDto
import com.scrap2025.scrap2025.navigation.EditMemo
import com.scrap2025.scrap2025.repository.ScrapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface EditMemoUiState {
    data object Loading : EditMemoUiState
    data class Success(val memoDto: ScrapMemoDto) : EditMemoUiState
    data class Error(val message: String? = null) : EditMemoUiState
}

@HiltViewModel
class EditMemoViewModel
@Inject constructor(
    private val scrapRepository: ScrapRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val scrapId = savedStateHandle.toRoute<EditMemo>().scrapId
    val initialMemo = savedStateHandle.toRoute<EditMemo>().initialMemo

    private val _editMemoUiState = MutableStateFlow<EditMemoUiState?>(null)
    val editMemoUiState: StateFlow<EditMemoUiState?> = _editMemoUiState.asStateFlow()

    fun editMemo(memo: String) {
        viewModelScope.launch {
            _editMemoUiState.value = EditMemoUiState.Loading
            val result = scrapRepository.updateScrapItem(scrapId, memo)
            result.fold(onSuccess = {
                _editMemoUiState.value = EditMemoUiState.Success(ScrapMemoDto(memo))
            }, onFailure = { _editMemoUiState.value = EditMemoUiState.Error(it.message) })
        }
    }
}
