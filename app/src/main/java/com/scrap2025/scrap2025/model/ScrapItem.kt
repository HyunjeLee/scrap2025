package com.scrap2025.scrap2025.model

import java.time.LocalDateTime

data class ScrapItem(
    val id: Long,
    val title: String,
    val description: String = "",
    val memo: String = "",
    val url: String,
    val imageUrl: String? = null,
    val createdDate: LocalDateTime,
    val isFavorite: Boolean = false,
    val categoryId: Long? = null,
    val categoryTitle: String? = null,
)
