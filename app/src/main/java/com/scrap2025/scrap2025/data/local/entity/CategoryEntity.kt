package com.scrap2025.scrap2025.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.scrap2025.scrap2025.data.model.SyncStatus
import com.scrap2025.scrap2025.model.CategoryItem

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val remoteId: Int? = null,
    val name: String,
    val scrapCount: Int,
    val isDefault: Boolean = false,
    val orderIndex: Int,
    val syncStatus: SyncStatus = SyncStatus.PENDING
) {
    fun toDomainModel(): CategoryItem {
        return CategoryItem(
            id = id,
            remoteId = remoteId,
            name = name,
            scrapCount = scrapCount,
            isDefault = isDefault,
            orderIndex = orderIndex,
            syncStatus = syncStatus
        )
    }

    companion object {
        fun fromDomainModel(item: CategoryItem): CategoryEntity {
            return CategoryEntity(
                id = item.id,
                remoteId = item.remoteId,
                name = item.name,
                scrapCount = item.scrapCount,
                isDefault = item.isDefault,
                orderIndex = item.orderIndex,
                syncStatus = item.syncStatus
            )
        }
    }
}
