package ua.chnu.recipes.domain.usecase

import ua.chnu.recipes.domain.repository.OnboardingRepository

/** Saves the result of the onboarding (the user has passed or skipped it). */
class SaveOnboardingResultUseCase(private val repository: OnboardingRepository) {
    operator fun invoke(completed: Boolean = true) = repository.setOnboardingCompleted(completed)
}
