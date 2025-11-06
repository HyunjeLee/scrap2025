package com.scrap2025.scrap2025.ui.scrap.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowCircleUp
import androidx.compose.material.icons.outlined.ArrowDropUp
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scrap2025.scrap2025.model.ViewMode
import com.scrap2025.scrap2025.ui.scrap.components.ScrapItemCardGrid
import com.scrap2025.scrap2025.ui.scrap.components.ScrapItemCardList
import com.scrap2025.scrap2025.ui.theme.BackgroundColor
import com.scrap2025.scrap2025.ui.theme.GrayColor
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep
import com.scrap2025.scrap2025.ui.theme.MainColorLight
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import com.scrap2025.scrap2025.ui.theme.WarningColor
import com.scrap2025.scrap2025.viewmodel.ScrapViewModel
import kotlinx.coroutines.launch

@Composable
fun ScrapScreen(
    categoryName: String = "분류되지 않음",
    viewModel: ScrapViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val scrapItems by viewModel.scrapItems.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()

    // Compose UI 상태 (View에서 관리)
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    // 파생 상태: 스크롤 위치에서 버튼 표시 여부 계산
    val showScrollToTop by remember {
        derivedStateOf {
            when (viewMode) {
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 톱바 - 제목
            TopBarWithTitle(categoryName = categoryName)

            // 톱바 - 검색
            SearchBar()

            // 정렬 바
            SortBar(
                viewMode = viewMode,
                onViewModeToggle = { viewModel.toggleViewMode() }
            )

            // 스크랩 리스트/그리드
            when (viewMode) {
                ViewMode.LIST -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        items(scrapItems) { scrapItem ->
                            ScrapItemCardList(
                                scrapItem = scrapItem,
                            )
                        }
                    }
                }
                ViewMode.GRID -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = gridState,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentPadding = PaddingValues(horizontal = 23.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(scrapItems) { scrapItem ->
                            ScrapItemCardGrid(
                                scrapItem = scrapItem
                            )
                        }
                    }
                }
            }
        }

        // 스크랩 추가 버튼
        FloatingActionButton(
            onClick = {
            },
            shape = CircleShape,
            containerColor = MainColor,
            contentColor = MainColorDeep,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 21.dp, bottom = 21.dp)
                .size(60.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "스크랩 추가",
                modifier = Modifier.size(50.dp)
            )
        }

        // 맨 위로가기 버튼
        AnimatedVisibility(
            visible = showScrollToTop,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 26.dp, bottom = 92.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        when (viewMode) {
                            ViewMode.LIST -> listState.animateScrollToItem(0)
                            ViewMode.GRID -> gridState.animateScrollToItem(0)
                        }
                    }
                },
                shape = CircleShape,
                containerColor = Color.White,
                contentColor = MainColorDeep,
                modifier = Modifier.size(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowUp,
                    contentDescription = "맨 위로가기",
                    tint = MainColorDeep,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

@Composable
fun TopBarWithTitle(
    categoryName: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(53.dp)
            .background(MainColor),
        contentAlignment = Alignment.CenterStart
    ) {
        // 카테고리명
        Text(
            text = categoryName,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(start = 21.dp)
                .padding(end = 85.dp)
        )

        // 오른쪽 아이콘들
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 22.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 수정 아이콘
            IconButton(
                onClick = { /* TODO: 편집 모드 */ },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "편집",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }

            // 삭제 아이콘
            IconButton(
                onClick = { /* TODO: 삭제 */ },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "삭제",
                    tint = WarningColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun SearchBar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(MainColor)
            .padding(horizontal = 21.dp, vertical = 5.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MainColorLight,
                    shape = RoundedCornerShape(7.dp)
                )
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "검색",
                    tint = Color.Black,
                    modifier = Modifier.size(27.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "제목, 본문내용, 메모로 검색하기",
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = GrayColor
                )
            }
        }
    }
}

@Composable
fun SortBar(
    viewMode: ViewMode = ViewMode.LIST,
    onViewModeToggle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .background(MainColor)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))
            // 정렬 아이콘
            Icon(
                imageVector = Icons.Outlined.ArrowCircleUp,
                contentDescription = "정렬",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 정렬 텍스트
            Text(
                text = "스크랩한 날짜 순",
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = GrayColor,
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 구분선
            Text(
                text = "|",
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = Color(0xFF8C8C8C)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 뷰모드 전환
            IconButton(
                onClick = onViewModeToggle,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = when (viewMode) {
                        ViewMode.LIST -> Icons.Outlined.ViewAgenda
                        ViewMode.GRID -> Icons.Outlined.GridView
                    },
                    contentDescription = "뷰모드 전환",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScrapScreenPreview() {
    Scrap2025Theme {
        ScrapScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun TopBarWithTitlePreview() {
    Scrap2025Theme {
        TopBarWithTitle(
            categoryName = "분류되지 않음",
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    Scrap2025Theme {
        SearchBar()
    }
}

@Preview(showBackground = true)
@Composable
fun SortBarPreview() {
    Scrap2025Theme {
        SortBar()
    }
}
