package com.scrap2025.scrap2025.repository

import android.util.Log
import com.scrap2025.scrap2025.model.LinkPreview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinkPreviewRepositoryImpl @Inject constructor() : LinkPreviewRepository {

    companion object {
        private const val TAG = "LinkPreviewRepository"
        private const val TIMEOUT_MS = 10000 // 10초
        private const val USER_AGENT = "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36"
    }

    override suspend fun fetchLinkPreview(url: String): Result<LinkPreview> =
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching link preview for: $url")

                val document: Document =
                    Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIMEOUT_MS).get()

                val preview = extractMetadata(document, url)

                Log.d(TAG, "Successfully fetched preview: ${preview.title}")
                Result.success(preview)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch link preview", e)
                Result.failure(e)
            }
        }

    private fun extractMetadata(document: Document, url: String): LinkPreview {
        val title = document.selectFirst("meta[property=og:title]")?.attr("content")
            ?: document.selectFirst("meta[name=twitter:title]")?.attr("content")
            ?: document.selectFirst("title")?.text() ?: "제목 없음"

        val description = document.selectFirst("meta[property=og:description]")?.attr("content")
            ?: document.selectFirst("meta[name=twitter:description]")?.attr("content")
            ?: document.selectFirst("meta[name=description]")?.attr("content")

        val imageUrl = document.selectFirst("meta[property=og:image]")?.attr("content")
            ?: document.selectFirst("meta[name=twitter:image]")?.attr("content")

        val siteName = document.selectFirst("meta[property=og:site_name]")?.attr("content")

        Log.d(TAG, "Extracted - Title: $title, Image: $imageUrl")

        return LinkPreview(
            url = url,
            title = title,
            description = description,
            imageUrl = imageUrl,
            siteName = siteName
        )
    }
}
