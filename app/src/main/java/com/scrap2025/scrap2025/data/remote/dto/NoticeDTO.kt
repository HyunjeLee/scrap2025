package com.scrap2025.scrap2025.data.remote.dto

import com.scrap2025.scrap2025.model.Notice
import com.scrap2025.scrap2025.model.NoticeSummary
import com.scrap2025.scrap2025.utils.toLocalDateTimeOrNull
import kotlinx.serialization.Serializable

@Serializable
data class NoticeListResponse(val notices: List<NoticeSummaryDto>, val meta: Meta)

@Serializable
data class NoticeSummaryDto(val id: Long, val title: String, val createdAt: String? = null) {
    fun toDomainModel(): NoticeSummary = NoticeSummary(
        id = id,
        title = title,
        createdAt = createdAt?.toLocalDateTimeOrNull()
    )
}

@Serializable
data class NoticeDetailResponse(
    val id: Long,
    val title: String,
    val content: String = "",
    val createdAt: String? = null
) {
    fun toDomainModel(): Notice = Notice(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt?.toLocalDateTimeOrNull()
    )
}
