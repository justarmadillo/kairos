# SearchRepository

> **In plain words** — search in two stages, for a reason worth understanding. The query is split into words; the **longest** word is handed to SQL to narrow the rows cheaply using an index, and then every remaining word is required to appear in the assembled text of each candidate row. Asking SQL for the whole multi-word match would be slow; filtering everything in memory would be slower still. Narrow in the database, refine in memory.

## Purpose

Implements global case search across patient, phone, age, case, diagnosis, and note text.

## Responsibilities

- Normalize a query into distinct lowercase tokens.
- Use the longest token as an indexed-down SQL candidate anchor.
- Require every query token to appear in the assembled row text.
- Map search rows into `SearchResult` domain projections.

## Dependencies

- [[Components/DAOs/CaseDao]] and its `SearchCaseRow` projection.
- `SearchResult`, Kotlin `Flow`, and locale-aware lowercase conversion.

## Called By

- [[Components/ViewModels/SearchViewModel]]

## Calls

- `CaseDao.observeSearchCases(likeQuery, limit = 120)`.
- Private tokenization, wildcard escaping, row filtering, and list-field mapping helpers.

## Important Methods

- `observeSearch(query)` returns an empty Flow for an empty token set.
- `searchTokens()` trims, lowercases, splits whitespace, and deduplicates.
- `escapeLikeWildcards()` escapes `\\`, `%`, and `_` for SQL `LIKE`.
- `searchableText()` combines all searchable columns before all-token matching.

## Design Patterns

- Two-stage search: SQL candidate reduction followed by deterministic Kotlin filtering.
- Reactive DAO query and read-model mapping.

## Common Pitfalls

- SQL considers only the longest token and caps candidates at 120; a valid all-token match beyond that window is not returned.
- Final results are capped at 50.
- Raw HTML notes are searched as markup-inclusive text.
- Locale-aware lowercase can produce locale-specific matching behavior.

## Related Pages

- [[Features/Global Search]]
- [[Components/DAOs/CaseDao]]
- [[Layers/Data Sources]]

## Source References

- `core/src/main/java/com/taha/kairos/core/repository/SearchRepository.kt`
- `data/src/main/java/com/taha/kairos/data/repository/SearchRepositoryImpl.kt`
- `data/src/main/java/com/taha/kairos/data/di/DataModule.kt`

