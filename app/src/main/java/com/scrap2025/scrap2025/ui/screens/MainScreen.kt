package com.scrap2025.scrap2025.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.scrap2025.scrap2025.navigation.NavRoute
import com.scrap2025.scrap2025.navigation.TabNavHost
import com.scrap2025.scrap2025.ui.components.BottomNavigationBar
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import com.scrap2025.scrap2025.viewmodel.MainViewModel

@Composable
fun MainScreen(
    parentNavController: NavHostController,
    mainViewModel: MainViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val selectedTabRoute by mainViewModel.selectedTabRoute.collectAsState()
    val tabNavController = rememberNavController()
    val currentBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // 현재 라우트에 따라 자동으로 바텀바 선택 상태 동기화
    LaunchedEffect(currentRoute) {
        when {
            currentRoute?.startsWith(NavRoute.CATEGORY) == true ->
                mainViewModel.selectTab(NavRoute.CATEGORY)

            currentRoute?.startsWith(NavRoute.SCRAP) == true ->
                mainViewModel.selectTab(NavRoute.SCRAP)

            currentRoute == NavRoute.FAVORITE ->
                mainViewModel.selectTab(NavRoute.FAVORITE)

            currentRoute == NavRoute.SEARCH ->
                mainViewModel.selectTab(NavRoute.SEARCH)

            currentRoute == NavRoute.MYPAGE ->
                mainViewModel.selectTab(NavRoute.MYPAGE)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (currentRoute != NavRoute.ADD_CATEGORY) {
                BottomNavigationBar(
                    selectedRoute = selectedTabRoute,
                    onItemClick = { route ->
                        if (selectedTabRoute != route) {  //memo: 현재 탭과 이동하려는 탭이 같은 탭이라면 이동하지 않도록 구현
                            tabNavController.navigate(route) {
                                popUpTo(0)
                            }

                            mainViewModel.selectTab(route)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabNavHost(tabNavController = tabNavController)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    Scrap2025Theme {
        MainScreen(
            parentNavController = rememberNavController()
        )
    }
}
