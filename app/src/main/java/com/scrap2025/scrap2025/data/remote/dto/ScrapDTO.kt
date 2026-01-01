package com.scrap2025.scrap2025.data.remote.dto

import com.scrap2025.scrap2025.data.local.entity.ScrapEntity
import com.scrap2025.scrap2025.data.model.SyncStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

// SCRAP-LIST
@Serializable
data class ScrapListResponse(
    val meta: Meta,
    val scraps: List<ScrapItemResponse>,
)
@Serializable
data class ScrapItemResponse(
    @SerialName("scrapId") val scrapRemoteId: Int = -1,
    @SerialName("title") val scrapTitle: String = "",
    @SerialName("scrapURL") val scrapUrl: String = "",
    @SerialName("imageURL") val imageUrl: String? = null,
    val isFavorite: Boolean = false,
    val scrapDate: String = ""
) {
    fun toEntity(categoryId: String): ScrapEntity {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val parsedDate = try {
            LocalDate.parse(scrapDate, formatter).atStartOfDay()
        } catch (e: Exception) {
            LocalDateTime.now() // 파싱 실패 시 기본값 처리
        }

        return ScrapEntity(
            id = UUID.randomUUID().toString(),
            remoteId = scrapRemoteId,
            title = scrapTitle,
            description = "",
            memo = "",
            url = scrapUrl,
            imageUrl = imageUrl,
            createdDate = parsedDate,
            isFavorite = isFavorite,
            categoryId = categoryId,
            syncStatus = SyncStatus.SYNCED,
        )
    }
}

// DETAIL
data class ScrapDetailResponse(
    @SerialName("scrapId") val scrapRemoteId: Int = -1,
    @SerialName("title") val scrapTitle: String = "",
    @SerialName("scrapURL") val scrapUrl: String = "",
    @SerialName("imageURL") val imageUrl: String? = null,
    @SerialName("description") val description: String = "",
    @SerialName("memo") val memo: String = "",
    @SerialName("isFavorite") val isFavorite: Boolean = false,
    @SerialName("scrapDate") val scrapDate: String = ""
)

// MOVE
@Serializable
data class MoveScrapRequest(val moveCategoryId: Long)
@Serializable
data class MoveScrapListRequest(val scrapIds: List<Long>, val moveCategoryId: Long)

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
    val scrapUrl: String,
    val imageURL: String,
    val title: String,
    val description: String,
    val memo: String? = null,
    val isFavorite: Boolean = false
)

// MEMO // REQUEST == RESPONSE
@Serializable data class ScrapMemoDto(val memo: String)

