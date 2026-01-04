package com.scrap2025.scrap2025.ui.login.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
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

    BackPressToExitHandler()

    when (uiState) {
        LoginUiState.Idle -> {
            LoginScreenContent(
                onLoginClick = { snsType ->
                    viewModel.login(snsType) { provider ->
                        provider.login(context)
                    }
                },
                modifier = modifier
            )
        }
        LoginUiState.Loading, LoginUiState.Success  -> {
            LoadingScreen()
        }
        is LoginUiState.Error -> {
            ErrorScreen((uiState as LoginUiState.Error).message)
        }
    }
}
