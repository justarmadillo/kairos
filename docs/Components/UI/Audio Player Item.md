# Audio Player Item

> **In plain words** — one voice note with play/pause, a seek bar, and elapsed/total time. The duration comes from the database (it was measured when recorded) rather than being computed from the file each time a list is drawn — reading every audio file to draw a list would be slow.

## Purpose

Render one voice note with play/pause, seek, elapsed/total duration, and optional deletion.

## Responsibilities

Own an `ExoPlayer` for `filePath`, poll playback every 250 ms, reset at end, and release the player when the composable leaves composition.

## Dependencies

Compose Material 3, Media3 `ExoPlayer`, a local file URI, and the shared `formatDuration` utility.

## Called By

`CaseDetailScreen` and [[Components/UI/Media Attachment Section|Media Attachment Section]].

## Calls

Media3 prepare/play/pause/seek/release and the optional deletion callback.

## Important Methods

`AudioPlayerItem(...)`; internal lifecycle work is implemented with `LaunchedEffect` and `DisposableEffect`.

## Design Patterns

Composable-owned resource keyed by file path; state hoisting for deletion only.

## Common Pitfalls

Each visible item owns a player and a permanent polling coroutine. Missing/unreadable files surface through player behavior rather than explicit UI error state.

## Related Pages

- [[Features/Media Capture and Playback|Media Capture and Playback]]
- [[Components/Services/AudioRecorderEngine|Audio Recorder Engine]]

## Source references

- `core/src/main/java/com/taha/kairos/core/components/AudioPlayerItem.kt`
- `features/src/main/java/com/taha/kairos/features/cases/CaseDetailScreen.kt`
