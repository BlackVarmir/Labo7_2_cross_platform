package ua.chnu.recipes.ui.auth

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
        data object NavigateToMain : Effect
    }
}
