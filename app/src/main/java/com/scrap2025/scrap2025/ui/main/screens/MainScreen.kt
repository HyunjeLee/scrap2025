package com.scrap2025.scrap2025.ui.main.screens

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.scrap2025.scrap2025.model.GlobalUiState
import com.scrap2025.scrap2025.navigation.Category
import com.scrap2025.scrap2025.navigation.CategorySelection
import com.scrap2025.scrap2025.navigation.Favorite
import com.scrap2025.scrap2025.navigation.Login
import com.scrap2025.scrap2025.navigation.MyPage
import com.scrap2025.scrap2025.navigation.Scrap
import com.scrap2025.scrap2025.navigation.Search
import com.scrap2025.scrap2025.navigation.TabNavHost
import com.scrap2025.scrap2025.ui.common.utils.BackPressToExitHandler
import com.scrap2025.scrap2025.ui.main.components.BottomNavigationBar
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import com.scrap2025.scrap2025.viewmodel.MainViewModel

@Composable
fun MainScreen(
    parentNavController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    val tabNavController = rememberNavController()
    val currentBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    val accessToken by viewModel.accessToken.collectAsState()

    // 토큰이 null이면 로그인 화면으로 이동
    // 초기값인 ""(빈 문자열)일 때는 무시
    LaunchedEffect(accessToken) {
        if (accessToken == null) {
            parentNavController.navigate(Login) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    // 메인 탭 화면들에서만 뒤로가기 종료 처리
    val isMainTab =
            currentDestination?.hasRoute<Category>() == true ||
                    currentDestination?.hasRoute<Scrap>() == true ||
                    currentDestination?.hasRoute<Favorite>() == true ||
                    currentDestination?.hasRoute<Search>() == true ||
                    currentDestination?.hasRoute<MyPage>() == true

    if (isMainTab) {
        BackPressToExitHandler()
    }

    val customBottomBar by GlobalUiState.customBottomBar.collectAsState()
    val sharedUrl by GlobalUiState.sharedUrl.collectAsState()

    // 공유된 URL이 있으면 AddScrapScreen으로 이동
    LaunchedEffect(sharedUrl) {
        if (sharedUrl != null) {
            tabNavController.navigate(CategorySelection)
        }
    }

    Scaffold(
            modifier = modifier.fillMaxSize(),
            bottomBar = {
                when (customBottomBar) {
                    null -> { // 기본 바텀 바
                        // 메인 5개 탭에서만 바텀바 표시
                        if (isMainTab) {
                            BottomNavigationBar(
                                    currentDestination = currentDestination,
                                    onItemClick = { route ->
                                        // 현재 라우트와 다른 경우에만 이동 (hasRoute로 체크)
                                        if (!currentDestination.hasRoute(route::class)) {
                                            tabNavController.navigate(route) {
                                                popUpTo(
                                                        tabNavController.graph
                                                                .findStartDestination()
                                                                .id
                                                ) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                            )
                        }
                    }
                    else -> {
                        customBottomBar!!.invoke()
                    }
                }
            }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            TabNavHost(tabNavController = tabNavController)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    Scrap2025Theme { MainScreen(parentNavController = rememberNavController()) }
}
