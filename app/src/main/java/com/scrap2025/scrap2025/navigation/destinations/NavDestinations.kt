package com.scrap2025.scrap2025.navigation.destinations

import com.scrap2025.scrap2025.ui.category.screens.Mode
import kotlinx.serialization.Serializable

/** Type-Safe Navigation Route Definitions */
sealed interface Route

// Auth Routes
@Serializable
object Login : Route

// Main Routes
@Serializable
object Main : Route

// Tab Routes
@Serializable
object Category : Route

@Serializable
data class CategorySelection(
    val mode: Mode,
    val scrapIds: List<Long>? = null,
    val initialCategoryId: Long? = null,
    val initialCategoryTitle: String? = null,
    // for search
    val initialSelectedIds: List<Long> = emptyList()
) : Route

@Serializable
object AddCategory : Route

@Serializable
object Scrap : Route

@Serializable
object AddScrap : Route

@Serializable
data class EditMemo(val scrapId: Long, val initialMemo: String) : Route

@Serializable
data class ScrapDetail(val scrapId: Long) : Route

@Serializable
object Favorite : Route

@Serializable
object Search : Route

@Serializable
object MyPage : Route

// Graph Routes
@Serializable
object ScrapGraph : Route

@Serializable
object CategoryGraph : Route
