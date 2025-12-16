package com.scrap2025.scrap2025.navigation

import androidx.navigation.NavController

fun NavController.navigateToScrap(categoryId: String, categoryName: String) {
    this.navigate("${NavRoute.SCRAP}?categoryId=$categoryId&categoryName=$categoryName") {
        popUpTo(0)
    }
}
