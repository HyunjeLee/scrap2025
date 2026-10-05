package com.scrap2025.scrap2025.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrap2025.scrap2025.data.local.PreferencesManager
import com.scrap2025.scrap2025.data.local.TokenManager
import com.scrap2025.scrap2025.model.Notice
import com.scrap2025.scrap2025.model.NoticeConfig
import com.scrap2025.scrap2025.repository.CategoryRepository
import com.scrap2025.scrap2025.repository.NoticeRepository
import com.scrap2025.scrap2025.repository.ServerNoticeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface MainUiState {
    data object Loading : MainUiState // 토큰이 있는지 확인 중인 상태

    data object LoginRequired : MainUiState // 인증 정보가 없어 로그인이 필요한 상태

    data object Initializing : MainUiState // 토큰은 있으나, 카테고리 등 초기 데이터를 가져오는 중

    data object Complete : MainUiState // 모든 데이터 준비 완료 (메인 화면 진입 가능)
}

@HiltViewModel
class MainViewModel
@Inject
constructor(
    tokenManager: TokenManager,
    private val categoryRepository: CategoryRepository,
    private val noticeRepository: NoticeRepository,
    private val serverNoticeRepository: ServerNoticeRepository,
    private val preferencesManager: PreferencesManager
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

    private var pendingSharedUrl: String? = null

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _notice = MutableStateFlow<NoticeConfig?>(null)
    val notice: StateFlow<NoticeConfig?> = _notice.asStateFlow()

    // 앱 실행 시 전체 화면으로 띄울 최신 서버 공지사항
    private val _latestNotice = MutableStateFlow<Notice?>(null)
    val latestNotice: StateFlow<Notice?> = _latestNotice.asStateFlow()

    // 토큰 갱신 등으로 초기화가 다시 일어나도 한 번의 실행에서는 한 번만 확인한다
    private var hasCheckedLatestNotice = false

    val selectedCategoryId: StateFlow<Long?> = categoryRepository.selectedCategoryId
    val selectedCategoryTitle: StateFlow<String?> = categoryRepository.selectedCategoryTitle

    init {
        viewModelScope.launch {
            accessToken.collect { token ->
                when {
                    token == "" -> _uiState.value = MainUiState.Loading
                    token == null -> _uiState.value = MainUiState.LoginRequired
                    else -> {
                        _uiState.value = MainUiState.Initializing
                        fetchDefaultCategories()
                    }
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
        pendingSharedUrl = url
        _sharedUrl.value = url
    }

    /** 공유 프로세스가 시작되면 트리거 상태를 해제합니다. */
    fun clearSharedUrlTrigger() {
        _sharedUrl.value = null
    }

    /** AddScrapScreen에서 사용할 실제 공유 URL을 가져오고 내부적으로 비웁니다. */
    fun consumePendingSharedUrl(): String? {
        val url = pendingSharedUrl
        pendingSharedUrl = null
        return url
    }

    fun setGlobalCategory(id: Long, title: String) {
        categoryRepository.setGlobalCategory(id, title)
    }

    fun setDefaultCategory() {
        categoryRepository.defaultCategory?.apply {
            categoryRepository.setGlobalCategory(id, title)
        }
    }

    /** 공지사항 다이얼로그를 닫습니다. 다음 실행 시에는 다시 노출됩니다. */
    fun closeNotice() {
        _notice.value = null
    }

    /** 공지사항을 닫고, 같은 공지(id)는 다시 노출하지 않도록 저장합니다. */
    fun dismissNoticePermanently() {
        val current = _notice.value ?: return
        _notice.value = null
        viewModelScope.launch {
            preferencesManager.setDismissedNoticeId(current.id)
        }
    }

    /** 최신 공지사항 화면을 닫습니다. 다음 실행 시에는 다시 노출됩니다. */
    fun closeLatestNotice() {
        _latestNotice.value = null
    }

    /** 최신 공지사항 화면을 닫고, 같은 공지(id)는 다시 노출하지 않도록 저장합니다. */
    fun dismissLatestNoticePermanently() {
        val current = _latestNotice.value ?: return
        _latestNotice.value = null
        viewModelScope.launch {
            preferencesManager.setDismissedServerNoticeId(current.id)
        }
    }

    private fun fetchDefaultCategories() {
        viewModelScope.launch {
            categoryRepository.refreshCategories()
            _uiState.value = MainUiState.Complete
            checkNotice()
            checkLatestNotice()
        }
    }

    private fun checkNotice() {
        viewModelScope.launch {
            val notice = noticeRepository.getNotice() ?: return@launch
            val dismissedNoticeId = preferencesManager.dismissedNoticeId.first()
            if (notice.id != dismissedNoticeId) {
                _notice.value = notice
            }
        }
    }

    /** 가장 최신 서버 공지사항 1건만 확인하며, 사용자가 "다시 보지 않기"를 누른 공지라면 띄우지 않습니다. */
    private fun checkLatestNotice() {
        if (hasCheckedLatestNotice) return
        hasCheckedLatestNotice = true

        viewModelScope.launch {
            val latest =
                serverNoticeRepository.getLatestNoticeSummary().getOrNull() ?: return@launch
            if (latest.id == preferencesManager.dismissedServerNoticeId.first()) return@launch

            serverNoticeRepository
                .getNoticeById(latest.id)
                .onSuccess { _latestNotice.value = it }
        }
    }
}
