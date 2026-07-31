# BackupPruner

> **In plain words** — decides which old backups to delete. The policy is *generational*: keep the newest few, plus the newest one from each earlier month, delete the rest. That gives dense recent history and thin long-term history without filling the folder forever. Like the lease policy, it is pure logic — it is handed a list of file names and returns which to remove, touching no files itself, which is why it has unit tests. See [[Learn/Testing Basics|Testing Basics]].

## Purpose

Selects obsolete Kairos backup ZIP names according to a generational retention policy.

## Responsibilities

- Recognize only exact Kairos backup filenames.
- Keep the five newest backups.
- Also keep the newest backup from each of the newest twelve distinct calendar months.
- Return every other recognized filename for deletion.

## Dependencies

- Kotlin collections and a strict backup-name regular expression.

## Called By

- [[Components/Workers/ScheduledBackupWorker]]

## Calls

- Pure sorting, grouping, and set operations; it performs no deletion itself.

## Important Methods

- `selectBackupsToDelete(names, keepRecent, keepMonthly)` returns a `Set<String>`.
- `KEEP_RECENT = 5`; `KEEP_MONTHLY_MONTHS = 12`.

## Design Patterns

- Pure decision function separated from SAF side effects.
- Generational retention rather than only a rolling newest-N window.

## Common Pitfalls

- Only `kairos-backup-yyyyMMdd-HHmmss.zip` names are eligible; foreign files are always preserved.
- Lexicographic ordering assumes the timestamp format never changes.
- Custom negative retention counts are not explicitly rejected.

## Related Pages

- [[Components/Services/BackupEngine]]
- [[Components/Workers/ScheduledBackupWorker]]
- [[Architecture/Background Work]]

## Source References

- `data/src/main/java/com/taha/kairos/data/backup/BackupPruner.kt`
- `data/src/test/java/com/taha/kairos/data/backup/BackupPrunerTest.kt`

