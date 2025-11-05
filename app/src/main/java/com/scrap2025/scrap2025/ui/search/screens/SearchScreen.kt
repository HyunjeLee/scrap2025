package com.scrap2025.scrap2025.ui.search.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme

@Composable
fun SearchScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("검색 화면")
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    Scrap2025Theme {
        SearchScreen()
    }
}
