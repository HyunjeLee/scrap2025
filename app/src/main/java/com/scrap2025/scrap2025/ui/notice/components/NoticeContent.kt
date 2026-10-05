package com.scrap2025.scrap2025.ui.notice.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scrap2025.scrap2025.model.Notice
import com.scrap2025.scrap2025.ui.theme.GrayColor
import com.scrap2025.scrap2025.ui.theme.LightGrayColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

                Text(
                    text = notice.content,
                    style = TextStyle(fontSize = 15.sp, lineHeight = 24.sp),
                    color = Color.Black
                )
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
                content = "안녕하세요, 스크랩 팀입니다.\n\n스크랩 정렬 기능이 개선되었습니다. 지금 바로 확인해보세요!",
                createdAt = LocalDateTime.of(2026, 10, 2, 10, 0)
            ),
            onClose = {},
            onDismissPermanently = {}
        )
    }
}
