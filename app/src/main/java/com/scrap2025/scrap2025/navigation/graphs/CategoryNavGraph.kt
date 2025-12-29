package com.scrap2025.scrap2025.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.scrap2025.scrap2025.model.GlobalUiState
import com.scrap2025.scrap2025.navigation.AddCategory
import com.scrap2025.scrap2025.navigation.AddScrap
import com.scrap2025.scrap2025.navigation.Category
import com.scrap2025.scrap2025.navigation.CategoryGraph
import com.scrap2025.scrap2025.navigation.CategorySelection
import com.scrap2025.scrap2025.navigation.Scrap
import com.scrap2025.scrap2025.ui.category.screens.AddCategoryScreen
import com.scrap2025.scrap2025.ui.category.screens.CategoryScreen
import com.scrap2025.scrap2025.ui.category.screens.CategorySelectionScreen

fun NavGraphBuilder.categoryNavGraph(navController: NavHostController) {
    navigation<CategoryGraph>(startDestination = Category) {
        composable<Category> {
            CategoryScreen(
                onCategoryClick = { category ->
                    GlobalUiState.setCategory(category.id, category.name)
                    navController.navigate(Scrap) { popUpTo(0) }
                },
                onAddClick = { navController.navigate(AddCategory) }
            )
        }

        composable<AddCategory> {
            AddCategoryScreen(onBack = { navController.popBackStack() })
        }

        composable<CategorySelection> {
            CategorySelectionScreen(
                onBack = { navController.popBackStack() },
                onAddClick = { navController.navigate(AddCategory) },
                onConfirmShare = { categoryId, categoryName ->
                    GlobalUiState.setCategory(categoryId, categoryName)
                    // 1. 해당 카테고리의 스크랩 리스트로 백스택 교체
                    navController.navigate(Scrap) { popUpTo(0) { inclusive = true } }
                    // 2. 스크랩 추가 화면으로 이동 (사용자가 추가 취소 시 스크랩 리스트로 돌아감)
                    navController.navigate(AddScrap)
                },
            )
        }
    }
}
