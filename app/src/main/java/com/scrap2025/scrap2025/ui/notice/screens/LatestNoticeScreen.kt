package com.scrap2025.scrap2025.ui.notice.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.scrap2025.scrap2025.model.Notice
import com.scrap2025.scrap2025.ui.notice.components.NoticeContent

/**
 * LatestNoticeScreen - 앱 실행 시 최신 서버 공지사항을 기존 화면 위에 전체 화면으로 띄운다.
 *
 * @param onClose "닫기" 또는 뒤로가기 시 호출 (다음 실행 시 다시 노출됨)
 * @param onDismissPermanently "다시 보지 않기" 클릭 시 호출 (같은 공지는 다시 노출되지 않음)
 */
@Composable
fun LatestNoticeScreen(
    notice: Notice,
    onClose: () -> Unit,
    onDismissPermanently: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onClose)

    // Surface가 터치를 소비하므로 아래에 깔린 화면은 조작되지 않는다
    Surface(color = Color.White, modifier = modifier.fillMaxSize()) {
        NoticeContent(
            notice = notice,
            onClose = onClose,
            onDismissPermanently = onDismissPermanently,
            modifier = Modifier.systemBarsPadding()
        )
    }
}
