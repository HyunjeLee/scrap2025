package com.scrap2025.scrap2025.data.model

import com.google.gson.annotations.SerializedName
import com.scrap2025.scrap2025.data.local.entity.CategoryEntity
import java.util.UUID

data class CategoryListResponse(
    @SerializedName("categories") val categories: List<CategoryResponse>,
    @SerializedName("total") val total: Int
)

data class CategoryResponse(
    @SerializedName("categoryId") val categoryId: Int,
    @SerializedName("categoryTitle") val categoryTitle: String,
    @SerializedName("scrapCnt") val scrapCnt: Int,
    @SerializedName("sequence") val sequence: Int,
    @SerializedName("isDefault") val isDefault: Boolean
) {
    fun toEntity(): CategoryEntity {
        return CategoryEntity(
            id = UUID.randomUUID().toString(),
            remoteId = categoryId,
            name = categoryTitle,
            scrapCount = scrapCnt,
            isDefault = isDefault,
            syncStatus = SyncStatus.SYNCED
        )
    }
}
