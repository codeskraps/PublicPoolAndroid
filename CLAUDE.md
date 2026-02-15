# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What This Is

Android app that monitors Bitcoin mining on [Public Pool](https://web.public-pool.io). Single-module Kotlin app using Jetpack Compose, Ktor, Koin, and Voyager. Tracks hash rate, worker status, and wallet balance.

## Build & Test

```bash
./gradlew assembleDebug                    # debug APK
./gradlew assembleRelease                  # release APK (ProGuard enabled)
./gradlew test                             # unit tests
./gradlew testDebugUnitTest --tests "com.codeskraps.publicpool.ExampleUnitTest"  # single test
./gradlew connectedAndroidTest             # instrumentation tests (needs device)
./gradlew lint                             # lint check
```

## Architecture

MVI + Clean Architecture in three layers: `presentation/` → `domain/` → `data/`. Each screen has a `{Screen}Mvi.kt` (State/Event/Effect), `{Screen}ScreenModel.kt` (Voyager StateScreenModel), and `{Screen}Content.kt` (Composable). DI via four Koin modules in `di/`. Navigation via Voyager TabNavigator (Dashboard, Workers, Wallet tabs) + separate Settings screen.

See [docs/architecture.md](docs/architecture.md) for layer details, DI module breakdown, and how to add new screens.

## API

Three external APIs: Public Pool (mining data, user-configurable base URL), Blockchain.info (wallet), Binance (BTC price). All calls through `KtorApiService`. DTOs in `data/remote/dto/`, mapped to domain models in `data/mappers/Mappers.kt`.

See [docs/api.md](docs/api.md) for endpoints, error handling, and DTO details.

## Key Conventions

- All dependency versions in `gradle/libs.versions.toml`
- Java 19 source/target compatibility
- Dark theme only (Material3)
- 6 locales: en, es, fr, de, hi, zh-rCN — string resources in `res/values-*/strings.xml`
- Debug builds use `.debug` applicationId suffix
- Use cases are single-responsibility classes with `operator fun invoke`
- ScreenModels are Koin factories (not singletons)
