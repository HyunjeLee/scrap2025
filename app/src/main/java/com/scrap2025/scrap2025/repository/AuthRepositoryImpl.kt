package com.scrap2025.scrap2025.repository

import android.util.Log
import com.scrap2025.scrap2025.data.local.AppDatabase
import com.scrap2025.scrap2025.data.local.DatabaseInitializer
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
    private val databaseInitializer: DatabaseInitializer
) : AuthRepository {
    companion object {
        private const val TAG = "AuthRepository"
    }

    override suspend fun loginToServer(snsType: SnsType, socialToken: String): Result<Unit> {
        return try {
            val result = authRemoteDataSource.login(sns = snsType.value, token = socialToken)
            if (result is com.scrap2025.scrap2025.model.Result.Success) {
                val loginResult = result.data
                tokenManager.saveTokens(
                    accessToken = loginResult.accessToken, refreshToken = loginResult.refreshToken
                )
                tokenManager.saveSnsType(snsType)
                Log.d(TAG, "Tokens and SnsType saved successfully")
                Result.success(Unit)
            } else {
                result as com.scrap2025.scrap2025.model.Result.Error
                Log.e(TAG, "Login failed: ${result.message}")
                Result.failure(result.exception)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login exception", e)
            Result.failure(e)
        }
    }

    override suspend fun logoutToServer(): Result<Unit> {
        return try {
            val result = authRemoteDataSource.logout()
            if (result is com.scrap2025.scrap2025.model.Result.Success) {
                tokenManager.clearTokens()
                Result.success(Unit)
            } else {
                result as com.scrap2025.scrap2025.model.Result.Error
                Result.failure(result.exception)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun withdrawToServer(): Result<Unit> {
        return try {
            val result = authRemoteDataSource.withdraw()
            if (result is com.scrap2025.scrap2025.model.Result.Success) {
                tokenManager.clearTokens()
                database.clearAllData()
                databaseInitializer.init()
                Result.success(Unit)
            } else {
                result as com.scrap2025.scrap2025.model.Result.Error
                Result.failure(result.exception)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
