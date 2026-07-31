# Common Questions

Plain answers to the questions this codebase provokes.

## Why so many files for one screen?

Because each file changes for a different reason. The screen changes when the design changes. The DAO changes when the query changes. The mapper changes when the shape changes. Merged into one file, every change risks every behaviour — and the compiler could no longer stop a screen from writing SQL. Walk through a real example in [[Learn/Code Tour One Feature|Code Tour One Feature]].

## Why an interface *and* an implementation for every repository?

So `:features` can depend on a contract without depending on the database. That gives testability, replaceability, and a one-screen summary of what can be done with each concept. → [[Learn/Architecture Patterns|Architecture Patterns]]

## Why two classes for a case — `Case` and `CaseEntity`?

`CaseEntity` is a database row, complete with `is_deleted`, `sync_state`, and `remote_id`. `Case` is what the app means by a case. Keeping them apart means adding a storage column does not ripple into every screen, and storage bookkeeping can never leak into the UI.

## Where is the login screen?

There isn't one. Kairos authorizes **devices**, not people. A server document decides whether this phone may run the app, so an administrator can revoke a lost device instantly and there is no password to steal. → [[Features/Device Authorization|Device Authorization]]

## Why does the app work with no internet?

Because all clinical data is on the phone. The network is used only to refresh the device authorization lease. → [[Learn/Data Storage Choices|Data Storage Choices]]

## The app locked itself. Is my data gone?

No. The database is untouched; the UI is simply not composed. The locked screen still offers a full export to a folder you pick — deliberately, so losing access to the software never means losing the records.

## Why is the lease 48 hours?

24 hours of normal validity plus a 24-hour offline grace period. Long enough to cover a weekend without signal, short enough that a revoked device stops working quickly. → [[Learn/Security And Privacy Basics|Security And Privacy Basics]]

## Can someone bypass it by changing the phone's clock?

That specific attack is closed. Lease age is measured with the monotonic clock (`elapsedRealtime`), which cannot be edited; a reboot invalidates the offline lease because that clock resets; and a wall-clock rollback of more than five minutes below the highest time ever observed marks the lease invalid.

## Why does deleting not delete?

It sets `is_deleted = 1` and a `deleted_at` timestamp. Every normal query filters those out; the trash screen shows them; a purge worker removes them permanently after the retention period. Accidental deletion of a patient record should be recoverable. → [[Features/Trash and Retention|Trash and Retention]]

## Why does the list update without a refresh button?

Room queries that return `Flow` re-run automatically whenever the queried tables change. The change travels repository → ViewModel → Compose, and only the affected rows redraw. → [[Learn/Coroutines And Flow|Coroutines And Flow]]

## Why are images stored as files rather than in the database?

Databases are for structured, queryable data. Hundreds of megabytes of photos and video would make every query and backup slow. Kairos stores the bytes in private app storage and only the **relative** path in the database — relative so a restore onto a different phone still resolves.

## My image will not load. Why?

Almost always a path problem. The database holds relative paths; `MediaFileManager.resolve` converts them to absolute ones, and repositories do that before returning data for display. If you bypassed the repository, you have a relative path where an absolute one is needed.

## What is the difference between a DAO and a repository?

A DAO knows SQL and nothing else — one function, one database operation. A repository knows the *rules*: it coordinates several DAOs, wraps writes in transactions, takes the data lock, maps entities to domain models, and resolves media paths.

## What does `@Inject` actually do?

Nothing at runtime by itself. At build time, Hilt reads it and generates the code that constructs the object and its dependencies. If it cannot, the build fails. → [[Learn/Dependency Injection Explained|Dependency Injection Explained]]

## What is `suspend` for?

It marks a function that may take real time and is therefore allowed to pause without blocking the main thread — so the UI never freezes during a database write.

## Why `StateFlow` and not just a variable?

A plain variable cannot notify anyone when it changes. A `StateFlow` always holds a current value *and* pushes updates to whoever is subscribed, which is exactly what a screen needs.

## What is `WhileSubscribed(5_000)`?

Keep the underlying query alive while a screen is watching, plus five seconds. The grace period stops a screen rotation from tearing down and immediately re-running the query.

## Why does adding a database column break the app?

Because the database file on the phone still has the old shape. Room refuses to guess. You must bump the version and write a `Migration` with the SQL to transform existing data — skipping this destroys real records. → [[Learn/Databases And Room|Databases And Room]]

## Why four Gradle modules instead of one?

To make the architecture physically enforced rather than merely agreed. `:features` cannot import `:data`, so no screen can reach the database directly. Faster incremental builds are a bonus. → [[Learn/Gradle And Modules|Gradle And Modules]]

## What are the `_Impl` files?

Generated code — Room's real DAO implementations, written by KSP during the build. They live under `build/` and must never be edited or committed.

## Why does the release build fail on my machine?

Most likely `keystore.properties` is missing, incomplete, or points at a key file that is not there. The build checks this on purpose so an unsigned or misconfigured release cannot be produced by accident. → [[Learn/Build And Run|Build And Run]]

## Where do I start if I want to change something?

Find the screen file by name, read its ViewModel, then follow the repository interface in the ViewModel's constructor. The procedure is written out in [[Learn/Reading The Codebase|Reading The Codebase]].

## What is the riskiest kind of change here?

Anything touching deletion, migration, backup, or authorization. Those are the places where a silent bug costs patient records rather than pixels. Read the **Common Pitfalls** section of the relevant page first, and prefer adding a test.

## Related pages

- [[Learn/Learn Index|Learn Index]]
- [[Learn/Reading The Codebase|Reading The Codebase]]
- [[Learn/Glossary|Glossary]]
