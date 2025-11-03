package com.scrap2025.scrap2025.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ManageSearch
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
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
import com.scrap2025.scrap2025.navigation.NavRoute
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme

data class BottomNavItem(
    val label: String,
    val icon: ImageVector?,
    val route: String
)

@Composable
fun BottomNavigationBar(
    selectedRoute: String = NavRoute.CATEGORY,
    onItemClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem("카테고리", Icons.Outlined.Folder, NavRoute.CATEGORY),
        BottomNavItem("스크랩", Icons.Outlined.Add, NavRoute.SCRAP),
        BottomNavItem("즐겨찾기", Icons.Outlined.Star, NavRoute.FAVORITE),
        BottomNavItem("검색", Icons.AutoMirrored.Outlined.ManageSearch, NavRoute.SEARCH),
        BottomNavItem("마이페이지", Icons.Outlined.Person, NavRoute.MYPAGE)
    )

    NavigationBar(
        modifier = modifier.clip(
            RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
        ),
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = item.route == selectedRoute,
                onClick = { onItemClick(item.route) },
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
        BottomNavigationBar(selectedRoute = NavRoute.CATEGORY)
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreviewSelected() {
    Scrap2025Theme {
        BottomNavigationBar(selectedRoute = NavRoute.FAVORITE)
    }
}
