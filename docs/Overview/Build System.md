# Build System

> **In plain words** — the *build* is the automated process that turns text files into an installable app. **Gradle** runs it. The *wrapper* (`gradlew`) is a script that pins everyone to the same Gradle version, so the build behaves identically on every machine. **KSP** is the step where Room and Hilt generate extra code from the `@` annotations. A **debug** build is for development; a **release** build is for real users, is optimised, and is cryptographically signed — and this project deliberately fails the release build if the signing key is missing. Commands and troubleshooting: [[Learn/Build And Run|Build And Run]].

Kairos uses the checked-in Gradle wrapper with Kotlin DSL and a central version catalog. Plugin versions are declared once at the root and applied per module.

## Toolchain

- Gradle wrapper 8.10.2; Android Gradle Plugin 8.7.3.
- Kotlin 2.1.0 and JVM target 17.
- KSP2 generates Hilt and Room code.
- Google and Maven Central are the only dependency repositories; project-local repositories are rejected.
- Parallel execution, build cache, and configuration cache are enabled.

## Android Configuration

- All modules compile against API 35 and require API 26 or newer.
- `:app` targets API 35, has application ID `com.taha.kairos`, and currently builds version code 6 / version name 1.10.
- Compose is enabled in `:app`, `:core`, and `:features`.
- Room schemas are exported from `:data` to `data/schemas`.

## Variants and Release

- Debug installs the Firebase App Check debug provider.
- Release installs Play Integrity App Check, enables minification, and applies `app/proguard-rules.pro`.
- `preReleaseBuild` depends on `verifyProductionReleaseConfig`, which requires `storeFile`, `storePassword`, `keyAlias`, and `keyPassword` in `keystore.properties` and verifies the key file exists.
- The Google Services plugin consumes `app/google-services.json`.

## Common Commands

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat test
.\gradlew.bat :features:compileDebugKotlin
.\gradlew.bat :data:kspDebugKotlin
.\gradlew.bat :app:installDebug
.\gradlew.bat assembleRelease
```

Release signing material and Firebase configuration are deployment inputs; do not reproduce their contents in documentation. See [[Architecture/Configuration|Configuration]] and [[Overview/Dependencies|Dependencies]].

## Source references

- `settings.gradle.kts`
- `build.gradle.kts`
- `gradle.properties`
- `gradle/wrapper/gradle-wrapper.properties`
- `app/build.gradle.kts`
- `data/build.gradle.kts`
- `app/proguard-rules.pro`
