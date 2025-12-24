package com.scrap2025.scrap2025.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.scrap2025.scrap2025.navigation.AddScrap
import com.scrap2025.scrap2025.navigation.Scrap
import com.scrap2025.scrap2025.navigation.ScrapGraph
import com.scrap2025.scrap2025.ui.scrap.screens.AddScrapScreen
import com.scrap2025.scrap2025.ui.scrap.screens.ScrapScreen

fun NavGraphBuilder.scrapNavGraph(navController: NavHostController) {
    navigation<ScrapGraph>(
        startDestination = Scrap
    ) {
        composable<Scrap> { ScrapScreen(navController = navController) }

        composable<AddScrap> { AddScrapScreen(navController = navController) }
    }
}
