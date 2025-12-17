package com.scrap2025.scrap2025.ui.main.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ManageSearch
import androidx.compose.material.icons.outlined.AttachFile
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
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.scrap2025.scrap2025.navigation.Category
import com.scrap2025.scrap2025.navigation.Favorite
import com.scrap2025.scrap2025.navigation.MyPage
import com.scrap2025.scrap2025.navigation.Route
import com.scrap2025.scrap2025.navigation.Scrap
import com.scrap2025.scrap2025.navigation.Search
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import kotlin.reflect.KClass

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: Route,
    val routeClass: KClass<out Route>
)

@Composable
fun BottomNavigationBar(
    currentDestination: NavDestination?,
    onItemClick: (Route) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem("카테고리", Icons.Outlined.Folder, Category, Category::class),
        BottomNavItem("스크랩", Icons.Outlined.AttachFile, Scrap(), Scrap::class),
        BottomNavItem("즐겨찾기", Icons.Outlined.Star, Favorite, Favorite::class),
        BottomNavItem("검색", Icons.AutoMirrored.Outlined.ManageSearch, Search, Search::class),
        BottomNavItem("마이페이지", Icons.Outlined.Person, MyPage, MyPage::class)
    )

    NavigationBar(
        modifier = modifier.clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)),
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            val isSelected = currentDestination?.hasRoute(item.routeClass) == true

            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemClick(item.route) },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                alwaysShowLabel = true
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    Scrap2025Theme { BottomNavigationBar(currentDestination = null) }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreviewSelected() {
    Scrap2025Theme { BottomNavigationBar(currentDestination = null) }
}
