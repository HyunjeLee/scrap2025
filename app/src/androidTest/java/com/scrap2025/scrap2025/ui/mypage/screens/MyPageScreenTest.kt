package com.scrap2025.scrap2025.ui.mypage.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import org.junit.Rule
import org.junit.Test

class MyPageScreenTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun myPageScreen_displaysEffectively() {
        // Given
        composeTestRule.setContent { Scrap2025Theme { MyPageScreen(onSignOut = {}) } }

        // When & Then - Verify Title
        composeTestRule.onNodeWithText("마이페이지").assertIsDisplayed()

        // When & Then - Verify Greeting
        // 'XXX' was set in the latest user edit
        composeTestRule.onNodeWithText("안녕하세요 XXX 님!").assertIsDisplayed()

        // When & Then - Verify Stats
        composeTestRule.onNodeWithText("스크랩").assertIsDisplayed()
        composeTestRule.onNodeWithText("276개").assertIsDisplayed()

        composeTestRule.onNodeWithText("카테고리").assertIsDisplayed()
        composeTestRule.onNodeWithText("6개").assertIsDisplayed()

        // Verify Icons exist (using content description if available, assuming "스크랩", "카테고리" were
        // used as content description in StatItem)
        // In MyPageScreen.kt: contentDescription = label
        composeTestRule.onNodeWithContentDescription("스크랩").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("카테고리").assertIsDisplayed()
    }

    @Test
    fun myPageScreen_menuItems_areClickable() {
        // Given
        composeTestRule.setContent {
            Scrap2025Theme { MyPageScreen(onSignOut = { }) }
        }

        // When & Then - Verify Menu Items
        val menuItems = listOf("고객센터", "로그아웃", "회원탈퇴")

        menuItems.forEach { item ->
            composeTestRule
                    .onNodeWithText(item)
                    .assertIsDisplayed()
                    .performClick() // Verify it handles click without crashing
        }
    }
}
