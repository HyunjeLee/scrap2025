@file:Suppress("NonAsciiCharacters")

package com.scrap2025.scrap2025.data.remote

import com.scrap2025.scrap2025.data.local.TokenManager
import com.scrap2025.scrap2025.data.model.BaseResponse
import com.scrap2025.scrap2025.data.model.LoginResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import javax.inject.Provider
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import retrofit2.Response as RetrofitResponse

class TokenAuthenticatorTest {

    private lateinit var tokenManager: TokenManager
    private lateinit var authService: AuthService
    private lateinit var authServiceProvider: Provider<AuthService>
    private lateinit var authenticator: TokenAuthenticator

    @Before
    fun setup() {
        tokenManager = mockk(relaxed = true)
        authService = mockk()
        authServiceProvider = mockk()

        every { authServiceProvider.get() } returns authService

        authenticator = TokenAuthenticator(tokenManager, authServiceProvider)
    }

    @Test
    fun `토큰 갱신에 성공하면 새로운 토큰을 헤더에 담아 재요청해야 한다`() = runBlocking {
        // Given
        val oldToken = "old_access_token"
        val oldRefreshToken = "old_refresh_token"
        val newAccessToken = "new_access_token"
        val newRefreshToken = "new_refresh_token"

        val originalRequest =
                Request.Builder()
                        .url("https://api.example.com/endpoint")
                        .header("Authorization", oldToken)
                        .build()

        val response =
                Response.Builder()
                        .request(originalRequest)
                        .protocol(Protocol.HTTP_1_1)
                        .code(401)
                        .message("Unauthorized")
                        .build()

        every { tokenManager.refreshToken } returns flowOf(oldRefreshToken)

        val loginResult = LoginResult(newAccessToken, newRefreshToken)
        val baseResponse = BaseResponse("SUCCESS", "Success", loginResult)
        coEvery { authService.refreshToken(oldRefreshToken) } returns
                RetrofitResponse.success(baseResponse)

        // When
        val resultRequest = authenticator.authenticate(null, response)

        // Then
        // Verify saveTokens was called
        coVerify { tokenManager.saveTokens(newAccessToken, newRefreshToken) }

        // Verify new request has new token
        assertEquals(newAccessToken, resultRequest?.header("Authorization"))
    }

    @Test
    fun `리프레시 토큰이 없으면 갱신을 시도하지 않고 null을 반환해야 한다`() = runBlocking {
        // Given
        every { tokenManager.refreshToken } returns flowOf(null)

        val response =
                Response.Builder()
                        .request(Request.Builder().url("https://a.com").build())
                        .protocol(Protocol.HTTP_1_1)
                        .code(401)
                        .message("Unauthorized")
                        .build()

        // When
        val result = authenticator.authenticate(null, response)

        // Then
        assertNull(result)
    }

    @Test
    fun `refresh API 요청 실패 시 null을 반환해야 한다`() = runBlocking {
        // Given
        val oldRefreshToken = "old_refresh_token"
        every { tokenManager.refreshToken } returns flowOf(oldRefreshToken)

        // Mock 400 error from server
        coEvery { authService.refreshToken(oldRefreshToken) } returns
                RetrofitResponse.error(400, "".toResponseBody(null))

        val response =
                Response.Builder()
                        .request(Request.Builder().url("https://a.com").build())
                        .protocol(Protocol.HTTP_1_1)
                        .code(401)
                        .message("Unauthorized")
                        .build()

        // When
        val result = authenticator.authenticate(null, response)

        // Then
        assertNull(result)
    }

    @Test
    fun `3번 이상의 API 요청 시 null을 반환해야한다`() {
        // Given
        val request = Request.Builder().url("https://a.com").build()
        val response =
                Response.Builder()
                        .request(request)
                        .protocol(Protocol.HTTP_1_1)
                        .code(401)
                        .message("Unauthorized")
                        .build()

        // response count 3: response -> prior -> prior -> null
        val response2 = response.newBuilder().build()
        val response1 = response.newBuilder().priorResponse(response2).build()
        val finalResponse = response.newBuilder().priorResponse(response1).build()

        // When
        val result = authenticator.authenticate(null, finalResponse)

        // Then
        assertNull(result)
    }
}
