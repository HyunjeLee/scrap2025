package com.scrap2025.scrap2025

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.scrap2025.scrap2025.navigation.NavHost.AppNavHost
import com.scrap2025.scrap2025.ui.common.components.LoadingScreen
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import com.scrap2025.scrap2025.viewmodel.MainUiState
import com.scrap2025.scrap2025.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainUiState by mainViewModel.uiState.collectAsState()

            Scrap2025Theme {
                when (mainUiState) {
                    MainUiState.LoginRequired, MainUiState.Complete -> AppNavHost()
                    MainUiState.Loading -> LoadingScreen("로딩 중 ...")
                    MainUiState.Initializing -> LoadingScreen("초기화 중 ...")
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
        intent
            ?.takeIf { it.action == Intent.ACTION_SEND }
            ?.getStringExtra(Intent.EXTRA_TEXT)
            ?.let { text ->
                extractUrl(text)?.also { url ->
                    Log.d("SharedLink", "Extracted URL: $url")
                    mainViewModel.setSharedUrl(url)
                } ?: Log.w("SharedLink", "No URL found in shared text: $text")
            }
    }

    private fun extractUrl(text: String): String? =
        Patterns.WEB_URL.matcher(text)
            .takeIf { it.find() }
            ?.group()
            ?.trimEnd('.', ',', ')')
}
