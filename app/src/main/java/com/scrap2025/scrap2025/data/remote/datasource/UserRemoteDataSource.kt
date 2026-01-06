package com.scrap2025.scrap2025.data.remote.datasource

import com.scrap2025.scrap2025.data.remote.dto.MyPageResponse

interface UserRemoteDataSource {
    suspend fun getMyPage(): MyPageResponse
}
