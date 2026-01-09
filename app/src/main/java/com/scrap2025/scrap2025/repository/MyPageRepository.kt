package com.scrap2025.scrap2025.repository

import com.scrap2025.scrap2025.data.remote.dto.MyPageResponse
import kotlinx.coroutines.flow.StateFlow

interface MyPageRepository {
    val myPageData: StateFlow<MyPageResponse?>
    suspend fun fetchMyPage()
}
