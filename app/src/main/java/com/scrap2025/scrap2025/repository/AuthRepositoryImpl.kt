package com.scrap2025.scrap2025.repository

import android.util.Log
import com.scrap2025.scrap2025.data.local.AppDatabase
import com.scrap2025.scrap2025.data.local.TokenManager
import com.scrap2025.scrap2025.data.remote.datasource.AuthRemoteDataSource
import com.scrap2025.scrap2025.model.enums.SnsType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl
@Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val tokenManager: TokenManager,
    private val database: AppDatabase,
) : AuthRepository {
    companion object {
        private const val TAG = "AuthRepository"
    }

    override suspend fun loginToServer(snsType: SnsType, socialToken: String): Result<Unit> {
        return try {
            val loginResult = authRemoteDataSource.login(sns = snsType.value, token = socialToken)
            tokenManager.saveTokens(
                accessToken = loginResult.accessToken, refreshToken = loginResult.refreshToken
            )
            tokenManager.saveSnsType(snsType)
            Log.d(TAG, "Tokens and SnsType saved successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Login exception", e)
            Result.failure(e)
        }
    }

    override suspend fun logoutToServer(): Result<Unit> {
        return try {
            authRemoteDataSource.logout()
            tokenManager.clearTokens()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Logout exception", e)
            Result.failure(e)
        }
    }

    override suspend fun withdrawToServer(): Result<Unit> {
        return try {
            authRemoteDataSource.withdraw()
            tokenManager.clearTokens()
            database.clearAllData()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Withdraw exception", e)
            Result.failure(e)
        }
    }
}
