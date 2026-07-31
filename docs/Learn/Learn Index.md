# Learn Index

This section is the beginner track of the wiki. Every other section describes **what Kairos does**. This section explains **the programming ideas those descriptions assume you already know**.

If a page elsewhere in the wiki uses a word you do not recognise — *coroutine*, *DAO*, *Hilt*, *composable*, *migration* — the explanation lives here.

## Who this is for

Someone who can use a computer, can read plain English, and has little or no programming background. No prior Kotlin, Java, Android, or SQL knowledge is assumed. Terms are introduced one at a time and always tied back to real Kairos code.

## Suggested reading order

Read top to bottom the first time. Later, come back to single pages as reference.

**Part 1 — Ground floor**

1. [[Learn/How To Read This Wiki|How To Read This Wiki]] — page structure, wiki links, source references, file paths.
2. [[Learn/What Is An App|What Is An App]] — from source code to an installed app on a phone.
3. [[Learn/Programming Fundamentals|Programming Fundamentals]] — values, types, functions, classes, lists, null.
4. [[Learn/Kotlin Basics|Kotlin Basics]] — the actual language Kairos is written in.

**Part 2 — The platform**

5. [[Learn/Android App Basics|Android App Basics]] — Activity, Application, manifest, permissions, lifecycle, API levels.
6. [[Learn/Gradle And Modules|Gradle And Modules]] — how the four Kairos modules are built and wired together.
7. [[Learn/Jetpack Compose Basics|Jetpack Compose Basics]] — how every screen in Kairos is drawn.

**Part 3 — Moving and storing data**

8. [[Learn/Coroutines And Flow|Coroutines And Flow]] — doing slow work without freezing the screen, and live data streams.
9. [[Learn/Databases And Room|Databases And Room]] — tables, SQL, keys, joins, transactions, migrations.
10. [[Learn/Data Storage Choices|Data Storage Choices]] — Room vs DataStore vs files vs Firestore, and why Kairos uses each.

**Part 4 — How the code is organised**

11. [[Learn/Architecture Patterns|Architecture Patterns]] — layers, MVVM, unidirectional data flow.
12. [[Learn/Dependency Injection Explained|Dependency Injection Explained]] — what Hilt does and why the code looks like that.
13. [[Learn/Design Patterns Glossary|Design Patterns Glossary]] — repository, DAO, mapper, singleton, gate, soft delete.

**Part 5 — Working with the project**

14. [[Learn/Code Tour One Feature|Code Tour One Feature]] — one user tap traced through every file it touches.
15. [[Learn/Reading The Codebase|Reading The Codebase]] — naming conventions, where things live, how to trace a bug.
16. [[Learn/Build And Run|Build And Run]] — building, installing, debug vs release.
17. [[Learn/Testing Basics|Testing Basics]] — what the existing tests prove.
18. [[Learn/Security And Privacy Basics|Security And Privacy Basics]] — device authorization, App Check, why clinical data never leaves the phone.
19. [[Learn/How Kairos Was Built|How Kairos Was Built]] — the project as an engineering case study.
20. [[Learn/Glossary|Glossary]] — every term, alphabetical.
21. [[Learn/Common Questions|Common Questions]] — plain answers to the questions this codebase provokes.

## The one-paragraph version of Kairos

Kairos is an Android app that stores surgical/clinical records **on the phone itself**. Patients, cases, diagnoses, shifts, consultation sessions, notes, photos, audio, and files live in a local database called Room. Nothing clinical is uploaded anywhere. The only network call the app makes is a check asking a server "is this specific phone allowed to use this app?". If that check fails for long enough, the app locks itself but still lets you export your data. Everything else — search, statistics, PDF export, backup, trash — runs offline.

## Related pages

- [[Home]]
- [[Overview/Project Overview|Project Overview]]
- [[Overview/Overview Index|Overview]]
