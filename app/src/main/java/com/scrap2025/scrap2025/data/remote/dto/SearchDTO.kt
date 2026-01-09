package com.scrap2025.scrap2025.data.remote.dto

import com.scrap2025.scrap2025.model.ScrapItem
import com.scrap2025.scrap2025.utils.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchRequest(
    val searchScope: List<String>,
    val categoryScope: List<Long>,
    val startDate: String,
    val endDate: String
)

@Serializable
data class SearchListResponse(
    val meta: Meta,
    val scraps: List<SearchItemResponse>
)

@Serializable
data class SearchItemResponse(
    @SerialName("scrapId") val scrapId: Long = -1L,
    val categoryTitle: String = "",
    val scrapTitle: String = "",
    @SerialName("scrapURL") val scrapUrl: String = "",
    @SerialName("imageURL") val imageUrl: String? = null,
    val isFavorite: Boolean = false,
    val scrapDate: String = ""
) {
    fun toDomainModel(): ScrapItem = ScrapItem(
        id = scrapId,
        title = scrapTitle,
        url = scrapUrl,
        imageUrl = imageUrl,
        createdDate = scrapDate.toLocalDateTime(),
        isFavorite = isFavorite,
        categoryTitle = categoryTitle,
    )
}

@Serializable
data class SearchFavoriteResponse(
    val total: Int,
    val scraps: List<SearchFavoriteItemResponse>
)

@Serializable
data class SearchFavoriteItemResponse(
    @SerialName("scrapId") val scrapId: Long,
    @SerialName("title") val scrapTitle: String,
    @SerialName("scrapURL") val scrapUrl: String = "",
    @SerialName("imageURL") val imageUrl: String? = null,
    val isFavorite: Boolean = true,
    val scrapDate: String = ""
) {
    fun toDomainModel(): ScrapItem = ScrapItem(
        id = scrapId,
        title = scrapTitle,
        url = scrapUrl,
        imageUrl = imageUrl,
        createdDate = scrapDate.toLocalDateTime(),
        isFavorite = isFavorite,
    )
}

@Serializable
data class SearchScrapResponse(
    val total: Int,
    val scraps: List<SearchScrapItemResponse>
)

@Serializable
data class SearchScrapItemResponse(
    @SerialName("scrapId") val scrapId: Long,
    @SerialName("title") val scrapTitle: String,
    @SerialName("scrapURL") val scrapUrl: String = "",
    @SerialName("imageURL") val imageUrl: String? = null,
    val isFavorite: Boolean = false,
    val scrapDate: String = ""
) {
    fun toDomainModel(): ScrapItem = ScrapItem(
        id = scrapId,
        title = scrapTitle,
        url = scrapUrl,
        imageUrl = imageUrl,
        createdDate = scrapDate.toLocalDateTime(),
        isFavorite = isFavorite,
    )
}