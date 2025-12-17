package com.scrap2025.scrap2025.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.scrap2025.scrap2025.ui.login.screens.LoginScreen
import com.scrap2025.scrap2025.ui.main.screens.MainScreen

fun NavGraphBuilder.mainNavGraph(navController: NavHostController) {
    composable<Login> {
        LoginScreen(
            onLoginClick = {
                navController.navigate(Main) {
                    popUpTo<Login> { inclusive = true }
                }
            }
        )
    }

    composable<Main> {
        MainScreen(
            parentNavController = navController
        )
    }
}
