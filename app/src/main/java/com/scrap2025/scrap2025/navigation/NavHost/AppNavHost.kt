package com.scrap2025.scrap2025.navigation.NavHost

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.scrap2025.scrap2025.navigation.destinaitons.Login
import com.scrap2025.scrap2025.navigation.destinaitons.Main
import com.scrap2025.scrap2025.navigation.graphs.mainNavGraph
import com.scrap2025.scrap2025.ui.common.components.LoadingScreen
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import com.scrap2025.scrap2025.viewmodel.MainViewModel

@Composable
fun AppNavHost(
    mainViewModel: MainViewModel = hiltViewModel(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner)
) {
    val navController = rememberNavController()
    val accessToken by mainViewModel.accessToken.collectAsState()

    if (accessToken == "") { // 토큰을 읽어오는 중
        return LoadingScreen()
    }

    val startRoute = if (accessToken != null) Main else Login

    NavHost(
        navController = navController,
        startDestination = startRoute
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
