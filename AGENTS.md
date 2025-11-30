# Repository Guidelines

## Project Structure & Module Organization
`composeApp/` holds the shared Kotlin Multiplatform implementation: `src/commonMain` for UI and domain code, `src/commonTest` for shared tests, and `src/androidMain`, `src/iosMain`, `src/androidUnitTest` for platform glue and validation. `iosApp/` is a minimal SwiftUI host that links against the generated framework. Gradle convention plugins live in `build-logic/`, and specs stay under `docs/`. Treat `build/` and `.gradle/` as disposable output directories.

## Build, Test, and Development Commands
- `./gradlew build` — compiles every target and runs the default verification tasks; run it before sharing.
- `./gradlew :composeApp:assembleDebug` — produces the Android debug APK for installation from Android Studio or `adb install`.
- `./gradlew :composeApp:testDebugUnitTest` — executes JVM tests from `commonTest` and `androidUnitTest`.
- `./gradlew :composeApp:linkDebugFrameworkIosArm64` — emits the device framework used by `iosApp`; switch to `linkDebugFrameworkIosSimulatorArm64` on the Simulator.
- `./gradlew :composeApp:dependencies` — inspects dependency graphs when evaluating upgrades.

## Coding Style & Naming Conventions
Follow the Kotlin style guide with four-space indentation, trailing commas on multiline literals, and `UpperCamelCase` for composables or classes. Keep packages lowercase and feature-based (for example `features.feed.list`). Favor immutable `val`, expose UI state through `StateFlow`, and drive all composables from a single state entry point. Order Compose modifiers layout → drawing → behavior.

## Testing Guidelines
Locate new tests next to their sources (`commonTest` for shared logic, `androidUnitTest` for Android-only concerns). Use JUnit + Kotlin Test with `kotlinx-coroutines-test`’s `runTest` helper when touching suspend functions or flow collectors, and fake Picsum responses to avoid network calls. Keep touched files at ≥80% coverage, name specs `shouldDoX_whenY`, and re-run `./gradlew :composeApp:testDebugUnitTest` before requesting review.

## Commit & Pull Request Guidelines
Commits follow Conventional prefixes seen in history (`feat:`, `refactor:`, `docs:`). Limit subjects to 72 characters, summarize the reason and testing evidence in the body, and keep each commit focused on one logical change. Pull requests need a behavior summary, references to issues, command output or screenshots, and notes on platform impact. Use draft PRs for design feedback and re-request reviews after addressing comments.

## Security & Configuration Tips
Never commit API secrets or environment overrides; store them in `local.properties` or untracked `.env` files and document the setup in `README.md`. Napier logging can leak identifiers, so disable verbose log trees for release builds and redact IDs from traces shared in issues. Validate dependency bumps via `./gradlew :composeApp:dependencies` before merging.
