# Kairos

Android medical documentation app for surgeons. Shifts, consultations, cases by diagnosis, rich notes, image/video/audio attachments.

## Setup

1. Open project in Android Studio (Hedgehog or newer recommended). Gradle sync will download wrapper jar automatically.
2. Or, from command line with a local Gradle 8.10+ install:
   ```
   gradle wrapper --gradle-version 8.10.2
   ./gradlew :app:installDebug
   ```

## Modules

- `:app` — entry, navigation host, theme application
- `:core` — theme, shared composables, domain models, repository interfaces, media helpers
- `:data` — Room DB, DAOs, repository implementations, backup engine
- `:features` — feature screens (shifts, consultation, cases, patient dialog, settings)

## Stack

- Kotlin + Jetpack Compose
- MVVM + StateFlow
- Room (KSP) + DataStore Preferences
- Hilt DI
- WorkManager (scheduled backup, trash purge)
- Media3 ExoPlayer (video + audio playback)
- compose-rich-editor (notes)

## Plan

Full design and phasing in `C:\Users\Oracle\.claude\plans\i-want-to-build-nested-perlis.md`.
