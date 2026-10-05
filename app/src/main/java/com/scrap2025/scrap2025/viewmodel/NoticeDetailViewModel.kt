package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.scrap2025.scrap2025.model.Notice
import com.scrap2025.scrap2025.navigation.destinations.NoticeDetail
import com.scrap2025.scrap2025.repository.ServerNoticeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface NoticeDetailUiState {
    data object Loading : NoticeDetailUiState

    data class Success(val notice: Notice) : NoticeDetailUiState

    data class Error(val message: String? = null) : NoticeDetailUiState
}

@HiltViewModel
class NoticeDetailViewModel
@Inject
constructor(
    private val serverNoticeRepository: ServerNoticeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val noticeId: Long = savedStateHandle.toRoute<NoticeDetail>().noticeId

    private val _uiState = MutableStateFlow<NoticeDetailUiState>(NoticeDetailUiState.Loading)
    val uiState: StateFlow<NoticeDetailUiState> = _uiState.asStateFlow()

    init {
        fetchNotice()
    }

    fun fetchNotice() {
        viewModelScope.launch {
            _uiState.value = NoticeDetailUiState.Loading
            serverNoticeRepository
                .getNoticeById(noticeId)
                .fold(
                    onSuccess = { _uiState.value = NoticeDetailUiState.Success(it) },
                    onFailure = { _uiState.value = NoticeDetailUiState.Error(it.message) }
                )
        }
    }
}
