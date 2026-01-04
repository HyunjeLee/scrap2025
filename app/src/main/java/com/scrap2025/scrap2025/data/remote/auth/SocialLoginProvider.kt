package com.scrap2025.scrap2025.data.remote.auth

import android.content.Context

interface SocialLoginProvider {
    suspend fun login(context: Context): Result<String>
    suspend fun logout(): Result<Unit>
}
