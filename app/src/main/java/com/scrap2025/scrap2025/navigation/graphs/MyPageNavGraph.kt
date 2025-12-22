package com.scrap2025.scrap2025.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.scrap2025.scrap2025.navigation.Login
import com.scrap2025.scrap2025.navigation.Main
import com.scrap2025.scrap2025.navigation.MyPage
import com.scrap2025.scrap2025.ui.mypage.screens.MyPageScreen

fun NavGraphBuilder.myPageNavGraph(rootNavController: NavHostController) {
    composable<MyPage> {
        MyPageScreen(
            onSignOut = {
                rootNavController.navigate(Login) { popUpTo(Main) { inclusive = true } }
            }
        )
    }
}
