package com.scrap2025.scrap2025.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.scrap2025.scrap2025.navigation.graphs.categoryNavGraph
import com.scrap2025.scrap2025.navigation.graphs.favoriteNavGraph
import com.scrap2025.scrap2025.navigation.graphs.myPageNavGraph
import com.scrap2025.scrap2025.navigation.graphs.scrapNavGraph
import com.scrap2025.scrap2025.navigation.graphs.searchNavGraph
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme

@Composable
fun TabNavHost(tabNavController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = tabNavController,
        startDestination = CategoryGraph,
        modifier = modifier.fillMaxSize()
    ) {
        categoryNavGraph(navController = tabNavController)
        scrapNavGraph(navController = tabNavController)
        favoriteNavGraph(navController = tabNavController)
        searchNavGraph(navController = tabNavController)
        myPageNavGraph()
    }
}

@Preview(showBackground = true)
@Composable
fun TabNavHostPreview() {
    Scrap2025Theme {
        TabNavHost(
            tabNavController = rememberNavController(),
        )
    }
}
