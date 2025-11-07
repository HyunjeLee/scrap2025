package com.scrap2025.scrap2025.model

import java.time.LocalDateTime

data class ScrapItem(
    val id: String,
    val title: String,
    val url: String,
    val imageUrl: String? = null,
    val createdDate: LocalDateTime,
    val isFavorite: Boolean = false,
    val categoryId: String? = null,
    val memo: String? = null,
)
