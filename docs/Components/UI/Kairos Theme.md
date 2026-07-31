# Kairos Theme

> **In plain words** — the app's colours, fonts, and shapes in one place. Components ask the theme for "the primary colour" instead of naming a specific colour, which is why light and dark mode work with no per-screen code: the theme supplies a different value and everything follows. Wrapped around the whole app once, in `MainActivity`.

## Purpose

Define the shared visual system consumed by app and feature composables.

## Responsibilities

Select light/dark Material 3 color schemes, provide Inter typography and shape tokens, publish `LocalKairosExtraColors`, and apply system-theme selection supplied by `MainActivity`.

## Dependencies

Compose Material 3, bundled Inter font resources, and the persisted `ThemeMode` interpreted by `MainActivity`.

## Called By

`MainActivity` wraps all authorization and authorized content in `KairosTheme`; shared/feature composables consume Material and extra colors.

## Calls

Material 3 `MaterialTheme` and Compose composition locals.

## Important Methods

`KairosTheme(darkTheme, content)`; `KairosExtraColors` models non-Material tokens.

## Design Patterns

Composition-local design system with token files split by color, type, and shape.

## Common Pitfalls

Theme changes are reactive because `MainActivity` collects settings. Hard-coded colors bypass dark-mode tokens; use Material or `LocalKairosExtraColors` instead.

## Related Pages

- [[Features/Settings and Backup|Settings and Backup]]
- [[Layers/UI Layer|UI Layer]]

## Source references

- `core/src/main/java/com/taha/kairos/core/theme/KairosTheme.kt`
- `core/src/main/java/com/taha/kairos/core/theme/Color.kt`
- `core/src/main/java/com/taha/kairos/core/theme/Type.kt`
- `core/src/main/java/com/taha/kairos/core/theme/Shape.kt`
- `app/src/main/java/com/taha/kairos/MainActivity.kt`
