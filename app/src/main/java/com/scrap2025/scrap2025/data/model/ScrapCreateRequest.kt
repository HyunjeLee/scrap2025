package com.scrap2025.scrap2025.data.model

data class ScrapCreateRequest(
    val scrapURL: String,
    val imageURL: String?,
    val title: String,
    val description: String,
    val memo: String?,
    val isFavorite: Boolean
)