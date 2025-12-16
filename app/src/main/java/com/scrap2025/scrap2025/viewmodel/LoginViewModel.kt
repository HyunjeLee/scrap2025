package com.scrap2025.scrap2025.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.navercorp.nid.NidOAuth
import com.navercorp.nid.oauth.util.NidOAuthCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import com.scrap2025.scrap2025.repository.AuthRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _loginErrorMessage = MutableStateFlow<String?>(null)
    val loginErrorMessage: StateFlow<String?> = _loginErrorMessage.asStateFlow()

    companion object {
        private const val TAG = "LoginViewModel"
    }

    fun loginWithNaver(context: Context) {

        val nidOAuthCallback = object : NidOAuthCallback {
            override fun onSuccess() {
                // 네이버 로그인 성공 시 액세스 토큰 가져오기
                val accessToken = NidOAuth.getAccessToken()
                val refreshToken = NidOAuth.getRefreshToken()
                Log.d(TAG, "네이버 로그인 성공")
                Log.d(TAG, "액세스 토큰: $accessToken")
                Log.d(TAG, "리프레시 토큰: $refreshToken")

                // 서버 로그인 시도
                if (accessToken != null) {
                    viewModelScope.launch {
                        val result = authRepository.loginWithNaver(accessToken)
                        if (result.isSuccess) {
                            _isLoggedIn.value = true
                            Log.d(TAG, "서버 로그인 성공")
                        } else {
                            _loginErrorMessage.value = "서버 로그인 실패"
                            Log.e(TAG, "서버 로그인 실패: ${result.exceptionOrNull()?.message}")
                        }
                    }
                }
            }

            override fun onFailure(errorCode: String, errorDesc: String) {
                Log.e(TAG, "네이버 로그인 실패 - Error Code: $errorCode, Error Description: $errorDesc")
                _loginErrorMessage.value = "로그인 실패: $errorDesc"
                _isLoggedIn.value = false
            }
        }

        // 네이버 로그인 인증 시작
        NidOAuth.requestLogin(context, nidOAuthCallback)
    }

    fun logout() {
        val nidOAuthCallback = object : NidOAuthCallback {
            override fun onSuccess() {
                // 클라이언트에서 토큰 삭제 성공
                _isLoggedIn.value = false
                Log.d(TAG, "네이버 로그아웃 성공")
                Log.d(TAG, "액세스 토큰: ${NidOAuth.getAccessToken()}")
                Log.d(TAG, "리프레시 토큰: ${NidOAuth.getRefreshToken()}")
            }

            override fun onFailure(errorCode: String, errorDesc: String) {
                // 클라이언트 토큰 삭제 실패
                Log.e(TAG, "로그아웃 실패 - Error Code: $errorCode, Error Description: $errorDesc")
            }
        }

        NidOAuth.logout(nidOAuthCallback)
    }

    fun clearErrorMessage() {
        _loginErrorMessage.value = null
    }
}
