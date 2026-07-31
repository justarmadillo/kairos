# Utilities Index

> **In plain words** — small helper classes that do not fit the other categories: the authorization rules, the clock and network probes they depend on, backup retention logic, file-sharing helpers, name formatting, database migrations, and the home-screen widget. Several of these are **pure logic** — they take values in and return values out, touching nothing else — which is exactly why they are the parts of the app covered by automated tests. See [[Learn/Testing Basics|Testing Basics]].

- [[Components/Utilities/AuthorizationLeasePolicy]]
- [[Components/Utilities/AuthorizationClock]]
- [[Components/Utilities/CachedAuthorizationGuard]]
- [[Components/Utilities/NetworkMonitor]]
- [[Components/Utilities/FirebaseAppCheckInitializer]]
- [[Components/Utilities/BackupPruner]]
- [[Components/Utilities/CaseShareFiles]]
- [[Components/Utilities/PatientNameFormatter]]
- [[Components/Utilities/Migrations]]
- [[Components/Utilities/QuickCaptureWidgetProvider]]

Cross-operation locking is documented separately at [[Components/Repositories/DataSafetyCoordinator]].

## Source References

- `core/src/main/java/com/taha/kairos/core/authorization/`
- `app/src/main/java/com/taha/kairos/authorization/`
- `data/src/main/java/com/taha/kairos/data/authorization/`
- `data/src/main/java/com/taha/kairos/data/backup/`
- `features/src/main/java/com/taha/kairos/features/cases/CaseShareFiles.kt`

