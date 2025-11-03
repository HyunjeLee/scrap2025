package com.scrap2025.scrap2025

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.scrap2025.scrap2025.navigation.NavRoute
import com.scrap2025.scrap2025.navigation.mainNavGraph
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scrap2025Theme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoute.LOGIN
    ) {
        mainNavGraph(navController)
    }
}

@Preview(showBackground = true)
@Composable
fun AppNavigationPreview() {
    Scrap2025Theme {
        AppNavigation()
    }
}