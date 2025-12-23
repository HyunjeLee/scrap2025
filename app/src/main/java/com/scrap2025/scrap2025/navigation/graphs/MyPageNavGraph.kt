package com.scrap2025.scrap2025.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.scrap2025.scrap2025.navigation.MyPage
import com.scrap2025.scrap2025.ui.mypage.screens.MyPageScreen

fun NavGraphBuilder.myPageNavGraph() {
    composable<MyPage> { MyPageScreen() }
}
