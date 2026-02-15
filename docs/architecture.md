# Architecture

## Overview

MVI (Model-View-Intent) with Clean Architecture. Single `app` module. All source under `app/src/main/java/com/codeskraps/publicpool/`.

```
presentation/  →  domain/  →  data/
(UI + State)     (UseCases)   (Repos + API + DataStore)
```

## Layers

### data/

Implements the contracts defined in `domain/`. Contains:

- **`remote/KtorApiService.kt`** — Interface + `KtorApiServiceImpl` for all HTTP calls. Base URL for Public Pool is injected dynamically via `baseUrlProvider` lambda (reads from DataStore at call time).
- **`remote/dto/`** — Kotlinx-serializable DTOs matching API JSON responses.
- **`remote/UmamiAnalyticsDataSource.kt`** — Umami analytics via WebView JavaScript evaluation.
- **`mappers/Mappers.kt`** — Extension functions mapping DTOs to domain models. All mapping logic lives in this single file.
- **`repository/PublicPoolRepositoryImpl.kt`** — Coordinates API service calls and DataStore reads/writes.
- **`repository/AnalyticsRepositoryImpl.kt`** — Wraps `UmamiAnalyticsDataSource`.
- **`local/PreferencesKeys.kt`** — DataStore preference key constants (`WALLET_ADDRESS`, `BASE_URL`).

### domain/

Pure Kotlin, no Android dependencies:

- **`model/`** — Domain entities: `NetworkInfo`, `ClientInfo`, `Worker`, `ChartDataPoint`, `WalletInfo`, `CryptoPrice`.
- **`repository/`** — Interfaces: `PublicPoolRepository`, `AnalyticsRepository`.
- **`usecase/`** — Single-responsibility use cases. Each is a class with an `operator fun invoke(...)` or `suspend operator fun invoke(...)`. Injected as factories via Koin.

### presentation/

Each screen has its own package (`dashboard/`, `workers/`, `wallet/`, `settings/`) containing three files:

1. **`{Screen}Mvi.kt`** — MVI contract: State data class, Event sealed interface, Effect sealed interface. All implement marker interfaces from `common/MviBase.kt` (`UiState`, `UiEvent`, `UiEffect`).
2. **`{Screen}ScreenModel.kt`** — Extends Voyager's `StateScreenModel<State>`. Handles events via `handleEvent(event)`. Emits one-shot effects via a `Channel<Effect>` exposed as `effect: Flow<Effect>`. Updates state via `mutableState.update { ... }`.
3. **`{Screen}Content.kt`** — `@Composable` function rendering the UI. Receives state, event handler lambda, and collects effects.

### di/

Koin modules loaded in `MainApplication.onCreate()`:

| Module | Scope | Contents |
|--------|-------|----------|
| `appModule` | singleton | DataStore, `AppReadinessState`, `AppLifecycleState` |
| `dataModule` | singleton | `HttpClient`, `KtorApiService`, repositories, Umami config |
| `domainModule` | factory | All use cases |
| `presentationModule` | factory | All ScreenModels |

### navigation/

Voyager-based. `HomeScreen` hosts a `TabNavigator` with 3 tabs:

| Tab | Index | Screen |
|-----|-------|--------|
| Dashboard | 0 | `DashboardTab` |
| Workers | 1 | `WorkersTab` |
| Wallet | 2 | `WalletTab` |

Settings is pushed as a separate screen via `Navigator.push(SettingsScreen)`.

## App Lifecycle

- **`AppReadinessState`** — Controls splash screen. Set to ready once wallet address is loaded from DataStore and analytics initialized.
- **`AppLifecycleState`** — Tracks foreground/background. ScreenModels observe `isAppInBackground` flow and auto-refresh data when app returns to foreground.

## Adding a New Screen

1. Create a package under `presentation/` (e.g., `presentation/newfeature/`).
2. Add `NewFeatureMvi.kt` with State, Event, Effect types implementing the base interfaces.
3. Add `NewFeatureScreenModel.kt` extending `StateScreenModel<NewFeatureState>`.
4. Add `NewFeatureContent.kt` with the `@Composable` UI.
5. Register the ScreenModel in `di/PresentationModule.kt` as `factoryOf(::NewFeatureScreenModel)`.
6. Wire up navigation in `presentation/navigation/`.
