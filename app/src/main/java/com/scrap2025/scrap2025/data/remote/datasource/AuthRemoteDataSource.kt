package com.scrap2025.scrap2025.data.remote.datasource

import com.scrap2025.scrap2025.data.remote.dto.LoginResponse

interface AuthRemoteDataSource {
    suspend fun login(sns: String, token: String): LoginResponse
    suspend fun refreshToken(refreshToken: String): LoginResponse
    suspend fun logout()
    suspend fun withdraw()
}
