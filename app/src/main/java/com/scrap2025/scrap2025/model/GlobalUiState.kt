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

    private var _pendingSharedUrl: String? = null

    /** 바텀바에 표시할 커스텀 컴포저블을 설정합니다. null을 전달하면 기본 바텀바를 표시합니다. */
    fun setBottomBar(content: (@Composable () -> Unit)?) {
        _customBottomBar.value = content
    }

    /** 다른 앱에서 공유된 URL을 설정하여 네비게이션을 트리거합니다. */
    fun setSharedUrl(url: String?) {
        _pendingSharedUrl = url
        _sharedUrl.value = url
    }

    /** 공유 프로세스가 시작되면 트리거 상태를 해제합니다. */
    fun clearSharedUrlTrigger() {
        _sharedUrl.value = null
    }

    /** AddScrapScreen에서 사용할 실제 공유 URL을 가져오고 내부적으로 비웁니다. */
    fun consumePendingSharedUrl(): String? {
        val url = _pendingSharedUrl
        _pendingSharedUrl = null
        return url
    }

    private val _selectedCategoryId = MutableStateFlow(CategoryItem.DEFAULT_ID)
    val selectedCategoryId: StateFlow<String> = _selectedCategoryId.asStateFlow()

    private val _selectedCategoryName = MutableStateFlow(CategoryItem.DEFAULT_NAME)
    val selectedCategoryName: StateFlow<String> = _selectedCategoryName.asStateFlow()

    fun setCategory(id: String, name: String) {
        _selectedCategoryId.value = id
        _selectedCategoryName.value = name
    }
}
