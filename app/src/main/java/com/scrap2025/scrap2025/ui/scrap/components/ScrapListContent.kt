package com.scrap2025.scrap2025.ui.scrap.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scrap2025.scrap2025.model.enums.ViewMode
import com.scrap2025.scrap2025.ui.theme.GrayColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep
import com.scrap2025.scrap2025.viewmodel.ScrapUiState

@Composable
fun ScrapListContent(
    scrapItemsState: ScrapUiState,
    viewMode: ViewMode,
    isPreferencesLoaded: Boolean,
    modifier: Modifier = Modifier,
    isSelectionMode: Boolean = false,
    selectedScrapIds: Set<String> = emptySet(),
    onItemClick: (String) -> Unit = {},
    onItemLongClick: (String) -> Unit = {},
    onItemSelectionToggle: (String) -> Unit = {},
    listState: LazyListState = androidx.compose.foundation.lazy.rememberLazyListState(),
    gridState: LazyGridState = androidx.compose.foundation.lazy.grid.rememberLazyGridState(),
    showCategory: Boolean = false,
) {
    when (val result = scrapItemsState) {
        is ScrapUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MainColorDeep)
            }
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
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MainColorDeep)
                }
            } else {
                val scrapItems = result.items
                when (viewMode) {
                    ViewMode.LIST -> {
                        LazyColumn(state = listState, modifier = modifier.fillMaxSize()) {
                            items(scrapItems) { scrapItem ->
                                ScrapItemCardList(
                                    scrapItem = scrapItem,
                                    showCategory = showCategory,
                                    isSelectionMode = isSelectionMode,
                                    isSelected = selectedScrapIds.contains(scrapItem.id),
                                    onClick = { onItemClick(scrapItem.id) },
                                    onLongClick = { onItemLongClick(scrapItem.id) },
                                    onSelectionToggle = { onItemSelectionToggle(scrapItem.id) })
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
                            items(scrapItems) { scrapItem ->
                                ScrapItemCardGrid(
                                    scrapItem = scrapItem,
                                    showCategory = showCategory,
                                    isSelectionMode = isSelectionMode,
                                    isSelected = selectedScrapIds.contains(scrapItem.id),
                                    onClick = { onItemClick(scrapItem.id) },
                                    onLongClick = { onItemLongClick(scrapItem.id) },
                                    onSelectionToggle = { onItemSelectionToggle(scrapItem.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}
