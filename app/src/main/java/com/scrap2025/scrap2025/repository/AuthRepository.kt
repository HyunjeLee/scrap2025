package com.scrap2025.scrap2025.repository

import android.util.Log
import com.scrap2025.scrap2025.data.local.TokenManager
import com.scrap2025.scrap2025.data.remote.AuthService
import javax.inject.Inject

class AuthRepository
@Inject
constructor(private val authService: AuthService, private val tokenManager: TokenManager) {
    suspend fun loginWithNaver(token: String): Result<Unit> {
        return try {
            val response = authService.login(sns = "naver", token = token)
            if (response.isSuccessful) {
                response.body()?.result?.let { loginResult ->
                    tokenManager.saveTokens(
                            accessToken = loginResult.accessToken,
                            refreshToken = loginResult.refreshToken
                    )
                    Log.d("AuthRepository", "Tokens saved successfully")
                }
                Result.success(Unit)
            } else {
                Log.e(
                        "AuthRepository",
                        "Login failed: ${response.code()} ${response.body()?.message}"
                )
                Result.failure(
                        Exception("Login failed: ${response.body()?.message ?: "Unknown error"}")
                )
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login exception", e)
            Result.failure(e)
        }
    }
}
