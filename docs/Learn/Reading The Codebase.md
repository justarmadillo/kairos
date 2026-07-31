# Reading The Codebase

How to find your way around ~150 files without reading them all.

## Naming conventions

Kairos names things predictably, so a file name tells you what a file is.

| Suffix | Means | Lives in |
|---|---|---|
| `...Screen` | a Compose screen | `:features` |
| `...ViewModel` | state holder for one screen | `:features` or `:app` |
| `...Repository` | interface — the contract | `:core` |
| `...RepositoryImpl` | the implementation | `:data` |
| `...Dao` | database access interface | `:data` |
| `...Entity` | one database table's row shape | `:data` |
| `...Mapper` | entity ↔ domain translation | `:data` |
| `...Worker` | background job | `:data` |
| `...Engine` / `...Exporter` | a focused service doing one heavy job | `:data` / `:features` |
| `...UiState` | one screen's complete state | next to its ViewModel |
| `..._Impl` | **generated** by Room or Hilt — never edit | `build/` |

Corollary: if you want to know *what can be done with cases*, open `CaseRepository.kt` (30 lines of interface), not `CaseRepositoryImpl.kt` (hundreds of lines of how).

## Where things live

```
core/     models, repository interfaces, shared UI components, theme, media helpers
data/     Room (entities, DAOs, database, migrations), mappers, repository impls,
          DataStore, Firestore authorization, backup engine, workers, Hilt modules
features/ one package per feature: screen + ViewModel (+ exporters)
app/      MainActivity, KairosApplication, navigation, bottom bar, auth gate, widget
```

Features are grouped by *feature*, not by type: `features/cases/` holds `CaseDetailScreen`, `CaseDetailViewModel`, `CaseFeedScreen`, `CaseFeedViewModel`, `CasePdfExporter` together. Everything you need to change one feature is in one folder.

## Four questions and where to start

**"Where does this screen get its data?"**
Screen → its `ViewModel` (same folder) → the repository interfaces in its constructor → `...Impl` in `:data` → the DAO → the SQL.

**"What happens when I tap this?"**
Find the `onClick` in the screen. It calls either a callback parameter (→ look in `KairosNavHost`, it is navigation) or a ViewModel method (→ it is an action).

**"Where is this text on screen coming from?"**
Search the codebase for the literal string. Compose screens contain their text inline, so this works reliably.

**"What writes to this table?"**
Search for the entity name (`CaseMediaEntity`) or the table name (`case_media`). Every write goes through a DAO, and every DAO call goes through a repository — there is no other path.

## Reading a file in the right order

1. **Skip the imports.** The first 20–40 lines are noise.
2. **Read the class declaration and its constructor.** That is the complete list of what this class can touch.
3. **Read the public functions' names and signatures.** That is what it offers.
4. **Only then** read a body, and only the one you care about.

## Recognising generated code

If a path contains `/build/`, it is output — regenerated on every build. `CaseDao_Impl.kt` is Room's generated SQL execution code. Reading it is occasionally instructive, but editing it is meaningless.

## Tracing a bug

A workable, non-guessing procedure:

1. **Reproduce it.** Note the exact screen and exact action.
2. **Locate the screen file** by name (`SearchScreen.kt`).
3. **Decide the layer.** Wrong pixels/layout → the screen. Wrong or stale values → the ViewModel or below. Wrong data even after restarting the app → the database or repository.
4. **Read the ViewModel's state.** What does the `UiState` say it should show? Is the field even populated?
5. **Follow one repository call down** into the impl, then the DAO's SQL. Most data bugs are a `WHERE` clause or a mapper field.
6. **Check the pitfalls section** of the relevant wiki page — several known traps are already written down there.

Reasoning tip: prefer bisecting to guessing. Ask "is the wrong value already wrong in the ViewModel?" — that single question eliminates half the codebase.

## Where the surprises are

Ranked by how often they bite:

- **Media paths.** Relative in the database, absolute out of the repository. Mixing them up produces images that will not load.
- **Soft delete.** Every list query must carry `is_deleted = 0`. Forget it and trashed records reappear.
- **Two-step saves.** `upsertCase` does *not* write attachments; `MediaRepository` is a separate call. See [[Components/Repositories/CaseRepository|CaseRepository]].
- **Authorization gate.** In a locked state the navigation graph is never composed, so a screen that "doesn't appear" may not be a navigation bug at all.
- **Flow lifetimes.** `WhileSubscribed(5_000)` means a query stops five seconds after the last observer leaves. Nothing is broken; it is by design.

## Using the wiki alongside the code

Each component page lists **Called By** and **Calls**. Before changing a repository method, read its **Called By** list — that is the blast radius. Before trusting a page, check its **Source references** and confirm the code still matches; the wiki describes the code, and code moves.

## Related pages

- [[Learn/Code Tour One Feature|Code Tour One Feature]]
- [[Overview/Folder Structure|Folder Structure]]
- [[Components/Components Index|Components]]
- [[Learn/Common Questions|Common Questions]]
