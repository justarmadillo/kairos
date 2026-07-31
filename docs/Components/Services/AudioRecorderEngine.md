# AudioRecorderEngine

> **In plain words** — a thin wrapper around Android's audio recorder. Wrapping it serves two purposes: the rest of the app deals with a small, clear interface (start, stop, cancel) instead of the platform's stateful API, and because it is injected rather than constructed inline, it can be replaced in a test. Recording must be stopped before a case is saved, or the file would be incomplete.

## Purpose

Wraps Android `MediaRecorder` behind a small injectable service for case audio capture.

## Responsibilities

- Configure and start AAC audio recording into a caller-provided file.
- Stop and release the active recorder safely.
- Cancel a recording and delete its output.
- Report whether a recorder is active.

## Dependencies

- Application `Context`, Android `MediaRecorder`, SDK-version checks, and Java `File`.

## Called By

- [[Components/ViewModels/PatientCaseViewModel]]

## Calls

- `MediaRecorder` configuration, `prepare`, `start`, `stop`, and `release`.
- `File.delete` during cancellation.

## Important Methods

- `start(outputFile)` first stops any previous recorder, then records MPEG-4/AAC, mono, 96 kbps, 44.1 kHz.
- `stop()` releases resources and tolerates the early-stop exception raised before audio data exists.
- `cancel(outputFile)` stops and deletes the file.
- `isRecording` reflects whether the wrapper owns a recorder instance.

## Design Patterns

- Singleton platform-service adapter with explicit resource lifecycle.
- Version-adaptive construction for Android 12 and earlier devices.

## Common Pitfalls

- Microphone permission is a caller/UI responsibility.
- Swallowed `stop` exceptions mean callers must validate output usability if needed.
- The caller tracks elapsed duration; the engine does not return it despite the legacy method comment.
- The output file lifecycle must be coordinated with pending media cleanup.

## Related Pages

- [[Components/Managers/MediaFileManager]]
- [[Features/Patient and Case Capture]]
- [[Components/UI/Audio Recorder Modal]]

## Source References

- `core/src/main/java/com/taha/kairos/core/media/AudioRecorderEngine.kt`
- `features/src/main/java/com/taha/kairos/features/patient/PatientCaseViewModel.kt`
