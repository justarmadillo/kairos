# Build And Run

Turning the source folder into a running app.

## What you need

- **Android Studio** (current stable). It bundles the Android SDK and an emulator.
- **JDK 17.** Every module targets Java 17; a different JDK produces "Unsupported class file major version" errors.
- **Android SDK 35** installed, since `compileSdk = 35`.
- A phone running **Android 8.0 or newer** (`minSdk = 26`), or an emulator.
- `local.properties` pointing at the SDK. Android Studio writes this for you; it is machine-specific and not committed.

## Opening the project

Open the **root folder** (the one containing `settings.gradle.kts`), not a module. Android Studio runs a Gradle sync: it reads the build files, downloads dependencies, and indexes the code. The first sync takes minutes.

## The commands

Run from the project root. `./gradlew` on macOS/Linux, `gradlew.bat` on Windows.

Build a debug APK:

```bash
./gradlew assembleDebug
```

Build and install onto a connected device:

```bash
./gradlew installDebug
```

Run all unit tests:

```bash
./gradlew test
```

Compile everything without packaging — the fastest way to check that a change is valid:

```bash
./gradlew assemble
```

Delete all build output (use when the build behaves inexplicably):

```bash
./gradlew clean
```

Build the signed release APK:

```bash
./gradlew assembleRelease
```

## Where the output lands

```
app/build/outputs/apk/debug/app-debug.apk
app/build/outputs/apk/release/app-release.apk
```

## Debug vs release

| | Debug | Release |
|---|---|---|
| Signing | automatic throwaway key | your key from `keystore.properties` |
| Minification | off | on (`isMinifyEnabled = true`) |
| App Check | debug provider | Play Integrity |
| Speed | slower, larger | optimised |
| Purpose | development | distribution |

A release build **fails deliberately** if `keystore.properties` is missing any of `storeFile`, `storePassword`, `keyAlias`, `keyPassword`, or if the key file itself is absent. That is the `verifyProductionReleaseConfig` task doing its job. See [[Overview/Build System|Build System]].

`keystore.properties` and the key file are excluded from version control on purpose. Anyone with them can publish updates impersonating this app.

## Running it

Press Run in Android Studio, or `installDebug` then open Kairos on the device. On first launch you will see the launch screen showing the **device ID** for at least 1.5 seconds while authorization is evaluated. A device that is not in the Firestore whitelist stays on the locked screen — that is working as designed, not a bug. See [[Features/Device Authorization|Device Authorization]] and `FIREBASE_DEVICE_AUTH_SETUP.md` in the project root.

## Firebase configuration

Firestore and App Check need `app/google-services.json` from the Firebase console. Without it the Google Services plugin fails the build. The relevant Firestore security rules live in `firestore.rules`.

## Reading a failure

- **Compile error** — a file and line are named. The code is wrong; nothing ran.
- **Hilt error** ("cannot be provided...") — a missing binding. See [[Learn/Dependency Injection Explained|Dependency Injection Explained]].
- **Room error** — usually SQL referring to a column that does not exist, or a schema change with no migration. See [[Learn/Databases And Room|Databases And Room]].
- **Runtime crash** — the build succeeded; read the **stack trace** in Logcat. The top line is the exception; the first line mentioning `com.taha.kairos` is your code, and that is where to look.

## Useful habits

- After changing anything annotated (`@Entity`, `@Dao`, `@Inject`, `@Module`), rebuild rather than relying on incremental compilation — code generators need to re-run.
- Gradle caches aggressively. If something is impossible, `clean` first, then judge.
- Never edit anything under `build/`.

## Related pages

- [[Overview/Build System|Build System]]
- [[Learn/Gradle And Modules|Gradle And Modules]]
- [[Learn/Testing Basics|Testing Basics]]
