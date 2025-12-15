package com.scrap2025.scrap2025

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.scrap2025.scrap2025.model.GlobalUiState
import com.scrap2025.scrap2025.navigation.AppNavHost
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { Scrap2025Theme { AppNavHost() } }

        // Handle intent when app is launched
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle intent when app is already running
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                if (intent.type == "text/plain") {
                    val sharedUrl = intent.getStringExtra(Intent.EXTRA_TEXT)
                    if (sharedUrl != null) {
                        Log.d("SharedLink", "Received shared URL: $sharedUrl")

                        // GlobalUiState에 공유된 URL 설정
                        // 스크랩 화면에서 이를 감지하여 자동으로 스크랩 추가
                        GlobalUiState.setSharedUrl(sharedUrl)
                    } else {
                        Log.w("SharedLink", "Shared URL is null")
                    }
                }
            }
        }
    }
}
