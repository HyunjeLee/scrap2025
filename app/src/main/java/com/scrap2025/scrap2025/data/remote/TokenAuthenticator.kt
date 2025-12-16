package com.scrap2025.scrap2025.data.remote

import com.scrap2025.scrap2025.data.local.TokenManager
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator
@Inject
constructor(
        private val tokenManager: TokenManager,
        private val authServiceProvider: Provider<AuthService> // Use Provider to avoid circular dependency
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // limit retry count to avoid infinite loop
        if (responseCount(response) >= 3) {
            return null
        }

        val newToken = runBlocking {
            val refreshToken = tokenManager.refreshToken.firstOrNull()
            if (refreshToken.isNullOrEmpty()) {
                return@runBlocking null
            }

            try {
                val tokenResponse = authServiceProvider.get().refreshToken(refreshToken)
                if (tokenResponse.isSuccessful) {
                    val body = tokenResponse.body()
                    if (body != null && body.result != null) {
                        val newAccessToken = body.result.accessToken
                        val newRefreshToken = body.result.refreshToken
                        tokenManager.saveTokens(newAccessToken, newRefreshToken)
                        return@runBlocking newAccessToken
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            null
        }

        return newToken?.let { response.request.newBuilder().header("Authorization", it).build() }
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
