# Recipes — Kotlin Multiplatform REST API client (Лабораторна робота №4)

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

## Run

* **Desktop:** `./gradlew :composeApp:run`
* **Android:** open the project in Android Studio and run the `composeApp` configuration
  (or `./gradlew :composeApp:installDebug` with a device/emulator attached).
* **iOS:** the shared framework (`ComposeApp`) and `MainViewController()` are ready.
  Create the Xcode host app with the KMP wizard (<https://kmp.jetbrains.com>) and call
  `MainViewControllerKt.MainViewController()` from SwiftUI, or copy `iosApp/` from a wizard project.

## Libraries

Compose Multiplatform 1.8.2 · Kotlin 2.1.21 · Ktor 3.1 · kotlinx.serialization · Coil 3 ·
**Kermit 2.0.6** (logging) · **multiplatform-settings 1.3.0** (user settings).

Font: [Manrope](https://github.com/sharanda/manrope) (SIL Open Font License, see `FONT_LICENSE_OFL.txt`).
