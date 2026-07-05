# Апекс — CMP Client

Compose Multiplatform client for the Apex karting center (Android, iOS, Web).

## Modules

- `shared` — common UI, domain models, MVI primitives, feature contracts and platform adapters.
- `androidApp` — Android host application (launcher label: **Апекс**).
- `webApp` — Web/Wasm host application.
- `iosApp` — native iOS host; `shared` produces the `ApexShared` framework.

## Commands

Run from `client/`:

```bash
./gradlew :shared:compileDebugKotlinAndroid
./gradlew :androidApp:assembleDebug
./gradlew :webApp:wasmJsBrowserDevelopmentRun
```

## Tests

Primary target for `commonTest` on all platforms:

```bash
./gradlew :shared:wasmJsTest
```

Android JVM unit tests (`:shared:testDebugUnitTest`) may fail with `ClassNotFoundException` on Windows when the project path contains non-ASCII characters (for example `практика`). The `shared` module redirects its `build/` to `%TEMP%/apex-karting-build/shared` in that case.

If tests still fail, map the repo to an ASCII drive letter and run tests from there:

```powershell
subst X: "D:\path\to\summer-school-karting"
Set-Location X:\client
.\gradlew.bat :shared:testDebugUnitTest
subst X: /d
```

## Architecture

The shared module follows clean architecture:

- `domain` contains entities and pure policies for availability, price and cancellation.
- `core` contains MVI, error, time, theme and reusable UI primitives.
- feature packages define repository contracts and screen-facing models.
- platform source sets provide `expect/actual` adapters such as route map rendering.

Brand theme: `core/theme/ApexColors.kt` (`ApexBrandColors` — racing red + asphalt).