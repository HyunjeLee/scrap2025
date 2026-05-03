package com.scrap2025.scrap2025.data.remote.auth

import android.util.Log
import com.scrap2025.scrap2025.data.local.TokenManager
import com.scrap2025.scrap2025.data.remote.api.TokenRefreshService
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator
@Inject
constructor(
    private val tokenManager: TokenManager,
    // Use Provider to avoid circular dependency
    private val tokenRefreshServiceProvider: Provider<TokenRefreshService>
) : Authenticator {

    private val mutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        Log.d("TokenAuthenticator", "==================================================")
        Log.d("TokenAuthenticator", "🚨 401 에러 발생: ${response.request.url.encodedPath}")

        // limit retry count to avoid infinite loop
        if (responseCount(response) >= 3) {
            Log.e("TokenAuthenticator", "❌ 재시도 횟수(3회) 초과로 갱신 포기")
            return null
        }

        val newToken =
            runBlocking {
                Log.d("TokenAuthenticator", "⏳ Mutex 락 획득 대기 중... (다른 통신이 갱신 중인지 대기)")
                mutex.withLock {
                    Log.d("TokenAuthenticator", "🔒 Mutex 락 획득! (동기화 구역 진입)")

                    // Check if token was already refreshed by another thread
                    val currentToken = tokenManager.accessToken.firstOrNull()
                    val originalToken = response.request.header(
                        "Authorization"
                    )?.removePrefix("Bearer ")

                    Log.d(
                        "TokenAuthenticator",
                        "🔍 토큰 비교 - 원본 요청 토큰(끝자리): ${originalToken?.takeLast(10)}"
                    )
                    Log.d(
                        "TokenAuthenticator",
                        "🔍 토큰 비교 - 현재 저장소 토큰(끝자리): ${currentToken?.takeLast(10)}"
                    )

                    if (currentToken != null &&
                        currentToken != originalToken &&
                        currentToken.isNotEmpty()
                    ) {
                        // Already refreshed
                        Log.d(
                            "TokenAuthenticator",
                            "✨ 이미 앞선 스레드에서 토큰을 갱신했습니다! (서버 요청 생략하고 새 토큰 무임승차)"
                        )
                        return@runBlocking currentToken
                    }

                    val refreshToken = tokenManager.refreshToken.firstOrNull()
                    if (refreshToken.isNullOrEmpty()) {
                        Log.e("TokenAuthenticator", "❌ Refresh Token이 존재하지 않아 강제 로그아웃 처리")
                        tokenManager.clearTokens()
                        return@runBlocking null
                    }

                    try {
                        Log.d("TokenAuthenticator", "🚀 서버(/token)로 새 토큰 발급을 요청합니다...")
                        val tokenResponse = tokenRefreshServiceProvider.get().refreshToken(
                            refreshToken
                        )
                        if (tokenResponse.isSuccessful) {
                            val body = tokenResponse.body()
                            if (body != null && body.result != null) {
                                val newAccessToken = body.result.accessToken
                                val newRefreshToken = body.result.refreshToken
                                Log.d("TokenAuthenticator", "🎉 토큰 갱신 성공! 새 토큰을 DataStore에 덮어씁니다.")
                                tokenManager.saveTokens(newAccessToken, newRefreshToken)
                                return@runBlocking newAccessToken
                            } else {
                                Log.e("TokenAuthenticator", "❌ 토큰 갱신 실패 (Body Null) -> 강제 로그아웃")
                                tokenManager.clearTokens()
                            }
                        } else {
                            Log.e(
                                "TokenAuthenticator",
                                "❌ 토큰 갱신 실패 (응답 에러: ${tokenResponse.code()}) -> RefreshToken 만료됨. 강제 로그아웃"
                            )
                            tokenManager.clearTokens()
                        }
                    } catch (e: Exception) {
                        Log.e("TokenAuthenticator", "❌ 토큰 갱신 중 네트워크 예외 발생: ${e.message}")
                        e.printStackTrace()
                        tokenManager.clearTokens()
                    }
                    null
                }
            }

        return newToken?.let {
            response.request
                .newBuilder()
                .header("Authorization", "Bearer $it")
                .build()
        }
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}
