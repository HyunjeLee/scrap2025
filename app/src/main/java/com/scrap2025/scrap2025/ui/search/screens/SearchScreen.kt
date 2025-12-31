package com.scrap2025.scrap2025.ui.search.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scrap2025.scrap2025.model.ViewMode
import com.scrap2025.scrap2025.ui.common.components.SortBar
import com.scrap2025.scrap2025.ui.common.dialogs.CommonDateRangePickerDialog
import com.scrap2025.scrap2025.ui.scrap.components.ScrapListContent
import com.scrap2025.scrap2025.ui.search.components.SearchHeader
import com.scrap2025.scrap2025.ui.theme.BackgroundColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import com.scrap2025.scrap2025.viewmodel.SearchViewModel
import com.scrap2025.scrap2025.viewmodel.SearchWarning
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onSelectCategoryClick: () -> Unit = {},
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedCategoryItems by viewModel.selectedCategoryItems.collectAsState()

    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    // DatePicker 상태 관리
    var showDatePicker by remember { mutableStateOf(false) }

    // 파생 상태: 스크롤 위치에서 버튼 표시 여부 계산
    val showScrollToTop by
    remember(uiState.viewMode) {
        derivedStateOf {
            when (uiState.viewMode) {
                ViewMode.LIST -> {
                    listState.firstVisibleItemIndex > 0 ||
                            listState.firstVisibleItemScrollOffset > 0
                }

                ViewMode.GRID -> {
                    gridState.firstVisibleItemIndex > 0 ||
                            gridState.firstVisibleItemScrollOffset > 0
                }
            }
        }
    }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SearchWarning.ShowMinRangeWarning -> {
                    Toast.makeText(context, "검색 범위는 1개 이상 지정되어야 합니다", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    if (showDatePicker) {
        val initialStart = viewModel.parseDateToMillis(uiState.startDate) ?: viewModel.nowMillis
        val initialEnd = viewModel.parseDateToMillis(uiState.endDate) ?: viewModel.nowMillis

        CommonDateRangePickerDialog(
            initialSelectedStartDateMillis = initialStart,
            initialSelectedEndDateMillis = initialEnd,
            onDateSelected = { start, end ->
                if (start != null && end != null) {
                    viewModel.onDateChange(
                        viewModel.formatMillisToDate(start),
                        viewModel.formatMillisToDate(end)
                    )
                }
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundColor,
        topBar = {
            Column {
                // 1. 검색 헤더 (ViewModel 상태와 연동)
                SearchHeader(
                    query = uiState.query,
                    onQueryChange = { viewModel.onQueryChange(it) },
                    searchRange = uiState.searchRanges,
                    onSearchRangeToggle = { viewModel.toggleSearchRange(it) },
                    selectedCategories = selectedCategoryItems,
                    onSelectCategoryClick = onSelectCategoryClick,
                    onRemoveCategory = { viewModel.removeCategory(it) },
                    startDate = uiState.startDate,
                    endDate = uiState.endDate,
                    onDateClick = { showDatePicker = true },
                )

                // 2. 정렬 바
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
        floatingActionButton = {
            AnimatedVisibility(
                visible = showScrollToTop,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
            ) {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            if (uiState.viewMode == ViewMode.LIST) {
                                listState.animateScrollToItem(0)
                            } else {
                                gridState.animateScrollToItem(0)
                            }
                        }
                    },
                    shape = CircleShape,
                    containerColor = Color.White,
                    contentColor = MainColorDeep,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowUp,
                        contentDescription = "맨 위로가기",
                        tint = MainColorDeep,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        // 3. 스크랩 리스트 영역
        ScrapListContent(
            scrapItemsResult = uiState.searchResults,
            viewMode = uiState.viewMode,
            isPreferencesLoaded = true,
            listState = listState,
            gridState = gridState,
            onItemClick = { /* 상세 이동 로직 */ },
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    Scrap2025Theme { SearchScreen() }
}
