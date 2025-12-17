package com.scrap2025.scrap2025.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.scrap2025.scrap2025.navigation.Favorite
import com.scrap2025.scrap2025.ui.favorite.screens.FavoriteScreen

fun NavGraphBuilder.favoriteNavGraph() {
    composable<Favorite> { FavoriteScreen() }
}
