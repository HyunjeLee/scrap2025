package com.scrap2025.scrap2025.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.scrap2025.scrap2025.data.model.SyncStatus
import java.time.LocalDateTime

@Entity(
    tableName = "scraps",
    foreignKeys = [ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.RESTRICT
    )],
    indices = [Index(value = ["categoryId"])]
)
data class ScrapEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val url: String,
    val imageUrl: String? = null,
    val description: String,
    val memo: String,
    val createdDate: LocalDateTime,
    val isFavorite: Boolean = false,
    val categoryId: Long,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)
