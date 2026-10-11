package com.scrap2025.scrap2025.utils

private val HARD_LINE_BREAK_REGEX = Regex("(?: {2,}|\\\\)\\n")

/**
 * 마크다운 렌더러(multiplatform-markdown-renderer 0.28.0)의 줄바꿈 처리 차이를 보정합니다.
 *
 * - 줄 끝 공백 2칸(또는 `\`) 줄바꿈: 렌더러가 줄바꿈 3개로 그려 빈 줄 2개가 생기므로 일반 줄바꿈으로 바꾼다.
 *   렌더러는 일반 줄바꿈을 한 줄 내림으로 그리기 때문에 웹과 같은 결과가 된다.
 * - CRLF(`\r\n`) 줄바꿈은 LF(`\n`)로 통일한다. (웹 textarea 입력은 CRLF로 전송되어 위 규칙에 매칭되지 않음)
 */
fun String.normalizeMarkdownLineBreaks(): String = replace("\r\n", "\n")
    .replace(HARD_LINE_BREAK_REGEX, "\n")
