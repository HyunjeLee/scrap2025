package com.scrap2025.scrap2025.data.remote

import com.scrap2025.scrap2025.data.local.TokenManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor
@Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val path = originalRequest.url.encodedPath

        // 토큰 주입 제외 경로  // 로그인, 토큰 갱신
        val skipPaths = listOf("/oauth/login", "/token")
        if (skipPaths.any { path.contains(it) }) {
            return chain.proceed(originalRequest)  // 수정없이 원본 통신
        }

        val token = runBlocking { tokenManager.accessToken.firstOrNull() }

        val newRequest = if (!token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .header("Accept", "application/json")
                .header("Authorization", token)
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}