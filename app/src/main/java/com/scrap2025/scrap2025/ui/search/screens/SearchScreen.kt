package com.scrap2025.scrap2025.ui.search.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scrap2025.scrap2025.data.local.ScrapDummyData
import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.model.SortDirection
import com.scrap2025.scrap2025.model.SortType
import com.scrap2025.scrap2025.model.ViewMode
import com.scrap2025.scrap2025.ui.common.components.SortBar
import com.scrap2025.scrap2025.ui.scrap.components.ScrapListContent
import com.scrap2025.scrap2025.ui.search.components.SearchHeader
import com.scrap2025.scrap2025.ui.theme.BackgroundColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme

@Composable
fun SearchScreen(modifier: Modifier = Modifier) {
    // 임시 상태 (나중에 ViewModel로 이동)
    var query by remember { mutableStateOf("") }
    var searchRange by remember { mutableStateOf(setOf("제목")) }
    var selectedCategories by remember { mutableStateOf(listOf("분류 섹션 1", "분류 섹션 2")) }
    var sortType by remember { mutableStateOf(SortType.DATE) }
    var sortDirection by remember { mutableStateOf(SortDirection.ASCENDING) }
    var viewMode by remember { mutableStateOf(ViewMode.LIST) }

    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()

    Column(modifier = modifier
        .fillMaxSize()
        .background(BackgroundColor)) {
        // 1. 검색 헤더 (신규 개발)
        SearchHeader(
            query = query,
            onQueryChange = { query = it },
            searchRange = searchRange,
            onSearchRangeToggle = { range ->
                searchRange =
                    if (searchRange.contains(range)) {
                        searchRange - range
                    } else {
                        searchRange + range
                    }
            },
            selectedCategories = selectedCategories,
            onAddCategoryClick = { /* 카테고리 추가 로직 */ },
            onRemoveCategory = { category ->
                selectedCategories = selectedCategories - category
            },
            startDate = "2024-05-23",
            endDate = "2024-05-30",
            onDateClick = { /* 날짜 선택 로직 */ }
        )

        // 2. 정렬 바 (기존 공통 컴포넌트 재사용)
        SortBar(
            sortType = sortType,
            sortDirection = sortDirection,
            viewMode = viewMode,
            onSortTypeToggle = {
                sortType = if (sortType == SortType.DATE) SortType.TITLE else SortType.DATE
            },
            onSortDirectionToggle = {
                sortDirection =
                    if (sortDirection == SortDirection.ASCENDING) {
                        SortDirection.DESCENDING
                    } else {
                        SortDirection.ASCENDING
                    }
            },
            onViewModeToggle = {
                viewMode = if (viewMode == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST
            }
        )

        // 3. 스크랩 리스트 영역 (기존 공통 컴포넌트 재사용)
        Box(modifier = Modifier.weight(1f)) {
            ScrapListContent(
                scrapItemsResult = Result.Success(ScrapDummyData.dummyScrapItems),
                viewMode = viewMode,
                isPreferencesLoaded = true,
                listState = listState,
                gridState = gridState,
                onItemClick = { /* 상세 이동 */ }
            )

            // 맨 위로 가기 버튼 (Mockup에 있는 초록색 화살표 버튼)
            FloatingActionButton(
                onClick = { /* 스크롤 로직 */ },
                shape = CircleShape,
                containerColor = Color.White,
                contentColor = MainColorDeep,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowUp,
                    contentDescription = "맨 위로가기",
                    tint = Color(0xFF4CAF50), // Mockup에 있는 초록색계열
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    Scrap2025Theme { SearchScreen() }
}
