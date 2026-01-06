package com.scrap2025.scrap2025.repository

import com.scrap2025.scrap2025.model.LinkPreview

interface LinkPreviewRepository {
    /**
     * URL로부터 링크 미리보기 데이터를 추출
     * @param url 미리보기를 가져올 URL
     * @return LinkPreview 데이터 또는 에러
     */
    suspend fun fetchLinkPreview(url: String): Result<LinkPreview>
}
