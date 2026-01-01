package com.scrap2025.scrap2025.data.remote.dto

import com.scrap2025.scrap2025.model.ScrapItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class FavoriteListToggleRequest(val scrapIdList: List<Long>)

@Serializable
data class FavoriteListResponse(
    val meta: Meta,
    val scraps: List<FavoriteItemResponse>
)

@Serializable
data class FavoriteItemResponse(
    val categoryTitle: String = "",
    @SerialName("scrapId") val scrapRemoteId: Int = -1,
    val scrapTitle: String = "",
    @SerialName("scrapURL") val scrapUrl: String = "",
    @SerialName("imageURL") val imageUrl: String? = null,
    val scrapDate: String = "",
) {
    fun toDomainModel(scrapLocalId: String, categoryLocalId: String): ScrapItem {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val parsedDate = try {
            LocalDate.parse(scrapDate, formatter).atStartOfDay()
        } catch (e: Exception) {
            LocalDateTime.now() // 파싱 실패 시 기본값 처리
        }

        return ScrapItem(
            id = scrapLocalId,
            remoteId = scrapRemoteId,
            title = scrapTitle,
            description = "",
            memo = "",
            url = scrapUrl,
            imageUrl = imageUrl,
            createdDate = parsedDate,
            isFavorite = true,
            categoryId = categoryLocalId,
            categoryTitle = categoryTitle,
        )
    }
}