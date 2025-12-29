package com.scrap2025.scrap2025.ui.favorite.screens

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.scrap2025.scrap2025.model.GlobalUiState
import com.scrap2025.scrap2025.ui.scrap.screens.ScrapScreenContent
import com.scrap2025.scrap2025.ui.scrap.screens.SelectionActionBar
import com.scrap2025.scrap2025.viewmodel.FavoriteViewModel

@Composable
fun FavoriteScreen(
    navigateToScrapDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val itemsResult by viewModel.sortedFavoriteItems.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val sortType by viewModel.sortType.collectAsState()
    val sortDirection by viewModel.sortDirection.collectAsState()
    val isSelectionMode by viewModel.isSelectionMode.collectAsState()
    val selectedScrapIds by viewModel.selectedScrapIds.collectAsState()
    val isPreferencesLoaded by viewModel.isPreferencesLoaded.collectAsState()

    val selectionBottomBar: @Composable () -> Unit = {
        SelectionActionBar(
            onDelete = { viewModel.deleteSelectedItems() },
            onMove = { /* todo */ },
            onShare = { /* todo */ },
            onFavorite = { viewModel.toggleFavoriteSelectedItems() }
        )
    }

    LaunchedEffect(isSelectionMode) {
        when (isSelectionMode) {
            true -> GlobalUiState.setBottomBar(selectionBottomBar)
            false -> GlobalUiState.setBottomBar(null)
        }
    }

    // 뒤로가기 버튼 처리
    BackHandler(enabled = isSelectionMode) { viewModel.exitSelectionMode() }

    ScrapScreenContent(
        categoryId = "favorite", // 고정값
        categoryTitle = "즐겨찾기",
        scrapItemsResult = itemsResult,
        viewMode = viewMode,
        sortType = sortType,
        sortDirection = sortDirection,
        isSelectionMode = isSelectionMode,
        selectedScrapIds = selectedScrapIds,
        isPreferencesLoaded = isPreferencesLoaded,
        onSortTypeToggle = { viewModel.toggleSortType() },
        onSortDirectionToggle = { viewModel.toggleSortDirection() },
        onViewModeToggle = { viewModel.toggleViewMode() },
        onItemClick = { scrapId -> navigateToScrapDetail(scrapId) },
        onItemLongClick = { itemId -> viewModel.enterSelectionMode(itemId) },
        onItemSelectionToggle = { itemId -> viewModel.toggleScrapItemSelection(itemId) },
        onSelectAll = { viewModel.selectAllScrapItems() },
        onDeselectAll = { viewModel.deselectAllScrapItems() },
        onAddScrap = { /* 즐겨찾기에서는 추가 버튼 없음 */ },
        onUpdateCategoryTitle = { _, _ -> /* 즐겨찾기 제목 수정 불가 */ },
        onDeleteCategory = { /* 즐겨찾기 카테고리 삭제 불가 */ },
        showAddScrapFab = false, // FAB 숨기기
        modifier = modifier
    )
}
