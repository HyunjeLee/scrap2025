package com.scrap2025.scrap2025.data.remote.datasource

import com.scrap2025.scrap2025.data.remote.api.AuthService
import com.scrap2025.scrap2025.data.remote.dto.LoginResponse
import com.scrap2025.scrap2025.model.Result
import javax.inject.Inject

class AuthRemoteDataSourceImpl @Inject constructor(private val authService: AuthService) :
    AuthRemoteDataSource {

    override suspend fun login(sns: String, token: String): Result<LoginResponse> {
        return try {
            val response = authService.login(sns, token)
            if (response.isSuccessful) {
                val body = response.body()?.result
                if (body != null) Result.Success(body)
                else Result.Error(Exception("Response body is null"), "로그인 응답 오류")
            } else {
                Result.Error(Exception("Login failed code: ${response.code()}"), "로그인 실패")
            }
        } catch (e: Exception) {
            Result.Error(e, "로그인 중 오류 발생")
        }
    }

    override suspend fun refreshToken(refreshToken: String): Result<LoginResponse> {
        return try {
            val response = authService.refreshToken(refreshToken)
            if (response.isSuccessful) {
                val body = response.body()?.result
                if (body != null) Result.Success(body)
                else Result.Error(Exception("Response body is null"), "토큰 갱신 응답 오류")
            } else {
                Result.Error(Exception("Refresh token failed code: ${response.code()}"), "토큰 갱신 실패")
            }
        } catch (e: Exception) {
            Result.Error(e, "토큰 갱신 중 오류 발생")
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            val response = authService.logout()
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Logout failed code: ${response.code()}"), "로그아웃 실패")
            }
        } catch (e: Exception) {
            Result.Error(e, "로그아웃 중 오류 발생")
        }
    }

    override suspend fun withdraw(): Result<Unit> {
        return try {
            val response = authService.withdraw()
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Withdraw failed code: ${response.code()}"), "탈퇴 실패")
            }
        } catch (e: Exception) {
            Result.Error(e, "탈퇴 중 오류 발생")
        }
    }
}
