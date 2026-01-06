package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrap2025.scrap2025.data.local.PreferencesManager
import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.model.ScrapItem
import com.scrap2025.scrap2025.model.enums.SortDirection
import com.scrap2025.scrap2025.model.enums.SortType
import com.scrap2025.scrap2025.model.enums.ViewMode
import com.scrap2025.scrap2025.repository.ScrapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel
@Inject constructor(
    private val scrapRepository: ScrapRepository, private val preferencesManager: PreferencesManager
) : ViewModel() {

    // 선택 모드 상태
    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode.asStateFlow()

    // 선택된 스크랩 아이템 ID 목록
    private val _selectedScrapIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedScrapIds: StateFlow<Set<String>> = _selectedScrapIds.asStateFlow()

    // Preferences 로딩 상태
    private val _isPreferencesLoaded = MutableStateFlow(false)
    val isPreferencesLoaded: StateFlow<Boolean> = _isPreferencesLoaded.asStateFlow()

    // query
    private val _queryState = MutableStateFlow("")
    val queryState: StateFlow<String> = _queryState.asStateFlow()

    // 정렬 타입 (DataStore에서 로드)
    val sortType: StateFlow<SortType> = preferencesManager.sortType.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SortType.SCRAP_DATE
    )

    // 정렬 방향 (DataStore에서 로드)
    val sortDirection: StateFlow<SortDirection> = preferencesManager.sortDirection.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SortDirection.ASC
    )

    // 뷰 모드 (DataStore에서 로드)
    val viewMode: StateFlow<ViewMode> = preferencesManager.viewMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ViewMode.LIST
    )

    /** FavoriteUiState - UI에 필요한 모든 상태를 하나의 객체로 관리 */
    data class FavoriteUiState(
        val scrapItemsResult: Result<List<ScrapItem>> = Result.Loading,
        val viewMode: ViewMode = ViewMode.LIST,
        val sortType: SortType = SortType.SCRAP_DATE,
        val sortDirection: SortDirection = SortDirection.ASC,
        val isSelectionMode: Boolean = false,
        val selectedScrapIds: Set<String> = emptySet(),
        val isPreferencesLoaded: Boolean = false,
        val query: String = ""
    )

    // Quartic 헬퍼 클래스 (4개 파라미터 combine용)
    data class Quartic<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

    // 정렬된 즐겨찾기 스크랩 아이템 목록
    val sortedFavoriteItems: StateFlow<Result<List<ScrapItem>>> =
        combine(queryState, sortType, sortDirection) { query, type, direction ->
            Triple(query, type, direction)
        }
            .distinctUntilChanged()
            .debounce(500L)
            .flatMapLatest { (query, type, direction) ->
                if (query.isBlank()) {
                    scrapRepository.getFavoriteScrapItemsFromRemote().map { result ->
                        when (result) {
                            is Result.Success -> {
                                val sortedItems = sortScrapItems(result.data, type, direction)
                                Result.Success(sortedItems)
                            }

                            is Result.Error -> result
                            is Result.Loading -> result
                        }
                    }
                } else {
                    flow<Result<List<ScrapItem>>> {
                        emit(Result.Loading)

                        val searchResult = scrapRepository.searchFavoriteScraps(
                            query = query, sortType = type.name, sortDirection = direction.name
                        )

                        when (searchResult) {
                            is Result.Success -> {
                                // SearchViewModel처럼 서버 결과를 그대로 쓰거나, 기존 Favorite의 로컬 정렬 로직을
                                // 유지할 수 있음
                                val sortedItems = sortScrapItems(searchResult.data, type, direction)
                                emit(Result.Success(sortedItems))
                            }

                            is Result.Error -> emit(searchResult)
                            is Result.Loading -> emit(Result.Loading)
                        }
                    }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Result.Loading
            )

    val preferenceFlow =
        combine(viewMode, sortType, sortDirection, isPreferencesLoaded) { mode, type, dir, loaded ->
            Quartic(mode, type, dir, loaded)
        }

    val selectionFlow = combine(isSelectionMode, selectedScrapIds) { selection, selectedIds ->
        selection to selectedIds
    }

    /** uiState - 모든 상태를 관찰하여 UI 레이어로 전달 */
    val uiState: StateFlow<FavoriteUiState> = combine(
        sortedFavoriteItems,
        preferenceFlow,
        selectionFlow,
        queryState
    ) { items, prefs, selection, query ->
        FavoriteUiState(
            scrapItemsResult = items,
            viewMode = prefs.a,
            sortType = prefs.b,
            sortDirection = prefs.c,
            isPreferencesLoaded = prefs.d,
            isSelectionMode = selection.first,
            selectedScrapIds = selection.second,
            query = query
        )
    }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FavoriteUiState()
        )

    init {
        viewModelScope.launch {
            combine(
                preferencesManager.sortType,
                preferencesManager.sortDirection,
                preferencesManager.viewMode
            ) { _, _, _ -> _isPreferencesLoaded.value = true }.take(1).collect()
        }
    }

    private fun sortScrapItems(
        items: List<ScrapItem>, sortType: SortType, sortDirection: SortDirection
    ): List<ScrapItem> {
        val sorted = when (sortType) {
            SortType.SCRAP_DATE -> items.sortedBy { it.createdDate }
            SortType.TITLE -> items.sortedBy { it.title.lowercase() }
        }
        return if (sortDirection == SortDirection.ASC) sorted else sorted.reversed()
    }

    fun toggleSortType() {
        viewModelScope.launch {
            val newSortType = if (sortType.value == SortType.SCRAP_DATE) SortType.TITLE
            else SortType.SCRAP_DATE
            preferencesManager.setSortType(newSortType)
            preferencesManager.setSortDirection(SortDirection.ASC)
        }
    }

    fun toggleSortDirection() {
        viewModelScope.launch {
            val newDirection = if (sortDirection.value == SortDirection.ASC) SortDirection.DESC
            else SortDirection.ASC
            preferencesManager.setSortDirection(newDirection)
        }
    }

    fun toggleViewMode() {
        viewModelScope.launch {
            val newViewMode = if (viewMode.value == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST
            preferencesManager.setViewMode(newViewMode)
        }
    }

    fun enterSelectionMode(itemId: String) {
        _isSelectionMode.value = true
        _selectedScrapIds.value = setOf(itemId)
    }

    fun exitSelectionMode() {
        _isSelectionMode.value = false
        _selectedScrapIds.value = emptySet()
    }

    fun toggleScrapItemSelection(id: String) {
        val currentSelection = _selectedScrapIds.value
        _selectedScrapIds.value =
            if (currentSelection.contains(id)) currentSelection - id else currentSelection + id
    }

    fun selectAllScrapItems() {
        val items = (sortedFavoriteItems.value as? Result.Success)?.data ?: return
        _selectedScrapIds.value = items.map { it.id }.toSet()
    }

    fun deselectAllScrapItems() {
        _selectedScrapIds.value = emptySet()
    }

    fun deleteSelectedItems() {
        viewModelScope.launch {
            _selectedScrapIds.value.forEach { id -> scrapRepository.deleteScrapItem(id) }
            exitSelectionMode()
        }
    }

    fun toggleFavoriteSelectedItems() {
        viewModelScope.launch {
            _selectedScrapIds.value.forEach { id -> scrapRepository.toggleFavorite(id) }
            exitSelectionMode()
        }
    }

    fun onQueryChange(newQuery: String) {
        _queryState.value = newQuery
    }
}
