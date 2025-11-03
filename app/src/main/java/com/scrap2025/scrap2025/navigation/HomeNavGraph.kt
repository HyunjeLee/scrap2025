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
import com.scrap2025.scrap2025.ui.screens.HomeScreen

fun NavGraphBuilder.homeNavGraph(navController: NavHostController) {
    navigation(
        route = NavRoute.HOME,
        startDestination = NavRoute.CATEGORY
    ) {
        composable(NavRoute.HOME) {
            HomeScreen(navController = navController)
        }

        categoryNavGraph()
        scrapNavGraph()
        favoriteNavGraph()
        searchNavGraph()
        myPageNavGraph()
    }
}
