package com.scrap2025.scrap2025.repository

import android.util.Log
import com.scrap2025.scrap2025.data.local.AppDatabase
import com.scrap2025.scrap2025.data.local.DatabaseInitializer
import com.scrap2025.scrap2025.data.local.TokenManager
import com.scrap2025.scrap2025.data.model.MyPageResult
import com.scrap2025.scrap2025.data.remote.AuthService
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    private val authService: AuthService,
    private val tokenManager: TokenManager,
    private val database: AppDatabase,
    private val databaseInitializer: DatabaseInitializer
) {
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

    suspend fun getMyPage(): Result<MyPageResult> {
        return try {
            val token =
                tokenManager.accessToken.firstOrNull()
                    ?: throw Exception("No access token found")
            val response = authService.getMyPage(token)
            if (response.isSuccessful) {
                response.body()?.result?.let { Result.success(it) }
                    ?: Result.failure(Exception("MyPage result is null"))
            } else {
                Result.failure(Exception("Failed to get MyPage: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            val token =
                tokenManager.accessToken.firstOrNull()
                    ?: throw Exception("No access token found")
            val response = authService.logout(token)
            if (response.isSuccessful) {
                tokenManager.clearTokens()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Logout failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun withdraw(): Result<Unit> {
        return try {
            val token =
                tokenManager.accessToken.firstOrNull()
                    ?: throw Exception("No access token found")
            val response = authService.withdraw(token)
            if (response.isSuccessful) {
                tokenManager.clearTokens()
                database.clearAllData()

                databaseInitializer.init()

                Result.success(Unit)
            } else {
                Result.failure(Exception("Signout failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
