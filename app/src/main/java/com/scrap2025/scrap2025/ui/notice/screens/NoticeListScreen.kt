package com.scrap2025.scrap2025.ui.notice.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.scrap2025.scrap2025.model.NoticeSummary
import com.scrap2025.scrap2025.ui.common.components.ErrorScreen
import com.scrap2025.scrap2025.ui.common.topbars.TopBarWithBack
import com.scrap2025.scrap2025.ui.notice.components.toNoticeDateText
import com.scrap2025.scrap2025.ui.theme.GrayColor
import com.scrap2025.scrap2025.ui.theme.LightGrayColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import com.scrap2025.scrap2025.viewmodel.NoticeListViewModel
import java.time.LocalDateTime

/** NoticeListScreen - 마이페이지 > 공지사항. 전체 공지사항 제목을 최신순으로 보여준다. */
@Composable
fun NoticeListScreen(
    onBack: () -> Unit,
    navigateToNoticeDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NoticeListViewModel = hiltViewModel()
) {
    val notices = viewModel.notices.collectAsLazyPagingItems()

    Column(modifier = modifier.fillMaxSize().background(Color.White)) {
        TopBarWithBack(title = "공지사항", onBack = onBack)

        when {
            notices.loadState.refresh is LoadState.Loading && notices.itemCount == 0 -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MainColorDeep)
                }
            }

            notices.loadState.refresh is LoadState.Error && notices.itemCount == 0 -> {
                ErrorScreen(
                    errorText = "공지사항을 불러오지 못했어요.",
                    onRetry = { notices.retry() }
                )
            }

            notices.itemCount == 0 -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "등록된 공지사항이 없어요.",
                        style = TextStyle(fontSize = 15.sp),
                        color = GrayColor
                    )
                }
            }

            else -> NoticeList(notices = notices, onNoticeClick = navigateToNoticeDetail)
        }
    }
}

@Composable
private fun NoticeList(notices: LazyPagingItems<NoticeSummary>, onNoticeClick: (Long) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(count = notices.itemCount, key = notices.itemKey { it.id }) { index ->
            notices[index]?.let { notice ->
                NoticeListItem(notice = notice, onClick = { onNoticeClick(notice.id) })
                HorizontalDivider(color = LightGrayColor, thickness = 1.dp)
            }
        }

        if (notices.loadState.append is LoadState.Loading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MainColorDeep,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NoticeListItem(notice: NoticeSummary, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = notice.title,
            style = TextStyle(fontSize = 16.sp),
            color = Color.Black,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        notice.createdAt?.let { createdAt ->
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = createdAt.toNoticeDateText(),
                style = TextStyle(fontSize = 12.sp),
                color = GrayColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NoticeListItemPreview() {
    Scrap2025Theme {
        NoticeListItem(
            notice = NoticeSummary(
                id = 1,
                title = "스크랩 서비스 업데이트 안내",
                createdAt = LocalDateTime.of(2026, 10, 2, 10, 0)
            ),
            onClick = {}
        )
    }
}
