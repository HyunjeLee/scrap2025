package com.scrap2025.scrap2025.navigation.graphs

import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.scrap2025.scrap2025.navigation.destinations.CategorySelection
import com.scrap2025.scrap2025.navigation.destinations.Favorite
import com.scrap2025.scrap2025.navigation.destinations.ScrapDetail
import com.scrap2025.scrap2025.ui.category.screens.Mode
import com.scrap2025.scrap2025.ui.favorite.screens.FavoriteScreen
import com.scrap2025.scrap2025.viewmodel.MainViewModel

fun NavGraphBuilder.favoriteNavGraph(navController: NavHostController) {
    composable<Favorite> {
        val mainViewModel: MainViewModel =
            hiltViewModel(LocalContext.current as ViewModelStoreOwner)

        FavoriteScreen(
            navigateToScrapDetail = { scrapId -> navController.navigate(ScrapDetail(scrapId)) },
            navigateToCategorySelection = { scrapIds ->
                navController.navigate(
                    CategorySelection(
                        mode = Mode.MOVE,
                        scrapIds = scrapIds,
                        initialCategoryId = mainViewModel.selectedCategoryId.value,
                        initialCategoryTitle = mainViewModel.selectedCategoryTitle.value
                    )
                )
            }
        )
    }
}
