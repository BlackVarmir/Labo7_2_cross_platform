package ua.chnu.recipes.ui.onboarding

import ua.chnu.recipes.domain.usecase.SaveOnboardingResultUseCase
import ua.chnu.recipes.ui.mvi.MviViewModel
import ua.chnu.recipes.ui.onboarding.OnboardingContract.Effect
import ua.chnu.recipes.ui.onboarding.OnboardingContract.Intent
import ua.chnu.recipes.ui.onboarding.OnboardingContract.State

class OnboardingViewModel(
    private val saveOnboardingResult: SaveOnboardingResultUseCase,
) : MviViewModel<State, Intent, Effect>(State()) {

    override fun onIntent(intent: Intent) {
        when (intent) {
            is Intent.PageChanged -> setState { copy(page = intent.page) }
            Intent.NextClicked -> {
                val current = state.value
                if (current.isLast) finish() else sendEffect(Effect.ScrollToPage(current.page + 1))
            }
            Intent.SkipClicked -> finish()
        }
    }

    private fun finish() {
        saveOnboardingResult(completed = true)
        sendEffect(Effect.NavigateToAuth)
    }
}
