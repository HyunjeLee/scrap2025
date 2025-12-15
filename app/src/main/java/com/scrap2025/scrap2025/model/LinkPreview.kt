package com.scrap2025.scrap2025.model

import java.time.LocalDateTime

/**
 * 링크 미리보기 데이터
 * jsoup을 통해 웹 페이지에서 추출한 메타데이터
 */
data class LinkPreview(
    val url: String,
    val title: String?,
    val description: String?,
    val imageUrl: String?,
    val siteName: String? = null,
)

/**
 * LinkPreview를 ScrapItem으로 변환
 * 프리뷰 카드 표시를 위한 임시 ScrapItem 생성
 */
fun LinkPreview.toScrapItem(): ScrapItem {
    return ScrapItem(
        id = "", // 임시 ID (프리뷰이므로 실제 ID 불필요)
        title = this.title ?: this.url, // 제목이 없으면 URL 표시
        url = this.url,
        imageUrl = this.imageUrl,
        createdDate = LocalDateTime.now(), // 임시 날짜
        isFavorite = false,
        categoryId = null,
        memo = null
    )
}
