package com.scrap2025.scrap2025.repository

import android.util.Log
import com.scrap2025.scrap2025.data.local.AppDatabase
import com.scrap2025.scrap2025.data.local.DatabaseInitializer
import com.scrap2025.scrap2025.data.local.TokenManager
import com.scrap2025.scrap2025.data.remote.AuthService
import com.scrap2025.scrap2025.model.SnsType
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    private val authService: AuthService,
    private val tokenManager: TokenManager,
    private val database: AppDatabase,
    private val databaseInitializer: DatabaseInitializer
) {
    companion object {
        private const val TAG = "AuthRepository"
    }

    suspend fun loginToServer(snsType: SnsType, socialToken: String): Result<Unit> {
        return try {
            val response = authService.login(sns = snsType.value, token = socialToken)
            if (response.isSuccessful) {
                response.body()?.result?.let { loginResult ->
                    tokenManager.saveTokens(
                        accessToken = loginResult.accessToken,
                        refreshToken = loginResult.refreshToken
                    )
                    Log.d(TAG, "Tokens saved successfully")
                }
                Result.success(Unit)
            } else {
                Log.e(TAG, "Login failed: ${response.code()} ${response.body()?.message}")

                Result.failure(
                    Exception("Login failed: ${response.body()?.message ?: "Unknown error"}")
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login exception", e)
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            val response = authService.logout()
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
            val response = authService.withdraw()
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
