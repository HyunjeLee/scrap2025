package com.scrap2025.scrap2025.ui.category.screens

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scrap2025.scrap2025.model.CategoryItem
import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.ui.category.components.CategoryItemCard
import com.scrap2025.scrap2025.ui.common.buttons.TwoButtons
import com.scrap2025.scrap2025.ui.common.components.ErrorScreen
import com.scrap2025.scrap2025.ui.common.components.LoadingScreen
import com.scrap2025.scrap2025.ui.common.topbars.TopBarWithBack
import com.scrap2025.scrap2025.ui.theme.BackgroundColor
import com.scrap2025.scrap2025.ui.theme.GrayColor
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep
import com.scrap2025.scrap2025.viewmodel.CategorySelectionViewModel

enum class Mode {
    MOVE, SHARE
}

@Composable
fun CategorySelectionScreen(
    title: String,
    confirmText: String,
    onBack: () -> Unit,
    onAddClick: () -> Unit,
    onConfirmClick: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CategorySelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.categoryUiState.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val selectedCategoryName by viewModel.selectedCategoryName.collectAsState()

    fun onConfirm() = {
        when (viewModel.mode) {
            Mode.MOVE -> {

            }

            Mode.SHARE -> {
                onConfirmClick(selectedCategoryId, selectedCategoryName)
            }
        }
    }

    when (val state = uiState) {
        Result.Loading -> {
            LoadingScreen()
        }

        is Result.Error -> {
            ErrorScreen(
                errorText = "카테고리를 불러올 수 없습니다",
                errorState = state
            )
        }

        is Result.Success -> {
            CategorySelectionScreenContent(
                categories = state.data,
                selectedCategoryId = selectedCategoryId,
                title = title,
                confirmText = confirmText,
                onBack = onBack,
                onCategoryClick = { id, name -> viewModel.updateSelectedCategory(id, name) },
                onAddClick = onAddClick,
                onConfirm = onConfirm(),
                modifier = modifier
            )
        }
    }
}

@Composable
fun CategorySelectionScreenContent(
    categories: List<CategoryItem>,
    selectedCategoryId: String,
    title: String,
    confirmText: String,
    onBack: () -> Unit,
    onCategoryClick: (String, String) -> Unit,
    onAddClick: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = { TopBarWithBack(title = title, onBack = onBack) },
        bottomBar = {
            TwoButtons(
                confirmText = confirmText,
                onCancel = onBack,
                onConfirm = onConfirm
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                shape = CircleShape,
                containerColor = MainColor,
                contentColor = MainColorDeep,
                modifier =
                    Modifier
                        .offset(y = 16.dp)  // 기본 여백 제거
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
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
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
                            isSelected = selectedCategoryId == item.id,
                            isSelectable = true,
                            categoryItem = item,
                            onClick = { onCategoryClick(item.id, item.name) }
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
        selectedCategoryId = "",
        title = "카테고리 선택",
        confirmText = "이동",
        onBack = {},
        onConfirm = {},
        onCategoryClick = { _, _ -> },
        onAddClick = {}
    )
}

val dummyCategories =
    listOf(
        CategoryItem(
            id = "1",
            name = "분류되지 않음",
            orderIndex = 0,
        ),
        CategoryItem(
            id = "2",
            name = "코테 자료",
            orderIndex = 0,
        ),
        CategoryItem(
            id = "3",
            name = "IBM Technology",
            orderIndex = 0,
        ),
    )