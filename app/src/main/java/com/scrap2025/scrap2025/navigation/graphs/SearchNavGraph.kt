package com.scrap2025.scrap2025.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.scrap2025.scrap2025.navigation.Search
import com.scrap2025.scrap2025.ui.search.screens.SearchScreen

fun NavGraphBuilder.searchNavGraph() {
    composable<Search> { SearchScreen() }
}
