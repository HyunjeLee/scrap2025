package com.scrap2025.scrap2025.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowCircleUp
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scrap2025.scrap2025.data.local.ScrapDummyData
import com.scrap2025.scrap2025.model.ScrapItem
import com.scrap2025.scrap2025.ui.components.ScrapItemCard
import com.scrap2025.scrap2025.ui.theme.BackgroundColor
import com.scrap2025.scrap2025.ui.theme.GrayColor
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep
import com.scrap2025.scrap2025.ui.theme.MainColorLight
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import com.scrap2025.scrap2025.ui.theme.WarningColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

val _scrapItems = MutableStateFlow<List<ScrapItem>>(ScrapDummyData.dummyScrapItems)
val scrapItemsStateFlow: StateFlow<List<ScrapItem>> = _scrapItems

@Composable
fun ScrapScreen(
    categoryName: String = "분류되지 않음",
    modifier: Modifier = Modifier
) {
    val scrapItems by scrapItemsStateFlow.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 톱바 - 제목
            TopBarWithTitle(categoryName = categoryName)

            // 톱바 - 검색
            SearchBar()

            // 정렬 바
            SortBar()

            // 스크랩 리스트
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                items(scrapItems) { scrapItem ->
                    ScrapItemCard(
                        scrapItem = scrapItem,
                    )
                }
            }
        }

        // 스크랩 추가 버튼
        FloatingActionButton(
            onClick = {
            },
            shape = CircleShape,
            containerColor = MainColor,
            contentColor = MainColorDeep,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 21.dp, bottom = 21.dp)
                .size(60.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "스크랩 추가",
                modifier = Modifier.size(50.dp)
            )
        }
    }
}

@Composable
fun TopBarWithTitle(
    categoryName: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(53.dp)
            .background(MainColor),
        contentAlignment = Alignment.CenterStart
    ) {
        // 카테고리명
        Text(
            text = categoryName,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(start = 21.dp)
                .padding(end = 85.dp)
        )

        // 오른쪽 아이콘들
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 22.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 수정 아이콘
            IconButton(
                onClick = { /* TODO: 편집 모드 */ },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "편집",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }

            // 삭제 아이콘
            IconButton(
                onClick = { /* TODO: 삭제 */ },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "삭제",
                    tint = WarningColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun SearchBar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(MainColor)
            .padding(horizontal = 21.dp, vertical = 5.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MainColorLight,
                    shape = RoundedCornerShape(7.dp)
                )
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "검색",
                    tint = Color.Black,
                    modifier = Modifier.size(27.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "제목, 본문내용, 메모로 검색하기",
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = GrayColor
                )
            }
        }
    }
}

@Composable
fun SortBar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
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
            // 정렬 아이콘
            Icon(
                imageVector = Icons.Outlined.ArrowCircleUp,
                contentDescription = "정렬",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 정렬 텍스트
            Text(
                text = "스크랩한 날짜 순",
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = GrayColor,
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 구분선
            Text(
                text = "|",
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = Color(0xFF8C8C8C)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 메뉴 아이콘
            Icon(
                imageVector = Icons.Outlined.ViewAgenda,
                contentDescription = "뷰모드 전환",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScrapScreenPreview() {
    Scrap2025Theme {
        ScrapScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun TopBarWithTitlePreview() {
    Scrap2025Theme {
        TopBarWithTitle(
            categoryName = "분류되지 않음",
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    Scrap2025Theme {
        SearchBar()
    }
}

@Preview(showBackground = true)
@Composable
fun SortBarPreview() {
    Scrap2025Theme {
        SortBar()
    }
}
