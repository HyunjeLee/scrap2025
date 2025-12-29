package com.scrap2025.scrap2025.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.scrap2025.scrap2025.navigation.AddScrap
import com.scrap2025.scrap2025.navigation.Category
import com.scrap2025.scrap2025.navigation.CategorySelection
import com.scrap2025.scrap2025.navigation.EditMemo
import com.scrap2025.scrap2025.navigation.Scrap
import com.scrap2025.scrap2025.navigation.ScrapDetail
import com.scrap2025.scrap2025.navigation.ScrapGraph
import com.scrap2025.scrap2025.ui.category.screens.Mode
import com.scrap2025.scrap2025.ui.scrap.screens.AddScrapScreen
import com.scrap2025.scrap2025.ui.scrap.screens.EditMemoScreen
import com.scrap2025.scrap2025.ui.scrap.screens.ScrapDetailScreen
import com.scrap2025.scrap2025.ui.scrap.screens.ScrapScreen

fun NavGraphBuilder.scrapNavGraph(navController: NavHostController) {
    navigation<ScrapGraph>(
        startDestination = Scrap
    ) {


        composable<Scrap> { ScrapScreen(
            navigateToAddScrap = { navController.navigate(AddScrap) },
            navigateToCategory = { navController.navigate(Category) },
            navigateToScrapDetail = { scrapId -> navController.navigate(ScrapDetail(scrapId)) }
        ) }

        composable<AddScrap> { AddScrapScreen(onBack = { navController.popBackStack() }) }

        composable<EditMemo> { EditMemoScreen(onBack = { navController.popBackStack() }) }

        composable<ScrapDetail> { backStackEntry ->
            val scrapId = backStackEntry.toRoute<ScrapDetail>().scrapId

            ScrapDetailScreen(
                onBack = { navController.popBackStack() },
                onEditMemo = { initialMemo -> navController.navigate(EditMemo(scrapId, initialMemo)) },
                onMove = { navController.navigate(CategorySelection(Mode.MOVE, scrapId)) }
            )
        }
    }
}
