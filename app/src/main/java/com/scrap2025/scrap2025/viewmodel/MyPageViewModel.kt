package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrap2025.scrap2025.data.model.MyPageResult
import com.scrap2025.scrap2025.repository.AuthRepository
import com.scrap2025.scrap2025.repository.CategoryRepository
import com.scrap2025.scrap2025.repository.ScrapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel
@Inject
constructor(
    private val authRepository: AuthRepository,
    scrapRepository: ScrapRepository,
    categoryRepository: CategoryRepository
) : ViewModel() {

    private val _myPageInfo = MutableStateFlow<MyPageResult?>(null)
    val myPageInfo: StateFlow<MyPageResult?> = _myPageInfo.asStateFlow()

    private val _showWithdrawDialog = MutableStateFlow(false)
    val showWithdrawDialog: StateFlow<Boolean> = _showWithdrawDialog.asStateFlow()

    val scrapCount: StateFlow<Int> =
        scrapRepository
            .getScrapCount()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val categoryCount: StateFlow<Int> =
        categoryRepository
            .getCategoryCount()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        fetchMyPageInfo()
    }

    fun fetchMyPageInfo() {
        viewModelScope.launch {
            authRepository.getMyPage().onSuccess { _myPageInfo.value = it }.onFailure {
                // TODO: Handle error
            }
        }
    }

    fun showWithdrawalDialog() {
        _showWithdrawDialog.value = true
    }

    fun dismissWithdrawalDialog() {
        _showWithdrawDialog.value = false
    }

    fun logout(onSignOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout().onSuccess {
                onSignOut()
            }
        }
    }

    fun withdraw(onSignOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.withdraw().onSuccess {
                _showWithdrawDialog.value = false
                onSignOut()
            }
        }
    }
}
