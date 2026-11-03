package ua.chnu.recipes.ui.auth

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ua.chnu.recipes.domain.model.AuthException
import ua.chnu.recipes.domain.model.AuthFailure
import ua.chnu.recipes.domain.usecase.LoginUseCase
import ua.chnu.recipes.ui.auth.AuthContract.Effect
import ua.chnu.recipes.ui.auth.AuthContract.Intent
import ua.chnu.recipes.ui.auth.AuthContract.State
import ua.chnu.recipes.ui.mvi.MviViewModel

class AuthViewModel(
    private val login: LoginUseCase,
) : MviViewModel<State, Intent, Effect>(State()) {

    override fun onIntent(intent: Intent) {
        when (intent) {
            is Intent.UsernameChanged -> setState {
                copy(
                    username = intent.value,
                    usernameError = if (submitted) AuthValidator.validateUsername(intent.value) else null,
                )
            }
            is Intent.PasswordChanged -> setState {
                copy(
                    password = intent.value,
                    passwordError = if (submitted) AuthValidator.validatePassword(intent.value) else null,
                )
            }
            Intent.TogglePasswordVisibility -> setState { copy(passwordVisible = !passwordVisible) }
            Intent.SignInClicked -> signIn()
        }
    }

    private fun signIn() {
        val current = state.value
        if (current.isLoading) return
        val usernameError = AuthValidator.validateUsername(current.username)
        val passwordError = AuthValidator.validatePassword(current.password)
        setState { copy(submitted = true, usernameError = usernameError, passwordError = passwordError) }
        if (usernameError != null || passwordError != null) return

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = login(current.username, current.password)
            setState { copy(isLoading = false) }
            result
                .onSuccess { sendEffect(Effect.NavigateToHome) }
                .onFailure { error ->
                    sendEffect(Effect.ShowError((error as? AuthException)?.failure ?: AuthFailure.Unknown))
                }
        }
    }
}
