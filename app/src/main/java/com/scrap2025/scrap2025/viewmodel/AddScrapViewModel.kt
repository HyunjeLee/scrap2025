package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrap2025.scrap2025.model.LinkPreview
import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.model.ScrapItem
import com.scrap2025.scrap2025.repository.LinkPreviewRepository
import com.scrap2025.scrap2025.repository.ScrapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddScrapViewModel
@Inject
constructor(
    private val scrapRepository: ScrapRepository,
    private val linkPreviewRepository: LinkPreviewRepository
) : ViewModel() {

    private val _addScrapState = MutableStateFlow<Result<Unit>?>(null)
    val addScrapState: StateFlow<Result<Unit>?> = _addScrapState.asStateFlow()

    private val _linkPreviewState = MutableStateFlow<Result<LinkPreview>?>(null)
    val linkPreviewState: StateFlow<Result<LinkPreview>?> = _linkPreviewState.asStateFlow()

    /**
     * URL로부터 링크 미리보기 데이터 가져오기
     * @param url 미리보기를 가져올 URL
     */
    fun fetchLinkPreview(url: String) {
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
        memo: String?,
        linkPreview: LinkPreview? = null,
        categoryId: String = "1" // 기본값: 분류되지 않음
    ) {
        viewModelScope.launch {
            // Loading 상태 설정
            _addScrapState.value = Result.Loading

            val newItem =
                ScrapItem(
                    id = UUID.randomUUID().toString(),
                    title = linkPreview?.title ?: url,
                    url = url,
                    imageUrl = linkPreview?.imageUrl,
                    createdDate = LocalDateTime.now(),
                    isFavorite = false,
                    categoryId = categoryId,
                    memo = memo
                )

            // Repository를 통해 스크랩 추가
            val result = scrapRepository.addScrapItem(newItem)
            _addScrapState.value = result
        }
    }

    /**
     * URL로부터 자동으로 링크 미리보기를 가져와서 스크랩 추가 (사용자가 직접 url을 기입하는 경우)
     * @param url 스크랩 URL
     * @param memo 메모 (선택사항)
     */
    fun addScrapItemWithPreview(url: String, memo: String? = null) {
        viewModelScope.launch {
            // 링크 미리보기 가져오기
            val previewResult = linkPreviewRepository.fetchLinkPreview(url)

            val linkPreview =
                when (previewResult) {
                    is Result.Success -> previewResult.data
                    else -> null
                }

            // 스크랩 추가
            addScrapItem(url, memo, linkPreview)
        }
    }

    /** 상태 초기화 (다음 추가를 위해) */
    fun resetState() {
        _addScrapState.value = null
    }
}
