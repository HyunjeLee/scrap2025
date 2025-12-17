package com.scrap2025.scrap2025.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Login
    ) {
        mainNavGraph(navController)
    }
}

@Preview(showBackground = true)
@Composable
fun AppNavHostPreview() {
    Scrap2025Theme {
        AppNavHost()
    }
}
