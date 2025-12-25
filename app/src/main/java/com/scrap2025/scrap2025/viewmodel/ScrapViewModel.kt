package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrap2025.scrap2025.data.local.PreferencesManager
import com.scrap2025.scrap2025.data.local.TokenManager
import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.model.ScrapItem
import com.scrap2025.scrap2025.model.SortDirection
import com.scrap2025.scrap2025.model.SortType
import com.scrap2025.scrap2025.model.ViewMode
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
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
    private val tokenManager: TokenManager,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    // 선택 모드 상태
    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode.asStateFlow()

    // 선택된 카테고리 ID (null이면 전체)
    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    // 선택된 스크랩 아이템 ID 목록
    private val _selectedScrapIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedScrapIds: StateFlow<Set<String>> = _selectedScrapIds.asStateFlow()

    // Preferences 로딩 상태
    private val _isPreferencesLoaded = MutableStateFlow(false)
    val isPreferencesLoaded: StateFlow<Boolean> = _isPreferencesLoaded.asStateFlow()

    // 카테고리 삭제 이벤트
    private val _categoryDeleteEvent = MutableSharedFlow<Result<Unit>>()
    val categoryDeleteEvent: SharedFlow<Result<Unit>> = _categoryDeleteEvent.asSharedFlow()

    // 정렬 타입 (DataStore에서 로드)
    val sortType: StateFlow<SortType> =
        preferencesManager.sortType.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SortType.DATE
        )

    // 정렬 방향 (DataStore에서 로드)
    val sortDirection: StateFlow<SortDirection> =
        preferencesManager.sortDirection.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SortDirection.ASCENDING
        )

    // 뷰 모드 (DataStore에서 로드)
    val viewMode: StateFlow<ViewMode> =
        preferencesManager.viewMode.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ViewMode.LIST
        )

    // 정렬된 스크랩 아이템 목록 (Repository, sortType, sortDirection, selectedCategory 조합)
    val sortedScrapItems: StateFlow<Result<List<ScrapItem>>> =
        combine(_selectedCategoryId, sortType, sortDirection) { categoryId, type, direction ->
            Triple(categoryId, type, direction)
        }
            .flatMapLatest { (categoryId, type, direction) ->
                // categoryId가 null이면 전체 조회, null이 아니면 해당 카테고리만 조회
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
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Result.Loading
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
                SortType.DATE -> {
                    items.sortedBy { it.createdDate }
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

    // 정렬 타입 토글 (DATE ⇄ TITLE, 오름차순으로 리셋)
    fun toggleSortType() {
        viewModelScope.launch {
            val newSortType =
                if (sortType.value == SortType.DATE) {
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
            val newDirection =
                if (sortDirection.value == SortDirection.ASCENDING) {
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
    fun toggleFavoriteSelectedItems() {
        viewModelScope.launch {
            _selectedScrapIds.value.forEach { id -> scrapRepository.toggleFavorite(id) }
            exitSelectionMode()
        }
    }

    // 카테고리 선택
    fun setSelectedCategory(categoryId: String?) {
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
                token = tokenManager.accessToken.firstOrNull()
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
            val token = tokenManager.accessToken.firstOrNull()
            // Repository 내에서 트랜잭션으로 처리 (스크랩 이동 + 카테고리 삭제)
            val result = categoryRepository.deleteCategory(
                id = id,
                token = token
            )
            _categoryDeleteEvent.emit(result)
        }
    }

    fun fetchScraps(categoryId: String) {
        viewModelScope.launch {
            val categoryResult = categoryRepository.getCategoryById(categoryId)

            if (categoryResult is Result.Success) {
                val remoteId = categoryResult.data.remoteId ?: return@launch
                val token = tokenManager.accessToken.firstOrNull() ?: return@launch

                scrapRepository.syncScrapsByCategoryId(token, categoryId, remoteId)
            }
        }
    }
}
