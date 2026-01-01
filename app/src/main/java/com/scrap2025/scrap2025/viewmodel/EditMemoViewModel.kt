package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.scrap2025.scrap2025.data.remote.dto.ScrapMemoDto
import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.navigation.EditMemo
import com.scrap2025.scrap2025.repository.ScrapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class EditMemoViewModel
@Inject
constructor(
    private val scrapRepository: ScrapRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val scrapId = savedStateHandle.toRoute<EditMemo>().scrapId
    val initialMemo = savedStateHandle.toRoute<EditMemo>().initialMemo

    private val _editMemoState = MutableStateFlow<Result<ScrapMemoDto>?>(null)
    val editMemoState: StateFlow<Result<ScrapMemoDto>?> = _editMemoState.asStateFlow()


    fun editMemo(memo: String) {
        viewModelScope.launch {
            val result = scrapRepository.updateScrapItem(scrapId, memo)

            when(result) {
                Result.Loading -> { _editMemoState.value = Result.Loading }
                is Result.Error -> { _editMemoState.value = Result.Error(result.exception, result.message) }
                is Result.Success -> { _editMemoState.value = Result.Success(ScrapMemoDto(memo)) }
            }
        }
    }
}