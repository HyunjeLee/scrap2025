package com.scrap2025.scrap2025.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.scrap2025.scrap2025.navigation.destinaitons.AddCategory
import com.scrap2025.scrap2025.navigation.destinaitons.AddScrap
import com.scrap2025.scrap2025.navigation.destinaitons.Category
import com.scrap2025.scrap2025.navigation.destinaitons.CategoryGraph
import com.scrap2025.scrap2025.navigation.destinaitons.CategorySelection
import com.scrap2025.scrap2025.navigation.destinaitons.Scrap
import com.scrap2025.scrap2025.ui.category.screens.AddCategoryScreen
import com.scrap2025.scrap2025.ui.category.screens.CategoryScreen
import com.scrap2025.scrap2025.ui.category.screens.CategorySelectionScreen
import com.scrap2025.scrap2025.viewmodel.MainViewModel

fun NavGraphBuilder.categoryNavGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    navigation<CategoryGraph>(startDestination = Category) {
        composable<Category> {
            CategoryScreen(
                onCategoryClick = { category ->
                    mainViewModel.setGlobalCategory(category.id, category.title)
                    navController.navigate(Scrap) { popUpTo(0) }
                },
                onAddClick = { navController.navigate(AddCategory) }
            )
        }

        composable<AddCategory> { AddCategoryScreen(onBack = { navController.popBackStack() }) }

        composable<CategorySelection> {
            CategorySelectionScreen(
                onBack = { navController.popBackStack() },
                onAddClick = { navController.navigate(AddCategory) },
                onConfirmAdd = { categoryId, categoryTitle ->
                    mainViewModel.setGlobalCategory(categoryId, categoryTitle)
                    // 1. 해당 카테고리의 스크랩 리스트로 백스택 교체
                    navController.navigate(Scrap) { popUpTo(0) { inclusive = true } }
                    // 2. 스크랩 추가 화면으로 이동 (사용자가 추가 취소 시 스크랩 리스트로 돌아감)
                    navController.navigate(AddScrap)
                },
                onConfirmSearch = { categories ->
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "selectedCategories",
                        categories
                    )
                }
            )
        }
    }
}
