package com.scrap2025.scrap2025.ui.login.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.scrap2025.scrap2025.BuildConfig
import com.scrap2025.scrap2025.R
import com.scrap2025.scrap2025.model.enums.SnsType
import com.scrap2025.scrap2025.model.enums.SnsType.GOOGLE
import com.scrap2025.scrap2025.model.enums.SnsType.KAKAO
import com.scrap2025.scrap2025.model.enums.SnsType.NAVER
import com.scrap2025.scrap2025.model.enums.SnsType.TEST
import com.scrap2025.scrap2025.ui.theme.GrayColor
import com.scrap2025.scrap2025.ui.theme.LastLoginBadgeColor
import com.scrap2025.scrap2025.ui.theme.LightGrayColor
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme

private const val TEST_PASSWORD = "scrap2025-test-login"

private val MainTextStyle =
    TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold, lineHeight = 28.sp)
private val ButtonTextStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

@Composable
fun LoginScreenContent(
    onLoginClick: (SnsType) -> Unit,
    onTestLogin: () -> Unit,
    lastLoginSnsType: SnsType? = null,
    modifier: Modifier = Modifier
) {
    val clickCountState = remember { mutableIntStateOf(0) }
    val lastClickTimeState = remember { mutableLongStateOf(0L) }
    var showSecretDialog by remember { mutableStateOf(false) }

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
            modifier =
            Modifier
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
                style = TextStyle(fontSize = 8.sp, color = GrayColor)
            )
        }

        // 하단 버튼 영역
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .border(1.5.dp, LightGrayColor, RoundedCornerShape(30.dp))
                    .background(
                        color = MainColor,
                        shape = RoundedCornerShape(30.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                handleSecretTap(
                                    clickCountState = clickCountState,
                                    lastClickTimeState = lastClickTimeState,
                                    goToSecretDialog = { showSecretDialog = true }
                                )
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "3초만에 시작하기 ✨",
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal)
                )
            }

            Column(
                Modifier.padding(vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (snsType in SnsType.entries) {
                    SocialLoginButton(
                        snsType = snsType,
                        lastLoginSnsType = lastLoginSnsType,
                        onLoginClick = onLoginClick
                    )
                }
            }
        }
    }

    if (showSecretDialog) {
        SecretLoginDialog(
            onDismiss = { showSecretDialog = false },
            onConfirm = {
                showSecretDialog = false
                onTestLogin() // 기존 onTestLogin 실행
            }
        )
    }
}

@Composable
private fun SocialLoginButton(
    snsType: SnsType,
    lastLoginSnsType: SnsType?,
    onLoginClick: (SnsType) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        when (snsType) {
            NAVER -> NaverLoginButton { onLoginClick(snsType) }
            KAKAO -> KakaoLoginButton { onLoginClick(snsType) }
            GOOGLE -> {} // 미구현
            TEST -> {} // 숨겨진 버튼으로 진입
        }
        if (snsType == lastLoginSnsType) LastLoginBadge()
    }
}

@Composable
private fun BoxScope.LastLoginBadge() {
    Box(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(end = 8.dp)
            .offset(y = (-16).dp)
            .background(LastLoginBadgeColor, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(
            text = "마지막으로 로그인한 계정",
            style = TextStyle(
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        )
        Icon(
            painterResource(R.drawable.ic_tooltip_arrow),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(y = 8.dp),
            tint = LastLoginBadgeColor
        )
    }
}

@Composable
private fun KakaoLoginButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier =
        Modifier
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
            Text(
                text = "카카오 로그인",
                style = ButtonTextStyle,
                color = Color(0xFF000000).copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun NaverLoginButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier =
        Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors =
        ButtonDefaults.buttonColors(
            containerColor = Color(0xFF03A94D),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
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

private fun handleSecretTap(
    clickCountState: MutableIntState,
    lastClickTimeState: MutableLongState,
    goToSecretDialog: () -> Unit
) {
    val currentTime = System.currentTimeMillis()
    val lastTime = lastClickTimeState.longValue
    val currentCount = clickCountState.intValue

    val newCount = if (currentTime - lastTime < 1000) {
        currentCount + 1
    } else {
        1
    }
    // 상태 업데이트
    clickCountState.intValue = newCount
    lastClickTimeState.longValue = currentTime

    if (newCount >= 5) {
        goToSecretDialog()
        clickCountState.intValue = 0
    }
}

@Composable
private fun SecretLoginDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // 전체 화면 사용
    ) {
        var password by remember { mutableStateOf("") }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val handleLoginCheck = {
                        if (password == TEST_PASSWORD) {
                            onConfirm()
                        } else {
                            Toast.makeText(context, "wrong password", Toast.LENGTH_SHORT).show()
                        }
                    }

                    Text(
                        text = "FOR_TEST_LOGIN",
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("enter password") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { handleLoginCheck() }),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = handleLoginCheck,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MainColorDeep),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("confirm", fontSize = 16.sp)
                    }
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("dismiss", color = GrayColor)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    Scrap2025Theme { LoginScreenContent(onLoginClick = {}, onTestLogin = {}) }
}

@Preview(showBackground = true)
@Composable
private fun LastLoginBadgePreview() {
    Scrap2025Theme {
        LoginScreenContent(
            onLoginClick = {},
            onTestLogin = {},
            lastLoginSnsType = KAKAO
        )
    }
}

@Preview
@Composable
private fun SecretDialogPreview() {
    SecretLoginDialog(onDismiss = {}, onConfirm = {})
}
