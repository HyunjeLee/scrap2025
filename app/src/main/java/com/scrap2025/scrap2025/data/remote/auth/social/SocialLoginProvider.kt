package com.scrap2025.scrap2025.data.remote.auth.social

import android.content.Context

interface SocialLoginProvider {
    suspend fun login(context: Context): Result<String>

    suspend fun logout(): Result<Unit>

    suspend fun disconnect(): Result<Unit>
}
