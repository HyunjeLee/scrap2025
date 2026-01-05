package com.scrap2025.scrap2025.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrap2025.scrap2025.data.local.TokenManager
import com.scrap2025.scrap2025.data.remote.auth.social.SocialLoginProvider
import com.scrap2025.scrap2025.data.remote.dto.MyPageResponse
import com.scrap2025.scrap2025.model.enums.SnsType
import com.scrap2025.scrap2025.repository.AuthRepository
import com.scrap2025.scrap2025.repository.CategoryRepository
import com.scrap2025.scrap2025.repository.MyPageRepository
import com.scrap2025.scrap2025.repository.ScrapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel
@Inject
constructor(
    scrapRepository: ScrapRepository,
    categoryRepository: CategoryRepository,
    private val authRepository: AuthRepository,
    private val myPageRepository: MyPageRepository,
    tokenManager: TokenManager,
    private val socialLoginProviders: Map<SnsType, @JvmSuppressWildcards SocialLoginProvider>,
) : ViewModel() {

    // Define UI State
    sealed interface MyPageUiState {
        data object Loading : MyPageUiState
        data class Success(
            val myPageInfo: MyPageResponse,
            val scrapCount: Int,
            val categoryCount: Int,
            val snsType: SnsType
        ) : MyPageUiState
    }

    private val _showWithdrawDialog = MutableStateFlow(false)
    val showWithdrawDialog: StateFlow<Boolean> = _showWithdrawDialog.asStateFlow()

    // Combine flows into a single UI State
    val uiState: StateFlow<MyPageUiState> =
        combine(
            myPageRepository.myPageData,
            scrapRepository.getScrapCount(),
            categoryRepository.getCategoryCount(),
            tokenManager.snsType
        ) { myPageInfo, scrapCount, categoryCount, snsType ->
            if (myPageInfo == null) {
                MyPageUiState.Loading
            } else {
                MyPageUiState.Success(
                    myPageInfo = myPageInfo,
                    scrapCount = scrapCount,
                    categoryCount = categoryCount,
                    snsType = snsType ?: SnsType.NAVER // 기본값 설정
                )
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = MyPageUiState.Loading
            )

    companion object {
        private const val TAG = "MyPageViewModel"
    }

    init {
        fetchMyPageInfo()
    }

    fun fetchMyPageInfo() {
        viewModelScope.launch { myPageRepository.invokeMyPageSync() }
    }

    fun showWithdrawalDialog() {
        _showWithdrawDialog.value = true
    }

    fun dismissWithdrawalDialog() {
        _showWithdrawDialog.value = false
    }

    fun logout(
        snsType: SnsType,
        socialLogoutCallback: suspend (SocialLoginProvider) -> Result<Unit>
    ) {
        viewModelScope.launch {
            // 소셜 로그아웃
            val socialResult = requestSocialLogout(snsType, socialLogoutCallback)

            socialResult
                .onSuccess {
                    Log.d(TAG, "소셜 로그아웃 성공")
                    // 서버 로그아웃
                    requestServerLogout()
                }
                .onFailure { exception ->
                    val errorMsg = "소셜 로그아웃 실패: ${exception.message}"
//                    _uiState.value = uiState.Error(errorMsg)
                    Log.e(TAG, errorMsg)
                }
        }
    }

    fun withdraw(
        snsType: SnsType,
        socialWithdrawCallback: suspend (SocialLoginProvider) -> Result<Unit>
    ) {
        viewModelScope.launch {
            // 소셜 연동 해제 (회원 탈퇴)
            val socialResult = requestSocialWithdraw(snsType, socialWithdrawCallback)

            socialResult
                .onSuccess {
                    Log.d(TAG, "소셜 연동해제 성공")
                    // 서버 회원 탈퇴
                    requestServerWithdraw()
                }
                .onFailure { exception ->
                    val errorMsg = "소셜 연동해제 실패: ${exception.message}"
//                    _uiState.value = uiState.Error(errorMsg)
                    Log.e(TAG, errorMsg)
                }
        }
    }

    private suspend fun requestSocialLogout(
        snsType: SnsType,
        callback: suspend (SocialLoginProvider) -> Result<Unit>
    ): Result<Unit> {
        val provider =
            socialLoginProviders[snsType]
                ?: return Result.failure(Exception("소셜 로그인 프로바이더를 찾을 수 없습니다."))

        return callback(provider)
    }

    private suspend fun requestServerLogout() {
        authRepository.logoutToServer()
            .onSuccess {
//                _uiState.value = LoginUiState.Success
                Log.d(TAG, "서버 로그아웃 성공")
            }
            .onFailure { exception ->
                val errorMsg = "서버 로그아웃 실패: ${exception.message}"
//                _uiState.value = LoginUiState.Error(errorMsg)
                Log.e(TAG, errorMsg)
            }
    }

    private suspend fun requestSocialWithdraw(
        snsType: SnsType,
        callback: suspend (SocialLoginProvider) -> Result<Unit>
    ): Result<Unit> {
        val provider =
            socialLoginProviders[snsType]
                ?: return Result.failure(Exception("소셜 로그인 프로바이더를 찾을 수 없습니다."))

        return callback(provider)
    }

    private suspend fun requestServerWithdraw() {
        authRepository.withdrawToServer()
            .onSuccess {
//                _uiState.value = LoginUiState.Success
                Log.d(TAG, "서버 회원탈퇴 성공")
            }
            .onFailure { exception ->
                val errorMsg = "서버 회원탈퇴 실패: ${exception.message}"
//                _uiState.value = LoginUiState.Error(errorMsg)
                Log.e(TAG, errorMsg)
            }
    }
}
