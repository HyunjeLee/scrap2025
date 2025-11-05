package com.scrap2025.scrap2025.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.scrap2025.scrap2025.navigation.NavRoute
import com.scrap2025.scrap2025.ui.screens.ScrapScreen

fun NavGraphBuilder.scrapNavGraph() {
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
        ScrapScreen(categoryName = categoryName)
    }
}
