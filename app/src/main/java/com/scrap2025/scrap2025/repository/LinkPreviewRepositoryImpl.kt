package com.scrap2025.scrap2025.repository

import android.util.Log
import com.scrap2025.scrap2025.model.LinkPreview
import com.scrap2025.scrap2025.model.Result
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

    override suspend fun fetchLinkPreview(url: String): Result<LinkPreview> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching link preview for: $url")

            // jsoup으로 HTML 가져오기
            val document: Document = Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT_MS)
                .get()

            // 메타데이터 추출
            val preview = extractMetadata(document, url)

            Log.d(TAG, "Successfully fetched preview: ${preview.title}")
            Result.Success(preview)

        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch link preview", e)
            Result.Error(e, "링크 미리보기를 가져올 수 없습니다")
        }
    }

    /**
     * HTML Document에서 메타데이터 추출
     * 우선순위: Open Graph > Twitter Card > 일반 meta 태그 > HTML 태그
     */
    private fun extractMetadata(document: Document, url: String): LinkPreview {
        // 제목 추출 (우선순위: og:title > twitter:title > <title> 태그)
        val title = document.selectFirst("meta[property=og:title]")?.attr("content")
            ?: document.selectFirst("meta[name=twitter:title]")?.attr("content")
            ?: document.selectFirst("title")?.text()
            ?: "제목 없음"

        // 설명 추출 (우선순위: og:description > twitter:description > meta description)
        val description = document.selectFirst("meta[property=og:description]")?.attr("content")
            ?: document.selectFirst("meta[name=twitter:description]")?.attr("content")
            ?: document.selectFirst("meta[name=description]")?.attr("content")

        // 이미지 URL 추출 (우선순위: og:image > twitter:image)
        val imageUrl = document.selectFirst("meta[property=og:image]")?.attr("content")
            ?: document.selectFirst("meta[name=twitter:image]")?.attr("content")

        // 사이트 이름 추출
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
