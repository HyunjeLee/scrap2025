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
import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.navigation.NavRoute
import com.scrap2025.scrap2025.navigation.navigateToScrap
import com.scrap2025.scrap2025.ui.category.components.CategoryItemCard
import com.scrap2025.scrap2025.ui.theme.BackgroundColor
import com.scrap2025.scrap2025.ui.theme.GrayColor
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import com.scrap2025.scrap2025.viewmodel.CategoryViewModel

/** CategoryScreen - Container Composable ViewModel에서 상태를 추출하여 CategoryScreenContent에 전달 */
@Composable
fun CategoryScreen(
    navController: NavHostController? = null,
    viewModel: CategoryViewModel,
    onCategoryClick: ((CategoryItem) -> Unit)? = null,
    showFab: Boolean = true,
    modifier: Modifier = Modifier
) {
    val categoriesResult by viewModel.categories.collectAsState()

    CategoryScreenContent(
        categoriesResult = categoriesResult,
        onCategoryClick = { category ->
            GlobalUiState.setCategory(category.id, category.name)

            if (onCategoryClick != null) {
                onCategoryClick(category)
            } else {
                navController?.navigateToScrap(
                    categoryId = category.id,
                    categoryName = category.name
                )
            }
        },
        onAddClick = { navController?.navigate(NavRoute.ADD_CATEGORY) },
        showFab = showFab,
        modifier = modifier
    )
}

/** CategoryScreenContent - Presentational Composable ViewModel 의존성 없이 순수한 데이터만 받아서 UI 렌더링 */
@Composable
fun CategoryScreenContent(
    categoriesResult: Result<List<CategoryItem>>,
    onCategoryClick: (CategoryItem) -> Unit,
    onAddClick: () -> Unit,
    showFab: Boolean = true,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
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

            // 카테고리 리스트 - Result 상태 처리
            when (val result = categoriesResult) {
                is Result.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MainColorDeep)
                    }
                }
                is Result.Error -> {
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
                            Text(
                                text = result.message ?: "알 수 없는 오류",
                                style = TextStyle(fontSize = 14.sp),
                                color = GrayColor,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
                is Result.Success -> {
                    val categoryList = result.data
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(count = categoryList.size) { index ->
                            val category = categoryList[index]
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
            categoriesResult = Result.Success(CategoryDummyData.dummyCategories),
            onCategoryClick = {},
            onAddClick = {}
        )
    }
}
