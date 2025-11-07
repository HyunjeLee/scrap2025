package com.scrap2025.scrap2025.ui.scrap.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.ArrowCircleDown
import androidx.compose.material.icons.outlined.ArrowCircleUp
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.scrap2025.scrap2025.data.local.ScrapDummyData
import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.model.ScrapItem
import com.scrap2025.scrap2025.model.SortDirection
import com.scrap2025.scrap2025.model.SortType
import com.scrap2025.scrap2025.model.ViewMode
import com.scrap2025.scrap2025.navigation.NavRoute
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

/**
 * ScrapScreen - Container Composable
 * ViewModel에서 상태를 추출하여 ScrapScreenContent에 전달
 */
@Composable
fun ScrapScreen(
    categoryName: String = "분류되지 않음",
    navController: NavHostController,
    viewModel: ScrapViewModel,
    modifier: Modifier = Modifier
) {
    val scrapItemsResult by viewModel.sortedScrapItems.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val sortType by viewModel.sortType.collectAsState()
    val sortDirection by viewModel.sortDirection.collectAsState()

    ScrapScreenContent(
        categoryName = categoryName,
        scrapItemsResult = scrapItemsResult,
        viewMode = viewMode,
        sortType = sortType,
        sortDirection = sortDirection,
        onSortTypeToggle = { viewModel.toggleSortType() },
        onSortDirectionToggle = { viewModel.toggleSortDirection() },
        onViewModeToggle = { viewModel.toggleViewMode() },
        onAddScrap = {
            navController.navigate(NavRoute.ADD_SCRAP)
        },
        onUpdateCategoryTitle = { newTitle ->
            viewModel.updateCategoryTitle(newTitle)
        },
        modifier = modifier
    )
}

/**
 * ScrapScreenContent - Presentational Composable
 * ViewModel 의존성 없이 순수한 데이터만 받아서 UI 렌더링
 */
@Composable
fun ScrapScreenContent(
    categoryName: String,
    scrapItemsResult: Result<List<ScrapItem>>,
    viewMode: ViewMode,
    sortType: SortType,
    sortDirection: SortDirection,
    onSortTypeToggle: () -> Unit,
    onSortDirectionToggle: () -> Unit,
    onViewModeToggle: () -> Unit,
    onAddScrap: () -> Unit,
    onUpdateCategoryTitle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
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
            TopBarWithTitle(
                categoryTitle = categoryName,
                onUpdateCategory = onUpdateCategoryTitle,
                onDeleteCategory = { /* TODO: 카테고리 삭제 기능 구현 */ }
            )

            // 톱바 - 검색
            SearchBar()

            // 정렬 바
            SortBar(
                sortType = sortType,
                sortDirection = sortDirection,
                viewMode = viewMode,
                onSortTypeToggle = onSortTypeToggle,
                onSortDirectionToggle = onSortDirectionToggle,
                onViewModeToggle = onViewModeToggle
            )

            // 스크랩 리스트/그리드
            when (val result = scrapItemsResult) {
                is Result.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MainColorDeep)
                    }
                }
                is Result.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
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
                is Result.Success -> {
                    val scrapItems = result.data
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
            }
        }

        // 스크랩 추가 버튼
        FloatingActionButton(
            onClick = onAddScrap,
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
                        listState.scrollToItem(0)
                        gridState.scrollToItem(0)
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

/**
 * TopBarWithTitle - 카테고리 제목 표시 및 편집 기능 '
 *
 *
 * 내부적으로 편집 모드 상태를 관리하며, 모드에 따라 TopBarDefault/TopBarEditMode를 전환
 */
@Composable
fun TopBarWithTitle(
    categoryTitle: String,
    onUpdateCategory: (String) -> Unit,
    onDeleteCategory: () -> Unit,
    modifier: Modifier = Modifier
) {
    // === 내부 상태: 편집 모드 관리 ===
    var isEditMode by remember { mutableStateOf(false) }

    var categoryTitleLocal by remember { mutableStateOf(categoryTitle) }

    if (isEditMode) {
        TopBarEditMode(
            initialCategoryTitle = categoryTitleLocal,
            onSave = { newTitle ->
                onUpdateCategory(newTitle)
                isEditMode = false
                categoryTitleLocal = newTitle  // todo: db 연결 시 수정할 것
            },
            onCancel = {
                isEditMode = false
            },
            modifier = modifier
        )
    } else {
        TopBarDefault(
            categoryTitle = categoryTitleLocal,
            onEditClick = { isEditMode = true },
            onDeleteClick = onDeleteCategory,
            modifier = modifier
        )
    }
}

/**
 * TopBarDefault - 일반 모드 (카테고리명 표시 + 편집/삭제 버튼)
 */
@Composable
private fun TopBarDefault(
    categoryTitle: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(53.dp)
            .background(MainColor),
        contentAlignment = Alignment.CenterStart
    ) {
        // 카테고리 제목
        Text(
            text = categoryTitle,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(start = 21.dp, end = 85.dp)
        )

        if (categoryTitle != "분류되지 않음") {
            // 편집/삭제 버튼
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 22.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 편집 버튼
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "편집",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // 삭제 버튼
                IconButton(
                    onClick = onDeleteClick,
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
}

/**
 * TopBarEditMode - 편집 모드 (TextField + 저장 버튼)
 * 커서를 텍스트 끝으로 자동 이동하며, 유효성 검사를 수행
 */
@Composable
private fun TopBarEditMode(
    initialCategoryTitle: String,
    onSave: (String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    // === 내부 상태: 편집 중인 텍스트와 커서 위치를 포함한 TextFieldValue ===
    var textFieldState by remember(initialCategoryTitle) {
        mutableStateOf(
            TextFieldValue(
                text = initialCategoryTitle,
                selection = TextRange(initialCategoryTitle.length) // 1. 초기 커서 위치: 맨 끝
            )
        )
    }

    // === 유효성 검사: 1자 이상 21자 이하 ===
    val isCategoryTitleValid = textFieldState.text.length in 1..21
    val saveButtonColor = if (isCategoryTitleValid) MainColorDeep else WarningColor

    // === 포커스 관리 ===
    val focusRequester = remember { FocusRequester() }

    // 편집 모드 진입 시 자동 포커스
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(53.dp)
            .background(MainColor),
        contentAlignment = Alignment.CenterStart
    ) {
        // 편집용 TextField
        BasicTextField(
            value = textFieldState,
            onValueChange = { newValue ->
                textFieldState = newValue
            },
            modifier = Modifier
                .padding(start = 21.dp, end = 60.dp)
                .fillMaxWidth()
                .focusRequester(focusRequester),
            textStyle = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            ),
            singleLine = true
        )

        // 저장 버튼
        IconButton(
            onClick = {
                if (isCategoryTitleValid) {
                    onSave(textFieldState.text)
                }
            },
            enabled = isCategoryTitleValid,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 22.dp)
                .size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "저장",
                tint = saveButtonColor,
                modifier = Modifier.size(24.dp)
            )
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
    sortType: SortType = SortType.DATE,
    sortDirection: SortDirection = SortDirection.ASCENDING,
    viewMode: ViewMode = ViewMode.LIST,
    onSortTypeToggle: () -> Unit = {},
    onSortDirectionToggle: () -> Unit = {},
    onViewModeToggle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val sortTypeText = when (sortType) {
        SortType.DATE -> "스크랩한 날짜 순"
        SortType.TITLE -> "제목 순"
    }

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

            // 정렬 아이콘 (클릭 시 오름차순/내림차순 토글)
            IconButton(
                onClick = onSortDirectionToggle,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = when (sortDirection) {
                        SortDirection.ASCENDING -> Icons.Outlined.ArrowCircleUp
                        SortDirection.DESCENDING -> Icons.Outlined.ArrowCircleDown
                    },
                    contentDescription = "정렬 방향",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 정렬 텍스트 (클릭 시 정렬 기준 토글)

            Text(
                text = sortTypeText,
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = GrayColor,
                modifier = Modifier.clickable(enabled = true, onClick = onSortTypeToggle)
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
fun ScrapScreenContentPreview() {
    Scrap2025Theme {
        ScrapScreenContent(
            categoryName = "분류되지 않음",
            scrapItemsResult = Result.Success(
                ScrapDummyData.dummyScrapItems
            ),
            viewMode = ViewMode.LIST,
            sortType = SortType.DATE,
            sortDirection = SortDirection.ASCENDING,
            onSortTypeToggle = {},
            onSortDirectionToggle = {},
            onViewModeToggle = {},
            onAddScrap = {},
            onUpdateCategoryTitle = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TopBarWithTitlePreview() {
    Scrap2025Theme {
        TopBarWithTitle(
            categoryTitle = "분류되지 않음",
            onUpdateCategory = {},
            onDeleteCategory = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TopBarDefaultPreview() {
    Scrap2025Theme {
        TopBarDefault(
            categoryTitle = "분류되지 않음",
            onEditClick = {},
            onDeleteClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TopBarEditModePreview() {
    Scrap2025Theme {
        TopBarEditMode(
            initialCategoryTitle = "분류되지 않음",
            onSave = {},
            onCancel = {}
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
