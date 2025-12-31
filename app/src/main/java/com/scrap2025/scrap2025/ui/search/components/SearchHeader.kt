package com.scrap2025.scrap2025.ui.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scrap2025.scrap2025.ui.theme.Duotone
import com.scrap2025.scrap2025.ui.theme.GrayColor
import com.scrap2025.scrap2025.ui.theme.LineGrayColor
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep
import com.scrap2025.scrap2025.ui.theme.MainColorLight
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme

@Composable
fun SearchHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    searchRange: Set<String>,
    onSearchRangeToggle: (String) -> Unit,
    selectedCategories: List<String>,
    onSelectCategoryClick: () -> Unit,
    onRemoveCategory: (String) -> Unit,
    startDate: String,
    endDate: String,
    onDateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(MainColor)
    ) {
        Text(
            text = "검색",
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(top = 19.dp, bottom = 16.dp, start = 21.dp)
        )

        // 검색창
        Box(
            modifier =
                Modifier
                    .padding(horizontal = 21.dp, vertical = 5.dp)
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(MainColorLight, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "검색",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(fontSize = 15.sp),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (query.isEmpty()) {
                                Text(
                                    text = "검색하기",
                                    color = GrayColor,
                                    fontSize = 15.sp
                                )
                            }
                            // 3. 실제 입력창 영역도 동일한 중앙 기준선에 배치됩니다.
                            innerTextField()
                        }
                    }
                )
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Filled.Cancel,
                            contentDescription = "지우기",
                            tint = GrayColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = LineGrayColor)

        // 검색 범위
        Box(
            modifier = Modifier.height(48.dp).fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 15.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "검색 범위",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                    modifier = Modifier.width(60.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                SearchRangeItem("제목", searchRange.contains("제목")) { onSearchRangeToggle("제목") }
                Spacer(modifier = Modifier.width(12.dp))
                SearchRangeItem("본문 내용", searchRange.contains("본문 내용")) { onSearchRangeToggle("본문 내용") }
                Spacer(modifier = Modifier.width(12.dp))
                SearchRangeItem("메모", searchRange.contains("메모")) { onSearchRangeToggle("메모") }
            }
        }

        HorizontalDivider(color = LineGrayColor)

        // 카테고리
        Box(
            modifier = Modifier.height(48.dp).fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 15.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "카테고리",
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium),
                    modifier = Modifier.width(60.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(
                    onClick = onSelectCategoryClick,
                    modifier =
                        Modifier
                            .size((22.5).dp)
                            .border((1.2).dp, Duotone, CircleShape)
                            .clip(CircleShape)
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "추가", modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(selectedCategories) { category ->
                        CategoryChip(category) { onRemoveCategory(category) }
                    }
                }
            }
        }

        HorizontalDivider(color = LineGrayColor)

        // 날짜
        Box(
            modifier = Modifier.height(48.dp).fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 15.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "날짜",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                )
                Spacer(modifier = Modifier.width(44.dp))
                DateBox(startDate, onClick = onDateClick)
                Text(" ~ ", modifier = Modifier.padding(horizontal = 4.dp))
                DateBox(endDate, onClick = onDateClick)
            }
        }

        HorizontalDivider(color = LineGrayColor)
    }
}

@Composable
fun SearchRangeItem(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
            contentDescription = label,
            tint = if (isSelected) MainColorDeep else GrayColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, style = TextStyle(fontSize = 14.sp))
    }
}

@Composable
fun CategoryChip(label: String, onRemove: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .border(1.dp, Duotone, RoundedCornerShape(16.dp))
                .padding(start = 10.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
    ) {
        Text(text = label, style = TextStyle(fontSize = 13.sp), modifier = Modifier.padding(bottom = 2.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = Icons.Default.Cancel,
            contentDescription = "삭제",
            modifier = Modifier
                .size(16.dp)
                .clickable { onRemove() },
            tint = Duotone
        )
    }
}

@Composable
fun DateBox(date: String, onClick: () -> Unit) {
    Box(
        modifier =
            Modifier
                .background(MainColorLight, RoundedCornerShape(9.dp))
                .clickable { onClick() }
                .padding(horizontal = 18.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) { Text(text = date, style = TextStyle(fontSize = 14.sp)) }
}

@Preview(showBackground = true)
@Composable
fun SearchHeaderPreview() {
    Scrap2025Theme {
        SearchHeader(
            query = "",
            onQueryChange = {},
            searchRange = setOf("제목"),
            onSearchRangeToggle = {},
            selectedCategories = listOf("분류 섹션 1", "분류 섹션 2"),
            onSelectCategoryClick = {},
            onRemoveCategory = {},
            startDate = "2024-05-23",
            endDate = "2024-05-30",
            onDateClick = {}
        )
    }
}
