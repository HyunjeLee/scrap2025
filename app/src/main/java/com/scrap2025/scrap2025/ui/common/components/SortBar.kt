package com.scrap2025.scrap2025.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowCircleDown
import androidx.compose.material.icons.outlined.ArrowCircleUp
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scrap2025.scrap2025.model.SortDirection
import com.scrap2025.scrap2025.model.SortType
import com.scrap2025.scrap2025.model.ViewMode
import com.scrap2025.scrap2025.ui.theme.GrayColor
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme

@Composable
fun SortBar(
    modifier: Modifier = Modifier,
    sortType: SortType = SortType.SCRAP_DATE,
    sortDirection: SortDirection = SortDirection.ASC,
    viewMode: ViewMode = ViewMode.LIST,
    onSortTypeToggle: () -> Unit = {},
    onSortDirectionToggle: () -> Unit = {},
    onViewModeToggle: () -> Unit = {}
) {
    val sortTypeText =
        when (sortType) {
            SortType.SCRAP_DATE -> "스크랩한 날짜 순"
            SortType.TITLE -> "제목 순"
        }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(46.dp)
                .background(MainColor)
                .padding(horizontal = 24.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // 정렬 아이콘 (클릭 시 오름차순/내림차순 토글)
            IconButton(onClick = onSortDirectionToggle, modifier = Modifier.size(20.dp)) {
                Icon(
                    imageVector =
                        when (sortDirection) {
                            SortDirection.ASC -> Icons.Outlined.ArrowCircleUp
                            SortDirection.DESC -> Icons.Outlined.ArrowCircleDown
                        },
                    contentDescription = "정렬 방향",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 정렬 텍스트 (클릭 시 정렬 기준 토글)
            Text(
                text = sortTypeText,
                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
                color = GrayColor,
                modifier = Modifier.clickable(enabled = true, onClick = onSortTypeToggle)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 구분선
            Text(
                text = "|",
                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
                color = Color(0xFF8C8C8C)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 뷰모드 전환
            IconButton(onClick = onViewModeToggle, modifier = Modifier.size(20.dp)) {
                Icon(
                    imageVector =
                        when (viewMode) {
                            ViewMode.LIST -> Icons.Outlined.ViewAgenda
                            ViewMode.GRID -> Icons.Outlined.GridView
                        },
                    contentDescription = "뷰모드 전환",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SortBarPreview() {
    Scrap2025Theme { SortBar() }
}
