package com.scrap2025.scrap2025.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scrap2025.scrap2025.model.CategoryItem
import com.scrap2025.scrap2025.ui.theme.LightGrayColor
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme

@Composable
fun CategoryItemCard(
    categoryItem: CategoryItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color.White)
            .padding(horizontal = 21.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 카테고리명 - ellipsis 처리
        Text(
            text = categoryItem.name,
            style = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal
            ),
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
        )

        // 스크랩 개수 badge
        Box(
            modifier = Modifier
                .background(
                    color = Color(0xFFF5F9F3),
                    shape = RoundedCornerShape(15.dp)
                )
                .padding(horizontal = 10.dp, vertical = 5.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = categoryItem.scrapCount.toString(),
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryItemCardPreview() {
    Scrap2025Theme {
        Column {
            CategoryItemCard(
                categoryItem = CategoryItem(
                    id = "1",
                    name = "분류되지 않음",
                    scrapCount = 322
                )
            )
            CategoryItemCard(
                categoryItem = CategoryItem(
                    id = "2",
                    name = "데이트",
                    scrapCount = 32
                )
            )
        }
    }
}
