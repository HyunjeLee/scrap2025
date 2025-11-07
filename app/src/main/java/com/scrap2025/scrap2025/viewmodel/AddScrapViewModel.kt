package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.model.ScrapItem
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
class AddScrapViewModel @Inject constructor(
    private val scrapRepository: ScrapRepository
) : ViewModel() {

    private val _addScrapState = MutableStateFlow<Result<Unit>?>(null)
    val addScrapState: StateFlow<Result<Unit>?> = _addScrapState.asStateFlow()

    /**
     * 스크랩 추가
     * @param url 스크랩 URL
     * @param memo 메모 (선택사항)
     */
    fun addScrapItem(url: String, memo: String?) {
        viewModelScope.launch {
            // Loading 상태 설정
            _addScrapState.value = Result.Loading

            val newItem = ScrapItem(
                id = UUID.randomUUID().toString(),
                title = url, // 추후 웹 스크래핑으로 실제 제목 추출 가능
                url = url,
                imageUrl = null, // 추후 웹 스크래핑으로 썸네일 추출 가능
                createdDate = LocalDateTime.now(),
                isFavorite = false,
                categoryId = null, // 분류되지 않음
                memo = memo
            )

            // Repository를 통해 스크랩 추가
            val result = scrapRepository.addScrapItem(newItem)
            _addScrapState.value = result
        }
    }

    /**
     * 상태 초기화 (다음 추가를 위해)
     */
    fun resetState() {
        _addScrapState.value = null
    }
}
