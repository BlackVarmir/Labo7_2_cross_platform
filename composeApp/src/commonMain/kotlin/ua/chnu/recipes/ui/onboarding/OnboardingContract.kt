package ua.chnu.recipes.ui.onboarding

/** MVI contract of the onboarding screen. */
object OnboardingContract {
    data class State(
        val page: Int = 0,
        val pageCount: Int = 3,
    ) {
        val isLast: Boolean get() = page == pageCount - 1
    }

    sealed interface Intent {
        data class PageChanged(val page: Int) : Intent
        data object NextClicked : Intent
        data object SkipClicked : Intent
    }

    sealed interface Effect {
        data class ScrollToPage(val page: Int) : Effect
        data object NavigateToAuth : Effect
    }
}
