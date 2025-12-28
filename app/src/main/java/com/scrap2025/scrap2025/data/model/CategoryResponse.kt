package com.scrap2025.scrap2025.data.model

import com.scrap2025.scrap2025.data.local.entity.CategoryEntity
import java.util.UUID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryListResponse(
    @SerialName("categories") val categories: List<CategoryResponse>,
    @SerialName("total") val total: Int
)

@Serializable
data class CategoryResponse(
    @SerialName("categoryId") val categoryId: Int,
    @SerialName("categoryTitle") val categoryTitle: String,
    @SerialName("scrapCnt") val scrapCnt: Int,
    @SerialName("sequence") val sequence: Int,
    @SerialName("isDefault") val isDefault: Boolean
) {
    fun toEntity(): CategoryEntity {
        return CategoryEntity(
            id = UUID.randomUUID().toString(),
            remoteId = categoryId,
            name = categoryTitle,
            scrapCount = scrapCnt,
            isDefault = isDefault,
            orderIndex = sequence - 1, // 서버에서 1부터 count  // 로컬은 0부터 count
            syncStatus = SyncStatus.SYNCED
        )
    }
}
