package com.scrap2025.scrap2025.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize().background(MainColor), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MainColorDeep)
    }
}