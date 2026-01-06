package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrap2025.scrap2025.data.local.PreferencesManager
import com.scrap2025.scrap2025.model.CategoryItem
import com.scrap2025.scrap2025.model.GlobalUiState
import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.model.ScrapItem
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
class ScrapViewModel
@Inject
constructor(
    private val categoryRepository: CategoryRepository,
    private val scrapRepository: ScrapRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    // 선택 모드 상태
    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode.asStateFlow()

    // 선택된 카테고리 ID
    private val _selectedCategoryId = MutableStateFlow<String>(CategoryItem.DEFAULT_ID)
    val selectedCategoryId: StateFlow<String> = _selectedCategoryId.asStateFlow()

    // 선택된 스크랩 아이템 ID 목록
    private val _selectedScrapIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedScrapIds: StateFlow<Set<String>> = _selectedScrapIds.asStateFlow()

    // Preferences 로딩 상태
    private val _isPreferencesLoaded = MutableStateFlow(false)
    val isPreferencesLoaded: StateFlow<Boolean> = _isPreferencesLoaded.asStateFlow()

    // 카테고리 삭제 이벤트
    private val _categoryDeleteEvent = MutableSharedFlow<Result<Unit>>()
    val categoryDeleteEvent: SharedFlow<Result<Unit>> = _categoryDeleteEvent.asSharedFlow()

    // query
    private val _queryState = MutableStateFlow("")
    val queryState: StateFlow<String> = _queryState.asStateFlow()

    // 정렬 타입 (DataStore에서 로드)
    val sortType: StateFlow<SortType> =
        preferencesManager.sortType.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SortType.SCRAP_DATE
        )

    // 정렬 방향 (DataStore에서 로드)
    val sortDirection: StateFlow<SortDirection> =
        preferencesManager.sortDirection.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SortDirection.ASC
        )

    // 뷰 모드 (DataStore에서 로드)
    val viewMode: StateFlow<ViewMode> =
        preferencesManager.viewMode.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ViewMode.LIST
        )

    // Quartic 헬퍼 클래스 (4개 파라미터 combine용)
    data class Quartic<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

    /** ScrapUiState - UI에 필요한 모든 상태를 하나의 객체로 관리 */
    data class ScrapUiState(
        val scrapItemsResult: Result<List<ScrapItem>> = Result.Loading,
        val categoryId: String = CategoryItem.DEFAULT_ID,
        val categoryName: String = CategoryItem.DEFAULT_NAME,
        val viewMode: ViewMode = ViewMode.LIST,
        val sortType: SortType = SortType.SCRAP_DATE,
        val sortDirection: SortDirection = SortDirection.ASC,
        val isSelectionMode: Boolean = false,
        val selectedScrapIds: Set<String> = emptySet(),
        val isPreferencesLoaded: Boolean = false,
        val query: String = ""
    )

    // 정렬된 스크랩 아이템 목록 (Repository, sortType, sortDirection, selectedCategory 조합)
    val sortedScrapItems: StateFlow<Result<List<ScrapItem>>> = combine(
        _selectedCategoryId,
        queryState,
        sortType,
        sortDirection
    ) { categoryId, query, type, direction ->
        Quartic(categoryId, query, type, direction)
    }
        .distinctUntilChanged()
        .debounce(500L)
        .flatMapLatest { (categoryId, query, type, direction) ->
            if (query.isBlank()) {
                scrapRepository.getScrapItems(categoryId).map { result ->
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
                    val categoryResult = categoryRepository.getCategoryById(categoryId)
                    if (categoryResult is Result.Success) {
                        val remoteId = categoryResult.data.remoteId?.toLong()
                        if (remoteId != null) {
                            val searchResult =
                                scrapRepository.searchScrapsByCategory(
                                    categoryRemoteId = remoteId,
                                    query = query,
                                    sortType = type.name,
                                    sortDirection = direction.name
                                )
                            when (searchResult) {
                                is Result.Success -> {
                                    emit(
                                        Result.Success(
                                            sortScrapItems(
                                                searchResult.data,
                                                type,
                                                direction
                                            )
                                        )
                                    )
                                }

                                is Result.Error -> emit(searchResult)
                                is Result.Loading -> emit(Result.Loading)
                            }
                        } else {
                            emit(Result.Success(emptyList()))
                        }
                    } else {
                        emit(Result.Error(Exception("Category not found")))
                    }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.Loading
        )

    val categoryFlow = combine(
        GlobalUiState.selectedCategoryId,
        GlobalUiState.selectedCategoryName
    ) { id, name -> id to name }

    val preferenceFlow = combine(
        viewMode,
        sortType,
        sortDirection,
        isPreferencesLoaded
    ) { mode, type, dir, loaded -> Quartic(mode, type, dir, loaded) }

    val selectionFlow = combine(
        isSelectionMode,
        selectedScrapIds
    ) { selection, selectedIds -> selection to selectedIds }


    /** uiState - 모든 상태를 관찰하여 UI 레이어로 전달 (8개 이상의 Flow를 위해 그룹화) */
    val uiState: StateFlow<ScrapUiState> = combine(
        sortedScrapItems,
        categoryFlow,
        preferenceFlow,
        selectionFlow,
        queryState
    ) { items, category, prefs, selection, query ->
        ScrapUiState(
            scrapItemsResult = items,
            categoryId = category.first,
            categoryName = category.second,
            viewMode = prefs.a,
            sortType = prefs.b,
            sortDirection = prefs.c,
            isPreferencesLoaded = prefs.d,
            isSelectionMode = selection.first,
            selectedScrapIds = selection.second,
            query = query
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ScrapUiState()
        )

    init {
        // DataStore에서 모든 preference가 로드되면 isPreferencesLoaded를 true로 설정
        viewModelScope.launch {
            combine(
                preferencesManager.sortType,
                preferencesManager.sortDirection,
                preferencesManager.viewMode
            ) { _, _, _ ->
                _isPreferencesLoaded.value = true // DataStore 로드 후 실행
            }
                .take(1)
                .collect() // 1번만 실행
        }
    }

    // 스크랩 아이템 정렬 로직
    private fun sortScrapItems(
        items: List<ScrapItem>,
        sortType: SortType,
        sortDirection: SortDirection
    ): List<ScrapItem> {
        val sorted =
            when (sortType) {
                SortType.SCRAP_DATE -> {
                    items.sortedBy { it.createdDate }
                }

                SortType.TITLE -> {
                    items.sortedBy { it.title.lowercase() }
                }
            }

        return if (sortDirection == SortDirection.ASC) {
            sorted
        } else {
            sorted.reversed()
        }
    }

    // 정렬 타입 토글 (DATE ⇄ TITLE, 오름차순으로 리셋)
    fun toggleSortType() {
        viewModelScope.launch {
            val newSortType =
                if (sortType.value == SortType.SCRAP_DATE) {
                    SortType.TITLE
                } else {
                    SortType.SCRAP_DATE
                }
            preferencesManager.setSortType(newSortType)
            preferencesManager.setSortDirection(SortDirection.ASC)
        }
    }

    // 정렬 방향 토글
    fun toggleSortDirection() {
        viewModelScope.launch {
            val newDirection =
                if (sortDirection.value == SortDirection.ASC) {
                    SortDirection.DESC
                } else {
                    SortDirection.ASC
                }
            preferencesManager.setSortDirection(newDirection)
        }
    }

    // 뷰 모드 토글
    fun toggleViewMode() {
        viewModelScope.launch {
            val newViewMode =
                if (viewMode.value == ViewMode.LIST) {
                    ViewMode.GRID
                } else {
                    ViewMode.LIST
                }
            preferencesManager.setViewMode(newViewMode)
        }
    }

    // 선택 모드 진입 (롱클릭 시)
    fun enterSelectionMode(itemId: String) {
        _isSelectionMode.value = true
        _selectedScrapIds.value = setOf(itemId)
    }

    // 선택 모드 종료
    fun exitSelectionMode() {
        _isSelectionMode.value = false
        _selectedScrapIds.value = emptySet()
    }

    // 개별 아이템 선택 토글
    fun toggleScrapItemSelection(id: String) {
        val currentSelection = _selectedScrapIds.value
        _selectedScrapIds.value =
            if (currentSelection.contains(id)) {
                currentSelection - id
            } else {
                currentSelection + id
            }
    }

    // 전체 선택
    fun selectAllScrapItems() {
        val items = (sortedScrapItems.value as? Result.Success)?.data ?: return
        _selectedScrapIds.value = items.map { it.id }.toSet()
    }

    // 전체 선택 해제
    fun deselectAllScrapItems() {
        _selectedScrapIds.value = emptySet()
    }

    // 선택된 아이템 삭제
    fun deleteSelectedItems() {
        viewModelScope.launch {
            _selectedScrapIds.value.forEach { id -> scrapRepository.deleteScrapItem(id) }
            exitSelectionMode()
        }
    }

    // 선택된 아이템 이동
    fun moveSelectedItems(categoryId: String) {
        viewModelScope.launch {
            _selectedScrapIds.value.forEach { id -> scrapRepository.moveScrapItem(id, categoryId) }
            // Todo: 구현 예정
            exitSelectionMode()
        }
    }

    // 선택된 아이템 공유
    fun shareSelectedItems(): List<ScrapItem> {
        val items = (sortedScrapItems.value as? Result.Success)?.data ?: return emptyList()
        return items.filter { it.id in _selectedScrapIds.value }
        // todo: 구현 예정
    }

    // 선택된 아이템 즐겨찾기 토글
    fun toggleFavoriteSelectedItems(onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            val scrapIdBulk = _selectedScrapIds.value.toList()
            val result = scrapRepository.toggleFavoriteBulk(scrapIdBulk)

            when (result) {
                is Result.Success -> {
                    onSuccess()
                    exitSelectionMode()
                }

                is Result.Error -> {
                    onFailure()
                }

                else -> {}
            }
        }
    }

    // 카테고리 선택
    fun setSelectedCategory(categoryId: String) {
        _selectedCategoryId.value = categoryId
    }

    /**
     * 카테고리명 업데이트
     * @param id 업데이트할 카테고리 ID
     * @param newTitle 새로운 카테고리명
     */
    fun updateCategoryTitle(id: String, newTitle: String) {
        viewModelScope.launch {
            categoryRepository.updateCategory(
                id = id,
                name = newTitle,
            )
        }
    }

    /**
     * 카테고리 삭제
     * @param id 삭제할 카테고리 ID
     */
    fun deleteCategory(id: String) {
        viewModelScope.launch {
            _categoryDeleteEvent.emit(Result.Loading)
            // Repository 내에서 트랜잭션으로 처리 (스크랩 이동 + 카테고리 삭제)
            val result =
                categoryRepository.deleteCategory(
                    id = id,
                )
            _categoryDeleteEvent.emit(result)
        }
    }

    fun fetchScraps(categoryId: String) {
        viewModelScope.launch {
            val categoryResult = categoryRepository.getCategoryById(categoryId)

            if (categoryResult is Result.Success) {
                val remoteId = categoryResult.data.remoteId ?: return@launch

                val syncScrapsResult = scrapRepository.syncScrapsByCategoryId(categoryId, remoteId)

                when (syncScrapsResult) {
                    Result.Loading -> {}
                    is Result.Error -> {
                        throw syncScrapsResult.exception
                    }

                    is Result.Success -> {}
                }
            }
        }
    }

    fun onQueryChange(newQuery: String) {
        _queryState.value = newQuery
    }
}
