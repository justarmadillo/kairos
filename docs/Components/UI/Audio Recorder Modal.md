# Audio Recorder Modal

> **In plain words** — the voice-note recording dialog. It draws the recording UI but owns none of the recording: start, stop, and cancel are callbacks handled by the caller. That separation is why the same dialog cannot get out of step with the actual recorder state.

## Purpose

Present the voice-note recording dialog while delegating recorder state and actions to its caller.

## Responsibilities

Show idle/recording copy, elapsed time, start/stop controls, and cancellation.

## Dependencies

Compose Material 3 and caller-provided state/callbacks.

## Called By

`NewPatientTab` in [[Features/Patient and Case Capture|Patient and Case Capture]].

## Calls

`onStart`, `onStop`, or `onCancel`; it performs no microphone I/O itself.

## Important Methods

`AudioRecorderModal(...)` and `formatDuration(ms)`.

## Design Patterns

Stateless dialog with event callbacks; recording lifecycle remains in [[Components/ViewModels/PatientCaseViewModel|PatientCaseViewModel]].

## Common Pitfalls

Dismissing the dialog is cancellation, not stop-and-save. Permissions and recorder exceptions must be handled outside this component.

## Related Pages

- [[Components/Services/AudioRecorderEngine|Audio Recorder Engine]]
- [[Components/UI/Audio Player Item|Audio Player Item]]

## Source references

- `core/src/main/java/com/taha/kairos/core/components/AudioRecorderModal.kt`
- `features/src/main/java/com/taha/kairos/features/patient/NewPatientTab.kt`
