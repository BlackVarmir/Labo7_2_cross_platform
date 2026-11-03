# Recipes — Kotlin Multiplatform REST API client (Лабораторна робота №4–5)

Compose Multiplatform app (Android, iOS, Desktop) that consumes the open
[dummyjson](https://dummyjson.com/docs/recipes) **recipes** API.

## What is implemented (lab checklist)

| # | Requirement | Where |
|---|-------------|-------|
| 1 | New KMP project | `composeApp` module (Android + iOS + Desktop targets) |
| 2 | UI designed with an AI tool | Claude Design (Claude) mockups: onboarding ×3, recipe list, recipe details, settings |
| 3 | Custom theme (colors, fonts) | `ui/theme/` — basil-green/tangerine palette (light + dark), Manrope font in `composeResources/font` |
| 4 | Kermit + multiplatform-settings | `Logger.withTag(...)` in `data/`, `ui/`, `App.kt`; `settings/AppSettings.kt` stores language, theme and "onboarding completed" |
| 5 | Onboarding with `Pager` (3–5 slides) | `ui/onboarding/OnboardingScreen.kt` — `HorizontalPager`, 3 slides, dots, Skip / Next / Get started |
| 6 | Localization: Ukrainian + English | `composeResources/values/strings.xml`, `values-uk/strings.xml`; language switch in Settings (`platform/LocalAppLocale`) |

REST API client: Ktor (`data/RecipesApi.kt`) → `GET /recipes`, `/recipes/search?q=`, `/recipes/{id}`.

## Lab 5 — navigation, DI, MVI, authorization

| # | Requirement | Where |
|---|-------------|-------|
| 1 | Navigation 3 | `navigation/Routes.kt`, `NavDisplay` in `App.kt` |
| 2 | Koin | `di/AppModule.kt`, `KoinApplication` in `App.kt` |
| 3 | multiplatform-settings | `settings/AppSettings.kt` (used by `OnboardingRepositoryImpl`) |
| 4 | Onboarding MVI + use case | `ui/onboarding/*Contract/ViewModel`, `SaveOnboardingResultUseCase` -> `OnboardingRepository` -> settings |
| 5-6 | Auth screen + onboarding -> auth transition | `ui/auth/AuthScreen.kt`, `OnboardingRoute -> AuthRoute` |
| 7-8 | Main ViewModel + start screen by onboarding result | `ui/main/MainViewModel.kt`, `IsOnboardingCompletedUseCase` |
| 9 | Auth design, MVI, validation, keyboard actions | `ui/auth/` (`AuthValidator`, Next -> password, Done -> Sign in) |

Flow: first launch `Onboarding -> Auth -> Recipes`; after onboarding is passed the app opens `Recipes` directly.

## Run

* **Desktop:** `./gradlew :composeApp:run`
* **Android:** open the project in Android Studio and run the `composeApp` configuration
  (or `./gradlew :composeApp:installDebug` with a device/emulator attached).
* **iOS:** the shared framework (`ComposeApp`) and `MainViewController()` are ready.
  Create the Xcode host app with the KMP wizard (<https://kmp.jetbrains.com>) and call
  `MainViewControllerKt.MainViewController()` from SwiftUI, or copy `iosApp/` from a wizard project.

## Libraries

Compose Multiplatform 1.10.3 · Kotlin 2.2.21 · Ktor 3.1 · kotlinx.serialization · Coil 3 ·
**Kermit 2.0.6** (logging) · **multiplatform-settings 1.3.0** (user settings) · **Navigation 3 1.1.1** · **Koin 4.1.1**.

Font: [Manrope](https://github.com/sharanda/manrope) (SIL Open Font License, see `FONT_LICENSE_OFL.txt`).
