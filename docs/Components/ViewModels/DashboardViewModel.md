# DashboardViewModel

> **In plain words** — the brain behind the home screen. It is the clearest example of **combining** streams: patient, case, and shift totals, recent cases, date-range comparisons, and settings all arrive independently, and any one of them changing produces a new dashboard state. It also derives things the database does not store, such as the backup-health warning and the milestone celebration.

## Purpose

Aggregate reactive application metrics, period comparisons, milestone presentation, and backup-health messaging for the dashboard.

## Responsibilities

- Combine active patient, case, and shift totals with recent cases.
- Calculate current/previous week and month case counts.
- Calculate the next/previous milestone and transient celebration state.
- Convert backup settings and last-run data into a warning.

## Dependencies

- [[Components/Repositories/DashboardRepository|DashboardRepository]]
- [[Components/Repositories/SettingsRepository|SettingsRepository]]
- `java.time` system-zone date calculations.

## Called By

`DashboardScreen` obtains it through `hiltViewModel()`, collects `ui`, and renders every field in `DashboardUiState`.

## Calls

- `observeTotalPatients()`, `observeTotalCases()`, `observeTotalShifts()`, and `observeRecentCases()`.
- Four `countCasesInRange(startMs, endMs)` calls per total-case emission.
- `SettingsRepository.observeSettings()`.

## Important Methods

- `loadPeriodCounts()` — Monday-based weekly and calendar-month ranges.
- `backupWarningFor()` — off, failed, and stale-backup messages.
- `maybeTriggerMilestoneCelebration()` — exact-threshold transition and 2.5-second clear.
- `nextMilestone()` / `previousMilestone()` — public pure helpers for stepped targets.

## Design Patterns

- `@HiltViewModel` and constructor-injected repository contracts.
- Flow composition with `combine`, `map`, and `stateIn`.
- Immutable `DashboardUiState` and `MilestoneProgress`.
- `SharingStarted.WhileSubscribed(5_000)` to retain state briefly across collectors.

## Common Pitfalls

- Initial state shows zeros rather than an explicit loading state.
- Period counts are tied to total-case emissions rather than a timer at midnight.
- Celebration requires landing exactly on a threshold after the first observed total.
- Backup warning wording is not conditioned on whether any patient data exists.
- Repository Flow failures are not caught in this ViewModel.

## Related Pages

- [[Features/Dashboard|Dashboard]]
- [[Features/Settings and Backup|Settings and Backup]]
- [[Architecture/State Management]]
- [[Execution Flows/Data Loading]]

## Source references

- `features/src/main/java/com/taha/kairos/features/dashboard/DashboardViewModel.kt`
- `features/src/main/java/com/taha/kairos/features/dashboard/DashboardScreen.kt`
- `core/src/main/java/com/taha/kairos/core/repository/DashboardRepository.kt`
- `core/src/main/java/com/taha/kairos/core/repository/SettingsRepository.kt`
