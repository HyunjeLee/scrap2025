package com.scrap2025.scrap2025.data.remote.datasource

import com.scrap2025.scrap2025.data.remote.api.UserService
import com.scrap2025.scrap2025.data.remote.dto.MyPageResponse
import javax.inject.Inject

class UserRemoteDataSourceImpl @Inject constructor(private val userService: UserService) :
    UserRemoteDataSource {

    override suspend fun getMyPage(): MyPageResponse {
        val response = userService.getMyPage()
        if (response.isSuccessful) {
            return response.body()?.result ?: throw Exception("Response body is null")
        } else {
            throw Exception("Failed to get mypage code: ${response.code()}")
        }
    }
}
