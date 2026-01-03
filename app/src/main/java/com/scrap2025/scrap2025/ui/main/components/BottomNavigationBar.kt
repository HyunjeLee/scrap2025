package com.scrap2025.scrap2025.ui.main.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.scrap2025.scrap2025.R
import com.scrap2025.scrap2025.navigation.Category
import com.scrap2025.scrap2025.navigation.Favorite
import com.scrap2025.scrap2025.navigation.MyPage
import com.scrap2025.scrap2025.navigation.Route
import com.scrap2025.scrap2025.navigation.Scrap
import com.scrap2025.scrap2025.navigation.Search
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep
import com.scrap2025.scrap2025.ui.theme.MainColorLight
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import kotlin.reflect.KClass

data class BottomNavItem(
    val label: String,
    val icon: Painter,
    val route: Route,
    val routeClass: KClass<out Route>
)

@Composable
fun BottomNavigationBar(
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
    onItemClick: (Route) -> Unit = {}
) {
    val items = listOf(
        BottomNavItem("카테고리", painterResource(R.drawable.ic_folder), Category, Category::class),
        BottomNavItem("스크랩", painterResource(R.drawable.ic_clip), Scrap, Scrap::class),
        BottomNavItem("즐겨찾기", painterResource(R.drawable.ic_fav_false), Favorite, Favorite::class),
        BottomNavItem("검색", painterResource(R.drawable.ic_search), Search, Search::class),
        BottomNavItem("마이페이지", painterResource(R.drawable.ic_user), MyPage, MyPage::class)
    )

    NavigationBar(
        modifier = modifier.clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)),
        contentColor = MainColorDeep,
        containerColor = MainColor,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            val isSelected = currentDestination?.hasRoute(item.routeClass) == true

            NavigationBarItem(
                selected = isSelected,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MainColorDeep,
                    selectedTextColor = MainColorDeep,
                    indicatorColor = MainColorLight
                ),
                onClick = { onItemClick(item.route) },
                icon = { Icon(painter = item.icon, contentDescription = item.label) },
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
