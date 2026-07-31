# Media Attachment Section

> **In plain words** — the attachments area of the case form: photos, videos, audio, and documents, with one marked primary. Note the word *pending* on this page — files chosen but not yet saved are staged in the ViewModel, and only committed to permanent storage when the case is saved. Abandoning the form must not leave records pointing at files, or files with no record.

## Purpose

Render and edit the pending/existing media collection in the patient-case form.

## Responsibilities

Expose photo, video, gallery, audio, and file actions; render visual thumbnails, voice notes, and file rows; emit remove and primary-selection events.

## Dependencies

`MediaDisplayItem`, `MediaType`, Coil, [[Components/UI/Audio Player Item|Audio Player Item]], and Material 3.

## Called By

`NewPatientTab`.

## Calls

All capture/import/remove/primary callbacks supplied by the screen.

## Important Methods

`MediaAttachmentSection(...)`; private `VisualThumbnail(...)` and `FileAttachmentItem(...)`.

## Design Patterns

State-hoisted editor with type-based presentation and stable local IDs.

## Common Pitfalls

Clicking a visual selects it as primary; deletion uses a separate overlay button. The component assumes file paths are readable and does not enforce the single-primary invariant itself.

## Related Pages

- [[Features/Media Capture and Playback|Media Capture and Playback]]
- [[Components/Repositories/MediaRepository|Media Repository]]

## Source references

- `core/src/main/java/com/taha/kairos/core/components/MediaAttachmentSection.kt`
- `features/src/main/java/com/taha/kairos/features/patient/NewPatientTab.kt`
