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
import androidx.lifecycle.ViewModelStoreOwner
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
import com.scrap2025.scrap2025.viewmodel.MainViewModel
import com.scrap2025.scrap2025.viewmodel.ScrapUiState
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
    navigateToScrapDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
    scrapViewModel: ScrapViewModel = hiltViewModel(),
    mainViewModel: MainViewModel =
        hiltViewModel(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner)
) {
    val uiState by scrapViewModel.uiState.collectAsState()

    val screenState =
        rememberScrapScreenState(
            viewMode = uiState.viewMode,
            categoryName = uiState.categoryName
        )

    SetSelectionBottomBar(uiState.isSelectionMode, scrapViewModel, mainViewModel)
    HandleCategoryDeleteEvents(
        scrapViewModel = scrapViewModel,
        mainViewModel = mainViewModel,
        onDeleteSuccess = navigateToCategory,
        onLoadingToggle = { screenState.isCategoryDeleting = it }
    )
    BackHandler(enabled = uiState.isSelectionMode) { scrapViewModel.exitSelectionMode() }

    when {
        screenState.isCategoryDeleting -> LoadingScreen()
        else ->
            ScrapScreenContent(
                topBar = {
                    if (uiState.isSelectionMode) {
                        val totalCount =
                            when (val state = uiState.scrapItemsState) {
                                is ScrapUiState.Success -> state.items.size
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
                            isEditable = uiState.isEditable,
                            onUpdateCategory = { id, newTitle ->
                                scrapViewModel.updateCategoryTitle(
                                    id = id,
                                    newTitle = newTitle
                                )
                                mainViewModel.setGlobalCategory(id, newTitle)
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
                        scrapItemsState = uiState.scrapItemsState,
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
                    categoryId = 1L,
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
                    scrapItemsState = ScrapUiState.Success(emptyList()),
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
                    scrapItemsState = ScrapUiState.Success(emptyList()),
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
private fun SetSelectionBottomBar(
    isSelectionMode: Boolean,
    scrapViewModel: ScrapViewModel,
    mainViewModel: MainViewModel
) {
    LaunchedEffect(isSelectionMode) {
        if (isSelectionMode) {
            mainViewModel.setBottomBar {
                ScrapSelectionBottomBar(
                    onDelete = { scrapViewModel.deleteSelectedItems() },
                    onMove = {  /* navigate to CategorySelection */ },
                    onShare = { /* todo */ },
                    onFavorite = { onSuccess, onFailure ->
                        scrapViewModel.toggleFavoriteSelectedItems(onSuccess, onFailure)
                    }
                )
            }
        } else {
            mainViewModel.setBottomBar(null)
        }
    }
}

@Composable
private fun HandleCategoryDeleteEvents(
    scrapViewModel: ScrapViewModel,
    mainViewModel: MainViewModel,
    onDeleteSuccess: () -> Unit,
    onLoadingToggle: (Boolean) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(scrapViewModel.categoryDeleteEvent) {
        scrapViewModel.categoryDeleteEvent.collect { result ->
            result
                .onSuccess {
                    onLoadingToggle(false)
                    mainViewModel.setDefaultCategory()
                    onDeleteSuccess()
                }
                .onFailure {
                    onLoadingToggle(false)
                    Toast.makeText(context, it.message ?: "삭제 실패", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
