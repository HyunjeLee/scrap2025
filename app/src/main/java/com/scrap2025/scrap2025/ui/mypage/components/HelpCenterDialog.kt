package com.scrap2025.scrap2025.ui.mypage.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices.PIXEL_3A
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.scrap2025.scrap2025.ui.theme.DarkGrayColor
import com.scrap2025.scrap2025.ui.theme.LightGrayColor
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme

@Composable
fun HelpCenterDialog(
    onDismiss: () -> Unit,
    onContactViaEmail: () -> Unit,
    onContactViaInstagram: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .wrapContentHeight()
                .background(color = MainColor, shape = RoundedCornerShape(20.dp))
                .padding(25.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = """
                        안녕하세요, 팀 스크랩입니다!
                        
                        스크랩 앱에 대한 문의가 있으실 경우
                        (ex. 기능 오류)
                        아래 대표 창구로 연락 부탁드립니다.
                    """.trimIndent(),
                    style = TextStyle(
                        fontSize = 15.sp,
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(33.dp))

                HelpCenterButton(
                    onClick = onContactViaEmail,
                    text = "contact via mail\n(cs@teamscrap.co.kr)"
                )

                Spacer(modifier = Modifier.height(13.dp))

                HelpCenterButton(
                    onClick = onContactViaInstagram,
                    text = "contact via instagram\n(@teamscrap2026)"
                )

                Spacer(modifier = Modifier.height(13.dp))

                HelpCenterButton(
                    onClick = onDismiss,
                    text = "취소",
                    containerColor = LightGrayColor,
                    contentColor = DarkGrayColor
                )
            }
        }
    }
}

@Composable
private fun HelpCenterButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MainColorDeep,
    contentColor: Color = MainColor,
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(15.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(10.dp)
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            ),
        )
    }
}

@Preview(showBackground = true, device = PIXEL_3A)
@Composable
private fun HelpCenterDialogPreview() {
    Scrap2025Theme {
        HelpCenterDialog(
            onDismiss = {},
            onContactViaEmail = {},
            onContactViaInstagram = {}
        )
    }
}