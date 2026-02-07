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
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MyPageViewModel
@Inject
constructor(
    scrapRepository: ScrapRepository,
    categoryRepository: CategoryRepository,
    private val authRepository: AuthRepository,
    private val myPageRepository: MyPageRepository,
    tokenManager: TokenManager,
    private val socialLoginProviders: Map<SnsType, @JvmSuppressWildcards SocialLoginProvider>
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

    sealed interface MyPageUiEvent {
        data class ShowToast(val message: String) : MyPageUiEvent
    }

    private val _event = MutableSharedFlow<MyPageUiEvent>()
    val event: SharedFlow<MyPageUiEvent> = _event.asSharedFlow()

    private val _showWithdrawDialog = MutableStateFlow(false)
    val showWithdrawDialog: StateFlow<Boolean> = _showWithdrawDialog.asStateFlow()

    private val _showHelpCenterDialog = MutableStateFlow(false)
    val showHelpCenterDialog: StateFlow<Boolean> = _showHelpCenterDialog.asStateFlow()

    // Combine flows into a single UI State
    val uiState: StateFlow<MyPageUiState> =
        combine(
            myPageRepository.myPageData,
            tokenManager.snsType
        ) { myPageInfo, snsType ->
            if (myPageInfo == null) {
                MyPageUiState.Loading
            } else {
                MyPageUiState.Success(
                    myPageInfo = myPageInfo,
                    scrapCount = myPageInfo.statistics.totalScrap,
                    categoryCount = myPageInfo.statistics.totalCategory,
                    snsType = snsType ?: SnsType.NAVER // 기본값 설정
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MyPageUiState.Loading
        )

    companion object {
        private const val TAG = "MyPageViewModel"
    }

    init {
        viewModelScope.launch {
            merge(
                scrapRepository.refreshEvent,
                categoryRepository.refreshEvent
            ).collect {
                // 어떤 신호든 들어오면 마이페이지 서버 정보 새로고침
                myPageRepository.fetchMyPage()
            }
        }
    }

    fun showHelpCenterDialog() {
        _showHelpCenterDialog.value = true
    }

    fun dismissHelpCenterDialog() {
        _showHelpCenterDialog.value = false
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
                }.onFailure { exception ->
                    val errorMsg = "소셜 로그아웃 실패: ${exception.message}"
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
                }.onFailure { exception ->
                    val errorMsg = "소셜 연동해제 실패: ${exception.message}"
                    Log.e(TAG, errorMsg)
                }
        }
    }

    private suspend fun requestSocialLogout(
        snsType: SnsType,
        callback: suspend (SocialLoginProvider) -> Result<Unit>
    ): Result<Unit> {
        if (snsType == SnsType.TEST) {
            return Result.success(Unit)
        }

        val provider =
            socialLoginProviders[snsType]
                ?: return Result.failure(Exception("소셜 로그인 프로바이더를 찾을 수 없습니다."))

        return callback(provider)
    }

    private suspend fun requestServerLogout() {
        authRepository
            .logoutToServer()
            .onSuccess {
                Log.d(TAG, "서버 로그아웃 성공")
            }.onFailure { exception ->
                val errorMsg = "서버 로그아웃 실패: ${exception.message}"
                Log.e(TAG, errorMsg)
            }
    }

    private suspend fun requestSocialWithdraw(
        snsType: SnsType,
        callback: suspend (SocialLoginProvider) -> Result<Unit>
    ): Result<Unit> {
        if (snsType == SnsType.TEST) {
            _event.emit(MyPageUiEvent.ShowToast("withdrawal is not supported for test logins"))
            return Result.failure(Exception("테스트 로그인은 회원탈퇴 기능이 지원되지 않습니다."))
        }

        val provider =
            socialLoginProviders[snsType]
                ?: return Result.failure(Exception("소셜 로그인 프로바이더를 찾을 수 없습니다."))

        return callback(provider)
    }

    private suspend fun requestServerWithdraw() {
        authRepository
            .withdrawToServer()
            .onSuccess {
                Log.d(TAG, "서버 회원탈퇴 성공")
            }.onFailure { exception ->
                val errorMsg = "서버 회원탈퇴 실패: ${exception.message}"
                Log.e(TAG, errorMsg)
            }
    }
}
