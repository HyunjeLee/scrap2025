package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.ViewModel
import com.scrap2025.scrap2025.data.local.ScrapDummyData
import com.scrap2025.scrap2025.model.ScrapItem
import com.scrap2025.scrap2025.model.ViewMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ScrapViewModel : ViewModel() {
    // 스크랩 아이템 목록
    private val _scrapItems = MutableStateFlow(ScrapDummyData.dummyScrapItems)
    val scrapItems: StateFlow<List<ScrapItem>> = _scrapItems.asStateFlow()

    // 뷰 모드 (LIST/GRID)
    private val _viewMode = MutableStateFlow(ViewMode.LIST)
    val viewMode: StateFlow<ViewMode> = _viewMode.asStateFlow()

    // 뷰 모드 토글
    fun toggleViewMode() {
        _viewMode.value = if (_viewMode.value == ViewMode.LIST) {
            ViewMode.GRID
        } else {
            ViewMode.LIST
        }
    }

    // 스크랩 아이템 추가
    fun addScrapItem(url: String, memo: String?) {
        val newItem = ScrapItem(
            id = UUID.randomUUID().toString(),
            title = url, // 추후 웹 스크래핑으로 실제 제목 추출 가능
            url = url,
            imageUrl = null, // 추후 웹 스크래핑으로 썸네일 추출 가능
            createdDate = getCurrentDate(),
            isFavorite = false,
            categoryId = null, // 분류되지 않음
            memo = memo
        )

        _scrapItems.value.add(newItem)
    }

    // 현재 날짜 반환 (yyyy.MM.dd 형식)
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
