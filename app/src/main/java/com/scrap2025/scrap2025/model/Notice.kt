package com.scrap2025.scrap2025.model

import java.time.LocalDateTime

/** 서버 공지사항 목록의 한 항목 (본문 제외) */
data class NoticeSummary(val id: Long, val title: String, val createdAt: LocalDateTime?)

/** 서버 공지사항 상세 (본문 포함) */
data class Notice(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: LocalDateTime?
)
