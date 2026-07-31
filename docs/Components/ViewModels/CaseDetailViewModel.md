# CaseDetailViewModel

> **In plain words** — the brain behind the single-case screen. It watches one case (so any edit elsewhere shows up here immediately) and handles deleting it, changing its attachments, and exporting it as a PDF or ZIP. The export result is a **one-shot** value: it is consumed once by the screen to launch the share sheet and then cleared, so rotating the phone does not reopen the share dialog.

## Purpose

Expose one complete case and coordinate case deletion, media mutations, and PDF/ZIP share export.

## Responsibilities

- Read and observe the route's case ID.
- Soft-delete the case.
- Delete media or change its primary item.
- Serialize one share export at a time.
- Publish and clear one-shot share payloads and user messages.

## Dependencies

- `SavedStateHandle`
- [[Components/Repositories/CaseRepository|CaseRepository]]
- [[Components/Repositories/MediaRepository|MediaRepository]]
- [[Components/Services/CasePdfExporter|CasePdfExporter]]
- [[Components/Services/CaseZipExporter|CaseZipExporter]]
- [[Components/Repositories/DataSafetyCoordinator|DataSafetyCoordinator]]

## Called By

- `CaseDetailScreen` collects `ui` and invokes delete, media, export, and consumption methods.
- `ImageViewerScreen` obtains a destination-scoped instance to observe the same route `caseId` for visual media.

## Calls

- `CaseRepository.observeById(caseId)` and `softDelete(caseId)`.
- `MediaRepository.delete(mediaId)` and `setPrimary(caseId, mediaId)`.
- `DataSafetyCoordinator.withDataLock { pdfExporter.export(...) / zipExporter.export(...) }`.

## Important Methods

- `softDelete(onDeleted)` — deletes, then invokes the navigation callback.
- `deleteMedia(mediaId)` / `setPrimaryMedia(mediaId)`.
- `exportPdf()` / `exportZip()` — format-specific entry points.
- `export(format)` — duplicate guard, data lock, payload construction, skipped-file warning, and failure mapping.
- `clearSharePayload()`, `reportShareFailure()`, and `clearMessage()` — one-shot event consumption.

## Design Patterns

- `@HiltViewModel` with navigation arguments and constructor injection.
- Reactive entity state combined with an orthogonal command state.
- One-shot event represented as state and explicitly consumed by the UI.
- Strategy selection between two exporters.
- Data-safety critical section; coroutine cancellation is rethrown.

## Common Pitfalls

- Missing `caseId` sets `isError`, but `CaseDetailScreen` does not render the flag; a missing/deleted case appears blank.
- Delete and media mutations do not catch or surface repository failure.
- `setPrimaryMedia()` has no current case-detail UI caller.
- Export uses the current state snapshot; later repository emissions do not change an in-progress file.
- Clearing the payload in `finally` prevents automatic re-share after configuration change but means a failed launcher must set a separate message.

## Related Pages

- [[Features/Case Detail and Sharing|Case Detail and Sharing]]
- [[Features/Media Capture and Playback|Media Capture and Playback]]
- [[Components/ViewModels/PatientCaseViewModel|PatientCaseViewModel]]
- [[Architecture/Error Handling]]

## Source references

- `features/src/main/java/com/taha/kairos/features/cases/CaseDetailViewModel.kt`
- `features/src/main/java/com/taha/kairos/features/cases/CaseDetailScreen.kt`
- `features/src/main/java/com/taha/kairos/features/cases/ImageViewerScreen.kt`
- `features/src/main/java/com/taha/kairos/features/cases/CasePdfExporter.kt`
- `features/src/main/java/com/taha/kairos/features/cases/CaseZipExporter.kt`
- `core/src/main/java/com/taha/kairos/core/repository/CaseRepository.kt`
- `core/src/main/java/com/taha/kairos/core/repository/MediaRepository.kt`
