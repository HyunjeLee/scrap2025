package com.scrap2025.scrap2025.ui.category.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.scrap2025.scrap2025.data.local.CategoryDummyData
import com.scrap2025.scrap2025.model.CategoryItem
import com.scrap2025.scrap2025.model.GlobalUiState
import com.scrap2025.scrap2025.navigation.AddCategory
import com.scrap2025.scrap2025.navigation.Scrap
import com.scrap2025.scrap2025.ui.category.components.CategoryItemCard
import com.scrap2025.scrap2025.ui.theme.BackgroundColor
import com.scrap2025.scrap2025.ui.theme.GrayColor
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import com.scrap2025.scrap2025.viewmodel.CategoryUiState
import com.scrap2025.scrap2025.viewmodel.CategoryViewModel

/** CategoryScreen - Container Composable ViewModel에서 상태를 추출하여 CategoryScreenContent에 전달 */
@Composable
fun CategoryScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null,
    viewModel: CategoryViewModel,
    onCategoryClick: ((CategoryItem) -> Unit)? = null,
    showFab: Boolean = true,
) {
    val uiState by viewModel.uiState.collectAsState()

    CategoryScreenContent(
        uiState = uiState,
        onCategoryClick = { category ->
            GlobalUiState.setCategory(category.id, category.name)

            if (onCategoryClick != null) {
                onCategoryClick(category)
            } else {
                navController?.navigate(
                    Scrap(categoryId = category.id, categoryName = category.name)
                ) { popUpTo(0) }
            }
        },
        onAddClick = { navController?.navigate(AddCategory) },
        showFab = showFab,
        modifier = modifier
    )
}

/** CategoryScreenContent - Presentational Composable ViewModel 의존성 없이 순수한 데이터만 받아서 UI 렌더링 */
@Composable
fun CategoryScreenContent(
    uiState: CategoryUiState,
    onCategoryClick: (CategoryItem) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    showFab: Boolean = true,
) {
    Box(modifier = modifier
        .fillMaxSize()
        .background(BackgroundColor)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 헤더 (높이 68dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .background(Color.White),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "카테고리",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(start = 21.dp),
                )
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = GrayColor,
                thickness = 0.5.dp
            )

            // 카테고리 리스트 - 상태 처리
            when (uiState) {
                is CategoryUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MainColorDeep)
                    }
                }

                is CategoryUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "카테고리를 불러올 수 없습니다",
                                style =
                                    TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                color = GrayColor
                            )
                            uiState.message?.let {
                                Text(
                                    text = it,
                                    style = TextStyle(fontSize = 14.sp),
                                    color = GrayColor,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }

                is CategoryUiState.Success -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(count = uiState.categories.size) { index ->
                            val category = uiState.categories[index]
                            CategoryItemCard(
                                categoryItem = category,
                                onClick = { onCategoryClick(category) }
                            )
                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth(),
                                color = GrayColor,
                                thickness = 0.5.dp
                            )
                        }
                    }
                }
            }
        }

        // FAB 버튼 - 오른쪽 하단에 고정
        if (showFab) {
            FloatingActionButton(
                onClick = onAddClick,
                shape = CircleShape,
                containerColor = MainColor,
                contentColor = MainColorDeep,
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 21.dp, bottom = 21.dp)
                        .size(60.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "카테고리 추가",
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryScreenContentPreview() {
    Scrap2025Theme {
        CategoryScreenContent(
            uiState = CategoryUiState.Success(CategoryDummyData.dummyCategories),
            onCategoryClick = {},
            onAddClick = {}
        )
    }
}
