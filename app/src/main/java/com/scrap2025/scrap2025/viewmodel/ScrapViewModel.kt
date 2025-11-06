package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.ViewModel
import com.scrap2025.scrap2025.data.local.ScrapDummyData
import com.scrap2025.scrap2025.model.ScrapItem
import com.scrap2025.scrap2025.model.ViewMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ScrapViewModel : ViewModel() {
    // 스크랩 아이템 목록
    private val _scrapItems = MutableStateFlow<List<ScrapItem>>(ScrapDummyData.dummyScrapItems)
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
}
