package com.scrap2025.scrap2025.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.scrap2025.scrap2025.navigation.NavRoute
import com.scrap2025.scrap2025.ui.screens.CategoryScreen

fun NavGraphBuilder.categoryNavGraph() {
    composable(NavRoute.CATEGORY) {
        CategoryScreen()
    }
}
