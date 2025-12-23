package com.scrap2025.scrap2025.data.local

import com.scrap2025.scrap2025.model.CategoryItem
import com.scrap2025.scrap2025.model.CategoryItem.Companion.DEFAULT_CATEGORY_ID

object CategoryDummyData {
    val dummyCategories = mutableListOf(CategoryItem(id = DEFAULT_CATEGORY_ID, name = "분류되지 않음", scrapCount = 0))
}
