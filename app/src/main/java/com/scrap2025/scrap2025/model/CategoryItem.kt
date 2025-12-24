package com.scrap2025.scrap2025.model

import com.scrap2025.scrap2025.data.model.SyncStatus

data class CategoryItem(
        val id: String,
        val remoteId: Int? = null,
        val name: String,
        val scrapCount: Int = 0,
        val syncStatus: SyncStatus = SyncStatus.PENDING
) {
    companion object {
        const val DEFAULT_CATEGORY_ID = "10"
    }
}
