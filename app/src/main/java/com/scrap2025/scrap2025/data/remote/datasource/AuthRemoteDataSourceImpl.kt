package com.scrap2025.scrap2025.data.remote.datasource

import com.scrap2025.scrap2025.data.remote.api.AuthService
import com.scrap2025.scrap2025.data.remote.dto.LoginResponse
import javax.inject.Inject

class AuthRemoteDataSourceImpl
@Inject
constructor(private val authService: AuthService) :
    AuthRemoteDataSource {
    override suspend fun login(sns: String, token: String): LoginResponse {
        val response = authService.login(sns, token)
        if (response.isSuccessful) {
            return response.body()?.result ?: throw Exception("Response body is null")
        } else {
            throw Exception("Login failed code: ${response.code()}")
        }
    }

    override suspend fun refreshToken(refreshToken: String): LoginResponse {
        val response = authService.refreshToken(refreshToken)
        if (response.isSuccessful) {
            return response.body()?.result ?: throw Exception("Response body is null")
        } else {
            throw Exception("Refresh token failed code: ${response.code()}")
        }
    }

    override suspend fun logout() {
        val response = authService.logout()
        if (!response.isSuccessful) {
            throw Exception("Logout failed code: ${response.code()}")
        }
    }

    override suspend fun withdraw() {
        val response = authService.withdraw()
        if (!response.isSuccessful) {
            throw Exception("Withdraw failed code: ${response.code()}")
        }
    }
}
