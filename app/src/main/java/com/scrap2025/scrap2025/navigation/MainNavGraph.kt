package com.scrap2025.scrap2025.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.scrap2025.scrap2025.navigation.graphs.categoryNavGraph
import com.scrap2025.scrap2025.navigation.graphs.favoriteNavGraph
import com.scrap2025.scrap2025.navigation.graphs.myPageNavGraph
import com.scrap2025.scrap2025.navigation.graphs.scrapNavGraph
import com.scrap2025.scrap2025.navigation.graphs.searchNavGraph
import com.scrap2025.scrap2025.ui.screens.LoginScreen
import com.scrap2025.scrap2025.ui.screens.MainScreen

fun NavGraphBuilder.mainNavGraph(navController: NavHostController) {
    composable(NavRoute.LOGIN) {
        LoginScreen(
            onLoginClick = {
                navController.navigate(NavRoute.MAIN) {
                    popUpTo(NavRoute.LOGIN) { inclusive = true }
                }
            }
        )
    }

    navigation(
        route = NavRoute.MAIN,
        startDestination = NavRoute.CATEGORY
    ) {
        composable(NavRoute.MAIN) {
            MainScreen(navController = navController)
        }

        categoryNavGraph()
        scrapNavGraph()
        favoriteNavGraph()
        searchNavGraph()
        myPageNavGraph()
    }
}
