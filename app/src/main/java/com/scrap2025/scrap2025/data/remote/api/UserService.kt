package com.scrap2025.scrap2025.data.remote.api

import com.scrap2025.scrap2025.data.remote.dto.BaseResponse
import com.scrap2025.scrap2025.data.remote.dto.MyPageResponse
import retrofit2.Response
import retrofit2.http.GET

interface UserService {
    @GET("/auth/mypage")
    suspend fun getMyPage(): Response<BaseResponse<MyPageResponse>>
}
