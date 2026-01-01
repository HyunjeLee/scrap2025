package com.scrap2025.scrap2025.navigation.graphs

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.scrap2025.scrap2025.navigation.CategorySelection
import com.scrap2025.scrap2025.navigation.ScrapDetail
import com.scrap2025.scrap2025.navigation.Search
import com.scrap2025.scrap2025.ui.category.screens.Mode
import com.scrap2025.scrap2025.ui.search.screens.SearchScreen
import com.scrap2025.scrap2025.viewmodel.SearchViewModel

fun NavGraphBuilder.searchNavGraph(navController: NavHostController) {
    composable<Search> { backStackEntry ->
        val viewModel: SearchViewModel = hiltViewModel()

        // 카테고리 선택 결과 수신 로직
        LaunchedEffect(backStackEntry) {
            backStackEntry.savedStateHandle.getStateFlow<List<String>?>("selectedCategories", null)
                .collect { categories ->
                    if (categories != null) {
                        viewModel.setSelectedCategories(categories)
                        // 결과 소비 후 데이터 비우기
                        backStackEntry.savedStateHandle.remove<List<String>>(
                            "selectedCategories"
                        )
                    }
                }
        }

        SearchScreen(
            navigateToDetail = { scrapId -> navController.navigate(ScrapDetail(scrapId)) },
            onSelectCategoryClick = {
                navController.navigate(
                    CategorySelection(
                        mode = Mode.SEARCH,
                        initialSelectedIds = viewModel.uiState.value.selectedCategoryIds
                    )
                )
            },
            viewModel = viewModel
        )
    }
}
