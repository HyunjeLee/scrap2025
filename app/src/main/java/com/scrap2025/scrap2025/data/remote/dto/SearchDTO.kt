package com.scrap2025.scrap2025.data.remote.dto

import com.scrap2025.scrap2025.model.ScrapItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class SearchRequest(
    val searchScope: List<String>,
    val categoryScope: List<Int>,
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
    @SerialName("scrapId") val scrapRemoteId: Int = -1,
    val categoryTitle: String = "",
    val scrapTitle: String = "",
    @SerialName("scrapURL") val scrapUrl: String = "",
    @SerialName("imageURL") val imageUrl: String? = null,
    val isFavorite: Boolean = false,
    val scrapDate: String = ""
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
            url = scrapUrl,
            imageUrl = imageUrl,
            createdDate = parsedDate,
            isFavorite = isFavorite,
            categoryId = categoryLocalId,
            categoryTitle = categoryTitle
        )
    }
}

@Serializable
data class SearchFavoriteResponse(
    val total: Int,
    val scraps: List<SearchFavoriteItemResponse>
)

@Serializable
data class SearchFavoriteItemResponse(
    @SerialName("scrapId") val scrapRemoteId: Int,
    @SerialName("title") val scrapTitle: String,
    @SerialName("scrapURL") val scrapUrl: String = "",
    @SerialName("imageURL") val imageUrl: String? = null,
    val isFavorite: Boolean = true,
    val scrapDate: String = ""
) {
    fun toDomainModel(scrapLocalId: String, categoryLocalId: String, categoryTitle: String): ScrapItem {
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
            url = scrapUrl,
            imageUrl = imageUrl,
            createdDate = parsedDate,
            isFavorite = isFavorite,
            categoryId = categoryLocalId,
            categoryTitle = categoryTitle,
        )
    }
}

@Serializable
data class SearchScrapResponse(
    val total: Int,
    val scraps: List<SearchScrapItemResponse>
)

@Serializable
data class SearchScrapItemResponse(
    @SerialName("scrapId") val scrapRemoteId: Int,
    @SerialName("title") val scrapTitle: String,
    @SerialName("scrapURL") val scrapUrl: String = "",
    @SerialName("imageURL") val imageUrl: String? = null,
    val isFavorite: Boolean = false,
    val scrapDate: String = ""
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
            url = scrapUrl,
            imageUrl = imageUrl,
            createdDate = parsedDate,
            isFavorite = isFavorite,
            categoryId = categoryLocalId,
        )
    }
}