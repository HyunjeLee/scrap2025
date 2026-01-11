package com.scrap2025.scrap2025.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.scrap2025.scrap2025.navigation.destinaitons.Favorite
import com.scrap2025.scrap2025.navigation.destinaitons.ScrapDetail
import com.scrap2025.scrap2025.ui.favorite.screens.FavoriteScreen

fun NavGraphBuilder.favoriteNavGraph(navController: NavHostController) {
    composable<Favorite> {
        FavoriteScreen(
            navigateToScrapDetail = { scrapId -> navController.navigate(ScrapDetail(scrapId)) }
        )
    }
}
