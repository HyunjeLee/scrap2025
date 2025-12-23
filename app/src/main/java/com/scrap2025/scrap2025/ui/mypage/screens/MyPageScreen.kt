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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scrap2025.scrap2025.ui.common.dialogs.CommonDeleteDialog
import com.scrap2025.scrap2025.ui.theme.DarkGrayColor
import com.scrap2025.scrap2025.ui.theme.LightGrayColor
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import com.scrap2025.scrap2025.viewmodel.MyPageViewModel

/** MyPageScreen - Container Composable */
@Composable
fun MyPageScreen(
    modifier: Modifier = Modifier,
    viewModel: MyPageViewModel = hiltViewModel()
) {
    val showWithdrawDialog by viewModel.showWithdrawDialog.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    MyPageScreenContent(
        uiState = uiState,
        showWithdrawDialog = showWithdrawDialog,
        onLogout = { viewModel.logout() },
        onWithdrawClick = { viewModel.showWithdrawalDialog() },
        onWithdrawConfirm = { viewModel.withdraw() },
        onWithdrawDismiss = { viewModel.dismissWithdrawalDialog() },
        modifier = modifier
    )
}

/** MyPageScreenContent - Presentational Composable */
@Composable
fun MyPageScreenContent(
    uiState: MyPageViewModel.MyPageUiState,
    showWithdrawDialog: Boolean,
    onLogout: () -> Unit,
    onWithdrawClick: () -> Unit,
    onWithdrawConfirm: () -> Unit,
    onWithdrawDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is MyPageViewModel.MyPageUiState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        }
        is MyPageViewModel.MyPageUiState.Success -> {
            Column(modifier = modifier
                .fillMaxSize()
                .background(Color.White)) {
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

                // Greeting
                val greetingText = buildAnnotatedString {
                    append("안녕하세요 ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(uiState.myPageInfo.memberInfo.name)
                    }
                    append(" 님!")
                }
                Text(
                    text = greetingText,
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

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
                        icon = Icons.Outlined.AttachFile,
                        count = "${uiState.scrapCount}개",
                        label = "스크랩"
                    )

                    Spacer(modifier = Modifier.width(80.dp))

                    // Category Stat
                    StatItem(
                        icon = Icons.Outlined.Folder,
                        count = "${uiState.categoryCount}개",
                        label = "카테고리"
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Menu Items
                HorizontalDivider(color = LightGrayColor, thickness = 1.dp)
                MenuItem(text = "고객센터") {}
                HorizontalDivider(color = LightGrayColor, thickness = 1.dp)
                MenuItem(text = "로그아웃") { onLogout() }
                HorizontalDivider(color = LightGrayColor, thickness = 1.dp)
                MenuItem(text = "회원탈퇴") { onWithdrawClick() }
                HorizontalDivider(color = LightGrayColor, thickness = 1.dp)
            }
        }
    }

    if (showWithdrawDialog) {
        CommonDeleteDialog(
            title = "정말 회원탈퇴 하시겠습니까?",
            confirmText = "회원탈퇴",
            onConfirm = onWithdrawConfirm,
            onDismiss = onWithdrawDismiss,
        )
    }
}

@Composable
fun StatItem(icon: ImageVector, count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
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
    Box(modifier = Modifier
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

@Preview(showBackground = true)
@Composable
fun MyPageScreenPreview() {
    Scrap2025Theme {
        MyPageScreenContent(
            uiState = MyPageViewModel.MyPageUiState.Loading,
            showWithdrawDialog = false,
            onLogout = {},
            onWithdrawClick = {},
            onWithdrawConfirm = {},
            onWithdrawDismiss = {}
        )
    }
}
