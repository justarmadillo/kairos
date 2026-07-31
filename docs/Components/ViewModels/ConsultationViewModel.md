# ConsultationViewModel

> **In plain words** — the brain behind the consultation calendar. It builds the two-year date strip from the configured weekday, tracks which day is selected, and switches to that day's cases whenever the selection changes. The dates themselves are computed, not stored, so changing the consultation weekday in settings reshapes the calendar with no data change at all.

## Purpose

Coordinate the configured consultation calendar, selected day/session, and cases linked to that session.

## Responsibilities

- Observe the consultation weekday setting.
- Observe consultation sessions in a fixed two-year window.
- Build date-strip presentation entries and select only eligible weekdays.
- Derive cases for the selected session.
- Lazily get or create the selected session before patient capture.

## Dependencies

- [[Components/Repositories/SettingsRepository|SettingsRepository]]
- [[Components/Repositories/ConsultationRepository|ConsultationRepository]]
- [[Components/Repositories/CaseRepository|CaseRepository]]
- `java.time` and the system time zone.

## Called By

`ConsultationCalendarScreen` collects `ui` and `cases`, calls date-selection controls, and passes navigation as the callback to `getOrCreateSessionForSelected()`.

## Calls

- `SettingsRepository.observeSettings()`.
- `ConsultationRepository.observeForDateRange()` and `getOrCreateForDate()`.
- `CaseRepository.observeBySession()`.

## Important Methods

- `selectDate(date)` — accepts only the currently configured weekday.
- `selectPreviousConsultationDate()` / `selectNextConsultationDate()`.
- `getOrCreateSessionForSelected(onReady)` — creates/restores the date session and returns its ID.
- `buildDateList()` — joins dates with sessions.
- `selectAdjacentConsultationDate()` — weekday search bounded to the window.

## Design Patterns

- `@HiltViewModel` repository injection.
- Derived state with `combine`, `flatMapLatest`, and two public `StateFlow` values.
- Lazy entity creation immediately before navigation.
- Immutable `ConsultationUiState` and `DateItem` presentation models.

## Common Pitfalls

- The range is computed once at ViewModel creation and does not roll forward at midnight.
- It materializes roughly 731 `DateItem` objects on every relevant emission.
- Today is initially selected even when it is not a consultation day.
- Settings changes restart an otherwise identical session query.
- Repository errors and get-or-create failure are not represented in state.

## Related Pages

- [[Features/Consultation Calendar|Consultation Calendar]]
- [[Components/ViewModels/PatientCaseViewModel|PatientCaseViewModel]]
- [[Features/Settings and Backup|Settings and Backup]]
- [[Architecture/State Management]]

## Source references

- `features/src/main/java/com/taha/kairos/features/consultation/ConsultationViewModel.kt`
- `features/src/main/java/com/taha/kairos/features/consultation/ConsultationCalendarScreen.kt`
- `core/src/main/java/com/taha/kairos/core/repository/ConsultationRepository.kt`
- `core/src/main/java/com/taha/kairos/core/repository/CaseRepository.kt`
- `core/src/main/java/com/taha/kairos/core/repository/SettingsRepository.kt`
