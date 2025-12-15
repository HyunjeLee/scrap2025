package com.scrap2025.scrap2025.ui.login.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scrap2025.scrap2025.ui.theme.LightGrayColor
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import com.scrap2025.scrap2025.viewmodel.LoginViewModel

private val MainTextStyle = TextStyle(
    fontSize = 22.sp,
    fontWeight = FontWeight.SemiBold,
    lineHeight = 28.sp
)

private val ButtonTextStyle = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.SemiBold
)

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            onLoginClick()
        }
    }

    Column(
        modifier = modifier
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
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "언제 어디서든", style = MainTextStyle)
            Text(text = "간편하게", style = MainTextStyle)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "스크랩하기_", style = MainTextStyle)
                Icon(
                    imageVector = Icons.Outlined.AttachFile,
                    contentDescription = "첨부 아이콘",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // 하단 버튼 영역
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .border(1.5.dp, LightGrayColor, RoundedCornerShape(30.dp))
                    .background(color = MainColor, shape = RoundedCornerShape(30.dp))
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
                /* 카카오 로그인 로직 */
            }
            Spacer(modifier = Modifier.height(12.dp))
            NaverLoginButton {
                viewModel.loginWithNaver(context)
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
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFFE500)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = "카카오 로그인", style = ButtonTextStyle, color = Color.Black)
    }
}

@Composable
private fun NaverLoginButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF00C73C),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = "네이버 로그인", style = ButtonTextStyle)
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    Scrap2025Theme {
        LoginScreen()
    }
}
