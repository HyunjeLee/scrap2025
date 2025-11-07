package com.scrap2025.scrap2025.navigation.graphs

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.scrap2025.scrap2025.navigation.NavRoute
import com.scrap2025.scrap2025.ui.category.screens.AddCategoryScreen
import com.scrap2025.scrap2025.ui.category.screens.CategoryScreen
import com.scrap2025.scrap2025.viewmodel.AddCategoryViewModel
import com.scrap2025.scrap2025.viewmodel.CategoryViewModel

/**
 * CategoryNavGraph - 카테고리 관련 네비게이션 그래프
 * ViewModel을 Hilt로 주입하여 MVVM 아키텍처 준수
 */
fun NavGraphBuilder.categoryNavGraph(navController: NavHostController) {
    composable(NavRoute.CATEGORY) {
        val viewModel: CategoryViewModel = hiltViewModel()
        CategoryScreen(
            navController = navController,
            viewModel = viewModel
        )
    }

    composable(NavRoute.ADD_CATEGORY) {
        val viewModel: AddCategoryViewModel = hiltViewModel()
        AddCategoryScreen(
            navController = navController,
            viewModel = viewModel
        )
    }
}
