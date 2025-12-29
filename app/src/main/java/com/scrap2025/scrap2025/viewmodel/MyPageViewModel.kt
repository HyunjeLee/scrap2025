package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrap2025.scrap2025.data.model.MyPageResult
import com.scrap2025.scrap2025.repository.AuthRepository
import com.scrap2025.scrap2025.repository.CategoryRepository
import com.scrap2025.scrap2025.repository.MyPageRepository
import com.scrap2025.scrap2025.repository.ScrapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel
@Inject
constructor(
    private val authRepository: AuthRepository,
    private val myPageRepository: MyPageRepository,
    scrapRepository: ScrapRepository,
    categoryRepository: CategoryRepository
) : ViewModel() {

    // Define UI State
    sealed interface MyPageUiState {
        data object Loading : MyPageUiState
        data class Success(
            val myPageInfo: MyPageResult,
            val scrapCount: Int,
            val categoryCount: Int
        ) : MyPageUiState
    }

    private val _showWithdrawDialog = MutableStateFlow(false)
    val showWithdrawDialog: StateFlow<Boolean> = _showWithdrawDialog.asStateFlow()

    // Combine flows into a single UI State
    val uiState: StateFlow<MyPageUiState> =
        combine(
            myPageRepository.myPageData,
            scrapRepository.getScrapCount(),
            categoryRepository.getCategoryCount()
        ) { myPageInfo, scrapCount, categoryCount ->
            if (myPageInfo == null) {
                MyPageUiState.Loading
            } else {
                MyPageUiState.Success(
                    myPageInfo = myPageInfo,
                    scrapCount = scrapCount,
                    categoryCount = categoryCount
                )
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = MyPageUiState.Loading
            )

    init {
        fetchMyPageInfo()
    }

    fun fetchMyPageInfo() {
        viewModelScope.launch { myPageRepository.invokeMyPageSync() }
    }

    fun showWithdrawalDialog() {
        _showWithdrawDialog.value = true
    }

    fun dismissWithdrawalDialog() {
        _showWithdrawDialog.value = false
    }

    fun logout() {
        viewModelScope.launch { authRepository.logout() }
    }

    fun withdraw() {
        viewModelScope.launch {
            authRepository.withdraw().onSuccess { _showWithdrawDialog.value = false }
        }
    }
}
