package ua.chnu.recipes.ui.auth

import ua.chnu.recipes.domain.model.AuthFailure

/** MVI contract of the authorization screen. */
object AuthContract {
    data class State(
        val username: String = "",
        val password: String = "",
        val passwordVisible: Boolean = false,
        val usernameError: UsernameError? = null,
        val passwordError: PasswordError? = null,
        /** Errors are shown only after the first sign-in attempt. */
        val submitted: Boolean = false,
        val isLoading: Boolean = false,
    )

    sealed interface Intent {
        data class UsernameChanged(val value: String) : Intent
        data class PasswordChanged(val value: String) : Intent
        data object TogglePasswordVisibility : Intent
        data object SignInClicked : Intent
    }

    sealed interface Effect {
        /** Signed in: open the home screen. */
        data object NavigateToHome : Effect

        /** The sign-in request failed: the screen shows [failure] in a snackbar. */
        data class ShowError(val failure: AuthFailure) : Effect
    }
}
