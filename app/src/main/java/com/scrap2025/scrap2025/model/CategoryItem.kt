package com.scrap2025.scrap2025.model

data class CategoryItem(
    val id: Long,
    val title: String,
    val scrapCount: Int = 0,
    val isDefault: Boolean = false,
    val orderIndex: Int
)
