package com.scrap2025.scrap2025.ui.mypage.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scrap2025.scrap2025.R
import com.scrap2025.scrap2025.model.enums.SnsType
import com.scrap2025.scrap2025.ui.common.components.LoadingScreen
import com.scrap2025.scrap2025.ui.common.dialogs.CommonDeleteDialog
import com.scrap2025.scrap2025.ui.mypage.components.HelpCenterDialog
import com.scrap2025.scrap2025.ui.theme.DarkGrayColor
import com.scrap2025.scrap2025.ui.theme.LightGrayColor
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import com.scrap2025.scrap2025.utils.INSTAGRAM_USERNAME
import com.scrap2025.scrap2025.utils.MAIL_ADDRESS
import com.scrap2025.scrap2025.utils.sendEmail
import com.scrap2025.scrap2025.utils.sendInstagramDM
import com.scrap2025.scrap2025.viewmodel.MyPageViewModel
import com.scrap2025.scrap2025.viewmodel.MyPageViewModel.MyPageUiState

/** MyPageScreen - Container Composable */
@Composable
fun MyPageScreen(
    modifier: Modifier = Modifier,
    viewModel: MyPageViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val showWithdrawDialog by viewModel.showWithdrawDialog.collectAsState()
    val showHelpCenterDialog by viewModel.showHelpCenterDialog.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        MyPageUiState.Loading -> {
            LoadingScreen()
        }

        is MyPageUiState.Success -> {
            val greetingText = remember(state.myPageInfo.memberInfo.name) {
                getGreetingText(state.myPageInfo.memberInfo.name)
            }

            MyPageScreenContent(
                greetingText = greetingText,
                snsType = state.snsType,
                scrapCount = state.scrapCount,
                categoryCount = state.categoryCount,
                onContactViaEmail = { context.sendEmail(MAIL_ADDRESS) },
                onContactViaInstagram = { context.sendInstagramDM(INSTAGRAM_USERNAME) },
                showHelpCenterDialog = showHelpCenterDialog,
                showWithdrawDialog = showWithdrawDialog,
                onHelpCenterClick = { viewModel.showHelpCenterDialog() },
                onHelpCenterDismiss = { viewModel.dismissHelpCenterDialog() },
                onLogout = { snsType ->
                    viewModel.logout(
                        snsType = snsType,
                        socialLogoutCallback = { provider -> provider.logout() }
                    )
                },
                onWithdrawClick = { viewModel.showWithdrawalDialog() },
                onWithdrawConfirm = { snsType ->
                    viewModel.withdraw(
                        snsType = snsType,
                        socialWithdrawCallback = { provider -> provider.disconnect() }
                    )
                },
                onWithdrawDismiss = { viewModel.dismissWithdrawalDialog() },
                modifier = modifier,
            )
        }
    }
}

/** MyPageScreenContent - Presentational Composable */
@Composable
fun MyPageScreenContent(
    greetingText: AnnotatedString,
    snsType: SnsType,
    scrapCount: Int,
    categoryCount: Int,
    onContactViaEmail: () -> Unit,
    onContactViaInstagram: () -> Unit,
    showHelpCenterDialog: Boolean,
    showWithdrawDialog: Boolean,
    onHelpCenterClick: () -> Unit,
    onHelpCenterDismiss: () -> Unit,
    onLogout: (SnsType) -> Unit,
    onWithdrawClick: () -> Unit,
    onWithdrawConfirm: (SnsType) -> Unit,
    onWithdrawDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Title
        Text(
            text = "마이페이지",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Row(
            Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = greetingText,
                fontSize = 20.sp,
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                painter = painterResource(snsType.getIconRes()),
                contentDescription = "SNS Icon",
                tint = Color.Unspecified,
                modifier = Modifier
                    .padding(top = 1.dp)
                    .size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Stats Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            // Scrap Stat
            StatItem(
                icon = painterResource(R.drawable.ic_clip),
                count = "${scrapCount}개",
                label = "스크랩"
            )

            Spacer(modifier = Modifier.width(80.dp))

            // Category Stat
            StatItem(
                icon = painterResource(R.drawable.ic_folder),
                count = "${categoryCount - 1}개", // default category 제외
                label = "카테고리"
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Menu Items
        HorizontalDivider(color = LightGrayColor, thickness = 1.dp)
        MenuItem(text = "고객센터") { onHelpCenterClick() }
        HorizontalDivider(color = LightGrayColor, thickness = 1.dp)
        MenuItem(text = "로그아웃") { onLogout(snsType) }
        HorizontalDivider(color = LightGrayColor, thickness = 1.dp)
        MenuItem(text = "회원탈퇴") { onWithdrawClick() }
        HorizontalDivider(color = LightGrayColor, thickness = 1.dp)
    }

    // 3. 다이얼로그 표시 로직
    if (showHelpCenterDialog) {
        HelpCenterDialog(
            onDismiss = onHelpCenterDismiss,
            onContactViaEmail = onContactViaEmail,
            onContactViaInstagram = onContactViaInstagram
        )
    }

    if (showWithdrawDialog) {
        CommonDeleteDialog(
            title = "정말 회원탈퇴 하시겠습니까?",
            confirmText = "회원탈퇴",
            onConfirm = { onWithdrawConfirm(snsType) },
            onDismiss = onWithdrawDismiss,
        )
    }
}

@Composable
fun StatItem(icon: Painter, count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = icon,
            contentDescription = label,
            modifier = Modifier.size(32.dp),
            tint = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = count, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
        Text(text = label, color = DarkGrayColor, fontSize = 12.sp)
    }
}

@Composable
fun MenuItem(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }) {
        Text(
            text = text,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )
    }
}

private fun getGreetingText(userName: String): AnnotatedString {
    return buildAnnotatedString {
        append("안녕하세요 ")
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(userName)
        }
        append(" 님!")
    }
}

private fun SnsType.getIconRes(): Int = when (this) {
    SnsType.NAVER -> R.drawable.ic_naver_logo
    SnsType.KAKAO -> R.drawable.ic_kakao_logo
    SnsType.GOOGLE -> 0 // 미구현 // R.drawable.ic_google_logo
}

@Preview(showBackground = true)
@Composable
private fun MyPageScreenPreview() {
    Scrap2025Theme {
        MyPageScreenContent(
            greetingText = getGreetingText("사용자"),
            snsType = SnsType.KAKAO,
            scrapCount = 243,
            categoryCount = 11,
            onContactViaEmail = {},
            onContactViaInstagram = {},
            showHelpCenterDialog = false,
            showWithdrawDialog = false,
            onHelpCenterClick = {},
            onHelpCenterDismiss = {},
            onLogout = {},
            onWithdrawClick = {},
            onWithdrawConfirm = {},
            onWithdrawDismiss = {},
        )
    }
}
