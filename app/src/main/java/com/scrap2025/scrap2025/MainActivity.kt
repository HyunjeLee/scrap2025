package com.scrap2025.scrap2025

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.scrap2025.scrap2025.ui.components.BottomNavigationBar
import com.scrap2025.scrap2025.ui.screens.CategoryScreen
import com.scrap2025.scrap2025.ui.screens.FavoriteScreen
import com.scrap2025.scrap2025.ui.screens.MyPageScreen
import com.scrap2025.scrap2025.ui.screens.ScrapScreen
import com.scrap2025.scrap2025.ui.screens.SearchScreen
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scrap2025Theme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val selectedIndex = remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(
                selectedIndex = selectedIndex.intValue,
                onItemClick = { selectedIndex.intValue = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedIndex.intValue) {
                0 -> CategoryScreen()
                1 -> ScrapScreen()
                2 -> FavoriteScreen()
                3 -> SearchScreen()
                4 -> MyPageScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    Scrap2025Theme {
        MainScreen()
    }
}