package com.scrap2025.scrap2025.data.remote.auth.social

import android.content.Context
import javax.inject.Inject

class GoogleLoginProvider
@Inject
constructor() : SocialLoginProvider {
    override suspend fun login(context: Context): Result<String> {
        // 구글 SDK 연동 필요
        return Result.failure(Exception("구글 로그인이 아직 구현되지 않았습니다."))
    }

    override suspend fun logout(): Result<Unit> = Result.success(Unit)

    override suspend fun disconnect(): Result<Unit> = Result.success(Unit)
}
