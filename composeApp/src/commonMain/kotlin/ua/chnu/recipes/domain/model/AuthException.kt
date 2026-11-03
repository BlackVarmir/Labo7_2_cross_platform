package ua.chnu.recipes.domain.model

/** Why an authorization request failed. The UI maps every value to a localized message. */
enum class AuthFailure {
    /** Wrong username or password. */
    InvalidCredentials,

    /** No internet, DNS problem or a timeout. */
    NoConnection,

    /** The server answered with 5xx. */
    Server,

    /** Everything else. */
    Unknown,
}

/** Domain error of the authorization use cases; carries the [failure] reason. */
class AuthException(val failure: AuthFailure, cause: Throwable? = null) :
    Exception("Authorization failed: $failure", cause)
