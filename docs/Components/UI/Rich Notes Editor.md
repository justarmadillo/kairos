# Rich Notes Editor

> **In plain words** — the notes field with bold, lists, and other formatting. Formatting has to be stored somehow, and it is stored as **HTML** — the same markup web pages use — in the `notes_html` column. That choice has visible consequences elsewhere: search results strip the HTML back to plain text before showing a preview, and PDF export has to render it.

## Purpose

Edit case notes as HTML-backed rich text.

## Responsibilities

Render the rich editor, keep focused content visible above the keyboard, and expose bold, italic, underline, bullet-list, and numbered-list controls.

## Dependencies

`richeditor-compose`, `RichTextState`, Compose relocation APIs, and Kairos theme colors.

## Called By

`NewPatientTab`.

## Calls

Mutating methods on the caller-owned `RichTextState`.

## Important Methods

`RichNotesEditor(...)`; private toolbar and toggle-button composables.

## Design Patterns

State object injection with a reusable toolbar; keyboard accommodation through `BringIntoViewRequester`.

## Common Pitfalls

Notes are stored as HTML, so search sees markup as well as text and exports must convert HTML to plain text. The 250 ms focus delay is UI timing, not a correctness guarantee.

## Related Pages

- [[Features/Patient and Case Capture|Patient and Case Capture]]
- [[Components/Repositories/SearchRepository|Search Repository]]

## Source references

- `core/src/main/java/com/taha/kairos/core/components/RichNotesEditor.kt`
- `features/src/main/java/com/taha/kairos/features/patient/NewPatientTab.kt`
- `features/src/main/java/com/taha/kairos/features/cases/CasePdfExporter.kt`
