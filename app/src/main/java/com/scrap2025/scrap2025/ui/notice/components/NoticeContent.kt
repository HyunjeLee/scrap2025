package com.scrap2025.scrap2025.ui.notice.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.mikepenz.markdown.coil2.*
import com.mikepenz.markdown.m3.*
import com.mikepenz.markdown.model.*
import com.scrap2025.scrap2025.model.*
import com.scrap2025.scrap2025.ui.theme.*
import com.scrap2025.scrap2025.utils.*
import java.time.*
import java.time.format.*

private val noticeDateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

fun LocalDateTime.toNoticeDateText(): String = format(noticeDateFormatter)

/**
 * NoticeContent - 공지사항 전체 내용을 한 화면으로 보여주는 컴포저블
 *
 * 본문이 길면 스크롤되며, "다시 보지 않기" / "닫기"는 본문이 끝나는 지점에 위치한다.
 * 본문이 짧아 스크롤이 필요 없으면 버튼은 화면 하단에 붙는다.
 *
 * @param onClose "닫기" 클릭 시 호출
 * @param onDismissPermanently "다시 보지 않기" 클릭 시 호출. null이면 버튼을 표시하지 않는다.
 */
@Composable
fun NoticeContent(
    notice: Notice,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    onDismissPermanently: (() -> Unit)? = null
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize().background(Color.White)) {
        val viewportHeight = maxHeight

        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = viewportHeight)
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Column {
                Text(
                    text = "공지사항",
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                    color = MainColorDeep
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = notice.title,
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                    color = Color.Black
                )

                notice.createdAt?.let { createdAt ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = createdAt.toNoticeDateText(),
                        style = TextStyle(fontSize = 13.sp),
                        color = GrayColor
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = LightGrayColor, thickness = 1.dp)
                Spacer(modifier = Modifier.height(20.dp))

                NoticeMarkdown(content = notice.content)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
            ) {
                if (onDismissPermanently != null) {
                    NoticeTextButton(
                        text = "다시 보지 않기",
                        color = GrayColor,
                        onClick = onDismissPermanently
                    )
                }
                NoticeTextButton(text = "닫기", color = MainColorDeep, onClick = onClose)
            }
        }
    }
}

/** 서버(웹)와 동일하게 공지 본문을 마크다운으로 렌더링한다. 이미지는 Coil, 링크는 브라우저로 연다. */
@Composable
private fun NoticeMarkdown(content: String) {
    val bodyStyle = TextStyle(fontSize = 15.sp, lineHeight = 24.sp)

    // 렌더러 줄바꿈 처리 차이 보정 (줄 끝 공백 2칸 → 빈 줄 2개로 보이는 문제 등)
    val normalizedContent = remember(content) { content.normalizeMarkdownLineBreaks() }

    Markdown(
        content = normalizedContent,
        colors = markdownColor(
            text = Color.Black,
            linkText = MainColorDeep,
            codeBackground = LightGrayColor,
            dividerColor = LightGrayColor
        ),
        typography = markdownTypography(
            h1 = TextStyle(fontSize = 22.sp, lineHeight = 30.sp, fontWeight = FontWeight.Bold),
            h2 = TextStyle(fontSize = 20.sp, lineHeight = 28.sp, fontWeight = FontWeight.Bold),
            h3 = TextStyle(fontSize = 18.sp, lineHeight = 26.sp, fontWeight = FontWeight.Bold),
            h4 = TextStyle(fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold),
            h5 = TextStyle(fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold),
            h6 = TextStyle(fontSize = 15.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold),
            text = bodyStyle,
            paragraph = bodyStyle,
            ordered = bodyStyle,
            bullet = bodyStyle,
            list = bodyStyle,
            quote = bodyStyle.copy(fontStyle = FontStyle.Italic, color = DarkGrayColor),
            code = TextStyle(fontSize = 14.sp, fontFamily = FontFamily.Monospace),
            inlineCode = bodyStyle.copy(fontFamily = FontFamily.Monospace),
            link = bodyStyle.copy(
                fontWeight = FontWeight.SemiBold,
                textDecoration = TextDecoration.Underline
            )
        ),
        // 기본 2dp는 문단 구분이 거의 보이지 않는다. 문단 사이에는 줄바꿈 토큰마다 여백이 더해져 약 3배가 된다.
        padding = markdownPadding(block = 8.dp),
        imageTransformer = Coil2ImageTransformerImpl,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun NoticeTextButton(text: String, color: Color, onClick: () -> Unit) {
    Text(
        text = text,
        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
        color = color,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 8.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun NoticeContentPreview() {
    Scrap2025Theme {
        NoticeContent(
            notice = Notice(
                id = 1,
                title = "스크랩 서비스 업데이트 안내",
                content = """
                    |## 업데이트 내용
                    |
                    |- **정렬 기능** 개선
                    |- 공지사항 [자세히 보기](https://teamscrap.co.kr)
                    |
                    |> 지금 바로 확인해보세요!
                """.trimMargin(),
                createdAt = LocalDateTime.of(2026, 10, 2, 10, 0)
            ),
            onClose = {},
            onDismissPermanently = {}
        )
    }
}
