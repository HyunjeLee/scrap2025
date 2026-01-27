package com.scrap2025.scrap2025.repository

import android.util.Log
import com.scrap2025.scrap2025.model.LinkPreview
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/** LinkPreviewRepository의 구현체. Jsoup 라이브러리를 사용하여 웹 페이지의 HTML을 파싱하고 메타 데이터를 추출합니다. */
@Singleton
class LinkPreviewRepositoryImpl
@Inject
constructor() : LinkPreviewRepository {
    companion object {
        private const val TAG = "LinkPreviewRepository"
        private const val TIMEOUT_MS = 10000 // 10초

        // kakaoTalk Scraper based on facebook
        private const val USER_AGENT = "facebookexternalhit/1.1; KakaoTalk/9.0.0"
    }

    override suspend fun fetchLinkPreview(url: String): Result<LinkPreview> =
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching link preview for: $url")

                // Jsoup을 사용한 HTML 문서 로드
                val document: Document =
                    Jsoup
                        .connect(url)
                        .userAgent(USER_AGENT)
                        .timeout(TIMEOUT_MS)
                        .get()

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

        // 우선순위: og:image -> twitter:image -> apple-touch-icon -> icon -> shortcut icon
        val imageUrl =
            document.selectFirst("meta[property=og:image]")?.attr("content")
                ?: document.selectFirst("meta[name=twitter:image]")?.attr("content")
                ?: document.selectFirst("link[rel=apple-touch-icon]")?.attr("abs:href")
                ?: document.selectFirst("link[rel=icon]")?.attr("abs:href")
                ?: document.selectFirst("link[rel=shortcut icon]")?.attr("abs:href")

        val siteName = document.selectFirst("meta[property=og:site_name]")?.attr("content")

        // 지도 서비스(네이버/카카오) 및 사이트명과 제목이 중복되는 경우 보정
        // 제목이 사이트 이름과 같거나 서비스명(네이버 지도, 카카오맵)일 때 처리
        val serviceNames = listOf("네이버 지도", "카카오맵", "Naver Map", "Kakao Maps", siteName)

        // 본문에 실제 장소 이름이 들어있는 경우가 많으므로 제목으로 끌어올림
        // 예: "공주칼국수 : 서울 영등포구..." -> 제목: 공주칼국수
        val refinedTitle =
            if (title.trim() in serviceNames && !description.isNullOrEmpty()) {
                description.split(":").firstOrNull()?.trim() ?: description
            } else {
                title
            }

        Log.d(TAG, "Extracted - Title: $title, Image: $imageUrl")

        return LinkPreview(
            url = url,
            title = refinedTitle,
            description = description,
            imageUrl = imageUrl?.let { if (it.length > 1000) null else it },
            siteName = siteName
        )
    }
}
