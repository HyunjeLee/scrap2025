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
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.scrap2025.scrap2025.model.CategoryItem
import com.scrap2025.scrap2025.model.GlobalUiState
import com.scrap2025.scrap2025.model.Result
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

@Stable
class ScrapScreenState(
    val listState: LazyListState,
    val gridState: LazyGridState,
    val viewMode: ViewMode,
    initialCategoryName: String
) {
    var showDeleteDialog by mutableStateOf(false)
    var isCategoryDeleting by mutableStateOf(false)
    var categoryTitle by mutableStateOf(initialCategoryName)

    val showScrollToTop by derivedStateOf {
        when (viewMode) {
            ViewMode.LIST -> listState.isScrolled
            ViewMode.GRID -> gridState.isScrolled
        }
    }

    // 외부에서 카테고리 이름이 바뀌면 내부 타이틀도 동기화
    fun updateCategoryTitle(newName: String) {
        categoryTitle = newName
    }
}

@Composable
fun rememberScrapScreenState(
    viewMode: ViewMode,
    categoryName: String,
    listState: LazyListState = rememberLazyListState(),
    gridState: LazyGridState = rememberLazyGridState(),
): ScrapScreenState {
    val screenState =
        remember(listState, gridState, viewMode) {
            ScrapScreenState(listState, gridState, viewMode, categoryName)
        }

    // 카테고리가 달라지면 타이틀 업데이트
    LaunchedEffect(categoryName) { screenState.updateCategoryTitle(categoryName) }

    return screenState
}

@Composable
fun ScrapScreen(
    navigateToAddScrap: () -> Unit,
    navigateToCategory: () -> Unit,
    navigateToScrapDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    scrapViewModel: ScrapViewModel = hiltViewModel()
) {
    val uiState by scrapViewModel.uiState.collectAsState()
    val screenState = rememberScrapScreenState(
        viewMode = uiState.viewMode,
        categoryName = uiState.categoryName
    )

    SyncCategory(uiState.categoryId, scrapViewModel)
    SetSelectionBottomBar(uiState.isSelectionMode, scrapViewModel)
    HandleCategoryDeleteEvents(
        scrapViewModel = scrapViewModel,
        onDeleteSuccess = navigateToCategory,
        onLoadingToggle = { screenState.isCategoryDeleting = it }
    )
    BackHandler(enabled = uiState.isSelectionMode) { scrapViewModel.exitSelectionMode() }

    when (screenState.isCategoryDeleting) {
        true -> LoadingScreen()
        false -> ScrapScreenContent(
                topBar = {
                    if (uiState.isSelectionMode) {
                        val totalCount =
                            when (val result = uiState.scrapItemsResult) {
                                is Result.Success -> result.data.size
                                else -> 0
                            }
                        SelectionTopBar(
                            categoryTitle = screenState.categoryTitle,
                            selectedCount = uiState.selectedScrapIds.size,
                            totalCount = totalCount,
                            onSelectAll = { scrapViewModel.selectAllScrapItems() },
                            onDeselectAll = { scrapViewModel.deselectAllScrapItems() }
                        )
                    } else {
                        ScrapTopBar(
                            categoryId = uiState.categoryId,
                            categoryTitle = screenState.categoryTitle,
                            onUpdateCategory = { categoryId, newTitle ->
                                scrapViewModel.updateCategoryTitle(
                                    id = categoryId,
                                    newTitle = newTitle
                                )
                                GlobalUiState.setCategory(categoryId, newTitle)
                            },
                            onDeleteCategory = { screenState.showDeleteDialog = true }
                        )
                        ScrapSearchBar(uiState.query, { scrapViewModel.onQueryChange(it) })
                        SortBar(
                            sortType = uiState.sortType,
                            sortDirection = uiState.sortDirection,
                            viewMode = uiState.viewMode,
                            onSortTypeToggle = { scrapViewModel.toggleSortType() },
                            onSortDirectionToggle = {
                                scrapViewModel.toggleSortDirection()
                            },
                            onViewModeToggle = { scrapViewModel.toggleViewMode() }
                        )
                    }
                },
                content = { contentModifier ->
                    ScrapListContent(
                        scrapItemsResult = uiState.scrapItemsResult,
                        viewMode = uiState.viewMode,
                        isPreferencesLoaded = uiState.isPreferencesLoaded,
                        isSelectionMode = uiState.isSelectionMode,
                        selectedScrapIds = uiState.selectedScrapIds,
                        onItemClick = { scrapId -> navigateToScrapDetail(scrapId) },
                        onItemLongClick = { itemId ->
                            scrapViewModel.enterSelectionMode(itemId)
                        },
                        onItemSelectionToggle = { itemId ->
                            scrapViewModel.toggleScrapItemSelection(itemId)
                        },
                        listState = screenState.listState,
                        gridState = screenState.gridState,
                        modifier = contentModifier
                    )
                },
                floatingActionButton = { modifier ->
                    ScrapFloatingButtons(
                        showScrollToTop = screenState.showScrollToTop,
                        viewMode = uiState.viewMode,
                        listState = screenState.listState,
                        gridState = screenState.gridState,
                        showAddScrapFab = true,
                        isSelectionMode = uiState.isSelectionMode,
                        onAddScrap = { navigateToAddScrap() },
                        modifier = modifier,
                    )
                },
                dialogs = {
                    if (screenState.showDeleteDialog) {
                        CommonDeleteDialog(
                            title = "정말 카테고리를 삭제하시겠습니까?",
                            description = "(해당 카테고리에 속한 모든 스크랩 또한 삭제됩니다)",
                            confirmText = "삭제하기",
                            onDismiss = { screenState.showDeleteDialog = false },
                            onConfirm = {
                                screenState.showDeleteDialog = false
                                scrapViewModel.deleteCategory(uiState.categoryId)
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
    topBar: @Composable () -> Unit,
    content: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
    floatingActionButton: @Composable (Modifier) -> Unit = {},
    dialogs: @Composable () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            topBar()
            content(Modifier.weight(1f))
        }
        floatingActionButton(modifier.align(Alignment.BottomEnd))
        dialogs()
    }
}

@Preview(showBackground = true)
@Composable
fun ScrapScreenContentPreview() {
    Scrap2025Theme {
        ScrapScreenContent(
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
            content = { modifier ->
                ScrapListContent(
                    showCategory = false,
                    scrapItemsResult = Result.Success(emptyList()),
                    viewMode = ViewMode.GRID,
                    isPreferencesLoaded = true,
                    isSelectionMode = false,
                    selectedScrapIds = emptySet(),
                    onItemClick = {},
                    onItemLongClick = {},
                    onItemSelectionToggle = {},
                    listState = rememberLazyListState(),
                    gridState = rememberLazyGridState(),
                    modifier = modifier
                )
            },
            floatingActionButton = { modifier ->
                ScrapFloatingButtons(
                    showScrollToTop = false,
                    viewMode = ViewMode.GRID,
                    listState = rememberLazyListState(),
                    gridState = rememberLazyGridState(),
                    showAddScrapFab = true,
                    isSelectionMode = false,
                    onAddScrap = {},
                    modifier = modifier,
                )
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ScrapScreenContentSelectionModePreview() {
    Scrap2025Theme {
        ScrapScreenContent(
            topBar = {
                SelectionTopBar(
                    categoryTitle = "분류되지 않음",
                    selectedCount = 0,
                    totalCount = 0,
                    onSelectAll = {},
                    onDeselectAll = {}
                )
            },
            content = { modifier ->
                ScrapListContent(
                    showCategory = false,
                    scrapItemsResult = Result.Success(emptyList()),
                    viewMode = ViewMode.LIST,
                    isPreferencesLoaded = true,
                    isSelectionMode = true,
                    selectedScrapIds = emptySet(),
                    onItemClick = {},
                    onItemLongClick = {},
                    onItemSelectionToggle = {},
                    listState = rememberLazyListState(),
                    gridState = rememberLazyGridState(),
                    modifier = modifier
                )
            },
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
    onDeleteSuccess: () -> Unit,
    onLoadingToggle: (Boolean) -> Unit
) {
    val context = LocalContext.current

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
