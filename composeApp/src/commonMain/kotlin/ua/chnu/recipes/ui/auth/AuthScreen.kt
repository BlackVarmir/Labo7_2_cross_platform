package ua.chnu.recipes.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import ua.chnu.recipes.domain.model.AuthFailure
import ua.chnu.recipes.resources.Res
import ua.chnu.recipes.resources.auth_error_password_empty
import ua.chnu.recipes.resources.auth_error_invalid_credentials
import ua.chnu.recipes.resources.auth_error_no_connection
import ua.chnu.recipes.resources.auth_error_password_short
import ua.chnu.recipes.resources.auth_error_server
import ua.chnu.recipes.resources.auth_error_unknown
import ua.chnu.recipes.resources.auth_error_username_chars
import ua.chnu.recipes.resources.auth_error_username_empty
import ua.chnu.recipes.resources.auth_error_username_short
import ua.chnu.recipes.resources.auth_hide_password
import ua.chnu.recipes.resources.auth_password
import ua.chnu.recipes.resources.auth_show_password
import ua.chnu.recipes.resources.auth_sign_in
import ua.chnu.recipes.resources.auth_subtitle
import ua.chnu.recipes.resources.auth_title
import ua.chnu.recipes.resources.auth_username
import ua.chnu.recipes.ui.adaptive.CenteredScrollColumn
import ua.chnu.recipes.ui.adaptive.WindowWidth
import ua.chnu.recipes.ui.adaptive.rememberWindowSize
import ua.chnu.recipes.ui.auth.AuthContract.Effect
import ua.chnu.recipes.ui.auth.AuthContract.Intent

/**
 * Sign-in screen (MVI). Keyboard actions:
 *  - username field: IME action "Next" moves the focus to the password field,
 *  - password field: IME action "Done" signs the user in (same as the "Sign in" button).
 */
@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onSignedIn: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current
    val currentOnSignedIn by rememberUpdatedState(onSignedIn)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Messages are resolved here, in the composition, so that they follow the language chosen in Settings.
    val errorMessages = mapOf(
        AuthFailure.InvalidCredentials to stringResource(Res.string.auth_error_invalid_credentials),
        AuthFailure.NoConnection to stringResource(Res.string.auth_error_no_connection),
        AuthFailure.Server to stringResource(Res.string.auth_error_server),
        AuthFailure.Unknown to stringResource(Res.string.auth_error_unknown),
    )
    val currentErrorMessages by rememberUpdatedState(errorMessages)

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                Effect.NavigateToHome -> currentOnSignedIn()
                is Effect.ShowError -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    // launched separately: showSnackbar suspends until the snackbar disappears
                    scope.launch { snackbarHostState.showSnackbar(currentErrorMessages.getValue(effect.failure)) }
                }
            }
        }
    }

    val signIn = {
        focusManager.clearFocus()
        viewModel.onIntent(Intent.SignInClicked)
    }

    val windowSize = rememberWindowSize()
    val sideBySide = windowSize.width == WindowWidth.Expanded || windowSize.isLandscapeCompactHeight

    // Header (title + subtitle) and the form are separate so that every window size class can arrange them differently.
    val form: @Composable () -> Unit = {
        OutlinedTextField(
            value = state.username,
            onValueChange = { viewModel.onIntent(Intent.UsernameChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.auth_username)) },
            singleLine = true,
            isError = state.usernameError != null,
            supportingText = state.usernameError?.let { { Text(it.message()) } },
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.onIntent(Intent.PasswordChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.auth_password)) },
            singleLine = true,
            isError = state.passwordError != null,
            supportingText = state.passwordError?.let { { Text(it.message()) } },
            visualTransformation = if (state.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { viewModel.onIntent(Intent.TogglePasswordVisibility) }) {
                    Text(
                        stringResource(
                            if (state.passwordVisible) Res.string.auth_hide_password else Res.string.auth_show_password
                        ),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { signIn() }),
        )
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { signIn() },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Text(stringResource(Res.string.auth_sign_in), style = MaterialTheme.typography.titleMedium)
            }
        }
    }

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        when {
            // Expanded window (desktop, large tablet) or phone in landscape: brand panel on the left, form on the right.
            sideBySide -> Row(Modifier.fillMaxSize().safeDrawingPadding().imePadding()) {
                Box(
                    Modifier.weight(1f).fillMaxHeight().background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(Modifier.widthIn(max = 420.dp).padding(32.dp)) { AuthHeader() }
                }
                CenteredScrollColumn(
                    Modifier.weight(1f).fillMaxHeight(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Column(Modifier.widthIn(max = 420.dp)) { form() }
                }
            }
            // Medium window (tablet in portrait): the same column, centered and not wider than 480dp.
            windowSize.width == WindowWidth.Medium -> CenteredScrollColumn(
                Modifier.fillMaxSize().safeDrawingPadding().imePadding(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(Modifier.widthIn(max = 480.dp)) {
                    AuthHeader()
                    Spacer(Modifier.height(32.dp))
                    form()
                }
            }
            // Compact window (phone in portrait): full-width column.
            else -> CenteredScrollColumn(
                Modifier.fillMaxSize().safeDrawingPadding().imePadding(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 32.dp),
            ) {
                AuthHeader()
                Spacer(Modifier.height(32.dp))
                form()
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).safeDrawingPadding().imePadding().padding(16.dp),
        )
    }
}

@Composable
private fun AuthHeader() {
    Text(
        stringResource(Res.string.auth_title),
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onBackground,
    )
    Spacer(Modifier.height(8.dp))
    Text(
        stringResource(Res.string.auth_subtitle),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun UsernameError.message(): String = when (this) {
    UsernameError.Empty -> stringResource(Res.string.auth_error_username_empty)
    UsernameError.TooShort -> stringResource(Res.string.auth_error_username_short, AuthValidator.MIN_USERNAME_LENGTH)
    UsernameError.InvalidChars -> stringResource(Res.string.auth_error_username_chars)
}

@Composable
private fun PasswordError.message(): String = when (this) {
    PasswordError.Empty -> stringResource(Res.string.auth_error_password_empty)
    PasswordError.TooShort -> stringResource(Res.string.auth_error_password_short, AuthValidator.MIN_PASSWORD_LENGTH)
}
