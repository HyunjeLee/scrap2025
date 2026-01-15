package com.scrap2025.scrap2025.ui.scrap.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.scrap2025.scrap2025.R
import com.scrap2025.scrap2025.model.ScrapItem
import com.scrap2025.scrap2025.ui.theme.FavoriteColor
import com.scrap2025.scrap2025.ui.theme.GrayColor
import com.scrap2025.scrap2025.ui.theme.LightGrayColor
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScrapItemCardGrid(
    scrapItem: ScrapItem,
    showCategory: Boolean,
    modifier: Modifier = Modifier,
    isSelectionMode: Boolean = false,
    isSelected: Boolean = false,
    onLongClick: () -> Unit = {},
    onSelectionToggle: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    Card(
        modifier =
            modifier
                .size(width = 164.dp, height = 190.dp)
                .combinedClickable(
                    onClick = {
                        if (isSelectionMode) { // 선택 모드인 경우
                            onSelectionToggle()
                        } else {
                            onClick()
                        }
                    },
                    onLongClick = {
                        if (!isSelectionMode) {
                            onLongClick()
                        }
                    }
                ),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // 이미지 영역
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(97.dp)
                            .background(
                                color = LightGrayColor,
                                shape =
                                    RoundedCornerShape(
                                        topStart = 15.dp,
                                        topEnd = 15.dp
                                    )
                            ),
                    contentAlignment = Alignment.Center
                ) {
                    if (scrapItem.imageUrl != null) {
                        AsyncImage(
                            model = scrapItem.imageUrl,
                            contentDescription = scrapItem.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // 텍스트 영역
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // 즐겨찾기 + 제목
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        if (scrapItem.isFavorite) {
                            Icon(
                                painter = painterResource(R.drawable.ic_fav_true),
                                contentDescription = "즐겨찾기",
                                tint = FavoriteColor,
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        Text(
                            text = scrapItem.title,
                            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
                            color = Color.Black,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    // URL
                    Text(
                        text = scrapItem.url,
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
                        color = GrayColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // 날짜
                        Text(
                            text =
                                scrapItem.createdDate.format(
                                    DateTimeFormatter.ofPattern("yyyy.MM.dd")
                                ),
                            style =
                                TextStyle(
                                    fontSize = 12.sp,
                                ),
                        )
                        // 카테고리 출력
                        if (showCategory) {
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = scrapItem.categoryTitle?.let { "[$it]" }.orEmpty(),
                                textAlign = TextAlign.End,
                                style =
                                    TextStyle(
                                        fontSize = 12.sp,
                                    ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // 선택 모드일 때 체크마크 표시 (왼쪽 상단)
            if (isSelectionMode) {
                Box(
                    modifier =
                        Modifier
                            .padding(8.dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color.Transparent)
                            .align(Alignment.TopStart)
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .align(Alignment.Center)
                    )
                    Icon(
                        painter =
                            if (isSelected) painterResource(R.drawable.ic_check_filled)
                            else painterResource(R.drawable.ic_check_unfilled),
                        contentDescription = if (isSelected) "선택됨" else "선택 안됨",
                        tint = Color.Unspecified,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScrapItemCardGridPreview() {
    Scrap2025Theme {
        Row(
            modifier = Modifier
                .background(Color(0xFFFCFCFC))
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ScrapItemCardGrid(
                scrapItem =
                    ScrapItem(
                        id = 1L,
                        title = "제목제목",
                        description = "description",
                        url = "주소주소주소주소",
                        imageUrl = null,
                        categoryId = 0L,
                        categoryTitle = "분류되지 않음",
                        createdDate = LocalDateTime.of(2024, 2, 26, 10, 0),
                        isFavorite = true
                    ),
                showCategory = true,
            )
            ScrapItemCardGrid(
                scrapItem =
                    ScrapItem(
                        id = 2L,
                        title = "제목제목제목제목제목제목제목제목제...",
                        description = "description",
                        url = "주소주소주소주소",
                        imageUrl =
                            "https://images.unsplash.com/photo-1506905925346-21bda4d32df4",
                        categoryId = 0L,
                        createdDate = LocalDateTime.of(2024, 2, 26, 14, 30),
                        isFavorite = false
                    ),
                showCategory = true,
            )
        }
    }
}
