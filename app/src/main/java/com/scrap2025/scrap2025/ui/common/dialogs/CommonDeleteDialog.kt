package com.scrap2025.scrap2025.ui.common.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.scrap2025.scrap2025.ui.theme.CautionColor
import com.scrap2025.scrap2025.ui.theme.DarkGrayColor
import com.scrap2025.scrap2025.ui.theme.LightGrayColor
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import com.scrap2025.scrap2025.ui.theme.WarningColor

/**
 * CommonDeleteDialog - 공통 삭제/탈퇴 확인 다이얼로그
 *
 * @param title 메인 메시지 (필수)
 * @param description 부가 설명 (선택, 주황색 텍스트)
 * @param confirmText 삭제/확인 버튼 텍스트 (필수)
 * @param onDismiss 취소 버튼 클릭 시 호출
 * @param onConfirm 삭제/확인 버튼 클릭 시 호출
 * @param isLoading true일 때 확인 버튼을 로딩 인디케이터로 교체하고 모든 버튼을 비활성화
 */
@Composable
fun CommonDeleteDialog(
    title: String,
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    isLoading: Boolean = false
) {
    Dialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = !isLoading,
            dismissOnClickOutside = !isLoading
        )
    ) {
        Box(
            modifier =
            modifier
                .width(318.dp)
                .height(245.dp)
                .background(color = Color.White, shape = RoundedCornerShape(20.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                // 상단 여백
                Spacer(modifier = Modifier.height(10.dp))

                // 경고 아이콘 (빨간색 원 + X)
                Box(
                    modifier =
                    Modifier
                        .size(40.dp)
                        .background(color = WarningColor, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "경고",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // 메인 메시지
                Text(
                    text = title,
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium),
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                // 부가 설명 (주황색, 선택적)
                if (description != null) {
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = description,
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                        color = CautionColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // 버튼 영역
                Row(modifier = Modifier.fillMaxWidth()) {
                    // 취소 버튼
                    Button(
                        onClick = onDismiss,
                        enabled = !isLoading,
                        modifier =
                        Modifier
                            .weight(1f)
                            .height(55.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors =
                        ButtonDefaults.buttonColors(
                            containerColor = LightGrayColor,
                            contentColor = DarkGrayColor
                        )
                    ) {
                        Text(
                            text = "취소",
                            style =
                            TextStyle(
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(13.dp))

                    // 삭제/확인 버튼
                    Button(
                        onClick = onConfirm,
                        enabled = !isLoading,
                        modifier =
                        Modifier
                            .weight(1f)
                            .height(55.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors =
                        ButtonDefaults.buttonColors(
                            containerColor = WarningColor,
                            contentColor = Color.White
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = confirmText,
                                style =
                                TextStyle(
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommonDeleteDialogPreview() {
    Scrap2025Theme {
        CommonDeleteDialog(
            title = "정말 카테고리를 삭제하시겠습니까?",
            description = "(해당 카테고리에 속한 모든 스크랩 또한 삭제됩니다)",
            confirmText = "삭제하기",
            onDismiss = {},
            onConfirm = {}
        )
    }
}

@Preview(showBackground = true, name = "No Description")
@Composable
fun CommonDeleteDialogNoDescriptionPreview() {
    Scrap2025Theme {
        CommonDeleteDialog(
            title = "정말 회원탈퇴 하시겠습니까?",
            confirmText = "회원탈퇴",
            onDismiss = {},
            onConfirm = {}
        )
    }
}
