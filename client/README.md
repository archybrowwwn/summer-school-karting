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

## Architecture

The shared module follows clean architecture:

- `domain` contains entities and pure policies for availability, price and cancellation.
- `core` contains MVI, error, time, theme and reusable UI primitives.
- feature packages define repository contracts and screen-facing models.
- platform source sets provide `expect/actual` adapters such as route map rendering.

Brand theme: `core/theme/ApexColors.kt` (`ApexBrandColors` — racing red + asphalt).