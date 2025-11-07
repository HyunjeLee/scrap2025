package com.scrap2025.scrap2025.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.scrap2025.scrap2025.data.local.PreferencesManager
import com.scrap2025.scrap2025.data.local.ScrapDummyData
import com.scrap2025.scrap2025.model.ScrapItem
import com.scrap2025.scrap2025.model.SortDirection
import com.scrap2025.scrap2025.model.SortType
import com.scrap2025.scrap2025.model.ViewMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ScrapViewModel(application: Application) : AndroidViewModel(application) {
    private val preferencesManager = PreferencesManager(application)

    // 원본 스크랩 아이템 목록
    private val _scrapItems = MutableStateFlow<List<ScrapItem>>(ScrapDummyData.dummyScrapItems.toList())

    // 정렬 타입 (DataStore에서 로드)
    val sortType: StateFlow<SortType> = preferencesManager.sortType.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SortType.DATE
    )

    // 정렬 방향 (DataStore에서 로드)
    val sortDirection: StateFlow<SortDirection> = preferencesManager.sortDirection.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SortDirection.ASCENDING
    )

    // 뷰 모드 (DataStore에서 로드)
    val viewMode: StateFlow<ViewMode> = preferencesManager.viewMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ViewMode.LIST
    )

    // 정렬된 스크랩 아이템 목록 (sortType, sortDirection, 원본 데이터를 조합)
    val sortedScrapItems: StateFlow<List<ScrapItem>> = combine(
        _scrapItems,
        sortType,
        sortDirection
    ) { items, type, direction ->
        sortScrapItems(items, type, direction)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ScrapDummyData.dummyScrapItems.toList()
    )

    // 스크랩 아이템 정렬 로직
    private fun sortScrapItems(
        items: List<ScrapItem>,
        sortType: SortType,
        sortDirection: SortDirection
    ): List<ScrapItem> {
        Log.d("add-scrap", "sortScrapItems 진입")

        val sorted = when (sortType) {
            SortType.DATE -> {
                items.sortedBy { parseDate(it.createdDate) }
            }

            SortType.TITLE -> {
                items.sortedBy { it.title.lowercase() }
            }
        }

        return if (sortDirection == SortDirection.ASCENDING) {
            sorted
        } else {
            sorted.reversed()
        }
    }

    // 날짜 문자열을 Date 객체로 파싱 (yyyy.MM.dd 형식)
    private fun parseDate(dateString: String): Date {
        return try {
            val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            dateFormat.parse(dateString) ?: Date(0)
        } catch (e: Exception) {
            Date(0)
        }
    }

    // 정렬 타입 토글 (DATE ⇄ TITLE, 오름차순으로 리셋)
    fun toggleSortType() {
        viewModelScope.launch {
            val newSortType = if (sortType.value == SortType.DATE) {
                SortType.TITLE
            } else {
                SortType.DATE
            }
            preferencesManager.setSortType(newSortType)
            preferencesManager.setSortDirection(SortDirection.ASCENDING)
        }
    }

    // 정렬 방향 토글
    fun toggleSortDirection() {
        viewModelScope.launch {
            val newDirection = if (sortDirection.value == SortDirection.ASCENDING) {
                SortDirection.DESCENDING
            } else {
                SortDirection.ASCENDING
            }
            preferencesManager.setSortDirection(newDirection)
        }
    }

    // 뷰 모드 토글
    fun toggleViewMode() {
        viewModelScope.launch {
            val newViewMode = if (viewMode.value == ViewMode.LIST) {
                ViewMode.GRID
            } else {
                ViewMode.LIST
            }
            preferencesManager.setViewMode(newViewMode)
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

        // 새로운 리스트 참조를 생성하여 StateFlow가 변경을 감지하도록 함
        _scrapItems.value = _scrapItems.value + newItem
    }

    // 현재 날짜 반환 (yyyy.MM.dd 형식)
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
