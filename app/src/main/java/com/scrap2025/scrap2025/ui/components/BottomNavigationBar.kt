package com.scrap2025.scrap2025.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme

data class BottomNavItem(
    val label: String,
    val icon: ImageVector?
)

@Composable
fun BottomNavigationBar(
    selectedIndex: Int = 0,
    onItemClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem("카테고리", Icons.Default.Home),
        BottomNavItem("스크랩", Icons.Default.FavoriteBorder),
        BottomNavItem("즐겨찾기", Icons.Default.FavoriteBorder),
        BottomNavItem("검색", Icons.Default.Search),
        BottomNavItem("마이페이지", Icons.Default.Person)
    )

    NavigationBar(
        modifier = modifier.clip(
            RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
        ),
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = index == selectedIndex,
                onClick = { onItemClick(index) },
                icon = {
                    item.icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = item.label
                        )
                    }
                },
                label = { Text(item.label) },
                alwaysShowLabel = true
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    Scrap2025Theme {
        BottomNavigationBar(selectedIndex = 0)
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreviewSelected() {
    Scrap2025Theme {
        BottomNavigationBar(selectedIndex = 2)
    }
}
