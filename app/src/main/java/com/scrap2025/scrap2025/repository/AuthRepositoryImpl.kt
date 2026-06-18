package com.scrap2025.scrap2025.repository

import android.util.Log
import com.scrap2025.scrap2025.data.local.AppDatabase
import com.scrap2025.scrap2025.data.local.PreferencesManager
import com.scrap2025.scrap2025.data.local.TokenManager
import com.scrap2025.scrap2025.data.remote.datasource.AuthRemoteDataSource
import com.scrap2025.scrap2025.model.enums.SnsType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AuthRepository의 구현체. [AuthRemoteDataSource]를 통한 원격 인증 처리와 [TokenManager], [AppDatabase]를 통한 로컬
 * 데이터 관리를 수행합니다.
 */
@Singleton
class AuthRepositoryImpl
@Inject
constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val tokenManager: TokenManager,
    private val database: AppDatabase,
    private val preferencesManager: PreferencesManager
) : AuthRepository {
    companion object {
        private const val TAG = "AuthRepository"
    }

    override suspend fun testLogin(
        testAccessToken: String,
        testRefreshToken: String
    ): Result<Unit> = try {
        tokenManager.saveTokens(testAccessToken, testRefreshToken)
        tokenManager.saveSnsType(SnsType.TEST)
        Log.d(TAG, "TEST Tokens and SnsType saved successfully")

        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "TEST Login exception", e)
        Result.failure(e)
    }

    override suspend fun loginToServer(snsType: SnsType, socialToken: String): Result<Unit> = try {
        val loginResult = authRemoteDataSource.login(
            sns = snsType.value,
            token = socialToken
        )
        tokenManager.saveTokens(
            accessToken = loginResult.accessToken,
            refreshToken = loginResult.refreshToken
        )
        tokenManager.saveSnsType(snsType)
        preferencesManager.saveLastLoginSnsType(snsType)
        Log.d(TAG, "Tokens and SnsType saved successfully")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Login exception", e)
        Result.failure(e)
    }

    override suspend fun logoutToServer(): Result<Unit> = try {
        authRemoteDataSource.logout()
        tokenManager.clearTokens()
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Logout exception", e)
        Result.failure(e)
    }

    override suspend fun withdrawToServer(): Result<Unit> = try {
        authRemoteDataSource.withdraw()
        tokenManager.clearTokens()
        database.clearAllData()

        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Withdraw exception", e)
        Result.failure(e)
    }
}
