package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.model.ScrapItem
import com.scrap2025.scrap2025.model.SortDirection
import com.scrap2025.scrap2025.model.SortType
import com.scrap2025.scrap2025.model.ViewMode
import com.scrap2025.scrap2025.repository.ScrapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.TimeZone.getTimeZone

data class SearchUiState(
    val query: String = "",
    val searchRanges: Set<String> = setOf("제목"),
    val selectedCategories: List<String> = emptyList(),
    val startDate: String = "",
    val endDate: String = "",
    val sortType: SortType = SortType.DATE,
    val sortDirection: SortDirection = SortDirection.ASCENDING,
    val viewMode: ViewMode = ViewMode.LIST,
    val searchResults: Result<List<ScrapItem>> = Result.Success(emptyList())
)

@HiltViewModel
class SearchViewModel
@Inject
constructor(
    private val scrapRepository: ScrapRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    // picker에서 사용할 '오늘' 날짜의 UTC 00:00 밀리초 계산
    val nowMillis = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        onDateChange(formatMillisToDate(nowMillis), formatMillisToDate(nowMillis))

        // 카테고리 선택 결과 관찰 (SavedStateHandle 이용)
        viewModelScope.launch {
            savedStateHandle.getStateFlow<List<String>?>("selectedCategories", null)
                .collect { categories ->
                    if (categories != null) {
                        setSelectedCategories(categories)
                        // 처리가 완료되었으므로 결과 비우기 (다음번 진입 시 중복 처리 방지)
                        savedStateHandle.remove<List<String>>("selectedCategories")
                    }
                }
        }

        // 검색 조건 관찰: 쿼리, 범위, 카테고리, 날짜, 정렬 중 하나라도 바뀌면 검색 수행
        viewModelScope.launch {
            uiState
                .map {
                    // 검색 결과(searchResults)만 제외하고 나머지를 묶음
                    Triple(
                        Triple(it.query, it.searchRanges, it.selectedCategories),
                        Triple(it.startDate, it.endDate, it.sortType),
                        it.sortDirection
                    )
                }
                .distinctUntilChanged()
                .debounce(500L)
                .collect { performSearch() }
        }
    }

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
    }

    fun toggleSearchRange(range: String) {
        _uiState.update { state ->
            val newRanges =
                if (state.searchRanges.contains(range)) {
                    state.searchRanges - range
                } else {
                    state.searchRanges + range
                }
            state.copy(searchRanges = newRanges)
        }
    }

    fun removeCategory(category: String) {
        _uiState.update { state ->
            state.copy(selectedCategories = state.selectedCategories - category)
        }
    }

    fun addCategory(category: String) {
        _uiState.update { state ->
            if (!state.selectedCategories.contains(category)) {
                state.copy(selectedCategories = state.selectedCategories + category)
            } else {
                state
            }
        }
    }

    fun setSelectedCategories(categories: List<String>) {
        _uiState.update { it.copy(selectedCategories = categories) }
    }

    fun onDateChange(start: String, end: String) {
        _uiState.update { it.copy(startDate = start, endDate = end) }
    }

    fun toggleSortType() {
        _uiState.update { state ->
            val newType = if (state.sortType == SortType.DATE) SortType.TITLE else SortType.DATE
            state.copy(sortType = newType, sortDirection = SortDirection.ASCENDING)
        }
    }

    fun toggleSortDirection() {
        _uiState.update { state ->
            val newDirection =
                if (state.sortDirection == SortDirection.ASCENDING) {
                    SortDirection.DESCENDING
                } else {
                    SortDirection.ASCENDING
                }
            state.copy(sortDirection = newDirection)
        }
    }

    fun toggleViewMode() {
        _uiState.update { state ->
            val newMode = if (state.viewMode == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST
            state.copy(viewMode = newMode)
        }
    }

    /** 서버와의 통신을 시뮬레이션하거나 준비하는 함수 로컬 DB는 일절 사용하지 않으며, 모든 파라미터를 서버에 넘겨줄 준비를 합니다. */
    fun performSearch() {
        val state = _uiState.value

        // 실제 서버 통신 코드가 위치할 곳 (주석 처리)
        /*
        viewModelScope.launch {
            _uiState.update { it.copy(searchResults = Result.Loading) }
            val result = scrapRepository.searchScrapsRemote(
                query = state.query,
                ranges = state.searchRanges.toList(),
                categories = state.selectedCategories,
                startDate = state.startDate,
                endDate = state.endDate,
                sortType = state.sortType,
                sortDirection = state.sortDirection
            )
            _uiState.update { it.copy(searchResults = result) }
        }
        */

        // 로그를 통해 서버로 전달될 파라미터 확인 (디버깅용)
        println(
            """
            [Server Search Request]
            Query: ${state.query}
            Ranges: ${state.searchRanges}
            Categories: ${state.selectedCategories}
            Date: ${state.startDate} ~ ${state.endDate}
            Sort: ${state.sortType} (${state.sortDirection})
        """.trimIndent()
        )
    }

    fun formatMillisToDate(millis: Long): String {
        val date = Date(millis)
        val formatter =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
                timeZone = getTimeZone("UTC")
            }
        return formatter.format(date)
    }

    fun parseDateToMillis(dateStr: String): Long? {
        if (dateStr.isEmpty()) return null
        return try {
            val formatter =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
                    timeZone = getTimeZone("UTC")
                }
            formatter.parse(dateStr)?.time
        } catch (e: Exception) {
            null
        }
    }
}
