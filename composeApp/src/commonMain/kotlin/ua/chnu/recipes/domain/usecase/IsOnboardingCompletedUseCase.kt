package ua.chnu.recipes.domain.usecase

import ua.chnu.recipes.domain.repository.OnboardingRepository

/** Checks whether the user has already passed the onboarding. */
class IsOnboardingCompletedUseCase(private val repository: OnboardingRepository) {
    operator fun invoke(): Boolean = repository.isOnboardingCompleted()
}
