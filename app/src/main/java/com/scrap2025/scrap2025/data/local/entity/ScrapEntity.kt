package com.scrap2025.scrap2025.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.scrap2025.scrap2025.data.model.SyncStatus
import com.scrap2025.scrap2025.model.ScrapItem
import java.time.LocalDateTime

@Entity(
    tableName = "scraps",
    foreignKeys =
        [
            ForeignKey(
                entity = CategoryEntity::class,
                parentColumns = ["id"],
                childColumns = ["categoryId"],
                onDelete = ForeignKey.RESTRICT
            )],
    indices = [Index(value = ["categoryId"])]
)
data class ScrapEntity(
    @PrimaryKey val id: String,
    val remoteId: Int? = null,
    val title: String,
    val url: String,
    val imageUrl: String? = null,
    val description: String,
    val memo: String,
    val createdDate: LocalDateTime,
    val isFavorite: Boolean = false,
    val categoryId: String,
    val syncStatus: SyncStatus = SyncStatus.PENDING
) {
    fun toDomainModel(): ScrapItem {
        return ScrapItem(
            id = id,
            remoteId = remoteId,
            title = title,
            url = url,
            imageUrl = imageUrl,
            description = description,
            memo = memo,
            createdDate = createdDate,
            isFavorite = isFavorite,
            categoryId = categoryId,
            syncStatus = syncStatus
        )
    }

    companion object {
        fun fromDomainModel(item: ScrapItem): ScrapEntity {
            return ScrapEntity(
                id = item.id,
                remoteId = item.remoteId,
                title = item.title,
                url = item.url,
                imageUrl = item.imageUrl,
                description = item.description,
                memo = item.memo,
                createdDate = item.createdDate,
                isFavorite = item.isFavorite,
                categoryId = item.categoryId,
                syncStatus = item.syncStatus
            )
        }
    }
}
