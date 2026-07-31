# CasePdfExporter

> **In plain words** — turns one case into a PDF for sharing. The file is written into the app's **cache** folder, not permanent storage, because it is a disposable copy that Android may clean up later; the real record stays in the database.

## Purpose

Generates a shareable PDF report for one case in the app cache.

## Responsibilities

- Render patient and case details into paginated A4-like PDF pages.
- Convert HTML notes to plain text.
- Optionally decode and embed image attachments.
- Write through a partial file and publish only a complete artifact.
- Prune stale case-share files before export.

## Dependencies

- Application `Context`, Android `PdfDocument`/graphics APIs, `Case`, `MediaType`, and [[Components/Managers/MediaFileManager]].
- [[Components/Utilities/CaseShareFiles]].

## Called By

- [[Components/ViewModels/CaseDetailViewModel]]
- [[Components/Services/CaseZipExporter]] for the archive's report.

## Calls

- `PdfWriter` page, text-wrap, image-decode, and footer helpers.
- Shared file naming, pruning, and partial publication helpers.
- `MediaFileManager.rootDir` for attachment resolution.

## Important Methods

- `export(case, includePhotos = true)` runs on `Dispatchers.IO`, writes `.partial`, publishes, and cleans incomplete files.
- `PdfWriter.drawReport` lays out patient, case, diagnosis, notes, metadata, and optional photos.
- `decodeSampledBitmap` bounds memory use for embedded images.

## Design Patterns

- Injectable stateless export service.
- Builder/writer object for document layout.
- Write-then-publish file safety.

## Common Pitfalls

- Only image media can be embedded; ZIP export is required for all attachment types.
- PDF rendering strips HTML formatting into plain text.
- Cached share files are temporary and pruned after 24 hours.
- Export must stay under [[Components/Repositories/DataSafetyCoordinator]] when the caller needs a consistent case/files snapshot.

## Related Pages

- [[Components/Services/CaseZipExporter]]
- [[Components/Utilities/CaseShareFiles]]
- [[Features/Case Detail and Sharing]]

## Source References

- `features/src/main/java/com/taha/kairos/features/cases/CasePdfExporter.kt`
- `features/src/main/java/com/taha/kairos/features/cases/CaseShareFiles.kt`

