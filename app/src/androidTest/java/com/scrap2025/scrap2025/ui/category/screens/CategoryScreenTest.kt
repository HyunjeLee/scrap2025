package com.scrap2025.scrap2025.ui.category.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.scrap2025.scrap2025.model.CategoryItem
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import com.scrap2025.scrap2025.viewmodel.CategoryUiState
import org.junit.Rule
import org.junit.Test

class CategoryScreenTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun categoryScreen_displaysList_whenSuccess() {
        // Given
        val categories =
            listOf(
                CategoryItem(id = 1, title = "카테고리 1", orderIndex = 0),
                CategoryItem(id = 2, title = "카테고리 2", orderIndex = 1)
            )

        // When
        composeTestRule.setContent {
            Scrap2025Theme {
                CategoryScreenContent(
                    uiState = CategoryUiState.Success(categories),
                    onCategoryClick = {},
                    isRefreshing = false // 평상시 상태
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("카테고리 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("카테고리 2").assertIsDisplayed()
    }

    @Test
    fun categoryScreen_keepsListVisible_duringRefresh() {
        // Given
        val categories = listOf(CategoryItem(id = 1, title = "유지되는 카테고리", orderIndex = 0))

        // When: isRefreshing = true (로딩 중)
        composeTestRule.setContent {
            Scrap2025Theme {
                CategoryScreenContent(
                    uiState = CategoryUiState.Success(categories),
                    onCategoryClick = {},
                    isRefreshing = true, // 리프레시 중!
                    onRefresh = {}
                )
            }
        }

        // Then: 리스트가 사라지지 않고 그대로 보여야 함 (No Blink 검증)
        composeTestRule.onNodeWithText("유지되는 카테고리").assertIsDisplayed()
    }
}
