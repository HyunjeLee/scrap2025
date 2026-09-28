package com.scrap2025.scrap2025.ui.common.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.scrap2025.scrap2025.model.NoticeConfig
import com.scrap2025.scrap2025.ui.theme.DarkGrayColor
import com.scrap2025.scrap2025.ui.theme.LightGrayColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme

/**
 * NoticeDialog - Remote Config로 내려받은 공지사항을 보여주는 다이얼로그
 *
 * @param notice 표시할 공지사항 (이미지는 선택)
 * @param onClose "닫기" 클릭 시 호출 (다음 실행 시 다시 노출됨)
 * @param onDismissPermanently "다시 보지 않기" 클릭 시 호출 (같은 공지는 다시 노출되지 않음)
 */
@Composable
fun NoticeDialog(
    notice: NoticeConfig,
    onClose: () -> Unit,
    onDismissPermanently: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Box(
            modifier = modifier
                .width(300.dp)
                .background(color = Color.White, shape = RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (!notice.imageUrl.isNullOrBlank()) {
                    var imageState by remember(notice.imageUrl) {
                        mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty)
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(color = LightGrayColor)
                    ) {
                        AsyncImage(
                            model = notice.imageUrl,
                            contentDescription = notice.title,
                            contentScale = ContentScale.Crop,
                            onState = { imageState = it },
                            modifier = Modifier.fillMaxSize()
                        )

                        when (imageState) {
                            is AsyncImagePainter.State.Empty, is AsyncImagePainter.State.Loading ->
                                CircularProgressIndicator(
                                    color = MainColorDeep,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(32.dp)
                                )

                            is AsyncImagePainter.State.Error -> Text(
                                text = "이미지를 불러오지 못했어요",
                                style = TextStyle(fontSize = 13.sp),
                                color = DarkGrayColor
                            )

                            is AsyncImagePainter.State.Success -> Unit
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = notice.title,
                    style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.SemiBold),
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = notice.body,
                    style = TextStyle(fontSize = 14.sp),
                    color = DarkGrayColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = onDismissPermanently,
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LightGrayColor,
                            contentColor = DarkGrayColor
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text(
                            text = "다시 보지 않기",
                            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        )
                    }

                    Spacer(modifier = Modifier.width(13.dp))

                    Button(
                        onClick = onClose,
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text(
                            text = "닫기",
                            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoticeDialogPreview() {
    Scrap2025Theme {
        NoticeDialog(
            notice = NoticeConfig(
                id = "2026-09-notice",
                enabled = true,
                title = "새로운 기능이 추가되었어요",
                body = "스크랩 정렬 기능이 개선되었습니다. 지금 바로 확인해보세요!",
                imageUrl = "https://picsum.photos/600/400"
            ),
            onClose = {},
            onDismissPermanently = {}
        )
    }
}

@Preview(showBackground = true, name = "No Image")
@Composable
fun NoticeDialogNoImagePreview() {
    Scrap2025Theme {
        NoticeDialog(
            notice = NoticeConfig(
                id = "2026-09-notice-text-only",
                enabled = true,
                title = "서버 점검 안내",
                body = "9월 21일 새벽 2시~4시 서버 점검이 진행됩니다."
            ),
            onClose = {},
            onDismissPermanently = {}
        )
    }
}
