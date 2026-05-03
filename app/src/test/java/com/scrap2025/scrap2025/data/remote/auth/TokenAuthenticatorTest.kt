package com.scrap2025.scrap2025.data.remote.auth

import com.scrap2025.scrap2025.data.local.TokenManager
import com.scrap2025.scrap2025.data.remote.api.TokenRefreshService
import com.scrap2025.scrap2025.data.remote.dto.BaseResponse
import com.scrap2025.scrap2025.data.remote.dto.LoginResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import javax.inject.Provider
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import retrofit2.Response as RetrofitResponse

class TokenAuthenticatorTest {

    private lateinit var tokenManager: TokenManager
    private lateinit var tokenRefreshService: TokenRefreshService
    private lateinit var tokenRefreshServiceProvider: Provider<TokenRefreshService>
    private lateinit var authenticator: TokenAuthenticator

    private val accessTokenFlow = MutableStateFlow<String?>("old_access_token")
    private val refreshTokenFlow = MutableStateFlow<String?>("valid_refresh_token")

    @Before
    fun setup() {
        tokenManager = mockk(relaxed = true)
        tokenRefreshService = mockk()
        tokenRefreshServiceProvider = mockk()

        every { tokenRefreshServiceProvider.get() } returns tokenRefreshService

        every { tokenManager.accessToken } returns accessTokenFlow
        every { tokenManager.refreshToken } returns refreshTokenFlow

        coEvery { tokenManager.saveTokens(any(), any()) } answers {
            accessTokenFlow.value = firstArg()
            refreshTokenFlow.value = secondArg()
        }

        coEvery { tokenManager.clearTokens() } answers {
            accessTokenFlow.value = ""
            refreshTokenFlow.value = ""
        }

        authenticator = TokenAuthenticator(tokenManager, tokenRefreshServiceProvider)
    }

    private fun createMockResponse(
        token: String = "old_access_token",
        retryCount: Int = 1
    ): Response {
        val request = Request.Builder()
            .url("https://api.teamscrap.co.kr/test")
            .header("Authorization", "Bearer $token")
            .build()

        var response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .build()

        // Mocking retry count chaining if needed, but for our test retry count is 1 for concurrent requests
        return response
    }

    @Test
    fun `when concurrent 401 requests arrive, only one refresh token API is called`() = runTest {
        // Given
        val newAccessToken = "new_access_token"
        val newRefreshToken = "new_refresh_token"

        val successResponse = RetrofitResponse.success(
            BaseResponse(
                code = "200",
                message = "Success",
                result = LoginResponse(
                    accessToken = newAccessToken,
                    refreshToken = newRefreshToken
                )
            )
        )

        // Mock tokenRefreshService to delay slightly to simulate network and test concurrent locks
        coEvery { tokenRefreshService.refreshToken("valid_refresh_token") } coAnswers {
            delay(100) // Simulate network delay
            successResponse
        }

        val request1 = createMockResponse()
        val request2 = createMockResponse()
        val request3 = createMockResponse()

        // When - simulate 3 concurrent 401s
        val deferred1 = async { authenticator.authenticate(null, request1) }
        val deferred2 = async { authenticator.authenticate(null, request2) }
        val deferred3 = async { authenticator.authenticate(null, request3) }

        val results = awaitAll(deferred1, deferred2, deferred3)

        // Then
        // The TokenRefreshService should only be called EXACTLY once due to Mutex and state checking
        coVerify(exactly = 1) { tokenRefreshService.refreshToken(any()) }

        // All three requests should return a new Request object with the NEW access token
        results.forEach { newRequest ->
            assertNotNull(newRequest)
            assertEquals("Bearer $newAccessToken", newRequest!!.header("Authorization"))
        }

        // DataStore should have the new tokens
        assertEquals(newAccessToken, accessTokenFlow.value)
    }

    @Test
    fun `when refresh token is also expired, it should clear tokens and return null`() = runTest {
        // Given
        val errorResponse = RetrofitResponse.error<BaseResponse<LoginResponse>>(
            401,
            okhttp3.ResponseBody.create(null, "")
        )

        coEvery { tokenRefreshService.refreshToken("valid_refresh_token") } returns errorResponse

        val response = createMockResponse()

        // When
        val result = authenticator.authenticate(null, response)

        // Then
        coVerify(exactly = 1) { tokenRefreshService.refreshToken("valid_refresh_token") }
        coVerify(exactly = 1) { tokenManager.clearTokens() }

        // It should return null so OkHttp stops retrying
        assertNull(result)
        assertEquals("", accessTokenFlow.value)
    }
}
