package com.scrap2025.scrap2025.ui.scrap.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.scrap2025.scrap2025.model.CategoryItem
import com.scrap2025.scrap2025.model.GlobalUiState
import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.model.ScrapItem
import com.scrap2025.scrap2025.model.enums.SortDirection
import com.scrap2025.scrap2025.model.enums.SortType
import com.scrap2025.scrap2025.model.enums.ViewMode
import com.scrap2025.scrap2025.ui.common.components.LoadingScreen
import com.scrap2025.scrap2025.ui.common.components.SortBar
import com.scrap2025.scrap2025.ui.common.dialogs.CommonDeleteDialog
import com.scrap2025.scrap2025.ui.scrap.components.ScrapFloatingButtons
import com.scrap2025.scrap2025.ui.scrap.components.ScrapListContent
import com.scrap2025.scrap2025.ui.scrap.components.ScrapSearchBar
import com.scrap2025.scrap2025.ui.scrap.components.ScrapSelectionBottomBar
import com.scrap2025.scrap2025.ui.scrap.components.ScrapTopBar
import com.scrap2025.scrap2025.ui.scrap.components.SelectionTopBar
import com.scrap2025.scrap2025.ui.theme.BackgroundColor
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import com.scrap2025.scrap2025.utils.isScrolled
import com.scrap2025.scrap2025.viewmodel.ScrapViewModel

/** ScrapScreen - Container Composable ViewModel에서 상태를 추출하여 ScrapScreenContent에 전달 */
@Composable
fun ScrapScreen(
    navigateToAddScrap: () -> Unit,
    navigateToCategory: () -> Unit,
    navigateToScrapDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    scrapViewModel: ScrapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val globalCategoryId by GlobalUiState.selectedCategoryId.collectAsState()
    val globalCategoryName by GlobalUiState.selectedCategoryName.collectAsState()

    var categoryTitle by remember(globalCategoryName) { mutableStateOf(globalCategoryName) }
    var isCategoryDeleting by remember { mutableStateOf(false) }

    val scrapItemsResult by scrapViewModel.sortedScrapItems.collectAsState()
    val viewMode by scrapViewModel.viewMode.collectAsState()
    val sortType by scrapViewModel.sortType.collectAsState()
    val sortDirection by scrapViewModel.sortDirection.collectAsState()
    val isSelectionMode by scrapViewModel.isSelectionMode.collectAsState()
    val selectedScrapIds by scrapViewModel.selectedScrapIds.collectAsState()
    val isPreferencesLoaded by scrapViewModel.isPreferencesLoaded.collectAsState()
    val queryState by scrapViewModel.queryState.collectAsState()

    // Compose UI 상태
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    // 파생 상태: 스크롤 위치에서 버튼 표시 여부 계산
    val showScrollToTop by remember(viewMode) {
        derivedStateOf {
            when (viewMode) {
                ViewMode.LIST -> listState.isScrolled
                ViewMode.GRID -> gridState.isScrolled
            }
        }
    }

    // 1. 카테고리 데이터 동기화
    SyncCategory(globalCategoryId, scrapViewModel)

    // 2. 선택 모드 바텀바 제어
    SetSelectionBottomBar(isSelectionMode, scrapViewModel)

    // 3. 카테고리 삭제 이벤트 처리
    HandleCategoryDeleteEvents(
        scrapViewModel = scrapViewModel,
        context = context,
        onDeleteSuccess = navigateToCategory,
        onLoadingToggle = { isCategoryDeleting = it }
    )

    // 뒤로가기 버튼 처리: 선택 모드일 때는 선택 모드 종료
    BackHandler(enabled = isSelectionMode) { scrapViewModel.exitSelectionMode() }

    if (isCategoryDeleting) {
        LoadingScreen()
    } else {
        ScrapScreenContent(
            scrapItemsResult = scrapItemsResult,
            viewMode = viewMode,
            isPreferencesLoaded = isPreferencesLoaded,
            isSelectionMode = isSelectionMode,
            selectedScrapIds = selectedScrapIds,
            onItemClick = { scrapId -> navigateToScrapDetail(scrapId) },
            onItemLongClick = { itemId -> scrapViewModel.enterSelectionMode(itemId) },
            onItemSelectionToggle = { itemId ->
                scrapViewModel.toggleScrapItemSelection(itemId)
            },
            listState = listState,
            gridState = gridState,
            topBar = {
                if (isSelectionMode) {
                    val totalCount =
                        when (val result = scrapItemsResult) {
                            is Result.Success -> result.data.size
                            else -> 0
                        }
                    SelectionTopBar(
                        categoryTitle = categoryTitle,
                        selectedCount = selectedScrapIds.size,
                        totalCount = totalCount,
                        onSelectAll = { scrapViewModel.selectAllScrapItems() },
                        onDeselectAll = { scrapViewModel.deselectAllScrapItems() }
                    )
                } else {
                    ScrapTopBar(
                        categoryId = globalCategoryId,
                        categoryTitle = categoryTitle,
                        onUpdateCategory = { categoryId, newTitle ->
                            scrapViewModel.updateCategoryTitle(
                                id = categoryId,
                                newTitle = newTitle
                            )
                            GlobalUiState.setCategory(categoryId, newTitle)
                        },
                        onDeleteCategory = { showDeleteDialog = true }
                    )
                    ScrapSearchBar(queryState, { scrapViewModel.onQueryChange(it) })
                    SortBar(
                        sortType = sortType,
                        sortDirection = sortDirection,
                        viewMode = viewMode,
                        onSortTypeToggle = { scrapViewModel.toggleSortType() },
                        onSortDirectionToggle = { scrapViewModel.toggleSortDirection() },
                        onViewModeToggle = { scrapViewModel.toggleViewMode() }
                    )
                }
            },
            floatingActionButton = {
                ScrapFloatingButtons(
                    showScrollToTop = showScrollToTop,
                    viewMode = viewMode,
                    listState = listState,
                    gridState = gridState,
                    showAddScrapFab = true,
                    isSelectionMode = isSelectionMode,
                    onAddScrap = { navigateToAddScrap() }
                )
            },
            dialogs = {
                if (showDeleteDialog) {
                    CommonDeleteDialog(
                        title = "정말 카테고리를 삭제하시겠습니까?",
                        description = "(해당 카테고리에 속한 모든 스크랩 또한 삭제됩니다)",
                        confirmText = "삭제하기",
                        onDismiss = { showDeleteDialog = false },
                        onConfirm = {
                            showDeleteDialog = false
                            scrapViewModel.deleteCategory(globalCategoryId)
                        }
                    )
                }
            },
            modifier = modifier
        )
    }
}

/** ScrapScreenContent - Presentational Composable ViewModel 의존성 없이 순수한 데이터만 받아서 UI 렌더링 */
@Composable
fun ScrapScreenContent(
    scrapItemsResult: Result<List<ScrapItem>>,
    viewMode: ViewMode,
    isPreferencesLoaded: Boolean,
    isSelectionMode: Boolean,
    selectedScrapIds: Set<String>,
    onItemClick: (String) -> Unit,
    onItemLongClick: (String) -> Unit,
    onItemSelectionToggle: (String) -> Unit,
    listState: LazyListState,
    gridState: LazyGridState,
    topBar: @Composable () -> Unit,
    floatingActionButton: @Composable () -> Unit,
    dialogs: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
    showCategory: Boolean = false,
) {

    Box(modifier = modifier
        .fillMaxSize()
        .background(BackgroundColor)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 상단 슬롯 (제목, 검색, 정렬 또는 선택 바)
            topBar()

            // 스크랩 리스트/그리드
            ScrapListContent(
                showCategory = showCategory,
                scrapItemsResult = scrapItemsResult,
                viewMode = viewMode,
                isPreferencesLoaded = isPreferencesLoaded,
                isSelectionMode = isSelectionMode,
                selectedScrapIds = selectedScrapIds,
                onItemClick = onItemClick,
                onItemLongClick = onItemLongClick,
                onItemSelectionToggle = onItemSelectionToggle,
                listState = listState,
                gridState = gridState,
                modifier = Modifier.weight(1f)
            )
        }

        // 플로팅 버튼 슬롯
        floatingActionButton()

        // 다이얼로그 슬롯
        dialogs()
    }
}

@Preview(showBackground = true)
@Composable
fun ScrapScreenContentPreview() {
    Scrap2025Theme {
        ScrapScreenContent(
            scrapItemsResult = Result.Success(emptyList()),
            viewMode = ViewMode.GRID,
            isSelectionMode = false,
            selectedScrapIds = emptySet(),
            isPreferencesLoaded = true,
            onItemClick = {},
            onItemLongClick = {},
            onItemSelectionToggle = {},
            listState = rememberLazyListState(),
            gridState = rememberLazyGridState(),
            topBar = {
                ScrapTopBar(
                    categoryId = "1",
                    categoryTitle = "분류되지 않음",
                    onUpdateCategory = { _, _ -> },
                    onDeleteCategory = {}
                )
                ScrapSearchBar("", {})
                SortBar(
                    sortType = SortType.SCRAP_DATE,
                    sortDirection = SortDirection.ASC,
                    viewMode = ViewMode.GRID,
                    onSortTypeToggle = {},
                    onSortDirectionToggle = {},
                    onViewModeToggle = {}
                )
            },
            floatingActionButton = {
                ScrapFloatingButtons(
                    showScrollToTop = false,
                    viewMode = ViewMode.GRID,
                    listState = rememberLazyListState(),
                    gridState = rememberLazyGridState(),
                    showAddScrapFab = true,
                    isSelectionMode = false,
                    onAddScrap = {}
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ScrapScreenContentSelectionModePreview() {
    Scrap2025Theme {
        ScrapScreenContent(
            scrapItemsResult = Result.Success(emptyList()),
            viewMode = ViewMode.LIST,
            isSelectionMode = true,
            selectedScrapIds = emptySet(),
            isPreferencesLoaded = true,
            onItemClick = {},
            onItemLongClick = {},
            onItemSelectionToggle = {},
            listState = rememberLazyListState(),
            gridState = rememberLazyGridState(),
            topBar = {
                SelectionTopBar(
                    categoryTitle = "분류되지 않음",
                    selectedCount = 0,
                    totalCount = 0,
                    onSelectAll = {},
                    onDeselectAll = {}
                )
            },
            floatingActionButton = {}
        )
    }
}

@Composable
private fun SyncCategory(categoryId: String, scrapViewModel: ScrapViewModel) {
    LaunchedEffect(categoryId) {
        scrapViewModel.setSelectedCategory(categoryId)
        scrapViewModel.fetchScraps(categoryId)
    }
}

@Composable
private fun SetSelectionBottomBar(isSelectionMode: Boolean, scrapViewModel: ScrapViewModel) {
    LaunchedEffect(isSelectionMode) {
        if (isSelectionMode) {
            GlobalUiState.setBottomBar {
                ScrapSelectionBottomBar(
                    onDelete = { scrapViewModel.deleteSelectedItems() },
                    onMove = { /* todo */ },
                    onShare = { /* todo */ },
                    onFavorite = { onSuccess, onFailure ->
                        scrapViewModel.toggleFavoriteSelectedItems(onSuccess, onFailure)
                    }
                )
            }
        } else {
            GlobalUiState.setBottomBar(null)
        }
    }
}

@Composable
private fun HandleCategoryDeleteEvents(
    scrapViewModel: ScrapViewModel,
    context: android.content.Context,
    onDeleteSuccess: () -> Unit,
    onLoadingToggle: (Boolean) -> Unit
) {
    LaunchedEffect(scrapViewModel.categoryDeleteEvent) {
        scrapViewModel.categoryDeleteEvent.collect { result ->
            when (result) {
                Result.Loading -> onLoadingToggle(true)
                is Result.Success -> {
                    onLoadingToggle(false)
                    GlobalUiState.setCategory(CategoryItem.DEFAULT_ID, CategoryItem.DEFAULT_NAME)
                    onDeleteSuccess()
                }

                is Result.Error -> {
                    onLoadingToggle(false)
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
