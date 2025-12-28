package com.scrap2025.scrap2025.data.model

import com.scrap2025.scrap2025.data.local.entity.ScrapEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Serializable
data class ScrapListResponse(
    @SerialName("scraps") val scraps: List<ScrapResponse>,
)

@Serializable
data class ScrapResponse(
    @SerialName("scrapId") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("scrapURL") val url: String,
    @SerialName("imageURL") val imageUrl: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("memo") val memo: String = "",
    @SerialName("isFavorite") val isFavorite: Boolean,
    @SerialName("scrapDate") val scrapDate: String = ""
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
            remoteId = id,
            title = title,
            description = description,
            memo = memo,
            url = url,
            imageUrl = imageUrl,
            createdDate = parsedDate,
            isFavorite = isFavorite,
            categoryId = categoryId,
            syncStatus = SyncStatus.SYNCED,
        )
    }
}
