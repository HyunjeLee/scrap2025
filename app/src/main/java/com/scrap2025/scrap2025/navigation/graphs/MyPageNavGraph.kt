package com.scrap2025.scrap2025.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.scrap2025.scrap2025.navigation.destinations.MyPage
import com.scrap2025.scrap2025.navigation.destinations.NoticeDetail
import com.scrap2025.scrap2025.navigation.destinations.NoticeList
import com.scrap2025.scrap2025.ui.mypage.screens.MyPageScreen
import com.scrap2025.scrap2025.ui.notice.screens.NoticeDetailScreen
import com.scrap2025.scrap2025.ui.notice.screens.NoticeListScreen

fun NavGraphBuilder.myPageNavGraph(navController: NavHostController) {
    composable<MyPage> {
        MyPageScreen(navigateToNoticeList = { navController.navigate(NoticeList) })
    }

    composable<NoticeList> {
        NoticeListScreen(
            onBack = { navController.popBackStack() },
            navigateToNoticeDetail = { noticeId -> navController.navigate(NoticeDetail(noticeId)) }
        )
    }

    composable<NoticeDetail> { NoticeDetailScreen(onBack = { navController.popBackStack() }) }
}
