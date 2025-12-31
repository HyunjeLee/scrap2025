package com.scrap2025.scrap2025.data.model

import com.scrap2025.scrap2025.model.ScrapItem
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
data class SearchResult(
    val meta: SearchMeta,
    val scraps: List<SearchScrapItem>
)

@Serializable
data class SearchMeta(
    val totalElemnt: Int, // todo: 서버에서 오타 수정 시 같이 수정할 것
    val numOfElement: Int,
    val isEnd: Boolean
)

@Serializable
data class SearchScrapItem(
    val scrapId: Int,
    val categoryTitle: String,
    val scrapTitle: String,
    val scrapURL: String,
    val imageURL: String?,
    val isFavorite: Boolean,
    val scrapDate: String
) {
    fun toDomainModel(): ScrapItem {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val parsedDate = try {
            LocalDate.parse(scrapDate, formatter).atStartOfDay()
        } catch (e: Exception) {
            LocalDateTime.now() // 파싱 실패 시 기본값 처리
        }

        return ScrapItem(
            id = "",
            remoteId = scrapId,
            title = scrapTitle,
            url = scrapURL,
            imageUrl = imageURL,
            createdDate = parsedDate,
            isFavorite = isFavorite,
            categoryId = "",
            categoryTitle = categoryTitle
        )
    }
}
