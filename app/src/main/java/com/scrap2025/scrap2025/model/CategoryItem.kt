package com.scrap2025.scrap2025.model

import com.scrap2025.scrap2025.data.model.SyncStatus

data class CategoryItem(
        val id: String,
        val remoteId: Int? = null,
        val name: String,
        val scrapCount: Int = 0,
        val isDefault: Boolean = false,
        val syncStatus: SyncStatus = SyncStatus.PENDING
) {
        companion object {
                const val DEFAULT_ID = "00000000-0000-0000-0000-000000000000"
                const val DEFAULT_NAME = "분류되지 않음"
        }
}
