package com.scrap2025.scrap2025.ui.notice.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.scrap2025.scrap2025.ui.common.components.ErrorScreen
import com.scrap2025.scrap2025.ui.common.components.LoadingScreen
import com.scrap2025.scrap2025.ui.common.topbars.TopBarWithBack
import com.scrap2025.scrap2025.ui.notice.components.NoticeContent
import com.scrap2025.scrap2025.viewmodel.NoticeDetailUiState
import com.scrap2025.scrap2025.viewmodel.NoticeDetailViewModel

/** NoticeDetailScreen - 마이페이지 공지사항 목록에서 선택한 공지사항 전체 내용 (닫기 버튼만 제공) */
@Composable
fun NoticeDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NoticeDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        NoticeDetailUiState.Loading -> LoadingScreen()

        is NoticeDetailUiState.Success -> NoticeContent(
            notice = state.notice,
            onClose = onBack,
            modifier = modifier
        )

        is NoticeDetailUiState.Error -> Column(
            modifier = modifier.fillMaxSize().background(Color.White)
        ) {
            TopBarWithBack(title = "공지사항", onBack = onBack)
            ErrorScreen(
                errorText = "공지사항을 불러오지 못했어요.",
                onRetry = { viewModel.fetchNotice() }
            )
        }
    }
}
