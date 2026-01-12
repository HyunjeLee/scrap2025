package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
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
        debouncedQuery,
        sortType,
        sortDirection,
        scrapRepository.refreshEvent
    ) { query, type, direction, _ ->
        Triple(query, type, direction)
    }.distinctUntilChanged().debounce(100L).flatMapLatest { (query, type, direction) ->
        if (query.isBlank()) {
            flow {
                emit(
                    ScrapUiState.Paged(
                        scrapRepository.getFavoriteScrapPagingFlow(
                            sort = type.name, direction = direction.name
                        ).cachedIn(viewModelScope)
                    )
                )
            }
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
        sortedFavoriteItems,
        preferenceFlow,
        selectionFlow,
        queryState
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
            preferencesManager.setSortTypeAndDirection(newSortType, SortDirection.ASC)
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

    fun selectAllScrapItems(ids: Set<Long>) {
        _selectedScrapIds.value = ids
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
        // 이 모드는 주로 삭제/이동/공유 시에 쓰이는데,
        // Paging 상태에서는 UI에서 이미 ID들을 관리하므로 리포지토리를 통해 상세 정보를 가져오는 등의 처리가 필요할 수 있습니다.
        // 현재는 선택된 ID들이 Set으로 ViewModel에 있으므로, UI에서 데이터를 넘겨주거나
        // 혹은 단순히 ID만 필요한 작업이라면 이 함수가 없어도 됩니다.
        // 하지만 공유 기능을 위해 ScrapItem 리스트가 필요하다면 UI에서 pagedItems를 통해 가져와야 합니다.
        // 우선은 빈 리스트를 반환하거나 에러를 방지하기 위해 Nullable로 유지합니다.
        return emptyList()
    }

    fun onQueryChange(newQuery: String) {
        _queryState.value = newQuery
    }
}
