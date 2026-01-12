package com.scrap2025.scrap2025.ui.scrap.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.scrap2025.scrap2025.model.ScrapItem
import com.scrap2025.scrap2025.model.enums.ViewMode
import com.scrap2025.scrap2025.ui.common.components.LoadingScreen
import com.scrap2025.scrap2025.ui.theme.GrayColor
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep
import com.scrap2025.scrap2025.viewmodel.ScrapUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrapListContent(
    scrapItemsState: ScrapUiState,
    pagedItems: LazyPagingItems<ScrapItem>?,
    viewMode: ViewMode,
    isPreferencesLoaded: Boolean,
    modifier: Modifier = Modifier,
    isSelectionMode: Boolean = false,
    selectedScrapIds: Set<Long> = emptySet(),
    onItemClick: (Long) -> Unit = {},
    onItemLongClick: (Long) -> Unit = {},
    onItemSelectionToggle: (Long) -> Unit = {},
    listState: LazyListState = androidx.compose.foundation.lazy.rememberLazyListState(),
    gridState: LazyGridState = androidx.compose.foundation.lazy.grid.rememberLazyGridState(),
    showCategory: Boolean = false,
) {
    when (val result = scrapItemsState) {
        is ScrapUiState.Loading -> {
            LoadingScreen()
        }

        is ScrapUiState.Error -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "스크랩을 불러올 수 없습니다",
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                        color = GrayColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = result.message ?: "알 수 없는 오류",
                        style = TextStyle(fontSize = 14.sp),
                        color = GrayColor
                    )
                }
            }
        }

        is ScrapUiState.Success -> {
            if (!isPreferencesLoaded) {
                LoadingScreen()
            } else {
                val scrapItems = result.items
                when (viewMode) {
                    ViewMode.LIST -> {
                        LazyColumn(state = listState, modifier = modifier.fillMaxSize()) {
                            items(
                                items = scrapItems,
                                key = { scrapItem -> scrapItem.id }
                            ) { scrapItem ->
                                ScrapItemCardList(
                                    scrapItem = scrapItem,
                                    showCategory = showCategory,
                                    isSelectionMode = isSelectionMode,
                                    isSelected = selectedScrapIds.contains(scrapItem.id),
                                    onClick = { onItemClick(scrapItem.id) },
                                    onLongClick = { onItemLongClick(scrapItem.id) },
                                    onSelectionToggle = { onItemSelectionToggle(scrapItem.id) }
                                )
                            }
                        }
                    }

                    ViewMode.GRID -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            state = gridState,
                            modifier = modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 23.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(
                                items = scrapItems,
                                key = { scrapItem -> scrapItem.id }
                            ) { scrapItem ->
                                ScrapItemCardGrid(
                                    scrapItem = scrapItem,
                                    showCategory = showCategory,
                                    isSelectionMode = isSelectionMode,
                                    isSelected = selectedScrapIds.contains(scrapItem.id),
                                    onClick = { onItemClick(scrapItem.id) },
                                    onLongClick = { onItemLongClick(scrapItem.id) },
                                    onSelectionToggle = { onItemSelectionToggle(scrapItem.id) }
                                )
                            }
                        }
                    }
                }
            }
        }

        is ScrapUiState.Paged -> {
            if (pagedItems == null) return

            when {
                // 초기 로딩 (설정 미로드 시 또는 페이징 첫 페이지 로딩 시)
                !isPreferencesLoaded || pagedItems.loadState.refresh is LoadState.Loading -> {
                    LoadingScreen()
                }

                // 에러 발생 (첫 페이지 로딩 실패 시)
                pagedItems.loadState.refresh is LoadState.Error -> {
                    val error = pagedItems.loadState.refresh as LoadState.Error
                    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "스크랩을 불러올 수 없습니다",
                                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                                color = GrayColor
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = error.error.message ?: "알 수 없는 오류",
                                style = TextStyle(fontSize = 14.sp),
                                color = GrayColor
                            )
                        }
                    }
                }

                else -> {
                    val isRefreshing = pagedItems.loadState.refresh is LoadState.Loading
                    val pullToRefreshState = rememberPullToRefreshState()

                    PullToRefreshBox(
                        state = pullToRefreshState,
                        isRefreshing = isRefreshing,
                        onRefresh = { pagedItems.refresh() },
                        indicator = {
                            PullToRefreshDefaults.Indicator(
                                state = pullToRefreshState,
                                isRefreshing = isRefreshing,
                                color = MainColorDeep,
                                containerColor = MainColor,
                                modifier = Modifier.align(Alignment.TopCenter)
                            )
                        },
                    ) {
                        when (viewMode) {
                            ViewMode.LIST -> {
                                LazyColumn(state = listState, modifier = modifier.fillMaxSize()) {
                                    items(
                                        count = pagedItems.itemCount,
                                        key = pagedItems.itemKey { it.id },
                                        contentType = pagedItems.itemContentType { "scrap" }
                                    ) { index ->
                                        val scrapItem = pagedItems[index]
                                        if (scrapItem != null) {
                                            ScrapItemCardList(
                                                scrapItem = scrapItem,
                                                showCategory = showCategory,
                                                isSelectionMode = isSelectionMode,
                                                isSelected = selectedScrapIds.contains(scrapItem.id),
                                                onClick = { onItemClick(scrapItem.id) },
                                                onLongClick = { onItemLongClick(scrapItem.id) },
                                                onSelectionToggle = {
                                                    onItemSelectionToggle(scrapItem.id)
                                                }
                                            )
                                        }
                                    }

                                    // 하단 추가 로딩 (Append)
                                    if (pagedItems.loadState.append is LoadState.Loading) {
                                        item {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(24.dp),
                                                    color = MainColorDeep
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            ViewMode.GRID -> {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    state = gridState,
                                    modifier = modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(horizontal = 23.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(
                                        count = pagedItems.itemCount,
                                        key = pagedItems.itemKey { it.id },
                                        contentType = pagedItems.itemContentType { "scrap" }
                                    ) { index ->
                                        val scrapItem = pagedItems[index]
                                        if (scrapItem != null) {
                                            ScrapItemCardGrid(
                                                scrapItem = scrapItem,
                                                showCategory = showCategory,
                                                isSelectionMode = isSelectionMode,
                                                isSelected = selectedScrapIds.contains(scrapItem.id),
                                                onClick = { onItemClick(scrapItem.id) },
                                                onLongClick = { onItemLongClick(scrapItem.id) },
                                                onSelectionToggle = {
                                                    onItemSelectionToggle(scrapItem.id)
                                                }
                                            )
                                        }
                                    }

                                    // 하단 추가 로딩 (Append)
                                    if (pagedItems.loadState.append is LoadState.Loading) {
                                        item(span = { GridItemSpan(maxLineSpan) }) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(24.dp),
                                                    color = MainColorDeep
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
