package ua.chnu.recipes.ui.auth

enum class UsernameError { Empty, TooShort, InvalidChars }

enum class PasswordError { Empty, TooShort }

/** Validation rules of the sign-in form. Returns null when the value is valid. */
object AuthValidator {
    const val MIN_USERNAME_LENGTH = 3
    const val MIN_PASSWORD_LENGTH = 8

    fun validateUsername(username: String): UsernameError? = when {
        username.isBlank() -> UsernameError.Empty
        username.length < MIN_USERNAME_LENGTH -> UsernameError.TooShort
        !username.all { it.isLetterOrDigit() || it == '.' || it == '_' } -> UsernameError.InvalidChars
        else -> null
    }

    fun validatePassword(password: String): PasswordError? = when {
        password.isEmpty() -> PasswordError.Empty
        password.length < MIN_PASSWORD_LENGTH -> PasswordError.TooShort
        else -> null
    }
}
