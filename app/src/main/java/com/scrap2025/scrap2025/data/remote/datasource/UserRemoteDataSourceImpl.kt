package com.scrap2025.scrap2025.data.remote.datasource

import com.scrap2025.scrap2025.data.remote.api.UserService
import com.scrap2025.scrap2025.data.remote.dto.MyPageResponse
import com.scrap2025.scrap2025.model.Result
import javax.inject.Inject

class UserRemoteDataSourceImpl @Inject constructor(private val userService: UserService) :
    UserRemoteDataSource {

    override suspend fun getMyPage(): Result<MyPageResponse> {
        return try {
            val response = userService.getMyPage()
            if (response.isSuccessful) {
                val body = response.body()?.result
                if (body != null) Result.Success(body)
                else Result.Error(Exception("Response body is null"), "마이페이지 응답 오류")
            } else {
                Result.Error(
                    Exception("Failed to get mypage code: ${response.code()}"), "마이페이지 조회 실패"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "마이페이지 조회 중 오류 발생")
        }
    }
}
