package com.scrap2025.scrap2025.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.scrap2025.scrap2025.data.model.SyncStatus

@Entity(tableName = "mypage")
data class MyPageEntity(
    @PrimaryKey val id: Int = 0, // Single User
    val name: String,
    val totalCategory: Int,
    val totalScrap: Int,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)
