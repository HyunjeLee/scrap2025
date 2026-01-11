package com.scrap2025.scrap2025.data.remote.dto

import com.scrap2025.scrap2025.model.ScrapItem
import com.scrap2025.scrap2025.utils.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// SCRAP-LIST
@Serializable
data class ScrapListResponse(
    val meta: Meta,
    val scraps: List<ScrapItemResponse>,
)

@Serializable
data class ScrapItemResponse(
    @SerialName("scrapId") val scrapId: Long = -1L,
    @SerialName("title") val scrapTitle: String = "",
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

// DETAIL
@Serializable
data class ScrapDetailResponse(
    @SerialName("scrapId") val scrapId: Long = -1L,
    @SerialName("title") val scrapTitle: String = "",
    @SerialName("scrapURL") val scrapUrl: String = "",
    @SerialName("imageURL") val imageUrl: String? = null,
    @SerialName("description") val description: String = "",
    @SerialName("memo") val memo: String = "",
    @SerialName("isFavorite") val isFavorite: Boolean = false,
    @SerialName("scrapDate") val scrapDate: String = ""
) {
    fun toDomainModel(): ScrapItem = ScrapItem(
        id = scrapId,
        title = scrapTitle,
        description = description,
        memo = memo,
        url = scrapUrl,
        imageUrl = imageUrl,
        createdDate = scrapDate.toLocalDateTime(),
        isFavorite = isFavorite,
    )
}

// MOVE
@Serializable
data class MoveScrapRequest(val moveCategoryId: Long)

@Serializable
data class MoveScrapBulkRequest(
    @SerialName("scrapIdList") val scrapIds: List<Long>,
    @SerialName("moveCategoryId") val categoryId: Long
)

// CREATE
@Serializable
data class CreateScrapRequest(
    val scrapURL: String,
    val imageURL: String?,
    val title: String,
    val description: String,
    val memo: String?,
    val isFavorite: Boolean
)

@Serializable
data class CreateScrapResponse(
    val scrapURL: String,
    val imageURL: String?,
    val title: String,
    val description: String? = null,
    val memo: String? = null,
    val isFavorite: Boolean = false
)

// DELETE
@Serializable
data class DeleteSCrapBulkRequest(val scrapIdList: List<Long>)

// MEMO // REQUEST == RESPONSE
@Serializable
data class ScrapMemoDto(val memo: String)
