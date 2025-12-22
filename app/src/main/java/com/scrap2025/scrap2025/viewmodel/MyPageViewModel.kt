package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel
@Inject
constructor(
// 추후 회원탈퇴 API 호출을 위한 Repository 주입 필요
) : ViewModel() {

    private val _showWithdrawDialog = MutableStateFlow(false)
    val showWithdrawDialog: StateFlow<Boolean> = _showWithdrawDialog.asStateFlow()

    fun showWithdrawalDialog() {
        _showWithdrawDialog.value = true
    }

    fun dismissWithdrawalDialog() {
        _showWithdrawDialog.value = false
    }

    fun withdraw() {
        // TODO: 실제 회원탈퇴 로직 구현 (API 호출 등)
        _showWithdrawDialog.value = false
    }
}
