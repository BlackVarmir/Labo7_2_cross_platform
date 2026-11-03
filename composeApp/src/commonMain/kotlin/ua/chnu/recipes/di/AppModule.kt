package ua.chnu.recipes.di

import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ua.chnu.recipes.data.RecipesApi
import ua.chnu.recipes.data.repository.OnboardingRepositoryImpl
import ua.chnu.recipes.domain.repository.OnboardingRepository
import ua.chnu.recipes.domain.usecase.IsOnboardingCompletedUseCase
import ua.chnu.recipes.domain.usecase.SaveOnboardingResultUseCase
import ua.chnu.recipes.settings.AppSettings
import ua.chnu.recipes.ui.RecipesViewModel
import ua.chnu.recipes.ui.auth.AuthViewModel
import ua.chnu.recipes.ui.main.MainViewModel
import ua.chnu.recipes.ui.onboarding.OnboardingViewModel

/** Koin module with all dependencies of the app. */
val appModule = module {
    // Data
    single { AppSettings() }
    single { RecipesApi() }
    singleOf(::OnboardingRepositoryImpl) bind OnboardingRepository::class

    // Use cases
    factoryOf(::SaveOnboardingResultUseCase)
    factoryOf(::IsOnboardingCompletedUseCase)

    // ViewModels
    viewModelOf(::MainViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::AuthViewModel)
    viewModelOf(::RecipesViewModel)
}
