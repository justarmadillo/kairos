# CaseZipExporter

> **In plain words** — packages a case as a ZIP: the PDF report plus its attachments. Note the graceful-degradation detail — if an attachment cannot be read, the archive still gets built and includes a note saying what was missing. Producing a partial, honest result beats failing the whole export over one unreadable file.

## Purpose

Builds a shareable case archive containing a PDF report, readable attachments, and an unavailable-attachment notice when necessary.

## Responsibilities

- Generate a report without duplicated embedded photos.
- Validate every attachment is a readable canonical file inside the media root.
- Sanitize and sequence archive entry names by media type.
- Preserve coroutine cancellation between copy chunks.
- Publish a completed ZIP atomically and report skipped attachment count.

## Dependencies

- [[Components/Services/CasePdfExporter]], [[Components/Managers/MediaFileManager]], and [[Components/Utilities/CaseShareFiles]].
- `Case`, `MediaItem`, `MediaType`, ZIP streams, and application cache.

## Called By

- [[Components/ViewModels/CaseDetailViewModel]]

## Calls

- `CasePdfExporter.export(case, includePhotos = false)`.
- `canonicalReadableFileWithinRoot`, filename sanitation, and `CaseArchiveWriter.write`.
- Shared partial-file publication and pruning utilities.

## Important Methods

- `export(case)` returns `CaseZipExportResult(file, skippedAttachmentCount)`.
- `CaseArchiveWriter.write` streams the report and attachments with a 64 KiB buffer.
- `canonicalReadableFileWithinRoot` prevents files outside the managed root from entering the archive.
- `sanitizeArchiveFileName` removes path/control characters, handles Windows device names, and limits UTF-8 length.

## Design Patterns

- Streaming archive writer, canonical-path allowlist, cancellation cooperation, and write-then-publish safety.

## Common Pitfalls

- Unavailable attachments do not fail export; they are counted and listed in `unavailable_attachments.txt`.
- The temporary PDF is always deleted after ZIP creation.
- Entry names are sanitized and may differ from original filenames.
- The caller should hold [[Components/Repositories/DataSafetyCoordinator]] to prevent concurrent media mutation.

## Related Pages

- [[Components/Services/CasePdfExporter]]
- [[Features/Case Detail and Sharing]]
- [[Components/Repositories/MediaRepository]]

## Source References

- `features/src/main/java/com/taha/kairos/features/cases/CaseZipExporter.kt`
- `features/src/main/java/com/taha/kairos/features/cases/CaseShareFiles.kt`
