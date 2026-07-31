# CaseShareFiles

> **In plain words** — shared plumbing for the PDF and ZIP exporters: naming temporary files safely, cleaning up old ones, publishing a file for sharing, and truncating text without splitting a character in half. That last one matters because a file name limit is measured in bytes while a name is written in characters — an accented or non-Latin character occupies several bytes, and cutting mid-character produces a corrupt name.

## Purpose

Provides safe temporary-file naming, retention, publication, and UTF-8 truncation helpers shared by case exporters.

## Responsibilities

- Derive a sanitized case-specific filename stem.
- Generate collision-resistant timestamped filenames.
- Prune share artifacts older than 24 hours.
- Publish a partial file by rename or guarded copy fallback.
- Truncate strings without splitting a Unicode code point.

## Dependencies

- `Case`, Java `File`, `UUID`, date formatting, and `IOException`.

## Called By

- [[Components/Services/CasePdfExporter]]
- [[Components/Services/CaseZipExporter]]

## Calls

- Filesystem create/list/delete/rename/copy operations.

## Important Methods

- `Case.shareFileStem()` sanitizes the patient name and appends the case ID.
- `uniqueShareFile(directory, stem, extension)` adds millisecond timestamp and UUID suffix.
- `pruneExpiredShareFiles` applies a 24-hour cutoff.
- `publishPartialFile` avoids exposing incomplete output.
- `truncateUtf8(maxBytes)` respects code-point boundaries.

## Design Patterns

- Internal shared utility functions and write-then-publish safety.

## Common Pitfalls

- Pruning is based on `lastModified`; external timestamp changes affect retention.
- `publishPartialFile` cannot guarantee an atomic rename on every filesystem, so its copy fallback cleans a failed destination.
- Helpers are `internal` to the feature module and are not a general storage API.

## Related Pages

- [[Features/Case Detail and Sharing]]
- [[Components/Services/CasePdfExporter]]
- [[Components/Services/CaseZipExporter]]

## Source References

- `features/src/main/java/com/taha/kairos/features/cases/CaseShareFiles.kt`

