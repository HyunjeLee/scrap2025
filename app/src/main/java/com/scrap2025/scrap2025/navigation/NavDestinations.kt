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

@Serializable object Scrap : Route

@Serializable object AddScrap : Route

@Serializable data class EditMemo(val scrapId: String, val initialMemo: String) : Route

@Serializable
data class ScrapDetail(val scrapId: String) : Route

@Serializable object Favorite : Route

@Serializable object Search : Route

@Serializable object MyPage : Route

// Graph Routes
@Serializable object ScrapGraph : Route

@Serializable object CategoryGraph : Route
