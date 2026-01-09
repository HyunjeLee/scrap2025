package com.scrap2025.scrap2025.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun String.toLocalDateTime(pattern: String = "yyyy-MM-dd"): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return try {
        LocalDate.parse(this, formatter).atStartOfDay()
    } catch (e: Exception) {
        LocalDateTime.now() // 파싱 실패 시 기본값 처리
    }
}
