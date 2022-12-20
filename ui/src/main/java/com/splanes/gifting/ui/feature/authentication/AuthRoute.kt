package com.splanes.gifting.ui.feature.authentication

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun AuthRoute(
    viewModel: AuthViewModel,
    onNavToDashboard: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AuthRoute(
        uiState = uiState,
        onSignIn = viewModel::signIn,
        onSignUp = viewModel::signUp,
        onOnBoardingEnd = viewModel::onOnBoardingEnd
    )
}

@Composable
fun AuthRoute(
    uiState: AuthUiState,
    onSignIn: () -> Unit,
    onSignUp: (String, String, String, Boolean) -> Unit,
    onOnBoardingEnd: () -> Unit
) {
    Crossfade(screenTypeOf(uiState)) { screenType ->
        when (screenType) {
            AuthScreenType.SigningIn -> {
                check(uiState is AuthUiState.SignIn)
                AuthAutoSigningInScreen()
            }
            AuthScreenType.OnBoarding -> {
                check(uiState is AuthUiState.SignUpWithOnBoarding)
                AuthOnBoardingScreen(uiState, onOnBoardingEnd)
            }
            AuthScreenType.SignUp -> {
                check(uiState is AuthUiState.SignUp)
                AuthSignUpScreen(uiState, onSignUp)
            }
            AuthScreenType.SignIn -> {
                AuthSignInScreen()
            }
        }
    }
}

private enum class AuthScreenType {
    SigningIn,
    SignUp,
    SignIn,
    OnBoarding
}

private fun screenTypeOf(uiState: AuthUiState) =
    when (uiState) {
        is AuthUiState.SignIn -> if (uiState.autoSignIn) {
            AuthScreenType.SigningIn
        } else {
            AuthScreenType.SignIn
        }
        is AuthUiState.SignUp -> AuthScreenType.SignUp
        is AuthUiState.SignUpWithOnBoarding -> AuthScreenType.OnBoarding
    }