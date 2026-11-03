package ua.chnu.recipes.domain.repository

/** Stores whether the user has already gone through the onboarding. */
interface OnboardingRepository {
    fun isOnboardingCompleted(): Boolean
    fun setOnboardingCompleted(completed: Boolean)
}
