package com.scrap2025.scrap2025.ui.category.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import com.scrap2025.scrap2025.model.CategoryItem
import com.scrap2025.scrap2025.ui.category.components.CategoryItemCard
import com.scrap2025.scrap2025.ui.common.buttons.TwoButtons
import com.scrap2025.scrap2025.ui.common.components.ErrorScreen
import com.scrap2025.scrap2025.ui.common.components.LoadingScreen
import com.scrap2025.scrap2025.ui.common.topbars.TopBarWithBack
import com.scrap2025.scrap2025.ui.theme.BackgroundColor
import com.scrap2025.scrap2025.ui.theme.GrayColor
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep
import com.scrap2025.scrap2025.viewmodel.BottomBarViewModel
import com.scrap2025.scrap2025.viewmodel.CategorySelectionViewModel
import com.scrap2025.scrap2025.viewmodel.CategoryUiState

enum class Mode {
    MOVE,
    ADD,
    SEARCH
}

@Composable
fun CategorySelectionScreen(
    onBack: () -> Unit,
    onAddClick: () -> Unit,
    onConfirmAdd: (Long, String) -> Unit,
    onConfirmSearch: (List<Long>) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CategorySelectionViewModel = hiltViewModel(),
    bottomBarViewModel: BottomBarViewModel =
        hiltViewModel(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner)
) {
    val context = LocalContext.current
    val mode = viewModel.mode
    val title =
        when (mode) {
            Mode.MOVE -> "이동하기"
            Mode.ADD -> "카테고리 선택하기"
            Mode.SEARCH -> "카테고리"
        }
    val confirmText =
        when (mode) {
            Mode.MOVE -> "이동하기"
            Mode.ADD -> "다음"
            Mode.SEARCH -> "완료"
        }

    val uiState by viewModel.categoryUiState.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val selectedCategoryName by viewModel.selectedCategoryName.collectAsState()
    val selectedCategoryIds by viewModel.selectedCategoryIds.collectAsState()

    fun onConfirm(): () -> Unit = {
        when (mode) {
            Mode.MOVE -> {
                val successCallback = {
                    onBack()
                    Toast.makeText(context, "이동 완료", Toast.LENGTH_SHORT).show()
                }
                val failureCallback = {
                    Toast.makeText(context, "스크랩 이동 실패", Toast.LENGTH_SHORT).show()
                }

                when (viewModel.scrapIds.size == 1) {
                    true -> { // Single
                        viewModel.moveScrap(successCallback, failureCallback)
                    }

                    false -> { // Bulk
                        viewModel.moveScrapBulk(successCallback, failureCallback)
                    }
                }
            }

            Mode.ADD -> {
                onConfirmAdd(selectedCategoryId, selectedCategoryName)
            }

            Mode.SEARCH -> {
                onConfirmSearch(viewModel.selectedCategoryIds.value.toList())
                onBack()
            }
        }
    }

    LaunchedEffect(selectedCategoryId, selectedCategoryName, selectedCategoryIds) {
        bottomBarViewModel.setBottomBar {
            TwoButtons(confirmText = confirmText, onCancel = onBack, onConfirm = onConfirm())
        }
    }

    DisposableEffect(Unit) {
        onDispose { bottomBarViewModel.setBottomBar(null) }
    }

    when (val state = uiState) {
        is CategoryUiState.Loading -> {
            LoadingScreen()
        }

        is CategoryUiState.Error -> {
            ErrorScreen(errorText = state.message ?: "카테고리를 불러올 수 없습니다.")
        }

        is CategoryUiState.Success -> {
            CategorySelectionScreenContent(
                categories = state.categories,
                selectedCategoryId = selectedCategoryId,
                selectedCategoryIds = selectedCategoryIds,
                isMultiSelect = mode == Mode.SEARCH,
                title = title,
                onBack = onBack,
                onCategoryClick = { id, title ->
                    if (mode == Mode.SEARCH) {
                        viewModel.toggleCategorySelection(id)
                    } else {
                        viewModel.updateSelectedCategory(id, title)
                    }
                },
                onAddClick = onAddClick,
                modifier = modifier
            )
        }
    }
}

@Composable
fun CategorySelectionScreenContent(
    categories: List<CategoryItem>,
    selectedCategoryId: Long,
    selectedCategoryIds: Set<Long>,
    isMultiSelect: Boolean,
    title: String,
    onBack: () -> Unit,
    onCategoryClick: (Long, String) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = { TopBarWithBack(title = title, onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                shape = CircleShape,
                containerColor = MainColor,
                contentColor = MainColorDeep,
                modifier =
                Modifier
                    .offset(y = 16.dp) // 기본 여백 제거
                    .padding(end = 5.dp)
                    .size(60.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "카테고리 추가",
                    modifier = Modifier.size(50.dp)
                )
            }
        },
        containerColor = BackgroundColor
    ) { innerPadding ->
        Box(
            modifier =
            modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 카테고리 목록
            Column {
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = GrayColor,
                    thickness = 0.5.dp
                )
                LazyColumn {
                    items(items = categories) { item ->
                        CategoryItemCard(
                            isSelected =
                            if (isMultiSelect) {
                                selectedCategoryIds.contains(item.id)
                            } else {
                                selectedCategoryId == item.id
                            },
                            isSelectable = true,
                            categoryItem = item,
                            onClick = { onCategoryClick(item.id, item.title) }
                        )
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            color = GrayColor,
                            thickness = 0.5.dp
                        )
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = GrayColor,
                    thickness = 0.5.dp
                )
            }
        }
    }
}

@Preview
@Composable
fun CategorySelectionScreenContentPreview() {
    CategorySelectionScreenContent(
        categories = dummyCategories,
        selectedCategoryId = 0,
        selectedCategoryIds = emptySet(),
        isMultiSelect = false,
        title = "카테고리 선택",
        onBack = {},
        onCategoryClick = { _, _ -> },
        onAddClick = {}
    )
}

val dummyCategories =
    listOf(
        CategoryItem(
            id = 1,
            title = "분류되지 않음",
            orderIndex = 0
        ),
        CategoryItem(
            id = 2,
            title = "코테 자료",
            orderIndex = 0
        ),
        CategoryItem(
            id = 3,
            title = "IBM Technology",
            orderIndex = 0
        )
    )
