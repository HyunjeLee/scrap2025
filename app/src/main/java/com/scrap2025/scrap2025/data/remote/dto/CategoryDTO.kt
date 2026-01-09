package com.scrap2025.scrap2025.data.remote.dto

import com.scrap2025.scrap2025.model.CategoryItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryListResponse(
    @SerialName("total") val total: Int,
    @SerialName("categories") val categories: List<CategoryItemResponse>,
)

@Serializable
data class CategoryItemResponse(
    @SerialName("categoryId") val categoryId: Long,
    @SerialName("categoryTitle") val categoryTitle: String,
    @SerialName("scrapCnt") val scrapCount: Int,
    @SerialName("sequence") val orderIndex: Int,
    @SerialName("isDefault") val isDefault: Boolean
) {
    fun toDomainModel(): CategoryItem {
        return CategoryItem(
            id = categoryId,
            title = categoryTitle,
            scrapCount = scrapCount,
            isDefault = isDefault,
            orderIndex = orderIndex,
        )
    }
}

// CREATE
@Serializable
data class CreateCategoryRequest(val categoryTitle: String)

// RENAME
@Serializable
data class RenameCategoryRequest(val newCategoryTitle: String)

@Serializable
data class RenameCategoryResponse(val newCategoryTitle: String)

// ORDER INDEX // SEQUENCE
@Serializable
data class SequenceCategoryRequest(val categoryIdList: List<Long>)

@Serializable
data class SequenceCategoryResponse(val categoryIdList: List<Long>)
