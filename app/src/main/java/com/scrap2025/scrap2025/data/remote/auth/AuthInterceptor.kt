package com.scrap2025.scrap2025.data.remote.auth

import android.util.Log
import com.scrap2025.scrap2025.data.local.TokenManager
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor
@Inject
constructor(private val tokenManager: TokenManager) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val path = originalRequest.url.encodedPath

        // 토큰 주입 제외 경로  // 로그인, 토큰 갱신
        val skipPaths = listOf("/oauth/login", "/token")
        if (skipPaths.any { path.contains(it) }) {
            return chain.proceed(originalRequest) // 수정없이 원본 통신
        }

        val token =
            runBlocking {
                withTimeoutOrNull(2500) {
                    tokenManager.accessToken.first { !it.isNullOrEmpty() }
                }
            }

        val newRequest =
            if (!token.isNullOrBlank()) {
                originalRequest
                    .newBuilder()
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer $token")
                    .build()
            } else {
                Log.d("AuthInterceptor", "Token is null or blank")

                originalRequest
            }

        return chain.proceed(newRequest)
    }
}
