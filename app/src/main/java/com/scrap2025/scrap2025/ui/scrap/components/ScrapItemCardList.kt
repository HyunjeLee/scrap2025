package com.scrap2025.scrap2025.ui.scrap.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
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
import com.scrap2025.scrap2025.ui.theme.MainColorDeep
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScrapItemCardList(
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
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.5.dp)
            .combinedClickable(
                onClick = {
                    if (isSelectionMode) {
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
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 이미지 영역
                Box(
                    modifier = Modifier
                        .size(width = 100.dp, height = 75.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(LightGrayColor)
                        .border(
                            width = 1.dp,
                            color = LightGrayColor,
                            shape = RoundedCornerShape(4.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (scrapItem.imageUrl != null) {
                        AsyncImage(
                            model = scrapItem.imageUrl,
                            contentDescription = scrapItem.title,
                            modifier = Modifier.size(width = 100.dp, height = 75.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // 텍스트 영역
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(75.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // 제목과 즐겨찾기
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        // 즐겨찾기 아이콘
                        if (scrapItem.isFavorite) {
                            Icon(
                                painter = painterResource(R.drawable.ic_fav_true),
                                contentDescription ="즐겨찾기",
                                tint = FavoriteColor,
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .size(15.dp)
                            )

                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        // 제목
                        Text(
                            text = scrapItem.title,
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            color = Color.Black,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // URL
                    Text(
                        text = scrapItem.url,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        ),
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
                            text = scrapItem.createdDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")),
                            style = TextStyle(fontSize = 12.sp,),
                        )
                        // 카테고리 출력
                        if (showCategory) {
                            Spacer(Modifier.weight(1f))
                            Text(
                                text = scrapItem.categoryTitle?.let { "[$it]" }.orEmpty(),
                                style = TextStyle(fontSize = 12.sp,),
                            )
                        }
                    }
                }
            }

            // 선택 모드일 때 체크마크 표시 (왼쪽 상단)
            if (isSelectionMode) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                        .align(Alignment.TopStart)
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .align(Alignment.Center)
                    )
                    Icon(
                        imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                        contentDescription = if (isSelected) "선택됨" else "선택 안됨",
                        tint = if (isSelected) MainColorDeep else GrayColor,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ScrapItemCardListPreview() {
    Scrap2025Theme {
        Column(
            modifier = Modifier.background(Color(0xFFFCFCFC))
        ) {
            ScrapItemCardList(
                scrapItem = ScrapItem(
                    id = "1",
                    title = "제목제목",
                    description = "description",
                    url = "주소주소주소주소",
                    imageUrl = null,
                    categoryId = "",
                    categoryTitle = "분류되지 않음",
                    createdDate = LocalDateTime.of(2024, 2, 22, 10, 0),
                    isFavorite = true
                ),
                showCategory = true,
            )
            ScrapItemCardList(
                scrapItem = ScrapItem(
                    id = "2",
                    title = "제목제목제목제목제목제목제목제목제목제목제목제끝",
                    description = "description",
                    url = "주소주소주소주소",
                    imageUrl = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4",
                    categoryId = "",
                    createdDate = LocalDateTime.of(2024, 2, 22, 14, 30),
                    isFavorite = true
                ),
                showCategory = true,
            )
            ScrapItemCardList(
                scrapItem = ScrapItem(
                    id = "3",
                    title = "제목제목",
                    description = "description",
                    url = "주소주소주소주소",
                    imageUrl = null,
                    categoryId = "",
                    createdDate = LocalDateTime.of(2024, 2, 22, 16, 45),
                    isFavorite = false
                ),
                showCategory = true,
            )
        }
    }
}
