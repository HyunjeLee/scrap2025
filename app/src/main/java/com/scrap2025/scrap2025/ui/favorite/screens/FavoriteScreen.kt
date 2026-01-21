package com.scrap2025.scrap2025.ui.favorite.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.paging.compose.collectAsLazyPagingItems
import com.scrap2025.scrap2025.model.ScrapItem
import com.scrap2025.scrap2025.model.enums.ViewMode
import com.scrap2025.scrap2025.ui.common.components.ScrapSearchBar
import com.scrap2025.scrap2025.ui.common.components.SortBar
import com.scrap2025.scrap2025.ui.scrap.components.ScrapFloatingButtons
import com.scrap2025.scrap2025.ui.scrap.components.ScrapListContent
import com.scrap2025.scrap2025.ui.scrap.components.ScrapSelectionBottomBar
import com.scrap2025.scrap2025.ui.scrap.components.ScrapTopBar
import com.scrap2025.scrap2025.ui.scrap.components.SelectionTopBar
import com.scrap2025.scrap2025.ui.scrap.screens.ScrapScreenContent
import com.scrap2025.scrap2025.utils.isScrolled
import com.scrap2025.scrap2025.viewmodel.FavoriteViewModel
import com.scrap2025.scrap2025.viewmodel.MainViewModel
import com.scrap2025.scrap2025.viewmodel.ScrapUiState

@Stable
class FavoriteScreenState(
    val listState: LazyListState,
    val gridState: LazyGridState,
    val viewMode: ViewMode
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
    gridState: LazyGridState = rememberLazyGridState()
) = remember(viewMode, listState, gridState) { FavoriteScreenState(listState, gridState, viewMode) }

@Composable
fun FavoriteScreen(
    navigateToScrapDetail: (Long) -> Unit,
    navigateToCategorySelection: (List<Long>) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoriteViewModel = hiltViewModel(),
    mainViewModel: MainViewModel =
        hiltViewModel(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner)
) {
    val uiState by viewModel.uiState.collectAsState()
    val screenState = rememberFavoriteScreenState(viewMode = uiState.viewMode)
    val context = LocalContext.current

    val pagedItems =
        (uiState.scrapItemsState as? ScrapUiState.Paged)?.pagedData?.collectAsLazyPagingItems()

    val selectionBottomBar: @Composable () -> Unit = {
        ScrapSelectionBottomBar(
            onDelete = { viewModel.deleteSelectedItems() },
            onMove = {
                navigateToCategorySelection(uiState.selectedScrapIds.toList())
                viewModel.exitSelectionMode()
            },
            onShare = {
                val selectedItems =
                    when (val state = uiState.scrapItemsState) {
                        is ScrapUiState.Success -> state.items.filter {
                            it.id in
                                uiState.selectedScrapIds
                        }

                        is ScrapUiState.Paged ->
                            pagedItems?.itemSnapshotList?.items?.filter {
                                it.id in uiState.selectedScrapIds
                            } ?: emptyList()

                        else -> emptyList()
                    }
                shareScraps(context, selectedItems)
                viewModel.exitSelectionMode()
            },
            onFavorite = { onSuccess, onFailure ->
                viewModel.toggleFavoriteSelectedItems(onSuccess, onFailure)
            }
        )
    }

    LaunchedEffect(uiState.isSelectionMode, uiState, pagedItems) {
        when (uiState.isSelectionMode) {
            true -> mainViewModel.setBottomBar(selectionBottomBar)
            false -> mainViewModel.setBottomBar(null)
        }
    }

    // 뒤로가기 버튼 처리
    BackHandler(enabled = uiState.isSelectionMode) { viewModel.exitSelectionMode() }

    ScrapScreenContent(
        topBar = {
            if (uiState.isSelectionMode) {
                val totalCount =
                    when (val state = uiState.scrapItemsState) {
                        is ScrapUiState.Success -> state.items.size
                        is ScrapUiState.Paged -> pagedItems?.itemSnapshotList?.items?.size ?: 0

                        else -> 0
                    }
                SelectionTopBar(
                    categoryTitle = "즐겨찾기",
                    selectedCount = uiState.selectedScrapIds.size,
                    totalCount = totalCount,
                    onSelectAll = {
                        when (val state = uiState.scrapItemsState) {
                            is ScrapUiState.Success ->
                                viewModel.selectAllScrapItems(
                                    state.items.map { it.id }.toSet()
                                )

                            is ScrapUiState.Paged -> {
                                pagedItems?.let { items ->
                                    val loadedIds =
                                        items.itemSnapshotList.items
                                            .map { it.id }
                                            .toSet()
                                    viewModel.selectAllScrapItems(loadedIds)
                                }
                            }

                            else -> {}
                        }
                    },
                    onDeselectAll = { viewModel.deselectAllScrapItems() }
                )
            } else {
                ScrapTopBar(
                    categoryId = -1L,
                    categoryTitle = "즐겨찾기",
                    isEditable = false,
                    onUpdateCategory = { _, _ -> }, // 즐겨찾기 제목 수정 불가
                    onDeleteCategory = { } // 즐겨찾기 카테고리 삭제 불가
                )
                ScrapSearchBar(
                    query = uiState.query,
                    onQueryChange = { viewModel.onQueryChange(it) }
                )
                SortBar(
                    sortType = uiState.sortType,
                    sortDirection = uiState.sortDirection,
                    viewMode = uiState.viewMode,
                    onSortTypeToggle = { viewModel.toggleSortType() },
                    onSortDirectionToggle = { viewModel.toggleSortDirection() },
                    onViewModeToggle = { viewModel.toggleViewMode() }
                )
            }
        },
        content = { contentModifier ->
            ScrapListContent(
                scrapItemsState = uiState.scrapItemsState,
                pagedItems = pagedItems,
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
                modifier = modifier
            )
        },
        modifier = modifier
    )
}

private fun shareScraps(context: Context, scraps: List<ScrapItem>) {
    if (scraps.isEmpty()) return

    val shareText =
        scraps.joinToString(separator = "\n\n") { scrapItem ->
            "[스크랩]\n${scrapItem.title}\n${scrapItem.url}"
        }

    val dataIntent =
        Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, "스크랩 ${scraps.size}개 공유하는 중..")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

    val shareIntent = Intent.createChooser(dataIntent, null)
    context.startActivity(shareIntent)
}
