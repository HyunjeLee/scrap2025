package com.scrap2025.scrap2025.data.remote.dto

import com.scrap2025.scrap2025.data.local.entity.CategoryEntity
import com.scrap2025.scrap2025.data.model.SyncStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class CategoryListResponse(
    @SerialName("total") val total: Int,
    @SerialName("categories") val categories: List<CategoryItemResponse>,
)

@Serializable
data class CategoryItemResponse(
    @SerialName("categoryId") val categoryRemoteId: Int,
    @SerialName("categoryTitle") val categoryTitle: String,
    @SerialName("scrapCnt") val scrapCount: Int,
    @SerialName("sequence") val orderIndex: Int,
    @SerialName("isDefault") val isDefault: Boolean
) {
    fun toEntity(): CategoryEntity {
        return CategoryEntity(
            id = UUID.randomUUID().toString(),
            remoteId = categoryRemoteId,
            name = categoryTitle,
            scrapCount = scrapCount,
            isDefault = isDefault,
            orderIndex = orderIndex - 1, // 서버에서 1부터 count  // 로컬은 0부터 count
            syncStatus = SyncStatus.SYNCED
        )
    }
}

// CREATE
@Serializable data class CreateCategoryRequest(val categoryTitle: String)
@Serializable data class CreateCategoryResponse(val categoryTitle: String)

// RENAME
@Serializable data class RenameCategoryRequest(val newCategoryTitle: String)
@Serializable data class RenameCategoryResponse(val newCategoryTitle: String)

// ORDER INDEX // SEQUENCE
@Serializable data class SequenceCategoryRequest(val categoryIdList: List<Int>)
@Serializable data class SequenceCategoryResponse(val categoryIdList: List<Int>)