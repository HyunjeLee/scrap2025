package com.scrap2025.scrap2025.ui.common.components

import android.graphics.Bitmap
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.scrap2025.scrap2025.model.LinkPreview
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// Data class for parsing JSON returned from JS
@Serializable
data class ParsedMetadata(
    val title: String? = null,
    val description: String? = null,
    val imageUrl: String? = null
)

@Composable
fun WebViewScrapDialog(url: String, onDismiss: () -> Unit, onScrapComplete: (LinkPreview) -> Unit) {
    var isLoading by remember { mutableStateOf(true) }
    var progress by remember { mutableStateOf(0.0f) }
    var hasError by remember { mutableStateOf(false) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var isInitialLoad by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2000) // Hide redirects for 2 seconds
        isInitialLoad = false
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // Use full screen
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Top Toolbar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color(0xFFF5F5F5))
            ) {
                IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }

                Text(text = "직접 확인해서 추가하기", modifier = Modifier.align(Alignment.Center))

                // Manual Trigger Button
                TextButton(
                    onClick = { webViewRef?.let { extractMetadata(it) } },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                ) {
                    Text(
                        text = "가져오기",
                        color = Color.Blue // Or MainColor if available, using standard Color
                        // for now to avoid dependency lookup if not imported
                    )
                }
            }

            // Loading Bar (Shows during page loads OR during initial 2s delay)
            if (isLoading || isInitialLoad) {
                LinearProgressIndicator(
                    progress = {
                        if (isInitialLoad) 0.5f else progress
                    }, // Fake progress for initial delay
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // WebView Area
            // WebView and Overlay Container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        WebView(context).apply {
                            webViewRef = this
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true

                            // Register JS Interface
                            addJavascriptInterface(
                                object {
                                    @JavascriptInterface
                                    fun processMetadata(jsonString: String) {
                                        Log.d(
                                            "WebViewScrap",
                                            "processMetadata called with: $jsonString"
                                        )
                                        try {
                                            val metadata =
                                                Json { ignoreUnknownKeys = true }
                                                    .decodeFromString<
                                                            ParsedMetadata>(
                                                        jsonString
                                                    )
                                            Log.d(
                                                "WebViewScrap",
                                                "Parsed metadata: $metadata"
                                            )
                                            val preview =
                                                LinkPreview(
                                                    url = url,
                                                    title = metadata.title ?: url,
                                                    description =
                                                        metadata.description
                                                            ?: "",
                                                    imageUrl = metadata.imageUrl,
                                                    siteName = null
                                                )
                                            // Callback on Main Thread
                                            post {
                                                Log.d(
                                                    "WebViewScrap",
                                                    "Posting onScrapComplete"
                                                )
                                                onScrapComplete(preview)
                                            }
                                        } catch (e: Exception) {
                                            Log.e(
                                                "WebViewScrap",
                                                "Metadata parsing failed",
                                                e
                                            )
                                        }
                                    }
                                },
                                "AndroidApp"
                            )

                            webViewClient =
                                object : WebViewClient() {
                                    override fun onPageStarted(
                                        view: WebView?,
                                        url: String?,
                                        favicon: Bitmap?
                                    ) {
                                        Log.d("WebViewScrap", "onPageStarted: $url")
                                        isLoading = true
                                        hasError = false
                                    }

                                    override fun onPageFinished(
                                        view: WebView?,
                                        url: String?
                                    ) {
                                        Log.d("WebViewScrap", "onPageFinished: $url")
                                        isLoading = false
                                        // Auto-extract removed. User must click 'Import'
                                    }

                                    override fun shouldOverrideUrlLoading(
                                        view: WebView?,
                                        request: WebResourceRequest?
                                    ): Boolean {
                                        val url = request?.url?.toString() ?: return false
                                        return if (url.startsWith("http://") ||
                                            url.startsWith("https://")
                                        ) {
                                            false
                                        } else {
                                            Log.d(
                                                "WebViewScrap",
                                                "Blocked URL scheme: $url"
                                            )
                                            true
                                        }
                                    }

                                    override fun onReceivedError(
                                        view: WebView?,
                                        request: WebResourceRequest?,
                                        error: WebResourceError?
                                    ) {
                                        super.onReceivedError(view, request, error)
                                        Log.e(
                                            "WebViewScrap",
                                            "onReceivedError: ${error?.description}, errorCode: ${error?.errorCode}"
                                        )
                                        hasError = true
                                    }

                                    override fun onReceivedHttpError(
                                        view: WebView?,
                                        request: WebResourceRequest?,
                                        errorResponse: WebResourceResponse?
                                    ) {
                                        super.onReceivedHttpError(
                                            view,
                                            request,
                                            errorResponse
                                        )
                                        Log.e(
                                            "WebViewScrap",
                                            "onReceivedHttpError: ${errorResponse?.statusCode}"
                                        )
                                        hasError = true
                                    }
                                }

                            webChromeClient =
                                object : WebChromeClient() {
                                    override fun onProgressChanged(
                                        view: WebView?,
                                        newProgress: Int
                                    ) {
                                        progress = newProgress / 100f
                                    }
                                }

                            Log.d("WebViewScrap", "Loading URL: $url")
                            loadUrl(url)
                        }
                    }
                )

                // Full screen overlay for initial load to hide redirects
                if (isInitialLoad) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color.Black)
                            Text(text = "페이지 연결 중...", modifier = Modifier.padding(top = 16.dp))
                        }
                    }
                }
            }
        }
    }
}

// JS Script for metadata extraction
private fun extractMetadata(webView: WebView) {
    val script =
        """
        (function() {
            var ogTitle = document.querySelector('meta[property="og:title"]')?.content;
            var ogImage = document.querySelector('meta[property="og:image"]')?.content;
            var ogDesc = document.querySelector('meta[property="og:description"]')?.content;
            var title = document.title;
            
            var data = {
                title: ogTitle || title,
                imageUrl: ogImage,
                description: ogDesc
            };
            
            // Call AndroidApp Interface
            window.AndroidApp.processMetadata(JSON.stringify(data));
        })();
    """.trimIndent()

    webView.evaluateJavascript(script, null)
}
