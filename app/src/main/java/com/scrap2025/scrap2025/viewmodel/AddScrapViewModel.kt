package com.scrap2025.scrap2025.viewmodel

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrap2025.scrap2025.model.LinkPreview
import com.scrap2025.scrap2025.model.ScrapItem
import com.scrap2025.scrap2025.repository.LinkPreviewRepository
import com.scrap2025.scrap2025.repository.ScrapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

sealed interface LinkPreviewUiState {
    data object Loading : LinkPreviewUiState
    data class Success(val preview: LinkPreview) : LinkPreviewUiState
    data class Error(val message: String? = null) : LinkPreviewUiState
}

sealed interface AddScrapUiState {
    data object Loading : AddScrapUiState
    data object Success : AddScrapUiState
    data class Error(val message: String? = null) : AddScrapUiState
}

@HiltViewModel
class AddScrapViewModel
@Inject
constructor(
    private val scrapRepository: ScrapRepository,
    private val linkPreviewRepository: LinkPreviewRepository,
) : ViewModel() {

    private val _addScrapState = MutableStateFlow<AddScrapUiState?>(null)
    val addScrapState: StateFlow<AddScrapUiState?> = _addScrapState.asStateFlow()

    private val _linkPreviewUiState = MutableStateFlow<LinkPreviewUiState?>(null)
    val linkPreviewUiState: StateFlow<LinkPreviewUiState?> = _linkPreviewUiState.asStateFlow()

    private val _url = MutableStateFlow("")
    val url: StateFlow<String> = _url.asStateFlow()

    private val _memo = MutableStateFlow("")
    val memo: StateFlow<String> = _memo.asStateFlow()

    init {
        // URL이 변경될 때마다 자동으로 미리보기 가져오기 (Flow Operator 사용)
        viewModelScope.launch {
            url.debounce(500) // 500ms 동안 변경이 없을 때만 방출
                .distinctUntilChanged() // 이전 값과 다를 때만
                .collect { currentUrl ->
                    // 1. 비어있는 경우 즉시 초기화
                    if (currentUrl.isBlank()) {
                        _linkPreviewUiState.value = null
                        return@collect
                    }

                    // 2. 기본적인 URL 형식 검증 (http/https 시작 여부만 간단히 확인)
                    if (currentUrl.startsWith("http://") || currentUrl.startsWith("https://")) {
                        fetchLinkPreview(currentUrl)
                    } else {
                        // 3. 올바르지 않은 형식인 경우에도 초기화
                        _linkPreviewUiState.value = null
                    }
                }
        }
    }

    fun updateUrl(newUrl: String) {
        val refinedUrl =
            if (newUrl.length > 400) {
                // 검색어(q) 파라미터만 남기고 나머지 수천 자의 추적 데이터 제거
                try {
                    val uri = newUrl.toUri()
                    val query = uri.getQueryParameter("q")
                    if (query != null)
                        uri.buildUpon()
                            .clearQuery()
                            .appendQueryParameter("q", query)
                            .build()
                            .toString()
                    else
                        newUrl
                } catch (e: Exception) {
                    newUrl
                }
            } else {
                newUrl
            }

        _url.value = refinedUrl.trim()
    }

    fun updateMemo(newMemo: String) {
        _memo.value = newMemo
    }

    /**
     * URL로부터 링크 미리보기 데이터 가져오기
     * @param url 미리보기를 가져올 URL
     */
    private fun fetchLinkPreview(url: String) {
        viewModelScope.launch {
            _linkPreviewUiState.value = LinkPreviewUiState.Loading
            val result = linkPreviewRepository.fetchLinkPreview(url)
            result.fold(
                onSuccess = { _linkPreviewUiState.value = LinkPreviewUiState.Success(it) },
                onFailure = { _linkPreviewUiState.value = LinkPreviewUiState.Error(it.message) }
            )
        }
    }

    /**
     * 스크랩 추가 (링크 미리보기 데이터 포함)
     * @param url 스크랩 URL
     * @param memo 메모 (선택사항)
     * @param linkPreview 링크 미리보기 데이터 (선택사항)
     */
    fun addScrapItem(
        memo: String,
        linkPreview: LinkPreview? = null,
        categoryId: Long
    ) {
        viewModelScope.launch {
            // Loading 상태 설정
            _addScrapState.value = AddScrapUiState.Loading

            val newItem =
                ScrapItem(
                    id = 0L, // Server will assign the real ID
                    title = linkPreview?.title ?: "제목 없음",
                    description = linkPreview?.description ?: "",
                    url = _url.value,
                    imageUrl = linkPreview?.imageUrl,
                    createdDate = LocalDateTime.now(),
                    isFavorite = false,
                    categoryId = categoryId,
                    memo = memo
                )

            // Repository를 통해 스크랩 추가
            val result = scrapRepository.createScrap(newItem)
            result.fold(
                onSuccess = { _addScrapState.value = AddScrapUiState.Success },
                onFailure = { _addScrapState.value = AddScrapUiState.Error(it.message) }
            )
        }
    }

    /** 상태 초기화 (다음 추가를 위해) */
    fun resetState() {
        _addScrapState.value = null
        _url.value = ""
        _memo.value = ""
        _linkPreviewUiState.value = null
    }
}
