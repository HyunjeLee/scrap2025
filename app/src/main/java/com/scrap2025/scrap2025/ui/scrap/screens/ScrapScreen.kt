package com.scrap2025.scrap2025.ui.scrap.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.material.icons.automirrored.outlined.DriveFileMove
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.ArrowCircleDown
import androidx.compose.material.icons.outlined.ArrowCircleUp
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.StarBorder
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scrap2025.scrap2025.data.local.ScrapDummyData
import com.scrap2025.scrap2025.model.CategoryItem
import com.scrap2025.scrap2025.model.GlobalUiState
import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.model.ScrapItem
import com.scrap2025.scrap2025.model.SortDirection
import com.scrap2025.scrap2025.model.SortType
import com.scrap2025.scrap2025.model.ViewMode
import com.scrap2025.scrap2025.ui.common.dialogs.CommonDeleteDialog
import com.scrap2025.scrap2025.ui.scrap.components.ScrapItemCardGrid
import com.scrap2025.scrap2025.ui.scrap.components.ScrapItemCardList
import com.scrap2025.scrap2025.ui.theme.BackgroundColor
import com.scrap2025.scrap2025.ui.theme.GrayColor
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep
import com.scrap2025.scrap2025.ui.theme.MainColorLight
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import com.scrap2025.scrap2025.ui.theme.WarningColor
import com.scrap2025.scrap2025.utils.UrlNavigator
import com.scrap2025.scrap2025.viewmodel.ScrapViewModel
import kotlinx.coroutines.launch

/** ScrapScreen - Container Composable ViewModel에서 상태를 추출하여 ScrapScreenContent에 전달 */
@Composable
fun ScrapScreen(
    navigateToAddScrap: () -> Unit,
    navigateToCategory: () -> Unit,
    modifier: Modifier = Modifier,
    categoryId: String = CategoryItem.DEFAULT_ID,
    scrapViewModel: ScrapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val globalCategoryId by GlobalUiState.selectedCategoryId.collectAsState()
    val globalCategoryName by GlobalUiState.selectedCategoryName.collectAsState()

    var categoryTitle by remember(globalCategoryName) { mutableStateOf(globalCategoryName) }

    val scrapItemsResult by scrapViewModel.sortedScrapItems.collectAsState()
    val viewMode by scrapViewModel.viewMode.collectAsState()
    val sortType by scrapViewModel.sortType.collectAsState()
    val sortDirection by scrapViewModel.sortDirection.collectAsState()
    val isSelectionMode by scrapViewModel.isSelectionMode.collectAsState()
    val selectedScrapIds by scrapViewModel.selectedScrapIds.collectAsState()
    val isPreferencesLoaded by scrapViewModel.isPreferencesLoaded.collectAsState()

    // 뷰모델에 현재 카테고리 설정 (DB 필터링용)
    LaunchedEffect(globalCategoryId) { scrapViewModel.setSelectedCategory(globalCategoryId) }

    val selectionBottomBar: @Composable () -> Unit = {
        // 이전에 정의된 SelectionActionBar 컴포저블을 호출합니다.
        SelectionActionBar(
                onDelete = { scrapViewModel.deleteSelectedItems() },
                onMove = { /* todo */},
                onShare = { /* todo */},
                onFavorite = { scrapViewModel.toggleFavoriteSelectedItems() }
        )
    }

    LaunchedEffect(isSelectionMode) {
        when (isSelectionMode) {
            true -> GlobalUiState.setBottomBar(selectionBottomBar)
            false -> GlobalUiState.setBottomBar(null)
        }
    }

    // 뒤로가기 버튼 처리: 선택 모드일 때는 선택 모드 종료
    BackHandler(enabled = isSelectionMode) { scrapViewModel.exitSelectionMode() }

    ScrapScreenContent(
            categoryId = globalCategoryId,
            categoryTitle = categoryTitle,
            scrapItemsResult = scrapItemsResult,
            viewMode = viewMode,
            sortType = sortType,
            sortDirection = sortDirection,
            isSelectionMode = isSelectionMode,
            selectedScrapIds = selectedScrapIds,
            isPreferencesLoaded = isPreferencesLoaded,
            onSortTypeToggle = { scrapViewModel.toggleSortType() },
            onSortDirectionToggle = { scrapViewModel.toggleSortDirection() },
            onViewModeToggle = { scrapViewModel.toggleViewMode() },
            onItemClick = { url -> UrlNavigator.openUrl(context, url) },
            onItemLongClick = { itemId -> scrapViewModel.enterSelectionMode(itemId) },
            onItemSelectionToggle = { itemId -> scrapViewModel.toggleScrapItemSelection(itemId) },
            onSelectAll = { scrapViewModel.selectAllScrapItems() },
            onDeselectAll = { scrapViewModel.deselectAllScrapItems() },
            onAddScrap = { navigateToAddScrap() },
            onUpdateCategoryTitle = { categoryId, newTitle ->
                scrapViewModel.updateCategoryTitle(id = categoryId, newTitle = newTitle)
                GlobalUiState.setCategory(categoryId, newTitle)
            },
            onDeleteCategory = {
                scrapViewModel.deleteCategory(categoryId)
                GlobalUiState.setCategory(CategoryItem.DEFAULT_ID, CategoryItem.DEFAULT_NAME)
                navigateToCategory()
            },
            modifier = modifier
    )
}

/** ScrapScreenContent - Presentational Composable ViewModel 의존성 없이 순수한 데이터만 받아서 UI 렌더링 */
@Composable
fun ScrapScreenContent(
        categoryId: String,
        categoryTitle: String,
        scrapItemsResult: Result<List<ScrapItem>>,
        viewMode: ViewMode,
        sortType: SortType,
        sortDirection: SortDirection,
        isSelectionMode: Boolean,
        selectedScrapIds: Set<String>,
        isPreferencesLoaded: Boolean,
        onSortTypeToggle: () -> Unit,
        onSortDirectionToggle: () -> Unit,
        onViewModeToggle: () -> Unit,
        onItemClick: (String) -> Unit,
        onItemLongClick: (String) -> Unit,
        onItemSelectionToggle: (String) -> Unit,
        onSelectAll: () -> Unit,
        onDeselectAll: () -> Unit,
        onAddScrap: () -> Unit,
        onUpdateCategoryTitle: (String, String) -> Unit,
        onDeleteCategory: () -> Unit,
        modifier: Modifier = Modifier
) {
    // Compose UI 상태 (View에서 관리)
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    // 삭제 확인 모달 표시 상태
    var showDeleteDialog by remember { mutableStateOf(false) }

    // 파생 상태: 스크롤 위치에서 버튼 표시 여부 계산
    val showScrollToTop by
            remember(viewMode) {
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

    Box(modifier = modifier.fillMaxSize().background(BackgroundColor)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 상단 바: 선택 모드에 따라 다른 UI 표시
            if (isSelectionMode) {
                // 선택 모드 - 선택 개수 및 전체 선택 버튼
                val totalCount =
                        when (val result = scrapItemsResult) {
                            is Result.Success -> result.data.size
                            else -> 0
                        }
                SelectionTopBar(
                        categoryTitle = categoryTitle,
                        selectedCount = selectedScrapIds.size,
                        totalCount = totalCount,
                        onSelectAll = onSelectAll,
                        onDeselectAll = onDeselectAll
                )
            } else {
                // 일반 모드 - 제목, 검색, 정렬 바
                TopBarWithTitle(
                        categoryId = categoryId,
                        categoryTitle = categoryTitle,
                        onUpdateCategory = { categoryId, categoryName ->
                            onUpdateCategoryTitle(categoryId, categoryName)
                        },
                        onDeleteCategory = { showDeleteDialog = true }
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
            }

            // 스크랩 리스트/그리드
            when (val result = scrapItemsResult) {
                is Result.Loading -> {
                    Box(
                            modifier = Modifier.fillMaxSize().weight(1f),
                            contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator(color = MainColorDeep) }
                }
                is Result.Error -> {
                    Box(
                            modifier = Modifier.fillMaxSize().weight(1f),
                            contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                    text = "스크랩을 불러올 수 없습니다",
                                    style =
                                            TextStyle(
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Medium
                                            ),
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
                    // Preferences가 로드되지 않았으면 로딩 표시
                    if (!isPreferencesLoaded) {
                        Box(
                                modifier = Modifier.fillMaxSize().weight(1f),
                                contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = MainColorDeep) }
                    } else {
                        // Preferences 로드 완료 후 실제 리스트/그리드 렌더링
                        val scrapItems = result.data
                        when (viewMode) {
                            ViewMode.LIST -> {
                                LazyColumn(
                                        state = listState,
                                        modifier = Modifier.fillMaxSize().weight(1f)
                                ) {
                                    items(scrapItems) { scrapItem ->
                                        ScrapItemCardList(
                                                scrapItem = scrapItem,
                                                isSelectionMode = isSelectionMode,
                                                isSelected =
                                                        selectedScrapIds.contains(scrapItem.id),
                                                onClick = { onItemClick(scrapItem.url) },
                                                onLongClick = { onItemLongClick(scrapItem.id) },
                                                onSelectionToggle = {
                                                    onItemSelectionToggle(scrapItem.id)
                                                }
                                        )
                                    }
                                }
                            }
                            ViewMode.GRID -> {
                                LazyVerticalGrid(
                                        columns = GridCells.Fixed(2),
                                        state = gridState,
                                        modifier = Modifier.fillMaxSize().weight(1f),
                                        contentPadding =
                                                PaddingValues(horizontal = 23.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(scrapItems) { scrapItem ->
                                        ScrapItemCardGrid(
                                                scrapItem = scrapItem,
                                                isSelectionMode = isSelectionMode,
                                                isSelected =
                                                        selectedScrapIds.contains(scrapItem.id),
                                                onClick = { onItemClick(scrapItem.url) },
                                                onLongClick = { onItemLongClick(scrapItem.id) },
                                                onSelectionToggle = {
                                                    onItemSelectionToggle(scrapItem.id)
                                                }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Column(
                modifier = Modifier.align(Alignment.BottomEnd).padding(end = 21.dp, bottom = 21.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 맨 위로 가기 버튼
            AnimatedVisibility(
                    visible = showScrollToTop,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                    modifier = Modifier
            ) {
                FloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                when (viewMode) {
                                    ViewMode.LIST -> {
                                        listState.animateScrollToItem(0)
                                        gridState.scrollToItem(0)
                                    }
                                    ViewMode.GRID -> {
                                        gridState.animateScrollToItem(0)
                                        listState.scrollToItem(0)
                                    }
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

            // 스크랩 추가 버튼
            if (!isSelectionMode) { // 일반 모드일 때만 FAB 표시
                Spacer(Modifier.height(16.dp))

                FloatingActionButton(
                        onClick = onAddScrap,
                        shape = CircleShape,
                        containerColor = MainColor,
                        contentColor = MainColorDeep,
                        modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "스크랩 추가",
                            modifier = Modifier.size(50.dp)
                    )
                }
            }

            // 삭제 확인 모달
            if (showDeleteDialog) {
                CommonDeleteDialog(
                        title = "정말 카테고리를 삭제하시겠습니까?",
                        description = "(해당 카테고리에 속한 모든 스크랩 또한 삭제됩니다)",
                        confirmText = "삭제하기",
                        onDismiss = { showDeleteDialog = false },
                        onConfirm = {
                            showDeleteDialog = false
                            onDeleteCategory()
                        }
                )
            }
        }
    }
}

/**
 * TopBarWithTitle - 카테고리 제목 표시 및 편집 기능 '
 *
 * 내부적으로 편집 모드 상태를 관리하며, 모드에 따라 TopBarDefault/TopBarEditMode를 전환
 */
@Composable
fun TopBarWithTitle(
        categoryId: String,
        categoryTitle: String,
        onUpdateCategory: (String, String) -> Unit,
        onDeleteCategory: () -> Unit,
        modifier: Modifier = Modifier
) {
    // === 내부 상태: 편집 모드 관리 ===
    var isEditMode by remember { mutableStateOf(false) }

    if (isEditMode) {
        TopBarEditMode(
                categoryTitle = categoryTitle,
                onSave = { newTitle ->
                    onUpdateCategory(categoryId, newTitle)
                    isEditMode = false
                },
                onCancel = { isEditMode = false },
                modifier = modifier
        )
    } else {
        TopBarDefault(
                categoryTitle = categoryTitle,
                onEditClick = { isEditMode = true },
                onDeleteClick = { onDeleteCategory() },
                modifier = modifier
        )
    }
}

/** TopBarDefault - 일반 모드 (카테고리명 표시 + 편집/삭제 버튼) */
@Composable
private fun TopBarDefault(
        categoryTitle: String,
        onEditClick: () -> Unit,
        onDeleteClick: () -> Unit,
        modifier: Modifier = Modifier
) {
    Box(
            modifier = modifier.fillMaxWidth().height(53.dp).background(MainColor),
            contentAlignment = Alignment.CenterStart
    ) {
        // 카테고리 제목
        Text(
                text = categoryTitle,
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 21.dp, end = 85.dp)
        )

        if (categoryTitle != "분류되지 않음") {
            // 편집/삭제 버튼
            Row(
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 22.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 편집 버튼
                IconButton(onClick = onEditClick, modifier = Modifier.size(28.dp)) {
                    Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "편집",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                    )
                }

                // 삭제 버튼
                IconButton(onClick = { onDeleteClick() }, modifier = Modifier.size(28.dp)) {
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

/** TopBarEditMode - 편집 모드 (TextField + 저장 버튼) 커서를 텍스트 끝으로 자동 이동하며, 유효성 검사를 수행 */
@Composable
private fun TopBarEditMode(
        categoryTitle: String,
        onSave: (String) -> Unit,
        onCancel: () -> Unit,
        modifier: Modifier = Modifier
) {
    // === 내부 상태: 편집 중인 텍스트와 커서 위치를 포함한 TextFieldValue ===
    var textFieldState by
            remember(categoryTitle) {
                mutableStateOf(
                        TextFieldValue(
                                text = categoryTitle,
                                selection = TextRange(categoryTitle.length) // 1. 초기 커서 위치: 맨 끝
                        )
                )
            }

    // === 유효성 검사: 1자 이상 21자 이하 ===
    val isCategoryTitleValid = textFieldState.text.length in 1..21
    val saveButtonColor = if (isCategoryTitleValid) MainColorDeep else WarningColor

    // === 포커스 관리 ===
    val focusRequester = remember { FocusRequester() }

    // 편집 모드 진입 시 자동 포커스
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Box(
            modifier = modifier.fillMaxWidth().height(53.dp).background(MainColor),
            contentAlignment = Alignment.CenterStart
    ) {
        // 편집용 TextField
        BasicTextField(
                value = textFieldState,
                onValueChange = { newValue -> textFieldState = newValue },
                modifier =
                        Modifier.padding(start = 21.dp, end = 60.dp)
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                textStyle =
                        TextStyle(
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
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 22.dp).size(28.dp)
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
            modifier =
                    modifier.fillMaxWidth()
                            .height(52.dp)
                            .background(MainColor)
                            .padding(horizontal = 21.dp, vertical = 5.dp)
    ) {
        Box(
                modifier =
                        Modifier.fillMaxSize()
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
                        style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
                        color = GrayColor
                )
            }
        }
    }
}

@Composable
fun SortBar(
        modifier: Modifier = Modifier,
        sortType: SortType = SortType.DATE,
        sortDirection: SortDirection = SortDirection.ASCENDING,
        viewMode: ViewMode = ViewMode.LIST,
        onSortTypeToggle: () -> Unit = {},
        onSortDirectionToggle: () -> Unit = {},
        onViewModeToggle: () -> Unit = {}
) {
    val sortTypeText =
            when (sortType) {
                SortType.DATE -> "스크랩한 날짜 순"
                SortType.TITLE -> "제목 순"
            }

    Box(
            modifier =
                    modifier.fillMaxWidth()
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
            IconButton(onClick = onSortDirectionToggle, modifier = Modifier.size(20.dp)) {
                Icon(
                        imageVector =
                                when (sortDirection) {
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
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
                    color = GrayColor,
                    modifier = Modifier.clickable(enabled = true, onClick = onSortTypeToggle)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 구분선
            Text(
                    text = "|",
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
                    color = Color(0xFF8C8C8C)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 뷰모드 전환
            IconButton(onClick = onViewModeToggle, modifier = Modifier.size(20.dp)) {
                Icon(
                        imageVector =
                                when (viewMode) {
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

/** SelectionTopBar - 선택 모드일 때 상단 바 선택된 개수 표시 및 "전체" 선택 버튼 */
@Composable
fun SelectionTopBar(
        categoryTitle: String,
        selectedCount: Int,
        totalCount: Int,
        onSelectAll: () -> Unit,
        onDeselectAll: () -> Unit,
        modifier: Modifier = Modifier
) {
    Column {
        Box(
                modifier = modifier.fillMaxWidth().height(53.dp).background(MainColor),
                contentAlignment = Alignment.CenterStart
        ) {
            // 카테고리 제목
            Text(
                    text = categoryTitle,
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 21.dp, end = 85.dp)
            )
        }

        Box(
                modifier = modifier.fillMaxWidth().height(53.dp).background(MainColor),
                contentAlignment = Alignment.CenterStart
        ) {
            // 선택 개수 표시
            Row(
                    modifier = Modifier.padding(start = 21.dp),
                    verticalAlignment = Alignment.CenterVertically
            ) {
                // 전체 선택 체크박스
                Row(
                        modifier =
                                Modifier.clickable {
                                    if (selectedCount == totalCount) {
                                        onDeselectAll()
                                    } else {
                                        onSelectAll()
                                    }
                                },
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                                imageVector =
                                        if (selectedCount == totalCount) {
                                            Icons.Filled.CheckCircle
                                        } else {
                                            Icons.Outlined.Circle
                                        },
                                contentDescription = "전체 선택",
                                tint =
                                        if (selectedCount == totalCount) {
                                            MainColorDeep
                                        } else {
                                            GrayColor
                                        },
                                modifier = Modifier.size(24.dp)
                        )
                        Text(
                                text = "전체",
                                style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
                                color = GrayColor
                        )
                    }
                }

                Spacer(modifier = Modifier.width(13.dp))

                // 선택 개수 표시
                Text(
                        text = "${selectedCount}개 선택됨",
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal),
                        modifier = Modifier.padding(bottom = 6.dp)
                )
            }
        }
    }
}

/** SelectionActionBar - 선택 모드일 때 하단 액션 바 삭제, 이동, 공유, 즐겨찾기 버튼 */
@Composable
fun SelectionActionBar(
        onDelete: () -> Unit,
        onMove: () -> Unit,
        onShare: () -> Unit,
        onFavorite: () -> Unit,
        modifier: Modifier = Modifier
) {
    Row(
            modifier =
                    modifier.fillMaxWidth()
                            .dropShadow(
                                    shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp),
                                    shadow =
                                            Shadow(
                                                    radius = 15.dp,
                                                    spread = 0.dp,
                                                    color = Color(0xFFBEBEBE).copy(alpha = 0.4f),
                                                    offset = DpOffset(x = 0.dp, y = (-3).dp)
                                            )
                            )
                            .clip(shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp))
                            .background(MainColor)
                            .padding(vertical = 10.dp)
                            .windowInsetsPadding(WindowInsets.navigationBars),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
    ) {
        // 삭제
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onDelete() }
        ) {
            Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "삭제",
                    tint = WarningColor,
                    modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                    text = "삭제",
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
                    color = Color.Black
            )
        }

        // 공유
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onShare() }
        ) {
            Icon(
                    imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                    contentDescription = "공유",
                    tint = Color.Black,
                    modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                    text = "공유",
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
                    color = Color.Black
            )
        }

        // 이동
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onMove() }
        ) {
            Icon(
                    imageVector = Icons.AutoMirrored.Outlined.DriveFileMove,
                    contentDescription = "이동",
                    tint = Color.Black,
                    modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                    text = "이동",
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
                    color = Color.Black
            )
        }

        // 즐겨찾기
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onFavorite() }
        ) {
            Icon(
                    imageVector = Icons.Outlined.StarBorder,
                    contentDescription = "즐겨찾기",
                    tint = Color.Black,
                    modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                    text = "즐겨찾기",
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
                    color = Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SelectionActionBarPreview() {
    Scrap2025Theme { SelectionActionBar(onDelete = {}, onMove = {}, onShare = {}, onFavorite = {}) }
}

@Preview(showBackground = true)
@Composable
fun ScrapScreenContentPreview() {
    Scrap2025Theme {
        ScrapScreenContent(
                categoryId = "1",
                categoryTitle = "분류되지 않음",
                scrapItemsResult = Result.Success(ScrapDummyData.dummyScrapItems),
                viewMode = ViewMode.GRID,
                sortType = SortType.DATE,
                sortDirection = SortDirection.ASCENDING,
                isSelectionMode = false,
                selectedScrapIds = emptySet(),
                isPreferencesLoaded = true,
                onSortTypeToggle = {},
                onSortDirectionToggle = {},
                onViewModeToggle = {},
                onItemClick = {},
                onItemLongClick = {},
                onItemSelectionToggle = {},
                onSelectAll = {},
                onDeselectAll = {},
                onAddScrap = {},
                onUpdateCategoryTitle = { dummy1, dummy2 -> },
                onDeleteCategory = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ScrapScreenContentSelectionModePreview() {
    Scrap2025Theme {
        ScrapScreenContent(
                categoryId = "1",
                categoryTitle = "분류되지 않음",
                scrapItemsResult = Result.Success(ScrapDummyData.dummyScrapItems),
                viewMode = ViewMode.LIST,
                sortType = SortType.DATE,
                sortDirection = SortDirection.ASCENDING,
                isSelectionMode = true,
                selectedScrapIds = emptySet(),
                isPreferencesLoaded = true,
                onSortTypeToggle = {},
                onSortDirectionToggle = {},
                onViewModeToggle = {},
                onItemClick = {},
                onItemLongClick = {},
                onItemSelectionToggle = {},
                onSelectAll = {},
                onDeselectAll = {},
                onAddScrap = {},
                onUpdateCategoryTitle = { dummy1, dummy2 -> },
                onDeleteCategory = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TopBarWithTitlePreview() {
    Scrap2025Theme {
        TopBarWithTitle(
                categoryId = "1",
                categoryTitle = "분류되지 않음",
                onUpdateCategory = { dummy1, dummy2 -> },
                onDeleteCategory = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TopBarDefaultPreview() {
    Scrap2025Theme {
        TopBarDefault(categoryTitle = "분류되지 않음", onEditClick = {}, onDeleteClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun TopBarEditModePreview() {
    Scrap2025Theme { TopBarEditMode(categoryTitle = "분류되지 않음", onSave = {}, onCancel = {}) }
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    Scrap2025Theme { SearchBar() }
}

@Preview(showBackground = true)
@Composable
fun SortBarPreview() {
    Scrap2025Theme { SortBar() }
}
