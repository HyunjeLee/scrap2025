package com.scrap2025.scrap2025.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme

@Composable
fun FavoriteScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("즐겨찾기 화면")
    }
}

@Preview(showBackground = true)
@Composable
fun FavoriteScreenPreview() {
    Scrap2025Theme {
        FavoriteScreen()
    }
}
