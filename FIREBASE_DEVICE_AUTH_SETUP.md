# Firebase device authorization setup

Kairos reads exactly one Cloud Firestore document:

```text
authorized_devices/{DEVICE_ID}
```

The document authorizes the device only when it exists and contains:

```text
authorized: true
```

## 1. Connect the Android app

1. Create a dedicated Firebase project (recommended), or choose an existing
   project whose Firestore rules you will merge carefully.
2. Add an Android app with package name `com.taha.kairos`.
3. Download `google-services.json`.
4. Place it at `app/google-services.json`.
5. Sync Gradle and rebuild the app.

The Firebase Gradle plugin and Firestore dependency are already configured.
`google-services.json` is required for every build variant, so a missing or
mismatched Firebase configuration fails during the build instead of producing
an unusable APK.

Use the same signed build that will be installed in production. Android's device
identifier is scoped to the app signing key, so debug and release builds can show
different Kairos device IDs.

`com.taha.kairos` is a different Android application identity from the former
`com.kairos` package. It has separate on-device storage and produces new Kairos
device IDs. Whitelist the ID displayed by the new release build. If migrating
existing data, export it from the old app and restore that backup in the new app
after the new package has been authorized.

## 2. Create Cloud Firestore and publish rules

Create a Cloud Firestore database. For a dedicated Kairos project, publish these
rules:

```text
rules_version = '2';

service cloud.firestore {
  match /databases/{database}/documents {
    match /authorized_devices/{deviceId} {
      allow get: if true;
      allow list, create, update, delete: if false;
    }

    match /{document=**} {
      allow read, write: if false;
    }
  }
}
```

These rules let an installed app fetch one exact, high-entropy device document.
They prevent clients from listing device IDs or changing the whitelist. Changes
made in the Firebase Console use administrator access and are still allowed.

If the Firebase project already serves other apps or collections, do **not**
replace its rules with this complete file: the final catch-all intentionally
denies every other client read/write. Instead, merge only the
`/authorized_devices/{deviceId}` match into the existing
`/databases/{database}/documents` block and retain the project's other rules.

## 3. Configure Firebase App Check

Kairos already uses the App Check debug provider in debug builds and the Play
Integrity provider in release builds. Token auto-refresh is explicitly enabled
when the provider is installed.

1. Add the SHA-256 fingerprint of the **release signing certificate** to the
   Android app in Firebase Project settings.
2. In Firebase Console > App Check, register `com.taha.kairos` with the Play
   Integrity provider.
3. Link the Play Integrity API to the same Cloud project used by Firebase.
4. For this exclusively sideloaded APK, set `PLAY_RECOGNIZED` to **Not
   required**, `LICENSED` to **Not required**, and minimum device integrity to
   **Device integrity**.
5. Install a release build and confirm valid requests appear in App Check
   metrics.
6. Only then enable App Check enforcement for Cloud Firestore.

For a debug build, launch it once, copy the App Check debug token from Logcat,
and add that token under App Check > Manage debug tokens. Never ship a build
that uses the debug provider.

## 4. Authorize a device

1. Install and launch the intended production build.
2. Copy the `KAIROS-....` device ID shown on the launch/locked screen.
3. In Firestore, create the collection `authorized_devices`.
4. Create a document whose document ID is the complete Kairos device ID.
5. Add a Boolean field named `authorized` with value `true`.
6. On the device, tap **Check authorization**.

To revoke a device, delete its document or set `authorized` to `false`. A device
that is currently offline can retain its already-issued local lease until the
offline deadline; it cannot renew without a server response.

## Lease behavior

- A successful server check grants 24 hours of normal offline use.
- At 24 hours, Kairos checks Firestore again.
- If Firestore is temporarily unreachable, a previously authorized device gets
  at most another 24 hours while Kairos retries when connectivity returns.
- At 48 hours after the last successful check, the app locks until Firebase
  positively authorizes it again.
- Rebooting the device requires one online Firebase check. This prevents changing
  the system clock and repeatedly rebooting from extending an offline lease.
- A missing document or `authorized: false` locks immediately when received.
- A first launch without a successful check is locked.
- The locked screen always permits a complete data export.
- Automatic backups and trash cleanup pause while locked; only the explicit
  emergency export remains available.
- A denial or non-retryable authorization failure is stored durably and cannot
  fall back to an older cached lease after the app process restarts.

## Security boundary

This is client-side device licensing. It prevents normal use on devices that are
not whitelisted. App Check raises the bar by requiring app/device attestation
for Firestore access, but a determined attacker can still try to remove a
client-side UI check. For higher-risk distribution, use server-signed
authorization leases and distribute only signed, integrity-protected builds.
