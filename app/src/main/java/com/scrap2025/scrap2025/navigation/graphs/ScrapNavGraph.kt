package com.scrap2025.scrap2025.navigation.graphs

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.scrap2025.scrap2025.navigation.NavRoute
import com.scrap2025.scrap2025.ui.scrap.screens.AddScrapScreen
import com.scrap2025.scrap2025.ui.scrap.screens.ScrapScreen
import com.scrap2025.scrap2025.viewmodel.AddScrapViewModel
import com.scrap2025.scrap2025.viewmodel.CategoryViewModel
import com.scrap2025.scrap2025.viewmodel.ScrapViewModel

fun NavGraphBuilder.scrapNavGraph(navController: NavHostController) {
    navigation(
        startDestination =
            "${NavRoute.SCRAP}?categoryId={categoryId}&categoryName={categoryName}",
        route = NavRoute.SCRAP_GRAPH
    ) {
        composable(
            route = "${NavRoute.SCRAP}?categoryId={categoryId}&categoryName={categoryName}",
            arguments =
                listOf(
                    navArgument("categoryId") {
                        type = NavType.StringType
                        defaultValue = "1"
                    },
                    navArgument("categoryName") {
                        type = NavType.StringType
                        defaultValue = "분류되지 않음"
                    }
                )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: "1"
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "분류되지 않음"

            // Hilt를 통한 ViewModel 주입
            val scrapViewModel: ScrapViewModel = hiltViewModel()
            val categoryViewModel: CategoryViewModel = hiltViewModel()

            ScrapScreen(
                categoryId = categoryId,
                initialCategoryName = categoryName,
                navController = navController,
                scrapViewModel = scrapViewModel,
                categoryViewModel = categoryViewModel
            )
        }

        composable(
            route = "${NavRoute.ADD_SCRAP}/{categoryId}",
            arguments =
                listOf(
                    navArgument("categoryId") {
                        type = NavType.StringType
                        defaultValue = "1" // 기본값: 분류되지 않음
                    }
                )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: "1"
            val addScrapViewModel: AddScrapViewModel = hiltViewModel()

            AddScrapScreen(
                categoryId = categoryId,
                navController = navController,
                viewModel = addScrapViewModel
            )
        }
    }
}
