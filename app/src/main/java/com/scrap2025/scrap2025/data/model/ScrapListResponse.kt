package com.scrap2025.scrap2025.data.model

import com.google.gson.annotations.SerializedName
import com.scrap2025.scrap2025.data.local.entity.ScrapEntity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.Int

data class ScrapListResponse(
    @SerializedName("scraps") val scraps: List<ScrapResponse>,
)

data class ScrapResponse(
    @SerializedName("scrapId") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("scrapURL") val url: String,
    @SerializedName("imageURL") val imageUrl: String,
    @SerializedName("isFavorite") val isFavorite: Boolean,
    @SerializedName("scrapDate") val scrapDate: String
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
            url = url,
            imageUrl = imageUrl,
            createdDate = parsedDate,
            isFavorite = isFavorite,
            categoryId = categoryId,
            syncStatus = SyncStatus.SYNCED,
        )
    }
}
