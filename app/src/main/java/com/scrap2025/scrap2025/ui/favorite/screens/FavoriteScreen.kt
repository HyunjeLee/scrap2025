package com.scrap2025.scrap2025.ui.favorite.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.scrap2025.scrap2025.model.CategoryItem
import com.scrap2025.scrap2025.model.GlobalUiState
import com.scrap2025.scrap2025.model.enums.ViewMode
import com.scrap2025.scrap2025.ui.common.components.SortBar
import com.scrap2025.scrap2025.ui.scrap.components.ScrapFloatingButtons
import com.scrap2025.scrap2025.ui.scrap.components.ScrapListContent
import com.scrap2025.scrap2025.ui.scrap.components.ScrapSearchBar
import com.scrap2025.scrap2025.ui.scrap.components.ScrapSelectionBottomBar
import com.scrap2025.scrap2025.ui.scrap.components.ScrapTopBar
import com.scrap2025.scrap2025.ui.scrap.components.SelectionTopBar
import com.scrap2025.scrap2025.ui.scrap.screens.ScrapScreenContent
import com.scrap2025.scrap2025.utils.isScrolled
import com.scrap2025.scrap2025.viewmodel.FavoriteViewModel
import com.scrap2025.scrap2025.viewmodel.ScrapUiState

@Stable
class FavoriteScreenState(
    val listState: LazyListState,
    val gridState: LazyGridState,
    val viewMode: ViewMode,
) {
    val showScrollToTop by derivedStateOf {
        when (viewMode) {
            ViewMode.LIST -> listState.isScrolled
            ViewMode.GRID -> gridState.isScrolled
        }
    }
}

@Composable
fun rememberFavoriteScreenState(
    viewMode: ViewMode,
    listState: LazyListState = rememberLazyListState(),
    gridState: LazyGridState = rememberLazyGridState(),
) = remember(viewMode, listState, gridState) { FavoriteScreenState(listState, gridState, viewMode) }

@Composable
fun FavoriteScreen(
    navigateToScrapDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val screenState = rememberFavoriteScreenState(viewMode = uiState.viewMode)

    val selectionBottomBar: @Composable () -> Unit = {
        ScrapSelectionBottomBar(
            onDelete = { viewModel.deleteSelectedItems() },
            onMove = { /* todo */ },
            onShare = { /* todo */ },
            onFavorite = { onSuccess, onFailure -> viewModel.toggleFavoriteSelectedItems() })
    }

    LaunchedEffect(uiState.isSelectionMode) {
        when (uiState.isSelectionMode) {
            true -> GlobalUiState.setBottomBar(selectionBottomBar)
            false -> GlobalUiState.setBottomBar(null)
        }
    }

    // 뒤로가기 버튼 처리
    BackHandler(enabled = uiState.isSelectionMode) { viewModel.exitSelectionMode() }

    ScrapScreenContent(
        topBar = {
            if (uiState.isSelectionMode) {
                val totalCount = when (val state = uiState.scrapItemsState) {
                    is ScrapUiState.Success -> state.items.size
                    else -> 0
                }
                SelectionTopBar(
                    categoryTitle = CategoryItem.FAVORITE_NAME,
                    selectedCount = uiState.selectedScrapIds.size,
                    totalCount = totalCount,
                    onSelectAll = { viewModel.selectAllScrapItems() },
                    onDeselectAll = { viewModel.deselectAllScrapItems() })
            } else {
                ScrapTopBar(
                    categoryId = CategoryItem.FAVORITE_ID,
                    categoryTitle = CategoryItem.FAVORITE_NAME,
                    onUpdateCategory = { _, _ -> /* 즐겨찾기 제목 수정 불가 */ },
                    onDeleteCategory = { /* 즐겨찾기 카테고리 삭제 불가 */ },
                )
                ScrapSearchBar(uiState.query, { viewModel.onQueryChange(it) })
                SortBar(
                    sortType = uiState.sortType,
                    sortDirection = uiState.sortDirection,
                    viewMode = uiState.viewMode,
                    onSortTypeToggle = { viewModel.toggleSortType() },
                    onSortDirectionToggle = { viewModel.toggleSortDirection() },
                    onViewModeToggle = { viewModel.toggleViewMode() })
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
                onItemLongClick = { itemId -> viewModel.enterSelectionMode(itemId) },
                onItemSelectionToggle = { itemId ->
                    viewModel.toggleScrapItemSelection(itemId)
                },
                listState = screenState.listState,
                gridState = screenState.gridState,
                showCategory = true,
                modifier = contentModifier
            )
        },
        floatingActionButton = { modifier ->
            ScrapFloatingButtons(
                showScrollToTop = screenState.showScrollToTop,
                viewMode = uiState.viewMode,
                listState = screenState.listState,
                gridState = screenState.gridState,
                showAddScrapFab = false, // FAB 숨기기
                isSelectionMode = uiState.isSelectionMode,
                onAddScrap = { /* 즐겨찾기에서는 추가 버튼 없음 */ },
                modifier = modifier,
            )
        },
        modifier = modifier,
    )
}
