package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrap2025.scrap2025.data.local.PreferencesManager
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
import kotlinx.coroutines.flow.onStart
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
    private val _selectedScrapIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedScrapIds: StateFlow<Set<Long>> = _selectedScrapIds.asStateFlow()

    // Preferences 로딩 상태
    private val _isPreferencesLoaded = MutableStateFlow(false)
    val isPreferencesLoaded: StateFlow<Boolean> = _isPreferencesLoaded.asStateFlow()

    // query
    private val _queryState = MutableStateFlow("")
    val queryState: StateFlow<String> = _queryState.asStateFlow()

    private val debouncedQuery =
        queryState.debounce { query -> if (query.isEmpty()) 0L else 500L }.distinctUntilChanged()

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
    data class FavoriteState(
        val scrapItemsState: ScrapUiState = ScrapUiState.Loading,
        val viewMode: ViewMode = ViewMode.LIST,
        val sortType: SortType = SortType.SCRAP_DATE,
        val sortDirection: SortDirection = SortDirection.ASC,
        val isSelectionMode: Boolean = false,
        val selectedScrapIds: Set<Long> = emptySet(),
        val isPreferencesLoaded: Boolean = false,
        val query: String = ""
    )

    // Quartic 헬퍼 클래스 (4개 파라미터 combine용)
    data class Quartic<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

    // 정렬된 즐겨찾기 스크랩 아이템 목록
    val sortedFavoriteItems: StateFlow<ScrapUiState> = combine(
        debouncedQuery, sortType, sortDirection, scrapRepository.refreshEvent
    ) { query, type, direction, _ ->
        Triple(query, type, direction)
    }.flatMapLatest { (query, type, direction) ->
        if (query.isBlank()) {
            scrapRepository.getAllFavoriteScraps(
                sort = type.name, direction = direction.name
            ).map { result ->
                result.fold(onSuccess = { items ->
                    ScrapUiState.Success(
                        sortScrapItems(items, type, direction)
                    )
                }, onFailure = { ScrapUiState.Error(it.message) })
            }.onStart { emit(ScrapUiState.Loading) }
        } else {
            flow {
                emit(ScrapUiState.Loading)

                val searchResult = scrapRepository.searchFavoriteScraps(
                    query = query, sortType = type.name, sortDirection = direction.name
                )

                searchResult.fold(onSuccess = { items ->
                    emit(
                        ScrapUiState.Success(
                            sortScrapItems(items, type, direction)
                        )
                    )
                }, onFailure = { emit(ScrapUiState.Error(it.message)) })
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ScrapUiState.Loading
    )

    val preferenceFlow =
        combine(viewMode, sortType, sortDirection, isPreferencesLoaded) { mode, type, dir, loaded ->
            Quartic(mode, type, dir, loaded)
        }

    val selectionFlow = combine(isSelectionMode, selectedScrapIds) { selection, selectedIds ->
        selection to selectedIds
    }

    /** uiState - 모든 상태를 관찰하여 UI 레이어로 전달 */
    val uiState: StateFlow<FavoriteState> = combine(
        sortedFavoriteItems, preferenceFlow, selectionFlow, queryState
    ) { items, prefs, selection, query ->
        FavoriteState(
            scrapItemsState = items,
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
        initialValue = FavoriteState()
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

    fun enterSelectionMode(itemId: Long) {
        _isSelectionMode.value = true
        _selectedScrapIds.value = setOf(itemId)
    }

    fun exitSelectionMode() {
        _isSelectionMode.value = false
        _selectedScrapIds.value = emptySet()
    }

    fun toggleScrapItemSelection(id: Long) {
        val currentSelection = _selectedScrapIds.value
        _selectedScrapIds.value =
            if (currentSelection.contains(id)) currentSelection - id else currentSelection + id
    }

    fun selectAllScrapItems() {
        val state = sortedFavoriteItems.value
        if (state is ScrapUiState.Success) {
            _selectedScrapIds.value = state.items.map { it.id }.toSet()
        }
    }

    fun deselectAllScrapItems() {
        _selectedScrapIds.value = emptySet()
    }

    fun deleteSelectedItems() {
        viewModelScope.launch {
            scrapRepository.deleteScrapBulk(_selectedScrapIds.value.toList())
            exitSelectionMode()
        }
    }

    fun toggleFavoriteSelectedItems(onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            val result = scrapRepository.toggleFavoriteBulk(_selectedScrapIds.value.toList())
            result.fold(onSuccess = {
                onSuccess()
                exitSelectionMode()
            }, onFailure = { onFailure() })
        }
    }

    fun getSelectedScraps(): List<ScrapItem> {
        val state = sortedFavoriteItems.value
        if (state is ScrapUiState.Success) {
            return state.items.filter { it.id in _selectedScrapIds.value }
        }
        return emptyList()
    }

    fun onQueryChange(newQuery: String) {
        _queryState.value = newQuery
    }
}
