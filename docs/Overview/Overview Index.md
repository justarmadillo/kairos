# Overview

> **In plain words** — this section answers "what is this project, and how is it put together?" before any code is read. If terms like *module*, *Gradle*, or *dependency* are new, read [[Learn/What Is An App|What Is An App]] and [[Learn/Gradle And Modules|Gradle And Modules]] first; the whole beginner track is at [[Learn/Learn Index|Learn]].

Start here for the project-wide map. Detailed runtime behavior lives in [[Architecture/Architecture Index|Architecture]], while concrete traces live in [[Execution Flows/Execution Flows Index|Execution Flows]].

- [[Overview/Project Overview|Project Overview]] — product scope and key constraints
- [[Overview/Architecture|Architecture]] — architectural style and boundaries
- [[Overview/Technology Stack|Technology Stack]] — platform and library choices
- [[Overview/Folder Structure|Folder Structure]] — source layout
- [[Overview/Build System|Build System]] — Gradle, variants, signing, and verification
- [[Overview/Gradle Modules|Gradle Modules]] — module responsibilities and dependency direction
- [[Overview/Dependencies|Dependencies]] — dependency catalog and module allocation

## Source references

- `settings.gradle.kts`
- `gradle/libs.versions.toml`
- `app/src/main/java/com/taha/kairos/MainActivity.kt`
- `data/src/main/java/com/taha/kairos/data/di/DataModule.kt`
