package com.scrap2025.scrap2025.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrap2025.scrap2025.data.remote.auth.social.SocialLoginProvider
import com.scrap2025.scrap2025.model.enums.SnsType
import com.scrap2025.scrap2025.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

@HiltViewModel
class LoginViewModel
@Inject
constructor(
    private val authRepository: AuthRepository,
    private val socialLoginProviders: Map<SnsType, @JvmSuppressWildcards SocialLoginProvider>
) : ViewModel() {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "LoginViewModel"
        private const val TEST_ACCESS_TOKEN =
            "eyJhbGciOiJIUzI1NiJ9.eyJ0eXBlIjoiQUNDRVNTIiwic3ViIjoiYWI1MGU1YjctZWEwMC00Mzc4LWE0ZTMtMjM2YmI3OWU0OTdlIiwiaWF0IjoxNzY3NTA1MzI5LCJleHAiOjE3ODMwNTczMjl9.p4oczlS6KG5OZODxtjgb5O_iYCIDhSu1qqT1A4EeLrc"
        private const val TEST_REFRESH_TOKEN =
            "refreshToken: eyJhbGciOiJIUzI1NiJ9.eyJ0eXBlIjoiUkVGUkVTSCIsInN1YiI6ImFiNTBlNWI3LWVhMDAtNDM3OC1hNGUzLTIzNmJiNzllNDk3ZSIsImlhdCI6MTc2NzUwNTMzMCwiZXhwIjoxNzk5MDQxMzMwLCJqdGkiOiI3NDEyNTA4MTEyNTU3NDcxNjYwIn0.rsw6Rb6g8nwshAXYuRao7EYphJ6uYx5B-hSmPTumAIs"
    }

    fun loginWithTestToken() {
        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            val result = authRepository.testLogin(TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN)

            result
                .onSuccess {
                    _uiState.value = LoginUiState.Success
                    Log.d(TAG, "테스트 로그인 성공")
                }
                .onFailure { exception ->
                    val errorMsg = "테스트 로그인 실패: ${exception.message}"
                    _uiState.value = LoginUiState.Error(errorMsg)
                    Log.e(TAG, errorMsg)
                }

        }
    }

    fun login(
        snsType: SnsType,
        socialLoginCallback: suspend (SocialLoginProvider) -> Result<String>
    ) {
        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            // 소셜 SDK 로그인
            val socialResult = requestSocialLogin(snsType, socialLoginCallback)

            socialResult
                .onSuccess { socialToken ->
                    Log.d(TAG, "소셜 로그인 성공")
                    // 서버 로그인
                    requestServerLogin(snsType, socialToken)
                }
                .onFailure { exception ->
                    val errorMsg = "소셜 로그인 실패: ${exception.message}"
                    _uiState.value = LoginUiState.Error(errorMsg)
                    Log.e(TAG, errorMsg)
                }
        }
    }

    private suspend fun requestSocialLogin(
        snsType: SnsType,
        callback: suspend (SocialLoginProvider) -> Result<String>
    ): Result<String> {
        val provider = socialLoginProviders[snsType]
            ?: return Result.failure(Exception("소셜 로그인 프로바이더를 찾을 수 없습니다."))

        return callback(provider)
    }

    private suspend fun requestServerLogin(snsType: SnsType, socialToken: String) {
        Log.d(TAG, "${snsType.name} 로그인 성공 - 토큰 획득")

        // 서버 로그인 시도
        val serverResult = authRepository.loginToServer(snsType, socialToken)

        serverResult
            .onSuccess {
                _uiState.value = LoginUiState.Success
                Log.d(TAG, "서버 로그인 성공")
            }
            .onFailure { exception ->
                val errorMsg = "서버 로그인 실패: ${exception.message}"
                _uiState.value = LoginUiState.Error(errorMsg)
                Log.e(TAG, errorMsg)
            }
    }
}
