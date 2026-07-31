# Project Overview

> **In plain words** — Kairos is a phone app for a doctor to record patients, cases, diagnoses, shifts, and consultations, with photos and voice notes attached. Everything is saved **on the phone**, not on a server, so it works with no internet. The one thing it does ask the internet is "is this phone allowed to run me?". *Local-first* means the phone holds the real copy of the data. *Room* is the local database. *DataStore* holds small settings. *Firestore* is the Google cloud service used only for that one permission check. See [[Learn/Data Storage Choices|Data Storage Choices]].

Kairos is a local-first Android medical documentation app for recording patients, clinical cases, diagnoses, shifts, consultation sessions, rich notes, and media. It also provides search, dashboard statistics, case sharing, backup/restore, trash retention, device authorization, and a quick-capture widget.

## Product Shape

- A single Android application with one launcher activity and a Compose UI.
- Clinical data is stored locally in Room; preferences and authorization leases use DataStore.
- Firestore is used only to verify whether the current device is authorized. There is no account login or remote clinical-data sync.
- Media remains in app-specific storage and is referenced by relative paths in the database.
- Android auto-backup is disabled; the app owns a verified database-and-media backup format.

## Primary Areas

- [[Features/Dashboard|Dashboard]], [[Features/Global Search|Global Search]], and [[Features/Diagnosis Browser|Diagnosis Browser]] expose the record set.
- [[Features/Patient and Case Capture|Patient and Case Capture]], [[Features/Shift Management|Shift Management]], and [[Features/Consultation Calendar|Consultation Calendar]] create and organize work.
- [[Features/Case Detail and Sharing|Case Detail and Sharing]] and [[Features/Media Capture and Playback|Media Capture and Playback]] present and export a case.
- [[Features/Settings and Backup|Settings and Backup]] and [[Features/Trash and Retention|Trash and Retention]] protect local data.
- [[Features/Device Authorization|Device Authorization]] gates protected UI and background mutation.

## Key Constraints

- Minimum Android API 26; target API 35.
- A positive server authorization is required on first launch and after reboot.
- Cached access lasts up to 48 hours without a successful refresh.
- Repository interfaces isolate feature code from Room, DataStore, Firestore, and file APIs.

See [[Overview/Architecture|Architecture]], [[Diagrams/Project Architecture|Project Architecture]], and [[Features/Features Index|Features]].

## Source references

- `README.md`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/taha/kairos/MainActivity.kt`
- `core/src/main/java/com/taha/kairos/core/model/Case.kt`
- `data/src/main/java/com/taha/kairos/data/db/KairosDatabase.kt`
- `data/src/main/java/com/taha/kairos/data/authorization/FirebaseDeviceAuthorizationRepository.kt`
