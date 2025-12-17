package com.scrap2025.scrap2025.ui.common.utils

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun BackPressToExitHandler() {
    val context = LocalContext.current
    var lastPressedTime by remember { mutableLongStateOf(0L) }
    val finishTimeout = 2000L // 2 seconds

    BackHandler {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastPressedTime < finishTimeout) {
            (context as? Activity)?.finish()
        } else {
            lastPressedTime = currentTime
            Toast.makeText(context, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
