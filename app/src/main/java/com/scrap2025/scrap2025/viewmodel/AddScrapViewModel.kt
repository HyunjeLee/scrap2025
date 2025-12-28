package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrap2025.scrap2025.data.local.TokenManager
import com.scrap2025.scrap2025.model.LinkPreview
import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.model.ScrapItem
import com.scrap2025.scrap2025.repository.LinkPreviewRepository
import com.scrap2025.scrap2025.repository.ScrapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddScrapViewModel
@Inject
constructor(
    private val scrapRepository: ScrapRepository,
    private val linkPreviewRepository: LinkPreviewRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _addScrapState = MutableStateFlow<Result<Unit>?>(null)
    val addScrapState: StateFlow<Result<Unit>?> = _addScrapState.asStateFlow()

    private val _linkPreviewState = MutableStateFlow<Result<LinkPreview>?>(null)
    val linkPreviewState: StateFlow<Result<LinkPreview>?> = _linkPreviewState.asStateFlow()

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
                        _linkPreviewState.value = null
                        return@collect
                    }

                    // 2. 기본적인 URL 형식 검증 (http/https 시작 여부만 간단히 확인)
                    if (currentUrl.startsWith("http://") || currentUrl.startsWith("https://")) {
                        fetchLinkPreview(currentUrl)
                    } else {
                        // 3. 올바르지 않은 형식인 경우에도 초기화
                        _linkPreviewState.value = null
                    }
                }
        }
    }

    fun updateUrl(newUrl: String) {
        _url.value = newUrl
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
            _linkPreviewState.value = Result.Loading
            val result = linkPreviewRepository.fetchLinkPreview(url)
            _linkPreviewState.value = result
        }
    }

    /**
     * 스크랩 추가 (링크 미리보기 데이터 포함)
     * @param url 스크랩 URL
     * @param memo 메모 (선택사항)
     * @param linkPreview 링크 미리보기 데이터 (선택사항)
     */
    fun addScrapItem(
        url: String,
        memo: String,
        linkPreview: LinkPreview? = null,
        categoryId: String
    ) {
        viewModelScope.launch {
            // Loading 상태 설정
            _addScrapState.value = Result.Loading
            val token = tokenManager.accessToken.firstOrNull()

            val newItem =
                ScrapItem(
                    id = UUID.randomUUID().toString(),
                    title = linkPreview?.title ?: url,
                    description = linkPreview?.description ?: "",
                    url = url,
                    imageUrl = linkPreview?.imageUrl,
                    createdDate = LocalDateTime.now(),
                    isFavorite = false,
                    categoryId = categoryId,
                    memo = memo
                )

            // Repository를 통해 스크랩 추가
            val result = scrapRepository.createScrap(newItem, token)
            _addScrapState.value = result
        }
    }

    /** 상태 초기화 (다음 추가를 위해) */
    fun resetState() {
        _addScrapState.value = null
        _url.value = ""
        _memo.value = ""
        _linkPreviewState.value = null
    }
}
