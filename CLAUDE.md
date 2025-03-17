# DroidconKotlin Development Guide

## Build Commands
- Build: `./gradlew build`
- Clean build: `./gradlew clean build`
- Check (includes lint): `./gradlew check`
- Android lint: `./gradlew lint`
- Run tests: `./gradlew test`
- ktlint check: `./gradlew ktlintCheck`
- ktlint format: `./gradlew ktlintFormat`
- Build ios: `cd /Users/kevingalligan/devel/DroidconKotlin/ios/Droidcon && xcodebuild -scheme Droidcon -sdk iphonesimulator`

## Modules

- android: The Android app
- ios: The iOS app
- shared: Shared logic code
- shared-ui: UI implemented with Compose Multiplatform and used by both Android and iOS

## Libraries

- Hyperdrive: KMP-focused architecture library. It is open source but rarely used by other apps. See docs/HyperDrivev1.md

## Code Style
- Kotlin Multiplatform project (Android/iOS)
- Use ktlint for formatting (version 1.4.0)
- Follow dependency injection pattern with Koin
- Repository pattern for data access
- Compose UI for shared UI components
- Class/function names: PascalCase for classes, camelCase for functions
- Interface implementations: Prefix with `Default` (e.g., `DefaultRepository`)
- Organize imports by package, no wildcard imports
- Type-safe code with explicit type declarations
- Coroutines for asynchronous operations
- Proper error handling with try/catch blocks

## Current Task

The app is designed to be configured for a single conference, and for different conferences the configuration needs to be changed, and a new app instance needs to be published to the app store. We want to refactor the app so that is can be used for all conferences, and allow the user to select the conference they want to display in the app.

The overall tasks:
- Update the SqlDelight tables to include a conference record. Currently the metadata for each conference will be defined in code as literal values. Eventually we will load this data from Firestore.
- The UI needs to be updated to allow the user to select the conference to show data for. They will need to select the conference on first run, and they should be able to change the selection by going into settings.
- 