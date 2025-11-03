package com.scrap2025.scrap2025.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.scrap2025.scrap2025.ui.screens.LoginScreen

fun NavGraphBuilder.mainNavGraph(navController: NavHostController) {
    composable(NavRoute.LOGIN) {
        LoginScreen(
            onLoginClick = {
                navController.navigate(NavRoute.HOME) {
                    popUpTo(NavRoute.LOGIN) { inclusive = true }
                }
            }
        )
    }

    homeNavGraph(navController)
}
