package com.scrap2025.scrap2025.navigation.graphs

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.scrap2025.scrap2025.navigation.AddCategory
import com.scrap2025.scrap2025.navigation.AddScrap
import com.scrap2025.scrap2025.navigation.Category
import com.scrap2025.scrap2025.navigation.CategorySelection
import com.scrap2025.scrap2025.navigation.Scrap
import com.scrap2025.scrap2025.ui.category.screens.AddCategoryScreen
import com.scrap2025.scrap2025.ui.category.screens.CategoryScreen
import com.scrap2025.scrap2025.viewmodel.AddCategoryViewModel
import com.scrap2025.scrap2025.viewmodel.CategoryViewModel

/** CategoryNavGraph - 카테고리 관련 네비게이션 그래프 ViewModel을 Hilt로 주입하여 MVVM 아키텍처 준수 */
fun NavGraphBuilder.categoryNavGraph(navController: NavHostController) {
    composable<Category> {
        val viewModel: CategoryViewModel = hiltViewModel()
        CategoryScreen(navController = navController, viewModel = viewModel)
    }

    composable<AddCategory> {
        val viewModel: AddCategoryViewModel = hiltViewModel()
        AddCategoryScreen(navController = navController, viewModel = viewModel)
    }

    composable<CategorySelection> {
        val viewModel: CategoryViewModel = hiltViewModel()
        CategoryScreen(
            navController = navController,
            viewModel = viewModel,
            onCategoryClick = { category ->
                // 1. 해당 카테고리의 스크랩 리스트로 백스택 교체
                navController.navigate(
                    Scrap(categoryId = category.id, categoryName = category.name)
                ) { popUpTo<CategorySelection> { inclusive = true } }
                // 2. 스크랩 추가 화면으로 이동 (사용자가 추가 취소 시 스크랩 리스트로 돌아감)
                navController.navigate(AddScrap(categoryId = category.id))
            },
            showFab = false
        )
    }
}
