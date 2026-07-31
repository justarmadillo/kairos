# SearchViewModel

> **In plain words** — turns typing into results. Two techniques carry the whole class: **debounce** (wait 250 ms after the last keystroke, so one search runs instead of one per letter) and **switch** (`flatMapLatest` abandons the previous search when a newer one starts, so stale results can never overtake fresh ones). The text you see in the box updates instantly regardless — responsiveness and querying are deliberately decoupled. See [[Learn/Coroutines And Flow|Coroutines And Flow]].

## Purpose

Translate free-text search input into a debounced, reactive list of case-oriented search results.

## Responsibilities

- Store the current query.
- Debounce input by 250 ms and suppress identical consecutive queries.
- Cancel the old result flow when a new debounced query arrives.
- Combine current text and results into `SearchUiState`.

## Dependencies

- [[Components/Repositories/SearchRepository|SearchRepository]]
- Kotlin Coroutines Flow and `viewModelScope`.

## Called By

`SearchScreen` collects `ui`; the text field calls `setQuery()` and its clear button calls `clearQuery()`.

## Calls

`SearchRepository.observeSearch(query)` through `flatMapLatest`.

## Important Methods

- `setQuery(value)` — immediately updates visible input.
- `clearQuery()` — resets input to blank.

## Design Patterns

- `@HiltViewModel` constructor injection.
- Reactive search pipeline: `debounce` → `distinctUntilChanged` → `flatMapLatest`.
- Immutable state shared with `SharingStarted.WhileSubscribed(5_000)`.

## Common Pitfalls

- `SearchUiState` has no loading or error field.
- The visible query changes before the debounced result Flow switches, so old results can appear briefly.
- Blank-query behavior is delegated to the repository even though the screen renders search guidance.
- This searches local data; it is unrelated to device authorization or a remote service.

## Related Pages

- [[Features/Global Search|Global Search]]
- [[Features/Quick Capture Widget|Quick Capture Widget]]
- [[Components/ViewModels/CaseDetailViewModel|CaseDetailViewModel]]
- [[Architecture/State Management]]

## Source references

- `features/src/main/java/com/taha/kairos/features/search/SearchViewModel.kt`
- `features/src/main/java/com/taha/kairos/features/search/SearchScreen.kt`
- `core/src/main/java/com/taha/kairos/core/repository/SearchRepository.kt`
- `data/src/main/java/com/taha/kairos/data/repository/SearchRepositoryImpl.kt`
