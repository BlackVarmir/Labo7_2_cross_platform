package ua.chnu.recipes.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import org.jetbrains.compose.resources.stringResource
import ua.chnu.recipes.resources.Res
import ua.chnu.recipes.resources.auth_error_password_empty
import ua.chnu.recipes.resources.auth_error_password_short
import ua.chnu.recipes.resources.auth_error_password_weak
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

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                Effect.NavigateToMain -> currentOnSignedIn()
            }
        }
    }

    val signIn = {
        focusManager.clearFocus()
        viewModel.onIntent(Intent.SignInClicked)
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Center,
    ) {
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
        Spacer(Modifier.height(32.dp))

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
    PasswordError.Weak -> stringResource(Res.string.auth_error_password_weak)
}
