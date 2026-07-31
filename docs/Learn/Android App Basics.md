# Android App Basics

The Android-specific vocabulary used throughout this wiki, grounded in Kairos's actual manifest and entry points.

## The manifest — the app's identity card

`app/src/main/AndroidManifest.xml` tells the Android system what this app is and what it may do. Kairos declares:

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

Each `uses-permission` is a capability the app must ask for. Camera and microphone are for attaching photos and voice notes to a case. Internet plus network-state exist *only* for the device authorization check, not for syncing clinical data. Notifications are needed so backup and purge jobs can report progress on Android 13+.

Two more manifest lines matter a great deal:

```xml
android:allowBackup="false"
```

This switches **off** Android's automatic cloud backup. Kairos deliberately refuses to let the OS copy the clinical database anywhere; it provides its own verified backup format instead. See [[Features/Settings and Backup|Settings and Backup]].

```xml
<uses-feature android:name="android.hardware.camera" android:required="false" />
```

"Uses a camera if there is one, but does not require it" — so the app still installs on devices without one.

## Application — the app-wide object

Exactly one `Application` object exists while the app runs, created before any screen. Kairos's is `KairosApplication`, and its `onCreate` does three things:

1. Initialise Firebase App Check (device attestation).
2. Register the daily trash purge job.
3. Watch the backup-schedule setting and re-register the backup job whenever it changes.

This is the correct place for "set up once, for the whole app" work. See [[Architecture/Application Lifecycle|Application Lifecycle]].

## Activity — one screenful of app

An **Activity** is a system-managed window. Kairos has exactly one, `MainActivity`, declared in the manifest with an intent filter that makes it the launcher entry point:

```xml
<intent-filter>
    <action android:name="android.intent.action.MAIN" />
    <category android:name="android.intent.category.LAUNCHER" />
</intent-filter>
```

That block is literally what puts the icon in the app drawer. Everything the user sees afterwards — dashboard, case forms, settings — is drawn *inside* this one Activity by Compose and switched by the navigation graph. This is the modern "single-activity" architecture. See [[Architecture/Navigation|Navigation]].

`android:exported="true"` means other apps (and the home screen) may launch it. That is why `MainActivity` whitelists which destinations an incoming widget tap is allowed to open — an exported entry point must never trust arbitrary input.

## Lifecycle — apps are interrupted constantly

A phone app is not in control of its own existence. The user rotates the screen, takes a call, switches apps; Android pauses, resumes, or outright destroys things. Key callbacks Kairos overrides:

- `onCreate` — build everything. Called once per Activity creation.
- `onResume` — the user is looking at the app again. Kairos calls `authorizationViewModel.onAppResumed()` here, so a returning user gets their authorization re-checked.

The practical rule that follows: **anything held only in memory can vanish at any time.** Hence databases for facts, `rememberSaveable` for small UI state that must survive rotation, and ViewModels for state that must survive rotation but not death of the app.

## Context — the handle to the system

`Context` is the object through which code reaches Android itself: files, databases, permissions, system services. You will see it injected as `@ApplicationContext context: Context`. Use the application context for long-lived things (like the database) and an Activity context only for UI. Holding an Activity context in a long-lived object is a classic memory leak.

## Permissions are asked at runtime

Declaring `CAMERA` in the manifest is not enough on modern Android. The app must *also* ask the user at the moment it needs it, and handle refusal. Kairos uses the Accompanist permissions library at the Compose layer for this. See [[Features/Media Capture and Playback|Media Capture and Playback]].

## API levels

```
minSdk = 26      // oldest Android version supported (Android 8.0)
targetSdk = 35   // the version the app is built and tested against
compileSdk = 35  // the version of the SDK used to compile
```

`minSdk = 26` means devices older than Android 8 cannot install Kairos — a deliberate trade of reach for a simpler, more modern codebase. `targetSdk` matters because Android applies stricter rules to apps that target newer versions; raising it is a compatibility commitment, not a cosmetic bump.

## The other manifest components

- **Receiver** — `QuickCaptureWidgetProvider`. A home-screen widget. It is *not exported*, and it launches `MainActivity` with a destination extra. See [[Features/Quick Capture Widget|Quick Capture Widget]].
- **FileProvider** — the safe, standard way to hand a file to another app (for sharing a PDF or a ZIP). Instead of exposing a raw file path, it hands out a temporary permissioned URI. See [[Components/Utilities/CaseShareFiles|CaseShareFiles]].
- **InitializationProvider with a removal** — Kairos disables WorkManager's automatic startup so it can supply its own configuration (needed for Hilt-injected workers) from `KairosApplication`.

## Background work

Android aggressively stops apps that are not on screen. To run work reliably later — a nightly backup, a trash purge — you hand it to **WorkManager**, which persists the request and runs it when conditions allow, surviving app death and reboot. Kairos schedules two: `ScheduledBackupWorker` and `TrashPurgeWorker`. See [[Architecture/Background Work|Background Work]].

## Storage locations

- **App-specific storage** — a private folder only Kairos can read. All media and the database live here; uninstalling the app deletes it.
- **Shared storage / SAF** — the user's own folders, reached only by asking the user to pick a location (`OpenDocumentTree`). Kairos uses this when exporting or backing up outward.

## Related pages

- [[Architecture/Application Lifecycle|Application Lifecycle]]
- [[Architecture/Background Work|Background Work]]
- [[Learn/Jetpack Compose Basics|Jetpack Compose Basics]]
- [[Learn/Security And Privacy Basics|Security And Privacy Basics]]
