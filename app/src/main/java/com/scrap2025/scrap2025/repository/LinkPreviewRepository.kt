package com.scrap2025.scrap2025.repository

import com.scrap2025.scrap2025.model.LinkPreview

/** URL로부터 웹사이트 미리보기 정보를 추출하는 리포지토리 인터페이스 */
interface LinkPreviewRepository {
    /**
     * 지정된 URL에서 OpenGraph(og:) 또는 일반 Meta 태그를 파싱하여 미리보기 데이터를 생성합니다.
     * @param url 미리보기를 가져올 대상 웹사이트 URL
     * @return 파싱된 [LinkPreview] 데이터 또는 에러 결과
     */
    suspend fun fetchLinkPreview(url: String): Result<LinkPreview>
}
