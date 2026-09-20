package com.scrap2025.scrap2025.model

import kotlinx.serialization.Serializable

/**
 * Remote Config의 `notice_config` 파라미터(JSON 문자열)를 파싱한 공지사항 데이터.
 *
 * [id]가 바뀌면 이전에 닫은 사용자에게도 다시 노출된다 ([id] 기준으로 닫음 여부를 저장하기 때문).
 */
@Serializable
data class NoticeConfig(
    val id: String = "",
    val enabled: Boolean = false,
    val title: String = "",
    val body: String = "",
    val imageUrl: String? = null
)
