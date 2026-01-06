package com.scrap2025.scrap2025.repository

import com.scrap2025.scrap2025.data.remote.dto.MyPageResponse
import kotlinx.coroutines.flow.Flow

interface MyPageRepository {
    val myPageData: Flow<MyPageResponse?>
    suspend fun invokeMyPageSync(): Boolean
}
