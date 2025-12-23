package com.scrap2025.scrap2025.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.scrap2025.scrap2025.data.model.SyncStatus
import com.scrap2025.scrap2025.model.CategoryItem

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val scrapCount: Int,
    val orderIndex: Int = 0,
    val syncStatus: SyncStatus = SyncStatus.PENDING
) {
    fun toDomainModel(): CategoryItem {
        return CategoryItem(
            id = id,
            name = name,
            scrapCount = scrapCount,
            orderIndex = orderIndex,
            syncStatus = syncStatus
        )
    }

    companion object {
        fun fromDomainModel(item: CategoryItem): CategoryEntity {
            return CategoryEntity(
                id = item.id,
                name = item.name,
                scrapCount = item.scrapCount,
                orderIndex = item.orderIndex,
                syncStatus = item.syncStatus
            )
        }
    }
}
