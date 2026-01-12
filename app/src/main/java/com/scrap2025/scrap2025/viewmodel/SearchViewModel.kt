package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.scrap2025.scrap2025.model.CategoryItem
import com.scrap2025.scrap2025.model.enums.SearchScope
import com.scrap2025.scrap2025.model.enums.SortDirection
import com.scrap2025.scrap2025.model.enums.SortType
import com.scrap2025.scrap2025.model.enums.ViewMode
import com.scrap2025.scrap2025.repository.CategoryRepository
import com.scrap2025.scrap2025.repository.ScrapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date
import java.util.Locale
import java.util.TimeZone.getTimeZone
import javax.inject.Inject

data class SearchState(
    val query: String = "",
    val searchRanges: Set<String> = setOf(SearchScope.TITLE.value),
    val selectedCategoryIds: List<Long> = emptyList(),
    val startDate: String = "",
    val endDate: String = "",
    val sortType: SortType = SortType.SCRAP_DATE,
    val sortDirection: SortDirection = SortDirection.ASC,
    val viewMode: ViewMode = ViewMode.LIST,
    val searchResults: ScrapUiState = ScrapUiState.Success(emptyList())
)

sealed interface SearchWarning {
    data object ShowMinRangeWarning : SearchWarning
}

@HiltViewModel
class SearchViewModel
@Inject
constructor(categoryRepository: CategoryRepository, private val scrapRepository: ScrapRepository) :
    ViewModel() {
    // picker에서 사용할 '오늘' 날짜의 UTC 00:00 밀리초 계산
    val nowMillis = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    private val _uiState = MutableStateFlow(SearchState())
    val uiState: StateFlow<SearchState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<SearchWarning>()
    val effect: SharedFlow<SearchWarning> = _effect.asSharedFlow()

    // ID 리스트와 전체 카테고리 목록을 결합하여 UI용 리스트 생성
    val selectedCategoryItems: StateFlow<List<CategoryItem>> =
        combine(
            categoryRepository.allCategories,
            uiState.map { it.selectedCategoryIds }.distinctUntilChanged()
        ) { categoriesResult, selectedIds ->
            categoriesResult.fold(
                onSuccess = { allCategories ->
                    selectedIds.mapNotNull { id ->
                        allCategories.find { it.id == id }
                    }
                },
                onFailure = { emptyList<CategoryItem>() }
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = emptyList()
            )

    init {
        // 초기 날짜 선택값을 오늘 -> 오늘 로 설정
        onDateChange(formatMillisToDate(nowMillis), formatMillisToDate(nowMillis))

        // 검색 조건 관찰: 쿼리, 범위, 카테고리, 날짜, 정렬 중 하나라도 바뀌면 검색 수행
        viewModelScope.launch {
            uiState
                .map {
                    // 검색 결과(searchResults)만 제외하고 나머지를 묶음
                    Triple(
                        Triple(it.query, it.searchRanges, it.selectedCategoryIds),
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
        val currentState = _uiState.value
        if (currentState.searchRanges.contains(range) && currentState.searchRanges.size == 1) {
            viewModelScope.launch { _effect.emit(SearchWarning.ShowMinRangeWarning) }
            return
        }

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

    fun removeCategory(categoryId: Long) {
        _uiState.update { state ->
            state.copy(selectedCategoryIds = state.selectedCategoryIds - categoryId)
        }
    }

    fun setSelectedCategories(categoryIds: List<Long>) {
        _uiState.update { it.copy(selectedCategoryIds = categoryIds) }
    }

    fun onDateChange(start: String, end: String) {
        _uiState.update { it.copy(startDate = start, endDate = end) }
    }

    fun toggleSortType() {
        _uiState.update { state ->
            val newType =
                if (state.sortType == SortType.SCRAP_DATE) SortType.TITLE
                else SortType.SCRAP_DATE
            state.copy(sortType = newType, sortDirection = SortDirection.ASC)
        }
    }

    fun toggleSortDirection() {
        _uiState.update { state ->
            val newDirection =
                if (state.sortDirection == SortDirection.ASC) {
                    SortDirection.DESC
                } else {
                    SortDirection.ASC
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

    /** 서버와의 통신을 통해 검색 결과를 페이징하여 가져옵니다. */
    fun performSearch() {
        val state = _uiState.value

        // 쿼리가 비어있으면 통신하지 않고 결과를 즉시 비움
        if (state.query.isBlank()) {
            _uiState.update { it.copy(searchResults = ScrapUiState.Success(emptyList())) }
            return
        }

        val pagingFlow =
            scrapRepository
                .getSearchScrapPagingFlow(
                    query = state.query,
                    searchScope = state.searchRanges.toList(),
                    categoryRemoteIds = state.selectedCategoryIds,
                    startDate = state.startDate,
                    endDate = state.endDate,
                    sortType = state.sortType.name,
                    sortDirection = state.sortDirection.name
                )
                .cachedIn(viewModelScope)

        _uiState.update { it.copy(searchResults = ScrapUiState.Paged(pagingFlow)) }

        // 로그를 통해 서버로 전달될 파라미터 확인 (디버깅용)
        println(
            """
            [Server Search Request]
            Query: ${state.query}
            Ranges: ${state.searchRanges}
            Categories: ${state.selectedCategoryIds}
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
