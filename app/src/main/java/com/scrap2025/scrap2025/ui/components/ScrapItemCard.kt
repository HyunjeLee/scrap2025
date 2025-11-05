package com.scrap2025.scrap2025.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.scrap2025.scrap2025.model.ScrapItem
import com.scrap2025.scrap2025.ui.theme.FavoriteColor
import com.scrap2025.scrap2025.ui.theme.GrayColor
import com.scrap2025.scrap2025.ui.theme.LightGrayColor
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme

@Composable
fun ScrapItemCard(
    scrapItem: ScrapItem,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.5.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
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
                            imageVector = Icons.Filled.Star,
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

                // 날짜
                Text(
                    text = scrapItem.createdDate,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = Color.Black
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScrapItemCardPreview() {
    Scrap2025Theme {
        Column(
            modifier = Modifier.background(Color(0xFFFCFCFC))
        ) {
            ScrapItemCard(
                scrapItem = ScrapItem(
                    id = "1",
                    title = "제목제목",
                    url = "주소주소주소주소",
                    imageUrl = null,
                    createdDate = "2024.02.22",
                    isFavorite = true
                )
            )
            ScrapItemCard(
                scrapItem = ScrapItem(
                    id = "2",
                    title = "제목제목제목제목제목제목제목제목제목제목제목제끝",
                    url = "주소주소주소주소",
                    imageUrl = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4",
                    createdDate = "2024.02.22",
                    isFavorite = true
                )
            )
            ScrapItemCard(
                scrapItem = ScrapItem(
                    id = "3",
                    title = "제목제목",
                    url = "주소주소주소주소",
                    imageUrl = null,
                    createdDate = "2024.02.22",
                    isFavorite = false
                )
            )
        }
    }
}
