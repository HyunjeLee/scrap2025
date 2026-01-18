package com.scrap2025.scrap2025.ui.login.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scrap2025.scrap2025.BuildConfig
import com.scrap2025.scrap2025.R
import com.scrap2025.scrap2025.model.enums.SnsType
import com.scrap2025.scrap2025.ui.theme.GrayColor
import com.scrap2025.scrap2025.ui.theme.LightGrayColor
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme

private val MainTextStyle =
    TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold, lineHeight = 28.sp)
private val ButtonTextStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

@Composable
fun LoginScreenContent(
    onLoginClick: (SnsType) -> Unit,
    onTestLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 20.dp)
                .navigationBarsPadding(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 상단 헤더
        Text(
            text = "로그인",
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Normal),
            modifier = Modifier
                .padding(top = 37.dp)
                .align(Alignment.CenterHorizontally)
        )

        // 메인 컨텐츠
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
            Text(text = "언제 어디서든", style = MainTextStyle)
            Text(text = "간편하게", style = MainTextStyle)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "스크랩하기_", style = MainTextStyle)
                Icon(
                    painter = painterResource(R.drawable.ic_scrap),
                    contentDescription = "스크랩 아이콘",
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = BuildConfig.VERSION_NAME,
                style = TextStyle(fontSize = 8.sp, color = GrayColor),
            )
        }

        // 하단 버튼 영역
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier =
                    Modifier
                        .border(1.5.dp, LightGrayColor, RoundedCornerShape(30.dp))
                        .background(
                            color = MainColor,
                            shape = RoundedCornerShape(30.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "3초만에 시작하기 ✨",
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            KakaoLoginButton {
                onLoginClick(SnsType.KAKAO)
            }
            Spacer(modifier = Modifier.height(12.dp))
            NaverLoginButton {
                onLoginClick(SnsType.NAVER)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun KakaoLoginButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE500)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_kakao),
                tint = Color.Unspecified,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(text = "카카오 로그인", style = ButtonTextStyle, color = Color(0xFF000000).copy(alpha = 0.85f))
        }
    }
}

@Composable
private fun NaverLoginButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = Color(0xFF03A94D),
                contentColor = Color.White
            ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_naver),
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(text = "네이버 로그인", style = ButtonTextStyle)
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    Scrap2025Theme { LoginScreenContent(onLoginClick = {}, onTestLogin = {}) }
}
