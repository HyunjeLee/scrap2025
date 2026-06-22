package com.scrap2025.scrap2025.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class BottomBarViewModel
@Inject
constructor() : ViewModel() {
    private val _bottomBar = MutableStateFlow<(@Composable () -> Unit)?>(null)
    val bottomBar: StateFlow<(@Composable () -> Unit)?> = _bottomBar.asStateFlow()

    /** 바텀바에 표시할 커스텀 컴포저블을 설정합니다. null을 전달하면 기본 바텀바를 표시합니다. */
    fun setBottomBar(content: (@Composable () -> Unit)?) {
        _bottomBar.value = content
    }
}
