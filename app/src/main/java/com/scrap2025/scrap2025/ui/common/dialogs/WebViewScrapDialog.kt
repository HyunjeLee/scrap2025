package com.scrap2025.scrap2025.ui.common.dialogs

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.scrap2025.scrap2025.model.LinkPreview
import com.scrap2025.scrap2025.ui.common.components.LoadingScreen
import com.scrap2025.scrap2025.ui.theme.BackgroundColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep
import com.scrap2025.scrap2025.ui.theme.MainColorLight
import java.net.URLDecoder
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

private const val TAG = "WebViewScrap"

@Composable
fun WebViewScrapDialog(url: String, onDismiss: () -> Unit, onScrapComplete: (LinkPreview) -> Unit) {
    var isLoading by remember { mutableStateOf(true) }
    var progress by remember { mutableStateOf(0.0f) }
    var hasError by remember { mutableStateOf(false) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var isInitialLoad by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2500) // Hide redirects for 2.5 seconds
        isInitialLoad = false
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // Use full screen
    ) {
        WebViewScrapDialogContent(
            title = "직접 확인해서 가져오기",
            onDismiss = onDismiss,
            onImport = { webViewRef?.let { extractMetadata(it) } },
            isLoading = isLoading || isInitialLoad,
            progress = if (isInitialLoad) 0.5f else progress,
            webViewContent = {
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
                                        Log.d(TAG, "processMetadata called with: $jsonString")
                                        try {
                                            val metadata =
                                                Json {
                                                    ignoreUnknownKeys = true
                                                }.decodeFromString<ParsedMetadata>(jsonString)

                                            Log.d(TAG, "Parsed metadata: $metadata")

                                            val preview =
                                                LinkPreview(
                                                    url = url,
                                                    title = metadata.title ?: url,
                                                    description = metadata.description ?: "",
                                                    imageUrl = metadata.imageUrl,
                                                    siteName = null
                                                )

                                            // Callback on Main Thread
                                            Handler(Looper.getMainLooper()).post {
                                                Log.d(
                                                    TAG,
                                                    "Posting onScrapComplete"
                                                )
                                                onScrapComplete(preview)
                                            }
                                        } catch (e: Exception) {
                                            Log.e(TAG, "Metadata parsing failed", e)
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
                                        Log.d(TAG, "onPageStarted: $url")

                                        isLoading = true
                                        hasError = false
                                    }

                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        Log.d(TAG, "onPageFinished: $url")

                                        isLoading = false
                                    }

                                    override fun shouldOverrideUrlLoading(
                                        view: WebView?,
                                        request: WebResourceRequest?
                                    ): Boolean {
                                        val url = request?.url?.toString() ?: return false

                                        if (url.startsWith("http://") ||
                                            url.startsWith("https://")
                                        ) {
                                            return false
                                        }

                                        // Handle intent:// schemes
                                        if (url.startsWith("intent://")) {
                                            try {
                                                // Extract fallback URL manually
                                                val fallbackUrl =
                                                    url
                                                        .substringAfter(
                                                            "S.browser_fallback_url=",
                                                            ""
                                                        ).substringBefore(";")

                                                if (fallbackUrl.isNotEmpty()) {
                                                    // Decode if necessary
                                                    // (browser_fallback_url is often
                                                    // URL-encoded)
                                                    val decodedUrl =
                                                        URLDecoder.decode(
                                                            fallbackUrl,
                                                            "UTF-8"
                                                        )
                                                    Log.d(
                                                        TAG,
                                                        "Redirecting to fallback URL: $decodedUrl"
                                                    )

                                                    view?.loadUrl(decodedUrl)
                                                    return true // Handled by manual load
                                                }
                                            } catch (e: Exception) {
                                                Log.e(TAG, "Failed to parse intent fallback", e)
                                            }
                                        }

                                        Log.d(TAG, "Blocked URL scheme: $url")
                                        return true
                                    }

                                    override fun onReceivedError(
                                        view: WebView?,
                                        request: WebResourceRequest?,
                                        error: WebResourceError?
                                    ) {
                                        super.onReceivedError(view, request, error)
                                        Log.e(
                                            TAG,
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
                                            TAG,
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

                            Log.d(TAG, "Loading URL: $url")
                            loadUrl(url)
                        }
                    }
                )
            }
        )
    }
}

/**
 * WebViewScrapDialogContent: UI 전용 Composable
 * - Preview 가능하도록 로직과 분리됨
 * - Slot API(webViewContent)를 통해 실제 WebView 또는 가짜 박스를 주입받음
 */
@Composable
fun WebViewScrapDialogContent(
    title: String,
    onDismiss: () -> Unit,
    onImport: () -> Unit,
    isLoading: Boolean,
    progress: Float,
    webViewContent: @Composable () -> Unit
) {
    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // Top Toolbar
        Box(
            modifier =
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(MainColorLight)
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MainColorDeep
                )
            }

            Text(
                text = title,
                modifier = Modifier.align(Alignment.Center)
            )

            // Manual Trigger Button
            TextButton(
                onClick = onImport,
                modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
            ) { Text(text = "가져오기", color = MainColorDeep) }
        }

        // Loading Bar (Shows during page loads OR during initial 2s delay)
        if (isLoading) {
            LinearProgressIndicator(
                progress = { progress },
                color = MainColorDeep,
                trackColor = MainColorLight,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // WebView Area
        // WebView and Overlay Container
        Box(
            modifier =
            Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            webViewContent()

            if (isLoading && progress < 0.6f) {
                LoadingScreen("페이지 연결 중...")
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

@Preview(showBackground = true)
@Composable
fun WebViewScrapDialogContentPreview() {
    WebViewScrapDialogContent(
        title = "직접 확인해서 추가하기",
        onDismiss = {},
        onImport = {},
        isLoading = true,
        progress = 0.5f,
        webViewContent = {
            Box(
                modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) { Text("WebView Placeholder") }
        }
    )
}
