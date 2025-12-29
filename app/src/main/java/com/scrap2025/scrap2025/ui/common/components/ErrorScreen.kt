package com.scrap2025.scrap2025.ui.common.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.ui.theme.GrayColor

@Composable
fun ErrorScreen(errorText: String, errorState: Result.Error) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = errorText,
                style =
                    TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    ),
                color = GrayColor
            )
            errorState.message?.let {
                Text(
                    text = it,
                    style = TextStyle(fontSize = 14.sp),
                    color = GrayColor,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}