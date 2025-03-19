# Droidcon Kotlin Multiplatform (KMP) Project Architecture

This document provides a detailed breakdown of the Droidcon application architecture, which is built using Kotlin Multiplatform (KMP) with Compose Multiplatform for UI.

## Project Overview

Droidcon is a cross-platform mobile application for conference information, built with KMP to share code between Android and iOS platforms. The project uses:

- **Kotlin Multiplatform** for shared business logic
- **Compose Multiplatform** for shared UI
- **SQLDelight** for database operations
- **Koin** for dependency injection
- **Ktor** for networking

## Module Structure

The application is divided into these key modules:

### 1. `shared` Module

The core module containing shared business logic, data handling, and domain-specific code.

#### Key Components:

- **Domain Layer** (`domain/`)
  - `entity/`: Core business models
  - `repository/`: Repository interfaces for data operations
  - `service/`: Domain service interfaces
  - `gateway/`: External service interfaces
  - `composite/`: Combined domain services

- **Application Layer** (`application/`)
  - `repository/`: Repository implementations
  - `service/`: Service implementations
  - `gateway/`: Gateway implementations
  - `composite/`: Composite service implementations

- **DTO Layer** (`dto/`)
  - Data transfer objects for API communication

- **Service Layer** (`service/`)
  - `DeepLinkNotificationHandler.kt`: Handles deep link notifications
  - `ParseUrlViewService.kt`: Service for parsing URLs

- **Utilities** (`util/`)
  - Shared utility functions and extensions

- **Dependencies**:
  - Kermit for logging
  - Kotlinx Coroutines for asynchronous programming
  - Kotlinx DateTime for date/time handling
  - SQLDelight for database operations
  - Ktor for networking
  - Multiplatform Settings for shared preferences
  - Koin for dependency injection

### 2. `shared-ui` Module

Contains the Compose Multiplatform UI components shared between platforms.

#### Key Components:

- **UI Layer** (`ui/`)
  - `MainComposeView.kt`: Main entry point for Compose UI
  - `BottomNavigationView.kt`: Bottom navigation implementation
  - `session/`: Session-related UI components
  - `sponsors/`: Sponsor-related UI components
  - `venue/`: Venue-related UI components
  - `settings/`: Settings UI components
  - `theme/`: Theme definitions and styling
  - `util/`: UI-specific utilities

- **ViewModel Layer** (`viewmodel/`)
  - `ApplicationViewModel.kt`: Main application-level view model
  - `session/`: Session-related view models
  - `sponsor/`: Sponsor-related view models
  - `settings/`: Settings-related view models
  - `FeedbackDialogViewModel.kt`: View model for the feedback dialog

- **Dependencies**:
  - Compose Multiplatform (UI, Foundation, Material3)
  - Coil for image loading
  - Shared module for business logic

### 3. `android` Module

Android-specific implementation and entry point.

#### Key Components:
- Android-specific UI customizations
- Android application setup
- Platform-specific implementations
- Firebase integration

### 4. `ios` Module

iOS-specific implementation and entry point.

#### Key Components:
- iOS-specific UI customizations
- iOS application setup
- Platform-specific implementations

## Key Architectural Patterns

1. **Clean Architecture**: Separation of concerns through Domain, Application, and UI layers
2. **MVVM Pattern**: Using ViewModels to handle UI state and logic
3. **Repository Pattern**: Abstracting data sources behind repository interfaces
4. **Dependency Injection**: Using Koin for service location and dependency management

## Database

The project uses SQLDelight for database operations:

```kotlin
sqldelight {
    databases.create("DroidconDatabase") {
        packageName.set("co.touchlab.droidcon.db")
    }
}
```

## Networking

Ktor is used for HTTP networking with platform-specific implementations:
- OkHttp client for Android
- Native HTTP client for iOS

## UI Architecture

The application follows a consistent UI approach:
1. **Composition**: UI is built using Compose Multiplatform components
2. **State Management**: ViewModels handle UI state
3. **Navigation**: Navigation is managed through a custom navigation system

## Platform-Specific Code

The project follows KMP conventions for platform-specific implementations:
- `androidMain`: Android-specific implementations
- `iosMain`: iOS-specific implementations
- `commonMain`: Shared code between platforms

## Dependency Injection

Koin is used for dependency injection, with module definitions in `Koin.kt`.

## Summary

This Kotlin Multiplatform project demonstrates a well-structured architecture that enables code sharing between Android and iOS while maintaining platform-specific optimizations where needed. The separation into multiple modules provides clear boundaries between different layers of the application, making it maintainable and scalable. 