package com.scrap2025.scrap2025.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrap2025.scrap2025.data.local.TokenManager
import com.scrap2025.scrap2025.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject
constructor(
    tokenManager: TokenManager,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {
    val accessToken: StateFlow<String?> =
        tokenManager.accessToken.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    private val _customBottomBar = MutableStateFlow<(@Composable () -> Unit)?>(null)
    val customBottomBar: StateFlow<(@Composable () -> Unit)?> = _customBottomBar.asStateFlow()

    private val _sharedUrl = MutableStateFlow<String?>(null)
    val sharedUrl: StateFlow<String?> = _sharedUrl.asStateFlow()

    private var _pendingSharedUrl: String? = null

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    val selectedCategoryId: StateFlow<Long?> = categoryRepository.selectedCategoryId
    val selectedCategoryTitle: StateFlow<String?> = categoryRepository.selectedCategoryTitle

    init {
        viewModelScope.launch {
            accessToken.collect { token ->
                if (token.isNullOrEmpty()) {  // 로그인이 안되어있다면
                    _isInitialized.value = true
                } else {  // 로그인이 되어 있다면
                    fetchDefaultCategories()
                }
            }
        }
    }

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

    fun setGlobalCategory(id: Long, title: String) {
        categoryRepository.selectCategory(id, title)
    }

    fun setDefaultCategory() {
        categoryRepository.defaultCategory?.apply { categoryRepository.selectCategory(id, title) }
    }

    private fun fetchDefaultCategories() {
        viewModelScope.launch {
            categoryRepository.refreshCategories()
            _isInitialized.value = true
        }
    }
}
