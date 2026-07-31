# Reusable UI Components

> **In plain words** — building blocks used by more than one screen, kept in `:core` so every feature draws the same card, the same empty message, the same audio player. They all follow the same discipline: they receive their data and their callbacks as parameters and hold no state of their own (*state hoisting*), which is what makes them reusable, previewable, and impossible to get out of sync with the screen using them. See [[Learn/Jetpack Compose Basics|Jetpack Compose Basics]].

Shared Compose components keep feature screens focused on layout and event wiring.

## Inventory

- [[Components/UI/Kairos Top Bar|Kairos Top Bar]] and [[Components/UI/Bottom Bar|Bottom Bar]] — application chrome.
- [[Components/UI/Kairos Theme|Kairos Theme]] — shared color, typography, shape, and extra-color locals.
- [[Components/UI/Empty State|Empty State]] and [[Components/UI/Case Card|Case Card]] — repeated feature content.
- [[Components/UI/Diagnosis Autocomplete|Diagnosis Autocomplete]], [[Components/UI/Phone Input Section|Phone Input Section]], and [[Components/UI/Rich Notes Editor|Rich Notes Editor]] — case form controls.
- [[Components/UI/Media Attachment Section|Media Attachment Section]], [[Components/UI/Audio Recorder Modal|Audio Recorder Modal]], and [[Components/UI/Audio Player Item|Audio Player Item]] — media controls.

Components are stateless where practical: feature code supplies state and callbacks. Media playback and small draft-only values are retained locally when their lifetime is inherently composable-scoped.

## Source references

- `core/src/main/java/com/taha/kairos/core/components/`
- `core/src/main/java/com/taha/kairos/core/theme/KairosTheme.kt`
- `app/src/main/java/com/taha/kairos/ui/BottomBar.kt`
- `features/src/main/java/com/taha/kairos/features/patient/NewPatientTab.kt`
