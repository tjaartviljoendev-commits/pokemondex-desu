# Pokedex Desu

A Pokedex Android app built with Jetpack Compose, consuming the [PokeAPI](https://pokeapi.co/). Built as a technical assessment.

## Architecture

Multi-module Clean Architecture with feature-based "journey" modules:

```
app/                    Entry point, navigation, splash screen, DI wiring
core/
  foundation/           Result type, DispatcherProvider, shared utilities
  domain/               Domain models, repository interface, use cases
  data/                 Repository implementation, Room DB, mappers
  network/              Retrofit service, DTOs, network DI
  ui/                   Shared Compose components, theme, color system
journey/
  home/                 Pokemon list screen + ViewModel
  info/                 Pokemon detail screen + ViewModel
```

Data flows unidirectionally: **Network/DB -> Repository -> Use Case -> ViewModel -> Compose UI**.

Each journey module only depends on `core:domain` and `core:ui` — never on `core:data` or `core:network` directly.

## Design Decisions

**Koin over Hilt** — Simpler API, less boilerplate, no annotation processing overhead. Koin's service locator pattern is a pragmatic fit for this project's scale.

**Moshi over kotlinx.serialization** — Stable codegen, good Retrofit integration, predictable behavior with Room's JSON column storage.

**Room with JSON columns** — `PokemonDetailEntity` stores stats, abilities, and types as JSON strings rather than normalised tables. For a read-heavy Pokedex with no relational queries across these fields, this keeps the schema simple and avoids unnecessary joins.

**Full index caching** — On first launch the app caches all ~1300 Pokemon names into Room. This powers instant local search without hitting the network on every keystroke. The detail enrichment (stats, types, abilities) happens lazily per page.

**Use case layer** — Thin delegation wrappers around the repository. Kept for consistency and to provide a seam for future business logic without touching ViewModels.

**Offline-first repository** — Cache-first reads with network fallback. If the network fails but cache exists, stale data is returned rather than an error. Pull-to-refresh forces a fresh network call.

**Shared element transitions** — Pokemon images animate between the list and detail screens using Compose shared element APIs, giving visual continuity.

## Tech Stack

| Category | Library | Version |
|---|---|---|
| UI | Jetpack Compose (via BOM) | 2026.02.01 |
| Navigation | Navigation Compose | 2.9.7 |
| Networking | Retrofit + OkHttp | 3.0.0 / 5.3.2 |
| Serialisation | Moshi | 1.15.2 |
| Database | Room | 2.8.4 |
| DI | Koin | 4.1.1 |
| Image loading | Coil | 2.7.0 |
| Coroutines | kotlinx-coroutines | 1.10.2 |

**Build:** AGP 9.0.1, Kotlin 2.3.10, Gradle 9.4.0, compileSdk 36, minSdk 24.

## Testing

Unit tests cover the data and presentation layers:

- **`PokemonMapperTest`** — DTO-to-domain mapping, URL ID extraction, Room entity round-trips
- **`PokemonRepositoryImplTest`** — Cache hits/misses, network fallback, error propagation, pagination
- **`HomeViewModelTest`** — Initial load, pagination, search with debounce, refresh, retry, state restoration
- **`InfoViewModelTest`** — Loading/success/error states, retry flow

Run tests:
```bash
./gradlew test
```

## Building & Running

```bash
./gradlew assembleDebug
```

Or open in Android Studio and run the `app` configuration on an emulator or device (API 24+).
