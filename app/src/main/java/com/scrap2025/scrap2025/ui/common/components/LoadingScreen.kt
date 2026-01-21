package com.scrap2025.scrap2025.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep

@Composable
fun LoadingScreen(message: String = "") {
    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .background(MainColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = MainColorDeep)
        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = message)
        }
    }
}

@Preview
@Composable
fun LoadingScreenPreview() {
    LoadingScreen("로딩 중 입니다...")
}
