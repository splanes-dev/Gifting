package com.splanes.gifting.ui.feature.authentication

import com.splanes.gifting.domain.common.base.usecase.UseCase
import com.splanes.gifting.domain.feature.user.model.AuthStateValue
import com.splanes.gifting.domain.feature.user.usecase.GetAuthStateUseCase
import com.splanes.gifting.ui.common.uistate.ErrorVisuals
import com.splanes.gifting.ui.common.uistate.LoadingVisuals
import com.splanes.gifting.ui.common.uistate.UiViewModel
import com.splanes.gifting.ui.common.uistate.UiViewModelState
import com.splanes.gifting.ui.feature.authentication.model.OnBoardingUiPage
import com.splanes.gifting.ui.feature.authentication.model.OnBoardingUiPages
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.update

data class AuthUiViewModelState(
    private val error: ErrorVisuals = ErrorVisuals.Empty,
    private val loading: LoadingVisuals = LoadingVisuals.Empty,
    private val isSignedUp: Boolean = false,
    private val email: String = "",
    private val username: String = "",
    private val password: String = "",
    private val autoSignIn: Boolean = false,
    private val onBoardingPages: List<OnBoardingUiPage> = emptyList()
) : UiViewModelState<AuthUiState> {
    override fun toUiState(): AuthUiState =
        when {
            isSignedUp -> {
                AuthUiState.SignIn(
                    error = error,
                    loading = loading,
                    email = email,
                    password = password,
                    autoSignIn = autoSignIn
                )
            }

            onBoardingPages.isEmpty() -> {
                AuthUiState.SignUp(
                    loading = loading,
                    error = error,
                    email = email,
                    password = password,
                    username = username,
                    autoSignIn = autoSignIn
                )
            }

            else -> {
                AuthUiState.SignUpWithOnBoarding(
                    loading = loading,
                    error = error,
                    email = email,
                    password = password,
                    username = username,
                    onBoardingPages = onBoardingPages
                )
            }
        }
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val getAuthState: GetAuthStateUseCase
) : UiViewModel<AuthUiState, AuthUiViewModelState>(
    AuthUiViewModelState(loading = LoadingVisuals(visible = true))
) {

    init {
        launch {
            getAuthState().collect { result ->
                viewModelState.update { state ->
                    when (result) {
                        is UseCase.Failure -> {
                            state.copy(
                                loading = LoadingVisuals(visible = false)
                                // error = ErrorVisuals(TODO)
                            )
                        }

                        is UseCase.Success -> {
                            state.copy(
                                loading = LoadingVisuals(visible = false),
                                isSignedUp = result.data.isSignedUp(),
                                autoSignIn = result.data == AuthStateValue.AutoSignIn,
                                onBoardingPages = if (result.data == AuthStateValue.OnBoarding) {
                                    OnBoardingUiPages().toList()
                                } else {
                                    emptyList()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    fun signIn() {
    }

    fun signUp(email: String, password: String, username: String, remember: Boolean) {
    }

    fun onOnBoardingEnd() {
        viewModelState.update { state ->
            state.copy(onBoardingPages = emptyList())
        }
    }
}