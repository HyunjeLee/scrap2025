package com.scrap2025.scrap2025.navigation.graphs

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.scrap2025.scrap2025.model.CategoryItem
import com.scrap2025.scrap2025.navigation.AddScrap
import com.scrap2025.scrap2025.navigation.Scrap
import com.scrap2025.scrap2025.navigation.ScrapGraph
import com.scrap2025.scrap2025.ui.scrap.screens.AddScrapScreen
import com.scrap2025.scrap2025.ui.scrap.screens.ScrapScreen
import com.scrap2025.scrap2025.viewmodel.AddScrapViewModel
import com.scrap2025.scrap2025.viewmodel.CategoryViewModel
import com.scrap2025.scrap2025.viewmodel.ScrapViewModel

fun NavGraphBuilder.scrapNavGraph(navController: NavHostController) {
    navigation<ScrapGraph>(
        startDestination = Scrap(categoryId = CategoryItem.DEFAULT_ID, categoryName = CategoryItem.DEFAULT_NAME)
    ) {
        composable<Scrap> { backStackEntry ->
            val route: Scrap = backStackEntry.toRoute()

            // Hilt를 통한 ViewModel 주입
            val scrapViewModel: ScrapViewModel = hiltViewModel()
            val categoryViewModel: CategoryViewModel = hiltViewModel()

            ScrapScreen(
                categoryId = route.categoryId,
                initialCategoryName = route.categoryName,
                navController = navController,
                scrapViewModel = scrapViewModel,
                categoryViewModel = categoryViewModel
            )
        }

        composable<AddScrap> { backStackEntry ->
            val route: AddScrap = backStackEntry.toRoute()
            val addScrapViewModel: AddScrapViewModel = hiltViewModel()

            AddScrapScreen(
                categoryId = route.categoryId,
                navController = navController,
                viewModel = addScrapViewModel
            )
        }
    }
}
