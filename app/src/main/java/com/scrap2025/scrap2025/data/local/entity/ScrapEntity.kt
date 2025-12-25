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
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL // 카테고리 삭제 시 스크랩은 유지하되 카테고리만 null/기본값 처리 등을 고려해야 함. 일단 SET_NULL
        )
    ],
    indices = [Index(value = ["categoryId"])]
)
data class ScrapEntity(
    @PrimaryKey val id: String,
    val remoteId: Int? = null,
    val title: String,
    val description: String,
    val url: String,
    val imageUrl: String? = null,
    val createdDate: LocalDateTime,
    val isFavorite: Boolean = false,
    val categoryId: String,
    val memo: String? = null,
    val syncStatus: SyncStatus = SyncStatus.PENDING
) {
    fun toDomainModel(): ScrapItem {
        return ScrapItem(
            id = id,
            remoteId = remoteId,
            title = title,
            description = description,
            url = url,
            imageUrl = imageUrl,
            createdDate = createdDate,
            isFavorite = isFavorite,
            categoryId = categoryId,
            memo = memo,
            syncStatus = syncStatus
        )
    }

    companion object {
        fun fromDomainModel(item: ScrapItem): ScrapEntity {
            return ScrapEntity(
                id = item.id,
                remoteId = item.remoteId,
                title = item.title,
                description = item.description,
                url = item.url,
                imageUrl = item.imageUrl,
                createdDate = item.createdDate,
                isFavorite = item.isFavorite,
                categoryId = item.categoryId,
                memo = item.memo,
                syncStatus = item.syncStatus
            )
        }
    }
}
