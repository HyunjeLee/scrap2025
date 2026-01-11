package com.scrap2025.scrap2025.repository

import android.util.Log
import com.scrap2025.scrap2025.model.LinkPreview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.inject.Inject
import javax.inject.Singleton

/** LinkPreviewRepository의 구현체. Jsoup 라이브러리를 사용하여 웹 페이지의 HTML을 파싱하고 메타 데이터를 추출합니다. */
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

                // Jsoup을 사용한 HTML 문서 로드
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

    /** Jsoup Document에서 OpenGraph 등의 메타 데이터를 추출합니다. */
    private fun extractMetadata(document: Document, url: String): LinkPreview {
        // 우선순위: og:title -> twitter:title -> title 태그
        val title =
            document.selectFirst("meta[property=og:title]")?.attr("content")
                ?: document.selectFirst("meta[name=twitter:title]")?.attr("content")
                ?: document.selectFirst("title")?.text() ?: "제목 없음"

        // 우선순위: og:description -> twitter:description -> description 태그
        val description =
            document.selectFirst("meta[property=og:description]")?.attr("content")
                ?: document.selectFirst("meta[name=twitter:description]")?.attr("content")
                ?: document.selectFirst("meta[name=description]")?.attr("content")

        // 우선순위: og:image -> twitter:image
        val imageUrl =
            document.selectFirst("meta[property=og:image]")?.attr("content")
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
