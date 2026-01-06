package com.scrap2025.scrap2025.data.remote.datasource

import com.scrap2025.scrap2025.data.remote.dto.LoginResponse
import com.scrap2025.scrap2025.model.Result

interface AuthRemoteDataSource {
    suspend fun login(sns: String, token: String): Result<LoginResponse>
    suspend fun refreshToken(refreshToken: String): Result<LoginResponse>
    suspend fun logout(): Result<Unit>
    suspend fun withdraw(): Result<Unit>
}
