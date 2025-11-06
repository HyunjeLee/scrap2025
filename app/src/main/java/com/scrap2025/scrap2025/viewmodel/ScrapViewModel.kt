package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrap2025.scrap2025.data.local.ScrapDummyData
import com.scrap2025.scrap2025.model.ScrapItem
import com.scrap2025.scrap2025.model.ViewMode
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

    // 맨 위로가기 버튼 표시 여부 (비즈니스 상태)
    private val _showScrollToTop = MutableStateFlow(false)
    val showScrollToTop: StateFlow<Boolean> = _showScrollToTop.asStateFlow()

    // 스크롤 투 탑 이벤트 (일회성 이벤트)
    private val _scrollToTopEvent = MutableSharedFlow<Unit>()
    val scrollToTopEvent: SharedFlow<Unit> = _scrollToTopEvent.asSharedFlow()

    // View에서 스크롤 위치가 변경될 때 호출
    fun updateScrollPosition(firstVisibleItemIndex: Int, firstVisibleItemScrollOffset: Int) {
        // 스크롤을 조금이라도 내렸으면 버튼 표시
        _showScrollToTop.value = firstVisibleItemIndex > 0 || firstVisibleItemScrollOffset > 0
    }

    // 맨 위로가기 버튼 클릭 시 호출
    fun scrollToTop() {
        viewModelScope.launch {
            _scrollToTopEvent.emit(Unit)
        }
    }
}
