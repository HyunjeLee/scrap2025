package com.scrap2025.scrap2025.ui.login.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.scrap2025.scrap2025.ui.common.utils.BackPressToExitHandler
import com.scrap2025.scrap2025.ui.login.components.LoginScreenContent
import com.scrap2025.scrap2025.viewmodel.LoginViewModel


@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    BackPressToExitHandler()

    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            onLoginClick()
        }
    }

    LoginScreenContent(
        onLoginClick = { viewModel.loginWithNaver(context) },
        modifier = modifier
    )
}