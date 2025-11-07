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
import com.scrap2025.scrap2025.viewmodel.ScrapViewModel

fun NavGraphBuilder.scrapNavGraph(navController: NavHostController) {
    navigation(
        startDestination = "${NavRoute.SCRAP}?categoryId={categoryId}&categoryName={categoryName}",
        route = "scrap_graph"
    ) {
        composable(
            route = "${NavRoute.SCRAP}?categoryId={categoryId}&categoryName={categoryName}",
            arguments = listOf(
                navArgument("categoryId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("categoryName") {
                    type = NavType.StringType
                    defaultValue = "분류되지 않음"
                }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "분류되지 않음"

            // Hilt를 통한 ViewModel 주입
            val viewModel: ScrapViewModel = hiltViewModel()

            ScrapScreen(
                categoryName = categoryName,
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(route = NavRoute.ADD_SCRAP) {
            val addScrapViewModel: AddScrapViewModel = hiltViewModel()

            AddScrapScreen(
                navController = navController,
                viewModel = addScrapViewModel
            )
        }
    }
}
