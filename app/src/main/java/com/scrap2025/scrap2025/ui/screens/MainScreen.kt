package com.scrap2025.scrap2025.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.scrap2025.scrap2025.navigation.NavRoute
import com.scrap2025.scrap2025.navigation.graphs.categoryNavGraph
import com.scrap2025.scrap2025.navigation.graphs.favoriteNavGraph
import com.scrap2025.scrap2025.navigation.graphs.myPageNavGraph
import com.scrap2025.scrap2025.navigation.graphs.scrapNavGraph
import com.scrap2025.scrap2025.navigation.graphs.searchNavGraph
import com.scrap2025.scrap2025.ui.components.BottomNavigationBar
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import com.scrap2025.scrap2025.viewmodel.MainViewModel

@Composable
fun MainScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val selectedTabRoute by mainViewModel.selectedTabRoute.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(
                selectedRoute = selectedTabRoute,
                onItemClick = { route ->
                    mainViewModel.selectTab(route)
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = NavRoute.CATEGORY,
                modifier = Modifier.fillMaxSize()
            ) {
                categoryNavGraph()
                scrapNavGraph()
                favoriteNavGraph()
                searchNavGraph()
                myPageNavGraph()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    Scrap2025Theme {
        MainScreen(
            navController = rememberNavController()
        )
    }
}
