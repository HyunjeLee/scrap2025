package com.scrap2025.scrap2025.model

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object GlobalUiState {
    private val _customBottomBar = MutableStateFlow<(@Composable () -> Unit)?>(null)
    val customBottomBar: StateFlow<(@Composable () -> Unit)?> = _customBottomBar.asStateFlow()

    /**
     * 바텀바에 표시할 커스텀 컴포저블을 설정합니다.
     * null을 전달하면 기본 바텀바를 표시합니다.
     */
    fun setBottomBar(content: (@Composable () -> Unit)?) {
        _customBottomBar.value = content
    }
}