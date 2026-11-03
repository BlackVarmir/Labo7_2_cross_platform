package ua.chnu.recipes.data.repository

import ua.chnu.recipes.domain.repository.OnboardingRepository
import ua.chnu.recipes.settings.AppSettings

/** [OnboardingRepository] backed by multiplatform-settings (through [AppSettings]). */
class OnboardingRepositoryImpl(private val settings: AppSettings) : OnboardingRepository {
    override fun isOnboardingCompleted(): Boolean = settings.onboardingCompleted

    override fun setOnboardingCompleted(completed: Boolean) {
        settings.onboardingCompleted = completed
    }
}
