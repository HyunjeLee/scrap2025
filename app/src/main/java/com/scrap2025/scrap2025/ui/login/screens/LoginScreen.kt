package com.scrap2025.scrap2025.ui.login.screens

import androidx.activity.compose.BackHandler
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.scrap2025.scrap2025.BuildConfig
import com.scrap2025.scrap2025.ui.common.components.ErrorScreen
import com.scrap2025.scrap2025.ui.common.components.LoadingScreen
import com.scrap2025.scrap2025.ui.common.utils.BackPressToExitHandler
import com.scrap2025.scrap2025.ui.login.components.LoginScreenContent
import com.scrap2025.scrap2025.viewmodel.LoginUiState
import com.scrap2025.scrap2025.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    navigateToMain: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState == LoginUiState.Success) {
            navigateToMain()
        }
    }

    if (BuildConfig.FLAVOR == "prod") {
        ProdLoginScreen(
            uiState = uiState,
            viewModel = viewModel,
            modifier = modifier,
            context = context
        )
    } else {
        DevLoginScreen(
            uiState = uiState,
            viewModel = viewModel,
            modifier = modifier,
            context = context
        )
    }
}

@Composable
private fun DevLoginScreen(
    uiState: LoginUiState,
    viewModel: LoginViewModel,
    modifier: Modifier,
    context: android.content.Context
) {
    // 에러 상태에서는 뒤로가기로 Idle 복귀, 그 외에는 앱 종료 핸들러
    if (uiState is LoginUiState.Error) {
        BackHandler { viewModel.resetError() }
    } else {
        BackPressToExitHandler()
    }

    when (uiState) {
        LoginUiState.Idle -> {
            LoginScreenContent(
                onLoginClick = { snsType ->
                    viewModel.login(snsType) { provider -> provider.login(context) }
                },
                onTestLogin = { viewModel.loginWithTestToken() },
                modifier = modifier
            )
        }
        LoginUiState.Loading, LoginUiState.Success -> {
            LoadingScreen("로그인 중 ...")
        }
        is LoginUiState.Error -> {
            ErrorScreen(
                errorText = uiState.message,
                onRetry = { viewModel.resetError() }
            )
        }
    }
}

@Composable
private fun ProdLoginScreen(
    uiState: LoginUiState,
    viewModel: LoginViewModel,
    modifier: Modifier,
    context: android.content.Context
) {
    val snackbarHostState = remember { SnackbarHostState() }

    BackPressToExitHandler()

    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Error) {
            snackbarHostState.showSnackbar("로그인에 실패했습니다. 다시 시도해 주세요.")
            viewModel.resetError()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(snackbarData = data)
            }
        }
    ) { _ ->
        when (uiState) {
            LoginUiState.Idle, is LoginUiState.Error -> {
                LoginScreenContent(
                    onLoginClick = { snsType ->
                        viewModel.login(snsType) { provider -> provider.login(context) }
                    },
                    onTestLogin = { viewModel.loginWithTestToken() },
                    modifier = modifier
                )
            }
            LoginUiState.Loading, LoginUiState.Success -> {
                LoadingScreen("로그인 중 ...")
            }
        }
    }
}
