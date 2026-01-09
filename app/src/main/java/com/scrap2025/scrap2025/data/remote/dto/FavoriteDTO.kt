package com.scrap2025.scrap2025.data.remote.dto

import com.scrap2025.scrap2025.model.ScrapItem
import com.scrap2025.scrap2025.utils.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteListToggleRequest(val scrapIdList: List<Long>)

@Serializable
data class FavoriteListResponse(val meta: Meta, val scraps: List<FavoriteItemResponse>)

@Serializable
data class FavoriteItemResponse(
    val categoryTitle: String = "",
    @SerialName("scrapId") val scrapId: Long = -1L,
    val scrapTitle: String = "",
    @SerialName("scrapURL") val scrapUrl: String = "",
    @SerialName("imageURL") val imageUrl: String? = null,
    val scrapDate: String = "",
) {
    fun toDomainModel(): ScrapItem = ScrapItem(
        id = scrapId,
        title = scrapTitle,
        description = "",
        memo = "",
        url = scrapUrl,
        imageUrl = imageUrl,
        createdDate = scrapDate.toLocalDateTime(),
        isFavorite = true,
        categoryId = -1L, // Favorite case, might need special handling
        categoryTitle = categoryTitle,
    )
}
