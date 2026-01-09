package com.scrap2025.scrap2025.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.scrap2025.scrap2025.data.model.SyncStatus

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val scrapCount: Int,
    val isDefault: Boolean = false,
    val orderIndex: Int,
    val syncStatus: SyncStatus = SyncStatus.SYNCED
)
