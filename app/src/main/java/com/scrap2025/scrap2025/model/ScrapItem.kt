package com.scrap2025.scrap2025.model

import com.scrap2025.scrap2025.data.model.SyncStatus
import java.time.LocalDateTime

data class ScrapItem(
    val id: String,
    val remoteId: Int? = null,
    val title: String,
    val description: String = "",
    val memo: String = "",
    val url: String,
    val imageUrl: String? = null,
    val createdDate: LocalDateTime,
    val isFavorite: Boolean = false,
    val categoryId: String,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)
