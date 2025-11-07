package com.scrap2025.scrap2025

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.scrap2025.scrap2025.navigation.AppNavHost
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scrap2025Theme {
                AppNavHost()
            }
        }
    }
}