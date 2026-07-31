# Migrations

> **In plain words** — instructions for upgrading a database that already contains real patient data. When a new app version changes the tables, the file on the user's phone still has the old shape, and Room refuses to guess: you must supply the exact SQL that transforms version N into version N+1. Kairos has one so far (adding an attachment's original file name). Skipping a migration means the app crashes on launch or destroys records — this is the highest-risk file in the project, and the rules are written at the top of the source. See [[Learn/Databases And Room|Databases And Room]].

## Purpose

Registers every explicit Room schema transition supported by [[Components/Databases/KairosDatabase]].

## Responsibilities

- Define ordered `Migration` objects.
- Preserve user data while advancing database versions.
- Expose `ALL_MIGRATIONS` for Hilt database construction.

## Dependencies

- AndroidX Room `Migration` and `SupportSQLiteDatabase`.

## Called By

- `DatabaseModule.provideDatabase`.

## Calls

- `SupportSQLiteDatabase.execSQL` during migration.

## Important Methods

- `MIGRATION_1_2.migrate(db)` adds nullable `case_media.original_file_name`.
- `ALL_MIGRATIONS` currently contains only `MIGRATION_1_2`.

## Design Patterns

- Explicit chronological migration registry.

## Common Pitfalls

- Never skip a schema version; each increment needs a path.
- A new migration must be added to `ALL_MIGRATIONS`, not merely declared.
- Update and verify the exported schema snapshots after changes.
- There is no destructive-migration fallback.

## Related Pages

- [[Components/Databases/KairosDatabase]]
- [[Execution Flows/Database Operations]]
- [[Overview/Build System]]

## Source References

- `data/src/main/java/com/taha/kairos/data/db/migrations/Migrations.kt`
- `data/src/main/java/com/taha/kairos/data/db/KairosDatabase.kt`
- `data/src/main/java/com/taha/kairos/data/di/DataModule.kt`
- `data/schemas/com.taha.kairos.data.db.KairosDatabase/`

