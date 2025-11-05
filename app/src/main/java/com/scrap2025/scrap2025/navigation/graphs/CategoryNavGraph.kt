package com.scrap2025.scrap2025.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.scrap2025.scrap2025.data.local.CategoryDummyData
import com.scrap2025.scrap2025.model.CategoryItem
import com.scrap2025.scrap2025.navigation.NavRoute
import com.scrap2025.scrap2025.ui.category.screens.AddCategoryScreen
import com.scrap2025.scrap2025.ui.category.screens.CategoryScreen
import com.scrap2025.scrap2025.ui.category.screens._categories
import java.util.UUID

fun NavGraphBuilder.categoryNavGraph(navController: NavHostController) {
    composable(NavRoute.CATEGORY) {
        CategoryScreen(navController = navController)
    }

    composable(NavRoute.ADD_CATEGORY) {
        AddCategoryScreen(
            navController = navController,
            onAddCategory = { categoryName ->
                // 새 카테고리 생성 및 리스트에 추가
                val newCategory = CategoryItem(
                    id = UUID.randomUUID().toString(),
                    name = categoryName,
                    scrapCount = 0
                )
                CategoryDummyData.dummyCategories.add(newCategory)

                // StateFlow 업데이트 (CategoryScreen이 이를 감시)
                _categories.value = CategoryDummyData.dummyCategories.toList()
            }
        )
    }
}
