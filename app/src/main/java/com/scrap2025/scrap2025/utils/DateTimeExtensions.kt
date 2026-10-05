package com.scrap2025.scrap2025.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun String.toLocalDateTime(pattern: String = "yyyy-MM-dd"): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return try {
        LocalDate.parse(this, formatter).atStartOfDay()
    } catch (e: Exception) {
        LocalDateTime.now() // 파싱 실패 시 기본값 처리
    }
}

/**
 * ISO-8601 형식의 일시 문자열을 기기 시간대의 [LocalDateTime]으로 변환합니다.
 * 오프셋이 있는 형식(예: 2026-10-02T01:36:40.355Z)과 없는 형식(예: 2026-10-02T10:36:40)을 모두 지원하며,
 * 파싱에 실패하면 null을 반환합니다.
 */
fun String.toLocalDateTimeOrNull(): LocalDateTime? = runCatching {
    OffsetDateTime
        .parse(this)
        .atZoneSameInstant(ZoneId.systemDefault())
        .toLocalDateTime()
}.recoverCatching { LocalDateTime.parse(this) }.getOrNull()
