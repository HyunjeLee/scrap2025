package com.scrap2025.scrap2025.navigation.graphs

import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.scrap2025.scrap2025.navigation.NavRoute
import com.scrap2025.scrap2025.ui.scrap.screens.AddScrapScreen
import com.scrap2025.scrap2025.ui.scrap.screens.ScrapScreen
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

            // Navigation Graph 수준에서 ViewModel 공유
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("scrap_graph")
            }
            val viewModel: ScrapViewModel = viewModel(viewModelStoreOwner = parentEntry)

            ScrapScreen(
                categoryName = categoryName,
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(route = NavRoute.ADD_SCRAP) { backStackEntry ->
            // 동일한 Navigation Graph의 ViewModel 공유
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("scrap_graph")
            }
            val viewModel: ScrapViewModel = viewModel(viewModelStoreOwner = parentEntry)

            AddScrapScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}
