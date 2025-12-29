package com.scrap2025.scrap2025.ui.category.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
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
import com.scrap2025.scrap2025.ui.theme.MainColorDeep
import com.scrap2025.scrap2025.ui.theme.MainColorLight
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme

@Composable
fun CategoryItemCard(
    categoryItem: CategoryItem,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isSelectable: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(if (isSelected) MainColorLight else Color.White)
            .clickable { onClick() }
            .padding(horizontal = 21.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 카테고리명 - ellipsis 처리
        Text(
            text = categoryItem.name,
            style = TextStyle(
                fontSize = 15.sp,
                fontWeight =
                    if (isSelected) FontWeight.SemiBold  else FontWeight.Normal
            ),
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
        )

        if (isSelectable) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = "선택",
                    tint = MainColorDeep
                )
            }
        } else {
            // 스크랩 개수 badge
            Box(
                modifier = Modifier
                    .background(
                        color = MainColorLight,
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
                    scrapCount = 322,
                    orderIndex = 0
                )
            )
            CategoryItemCard(
                categoryItem = CategoryItem(
                    id = "2",
                    name = "데이트",
                    scrapCount = 32,
                    orderIndex = 0
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryItemCardSelectionPreview() {
    Scrap2025Theme {
        Column {
            CategoryItemCard(
                isSelected = true,
                isSelectable = true,
                categoryItem = CategoryItem(
                    id = "1",
                    name = "isSelectable",
                    scrapCount = 322,
                    orderIndex = 0
                )
            )
            CategoryItemCard(
                isSelected = false,
                isSelectable = true,
                categoryItem = CategoryItem(
                    id = "2",
                    name = "데이트",
                    scrapCount = 32,
                    orderIndex = 0
                )
            )
        }
    }
}
