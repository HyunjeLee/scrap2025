package com.scrap2025.scrap2025.model

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object GlobalUiState {
    private val _customBottomBar = MutableStateFlow<(@Composable () -> Unit)?>(null)
    val customBottomBar: StateFlow<(@Composable () -> Unit)?> = _customBottomBar.asStateFlow()

    private val _sharedUrl = MutableStateFlow<String?>(null)
    val sharedUrl: StateFlow<String?> = _sharedUrl.asStateFlow()

    /**
     * 바텀바에 표시할 커스텀 컴포저블을 설정합니다.
     * null을 전달하면 기본 바텀바를 표시합니다.
     */
    fun setBottomBar(content: (@Composable () -> Unit)?) {
        _customBottomBar.value = content
    }

    /**
     * 다른 앱에서 공유된 URL을 설정합니다.
     * 스크랩 화면에서 이 URL을 감지하여 자동으로 스크랩을 추가할 수 있습니다.
     */
    fun setSharedUrl(url: String?) {
        _sharedUrl.value = url
    }
}