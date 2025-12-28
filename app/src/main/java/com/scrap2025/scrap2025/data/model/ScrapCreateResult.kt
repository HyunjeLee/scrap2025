package com.scrap2025.scrap2025.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ScrapCreateResult(
    val scrapUrl: String,
    val imageURL: String,
    val title: String,
    val description: String,
    val memo: String? = null,
    val isFavorite: Boolean = false
)
