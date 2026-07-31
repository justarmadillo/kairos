# How Kairos Was Built

Kairos read as an engineering case study: the decisions, the trade-offs each one bought, and what a beginner should take from them.

## The requirement

A surgeon needs to record patients, cases, diagnoses, shifts, consultation sessions, notes, photos, and voice memos — fast, in a hospital, on a phone, without depending on a signal, without a hospital IT project, and without patient data ending up on someone else's server.

Almost every structural decision below follows from those constraints. This is the real lesson: architecture is downstream of requirements, not fashion.

## Decision 1 — local-first, no clinical backend

**Chosen:** everything in a local Room database; the network used only to check device authorization.

**Bought:** works with no signal; no server to breach, host, or pay for; instant reads; no data-processing agreement needed.

**Cost:** the phone is the only copy. That single cost is why backup, trash retention, and export are first-class features rather than afterthoughts — a whole subsystem exists to pay for this decision. → [[Learn/Data Storage Choices|Data Storage Choices]]

## Decision 2 — device authorization instead of user accounts

**Chosen:** a Firestore whitelist of device IDs, with a 24-hour lease and a 24-hour offline grace period.

**Bought:** revocation of a lost phone; no password to phish or share; no account system to build; offline operation.

**Cost:** it controls devices, not people — anyone who can unlock the phone sees the data.

The lease logic is worth studying as a security exercise: the naive version (store an expiry timestamp) is defeated by changing the phone's clock, so the real implementation measures age with the monotonic clock, invalidates on reboot, and rejects wall-clock rollbacks. → [[Learn/Security And Privacy Basics|Security And Privacy Basics]]

## Decision 3 — four Gradle modules

**Chosen:** `:core` (models + contracts), `:data` (persistence), `:features` (screens), `:app` (assembly).

**Bought:** the architecture is enforced by the compiler. `:features` does not depend on `:data`, so no screen can reach the database, no matter how convenient it would be one afternoon.

**Cost:** more build files, and a new dependency sometimes has to be threaded through `:core`.

Take from it: the best rules are the ones you cannot break by accident. → [[Learn/Gradle And Modules|Gradle And Modules]]

## Decision 4 — repository interfaces in `:core`

**Chosen:** contracts in `:core`, implementations in `:data`, wired by Hilt.

**Bought:** ViewModels are testable without a database; storage can change without touching screens; each interface is a readable summary of one concept.

**Cost:** two files per repository, and one more indirection to follow when reading.

## Decision 5 — no use-case layer

**Chosen:** ViewModels call repositories directly.

**Bought:** far fewer files; less forwarding ceremony.

**Cost:** orchestration logic lives in ViewModels, so some of them are larger than they would otherwise be.

This is a deliberate right-sizing. A textbook Clean Architecture diagram would add a use-case layer; for an app this size that layer would mostly forward calls. Knowing when *not* to apply a pattern is a senior skill. → [[Components/Use Cases|Use Cases]]

## Decision 6 — Compose and a single Activity

**Chosen:** one `MainActivity`, all screens as composables, navigation by route.

**Bought:** declarative UI with no manual view mutation; screens that are previewable and testable because they take state and callbacks.

**Cost:** a modern-only toolkit and `minSdk 26`.

## Decision 7 — soft delete plus scheduled purge

**Chosen:** `is_deleted` + `deleted_at`, a trash screen, a purge worker.

**Bought:** accidental deletion is recoverable — the correct trade in a clinical setting.

**Cost:** every list query must remember `is_deleted = 0`, forever. A rule you must not forget is a rule you eventually forget, which is why it appears in the pitfalls of several wiki pages.

## Decision 8 — media as files, metadata in the database

**Chosen:** bytes in private app storage; **relative** paths in `case_media`; absolute paths resolved on the way out.

**Bought:** the database stays small and fast; backups can move media independently; a restore onto a new device still resolves every attachment.

**Cost:** a two-representation rule that must be respected. → [[Components/Managers/MediaFileManager|MediaFileManager]]

## Decision 9 — a global data lock

**Chosen:** `DataSafetyCoordinator.withDataLock { }` around case writes and backup runs.

**Bought:** a backup can never capture a half-written case.

**Cost:** writes are globally serialised — a deliberate choice of safety over throughput, correct when the alternative is a corrupted patient record.

## Decision 10 — the build refuses a misconfigured release

**Chosen:** a Gradle task that fails the release build if signing properties or the key file are missing.

**Bought:** a whole class of "oops, shipped it wrong" mistakes made impossible.

**Cost:** none worth mentioning.

Take from it: encode operational rules in the build, not in a checklist someone reads once.

## Where the complexity actually lives

Not in the screens — those are largely lists and forms. The hard parts are:

1. **Authorization leasing** — clock trust, reboot handling, fail-closed behaviour.
2. **Backup and restore** — verification, generational retention, media plus database together.
3. **Case save** — one logical operation spanning several tables, links, and files, under a lock.
4. **Search** — one SQL query covering names, ages, phones, mechanism, notes, and diagnoses.

If you want to learn the most per page read, read those four.

## What a beginner should take away

- Requirements drive architecture. Every layer here pays for something specific.
- Make invariants structural. Modules, transactions, and build checks beat good intentions.
- Separate pure logic from the framework, and it becomes testable for free — that is why `AuthorizationLeasePolicy` and `BackupPruner` have tests and the screens do not.
- Right-size the patterns. Skipping the use-case layer was as considered a decision as adding the data lock.
- Design for the bad day: lost phone, no signal, accidental delete, broken device. Kairos has an answer for each, and those answers *are* the codebase.

## Related pages

- [[Overview/Project Overview|Project Overview]]
- [[Overview/Architecture|Architecture]]
- [[Learn/Architecture Patterns|Architecture Patterns]]
- [[Learn/Security And Privacy Basics|Security And Privacy Basics]]
- [[Learn/Code Tour One Feature|Code Tour One Feature]]
