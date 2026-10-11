package com.scrap2025.scrap2025.utils

import junit.framework.TestCase.assertEquals
import org.junit.Test

class MarkdownExtensionsTest {
    @Test
    fun `줄 끝 공백 2칸 줄바꿈은 일반 줄바꿈으로 바뀐다`() {
        val content = "**스크랩의 \"웹 버전(베타)\"**이  \n2026년 9월 26일 새롭게 출시되었습니다!"

        assertEquals(
            "**스크랩의 \"웹 버전(베타)\"**이\n2026년 9월 26일 새롭게 출시되었습니다!",
            content.normalizeMarkdownLineBreaks()
        )
    }

    @Test
    fun `역슬래시 줄바꿈은 일반 줄바꿈으로 바뀐다`() {
        assertEquals("첫 줄\n둘째 줄", "첫 줄\\\n둘째 줄".normalizeMarkdownLineBreaks())
    }

    @Test
    fun `문단 구분과 목록은 그대로 유지된다`() {
        val content = "안녕하세요.\n\n- 출시일: 2026년 9월 26일\n- 로그인: 동일"

        assertEquals(content, content.normalizeMarkdownLineBreaks())
    }

    @Test
    fun `CRLF 줄바꿈도 같은 규칙으로 정리된다`() {
        assertEquals(
            "첫 줄\n둘째 줄\n\n셋째 줄",
            "첫 줄  \r\n둘째 줄\r\n\r\n셋째 줄".normalizeMarkdownLineBreaks()
        )
    }
}
