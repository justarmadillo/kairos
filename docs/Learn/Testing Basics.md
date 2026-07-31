# Testing Basics

What automated tests are, and what Kairos's existing tests actually prove.

## The idea

A test is code that runs your code and asserts the result:

```kotlin
@Test
fun capitalizesEverySpaceSeparatedWord() {
    assertEquals(
        "John Michael Doe",
        "john michael doe".toCapitalizedPatientName(Locale.ENGLISH),
    )
}
```

`@Test` marks it runnable. `assertEquals(expected, actual)` fails the test if they differ. Run `./gradlew test` and every such function executes in seconds.

The value is not in catching today's bug — you would find that by hand. It is that the test keeps running forever, so the day someone "simplifies" name capitalisation, the build tells them they broke apostrophes.

## Two kinds

| | Unit test | Instrumented test |
|---|---|---|
| Folder | `src/test/` | `src/androidTest/` |
| Runs on | your computer's JVM | a real device or emulator |
| Speed | milliseconds | seconds to minutes |
| Can use Android APIs | no | yes |
| Command | `./gradlew test` | `./gradlew connectedAndroidTest` |

Kairos's tests are all unit tests, which is exactly why the pure-logic pieces were designed as pure logic.

## What is tested today

**`PatientNameTest`** (`:core`) — patient name capitalisation. Four cases: ordinary words, hyphens and both apostrophe characters, preservation of existing case and spacing (`"McDONALD"` must not become `"Mcdonald"`), and Unicode with combining marks. Real-world name handling is subtle, and these tests pin the behaviour down.

**`AuthorizationLeasePolicyTest`** (`:core`) — the security rules from [[Learn/Security And Privacy Basics|Security And Privacy Basics]]: fresh, grace, expired, reboot invalidation, clock rollback. Testable precisely because `AuthorizationLeasePolicy.evaluate` is a pure function taking a lease and a time snapshot — no database, no clock, no network.

**`BackupPrunerTest`** (`:data`) — generational retention. Given a list of backup file names, which should be deleted? Keep the newest few, keep the newest of each older month, delete the rest. Note the test style:

```kotlin
@Test
fun `keeps newest backup of older months`() { ... }
```

Kotlin allows backtick-quoted function names, so a failing test reads like a sentence describing what broke.

**`CaseArchiveWriterTest`** (`:features`) — case ZIP export.

**`AuthorizationGateViewModelTest`** (`:app`) — the authorization state machine, with a helper:

```kotlin
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) { Dispatchers.setMain(testDispatcher) }
    override fun finished(description: Description) { Dispatchers.resetMain() }
}
```

A ViewModel uses `Dispatchers.Main`, which does not exist off-device. This rule swaps in a test dispatcher before each test and restores it afterwards — and, being a `StandardTestDispatcher`, it lets the test control *when* coroutines advance instead of waiting on real time. This is the standard way to test coroutine code.

## The pattern to notice

Every tested piece has one property in common: **it is separable from Android.** `AuthorizationLeasePolicy` takes time as a parameter instead of calling the system clock. `BackupPruner` takes file names instead of touching the filesystem. That is not an accident — it is why they are cheap to test, and it is a good reason to keep pure logic pure.

Conversely, the untested parts are the ones welded to the framework: Compose screens, DAOs, repository implementations. Testing those needs an emulator (Room has `MigrationTestHelper` for verifying migrations, and Compose has UI test rules — the dependencies are already declared).

## Arrange, act, assert

Every test has the same three beats:

```kotlin
val names = (1..9).map { name("2026070$it") }        // arrange: set up inputs
val toDelete = BackupPruner.selectBackupsToDelete(names)   // act: run the thing
assertEquals(setOf(...), toDelete)                    // assert: check the result
```

## What to test if you add a feature

Prioritise by cost of being wrong: anything touching patient data integrity, deletion, backup, or authorization. Those are the places where a silent bug is not an annoyance but a lost record. Cosmetic layout is the least valuable thing to test automatically.

## Related pages

- [[Components/Utilities/AuthorizationLeasePolicy|AuthorizationLeasePolicy]]
- [[Components/Utilities/BackupPruner|BackupPruner]]
- [[Learn/Build And Run|Build And Run]]
- [[Learn/Architecture Patterns|Architecture Patterns]]
