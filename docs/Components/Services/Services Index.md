# Services Index

> **In plain words** — "service" here does not mean an Android background service; it means a class that does one heavy, self-contained job: record audio, produce a backup archive, build a PDF, build a ZIP. They are separated from repositories and ViewModels because each is complex enough to be worth reading, testing, and changing on its own.

- [[Components/Services/AudioRecorderEngine]]
- [[Components/Services/BackupEngine]]
- [[Components/Services/CasePdfExporter]]
- [[Components/Services/CaseZipExporter]]

## Source References

- `core/src/main/java/com/taha/kairos/core/media/AudioRecorderEngine.kt`
- `data/src/main/java/com/taha/kairos/data/backup/BackupEngine.kt`
- `features/src/main/java/com/taha/kairos/features/cases/CasePdfExporter.kt`
- `features/src/main/java/com/taha/kairos/features/cases/CaseZipExporter.kt`

