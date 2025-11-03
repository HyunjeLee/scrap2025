package com.scrap2025.scrap2025.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
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

    composable(NavRoute.MAIN) {
        MainScreen(parentNavController = navController)
    }
}
