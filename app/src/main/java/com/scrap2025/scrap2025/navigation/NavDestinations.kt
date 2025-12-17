package com.scrap2025.scrap2025.navigation

import kotlinx.serialization.Serializable

/** Type-Safe Navigation Route Definitions */
sealed interface Route

// Auth Routes
@Serializable object Login : Route

// Main Routes
@Serializable object Main : Route

// Tab Routes
@Serializable object Category : Route

@Serializable object CategorySelection : Route

@Serializable object AddCategory : Route

@Serializable
data class Scrap(val categoryId: String = "1", val categoryName: String = "분류되지 않음") : Route

@Serializable
data class AddScrap(val categoryId: String = "1") : Route

@Serializable object Favorite : Route

@Serializable object Search : Route

@Serializable object MyPage : Route

// Graph Routes
@Serializable object ScrapGraph : Route
