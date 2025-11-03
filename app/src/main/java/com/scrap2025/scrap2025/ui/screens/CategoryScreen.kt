package com.scrap2025.scrap2025.ui.screens

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
import com.scrap2025.scrap2025.navigation.NavRoute
import com.scrap2025.scrap2025.ui.components.CategoryItemCard
import com.scrap2025.scrap2025.ui.theme.BackgroundColor
import com.scrap2025.scrap2025.ui.theme.GrayColor
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

val _categories = MutableStateFlow<List<CategoryItem>>(CategoryDummyData.dummyCategories)
val categoriesStateFlow: StateFlow<List<CategoryItem>> = _categories

@Composable
fun CategoryScreen(
    navController: NavHostController? = null,
    modifier: Modifier = Modifier
) {
    val categories by categoriesStateFlow.collectAsState()
    val categoryList = categories

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
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
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(start = 21.dp),
                )
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = GrayColor,
                thickness = 0.5.dp
            )

            // 카테고리 리스트
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(count = categoryList.size) { index ->
                    val category = categoryList[index]
                    CategoryItemCard(categoryItem = category)
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        color = GrayColor,
                        thickness = 0.5.dp
                    )
                }
            }
        }

        // FAB 버튼 - 오른쪽 하단에 고정 (Scaffold의 bottomBar 위에 배치)
        FloatingActionButton(
            onClick = {
                navController?.navigate(NavRoute.ADD_CATEGORY)
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
                contentDescription = "카테고리 추가",
                modifier = Modifier.size(50.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryScreenPreview() {
    Scrap2025Theme {
        CategoryScreen()
    }
}
