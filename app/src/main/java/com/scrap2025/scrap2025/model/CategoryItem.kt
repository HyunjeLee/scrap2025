package com.scrap2025.scrap2025.model

import com.scrap2025.scrap2025.data.model.SyncStatus

data class CategoryItem(
        val id: String,
        val name: String,
        val scrapCount: Int = 0,
        val syncStatus: SyncStatus = SyncStatus.PENDING
)
