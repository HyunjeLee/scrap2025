package com.scrap2025.scrap2025.model

data class ScrapItem(
    val id: String,
    val title: String,
    val url: String,
    val imageUrl: String? = null,
    val createdDate: String,
    val isFavorite: Boolean = false,
    val categoryId: String? = null,
    val memo: String? = null,
)
