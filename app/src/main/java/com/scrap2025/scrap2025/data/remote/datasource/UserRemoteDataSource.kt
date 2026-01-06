package com.scrap2025.scrap2025.data.remote.datasource

import com.scrap2025.scrap2025.data.remote.dto.MyPageResponse
import com.scrap2025.scrap2025.model.Result

interface UserRemoteDataSource {
    suspend fun getMyPage(): Result<MyPageResponse>
}
