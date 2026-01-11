package com.scrap2025.scrap2025

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.scrap2025.scrap2025.navigation.NavHost.AppNavHost
import com.scrap2025.scrap2025.ui.common.components.LoadingScreen
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import com.scrap2025.scrap2025.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isInitialized by mainViewModel.isInitialized.collectAsState()

            Scrap2025Theme {
                when (isInitialized) {
                    true -> {
                        AppNavHost()
                    }

                    false -> {
                        LoadingScreen("로딩 중 입니다...")
                    }
                }
            }
        }

        // Handle intent when app is launched
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle intent when app is already running
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val sharedText =
                intent.getStringExtra(Intent.EXTRA_TEXT)
                    ?: intent.clipData?.getItemAt(0)?.text?.toString()

            if (sharedText != null) {
                val extractedUrl = extractUrl(sharedText)
                if (extractedUrl != null) {
                    Log.d("SharedLink", "Extracted URL: $extractedUrl")
                    mainViewModel.setSharedUrl(extractedUrl)
                } else {
                    Log.w("SharedLink", "No URL found in shared text: $sharedText")
                }
            }
        }
    }

    private fun extractUrl(text: String): String? {
        // Regex to find URLs starting with http or https
        val urlRegex = "(https?://[\\w-]+(\\.[\\w-]+)+(:\\d+)?(/\\S*)?)".toRegex()
        return urlRegex.find(text)?.value
    }
}
