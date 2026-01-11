package com.scrap2025.scrap2025.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.scrap2025.scrap2025.navigation.destinations.AddScrap
import com.scrap2025.scrap2025.navigation.destinations.Category
import com.scrap2025.scrap2025.navigation.destinations.CategorySelection
import com.scrap2025.scrap2025.navigation.destinations.EditMemo
import com.scrap2025.scrap2025.navigation.destinations.Scrap
import com.scrap2025.scrap2025.navigation.destinations.ScrapDetail
import com.scrap2025.scrap2025.navigation.destinations.ScrapGraph
import com.scrap2025.scrap2025.ui.category.screens.Mode
import com.scrap2025.scrap2025.ui.scrap.screens.AddScrapScreen
import com.scrap2025.scrap2025.ui.scrap.screens.EditMemoScreen
import com.scrap2025.scrap2025.ui.scrap.screens.ScrapDetailScreen
import com.scrap2025.scrap2025.ui.scrap.screens.ScrapScreen
import com.scrap2025.scrap2025.viewmodel.MainViewModel

fun NavGraphBuilder.scrapNavGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    navigation<ScrapGraph>(startDestination = Scrap) {
        composable<Scrap> {
            ScrapScreen(
                navigateToAddScrap = { navController.navigate(AddScrap) },
                navigateToCategory = { navController.navigate(Category) },
                navigateToCategorySelection = { scrapIds ->
                    navController.navigate(
                        CategorySelection(
                            mode = Mode.MOVE,
                            scrapIds = scrapIds,
                            initialCategoryId = mainViewModel.selectedCategoryId.value,
                            initialCategoryTitle = mainViewModel.selectedCategoryTitle.value,
                        )
                    )
                },
                navigateToScrapDetail = { scrapId ->
                    navController.navigate(ScrapDetail(scrapId))
                }
            )
        }

        composable<AddScrap> { AddScrapScreen(onBack = { navController.popBackStack() }) }

        composable<EditMemo> { EditMemoScreen(onBack = { navController.popBackStack() }) }

        composable<ScrapDetail> { backStackEntry ->
            val scrapId = backStackEntry.toRoute<ScrapDetail>().scrapId

            ScrapDetailScreen(
                onBack = { navController.popBackStack() },
                onEditMemo = { initialMemo ->
                    navController.navigate(EditMemo(scrapId, initialMemo))
                },
                onMove = {
                    navController.navigate(
                        CategorySelection(
                            mode = Mode.MOVE,
                            scrapIds = listOf(scrapId),
                            initialCategoryId = mainViewModel.selectedCategoryId.value,
                            initialCategoryTitle = mainViewModel.selectedCategoryTitle.value
                        )
                    )
                }
            )
        }
    }
}
