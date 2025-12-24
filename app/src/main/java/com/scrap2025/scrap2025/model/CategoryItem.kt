package com.scrap2025.scrap2025.model

import com.scrap2025.scrap2025.data.model.SyncStatus

data class CategoryItem(
        val id: String,
        val remoteId: Int? = null,
        val name: String,
        val scrapCount: Int = 0,
        val isDefault: Boolean = false,
        val syncStatus: SyncStatus = SyncStatus.PENDING
)
