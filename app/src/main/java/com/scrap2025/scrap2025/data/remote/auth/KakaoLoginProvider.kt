package com.scrap2025.scrap2025.data.remote.auth

import android.content.Context
import javax.inject.Inject

class KakaoLoginProvider @Inject constructor() : SocialLoginProvider {
    override suspend fun login(context: Context): Result<String> {
        // 카카오 SDK 연동 필요
        return Result.failure(Exception("카카오 로그인이 아직 구현되지 않았습니다."))
    }

    override suspend fun logout(): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun disconnect(): Result<Unit> {
        return Result.success(Unit)
    }
}
