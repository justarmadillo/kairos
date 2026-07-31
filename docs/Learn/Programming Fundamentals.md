# Programming Fundamentals

The seven ideas you need before any Kairos code makes sense. Examples use Kotlin, but the concepts are universal.

## 1. A value has a name and a type

```kotlin
val patientAge: Int = 47
```

- `val` — declare a name.
- `patientAge` — the name.
- `Int` — the **type**: a whole number.
- `47` — the value.

A **type** is a promise about what kind of thing this is. `Int` can hold `47` but never `"Ahmed"`. The compiler enforces this before the app is ever built, which kills an entire family of bugs for free. Common types in Kairos:

| Type | Holds | Example in Kairos |
|---|---|---|
| `Int` | whole number | patient age |
| `Long` | big whole number | every ID, and every timestamp |
| `String` | text | patient name, diagnosis name |
| `Boolean` | true / false | `isDeleted` |
| `List<T>` | many of type `T` | `List<Diagnosis>` on a case |

Why `Long` for time: Kairos stores every date as **milliseconds since 1 January 1970**, a single big number. `1750000000000` is a moment in time. Storing time as a number instead of text makes sorting and comparing trivial.

## 2. `val` vs `var`

```kotlin
val id = 12      // cannot be reassigned
var count = 0    // can be reassigned
count = count + 1
```

Kairos overwhelmingly uses `val`. Values that cannot change are values that cannot be changed *behind your back*, which matters enormously once several things run at once. See [[Learn/Coroutines And Flow|Coroutines And Flow]].

## 3. A function is a named action

```kotlin
fun add(a: Int, b: Int): Int {
    return a + b
}
```

- `fun` — declaring a function.
- `add` — its name.
- `(a: Int, b: Int)` — **parameters**: what it needs.
- `: Int` — **return type**: what it hands back.

You **call** it with `add(2, 3)` and get `5`. Functions are how work is named and reused. `softDelete(id)` is a function; you do not care how it deletes, only that it does.

## 4. A class is a blueprint; an object is one made from it

A class describes the *shape* of a thing. An object is one actual thing of that shape.

```kotlin
data class Patient(
    val id: Long,
    val name: String,
    val age: Int?,
)
```

That is the blueprint. Now one actual patient:

```kotlin
val p = Patient(id = 1, name = "Sara", age = 34)
p.name   // "Sara"
```

`data class` is Kotlin's shorthand for "this class exists only to hold data" — it automatically gets sensible copying and comparison behaviour. Most of Kairos's information (`Case`, `Patient`, `Diagnosis`, `Shift`) is `data class`.

Read it as a **form with fields**. A blank patient form is the class; a filled-in form is the object.

## 5. Null means "nothing is here"

`null` is the deliberate absence of a value. It is not zero and not empty text. Kairos uses it constantly and precisely:

```kotlin
val age: Int?          // ? means: may be a number, may be null
val deletedAt: Long?   // null = not deleted; a number = deleted at that moment
```

The `?` is the crucial part. Kotlin will **refuse to compile** code that uses a possibly-null value carelessly. This eliminates the single most common crash in Java and Android history (`NullPointerException`).

To use one safely:

```kotlin
patient?.name           // if patient is null, the whole expression is null
patient?.name ?: "—"    // ?: means "or else use this instead"
```

## 6. Collections hold many things

```kotlin
val diagnoses: List<Diagnosis> = listOf(d1, d2, d3)
diagnoses.size                       // 3
diagnoses.map { it.name }            // List<String> of the names
diagnoses.filter { it.name != "" }   // only non-empty ones
```

`map` transforms every element. `filter` keeps only matching elements. `it` is the automatic name for "the current element". You will see `map` on nearly every screen in Kairos, because turning database rows into screen-ready values *is* mapping.

## 7. Control flow: choices and repetition

```kotlin
if (state.cases.isEmpty()) {
    showEmptyMessage()
} else {
    showList()
}

when (themeMode) {
    ThemeMode.LIGHT  -> false
    ThemeMode.DARK   -> true
    ThemeMode.SYSTEM -> followPhoneSetting()
}

for (case in cases) {
    print(case.id)
}
```

`when` is Kotlin's version of a multi-way switch, and it is used heavily in Kairos — that exact `ThemeMode` example is real, from `MainActivity`.

## Putting it together

Almost all application code is: *take some values, call some functions on them, decide between branches, produce new values.* Everything else in this wiki — repositories, ViewModels, Flows — is organisation on top of those four moves.

## Related pages

- [[Learn/Kotlin Basics|Kotlin Basics]]
- [[Learn/Architecture Patterns|Architecture Patterns]]
- [[Learn/Glossary|Glossary]]
