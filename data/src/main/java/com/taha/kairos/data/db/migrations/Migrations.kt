package com.taha.kairos.data.db.migrations

import androidx.room.migration.Migration

/**
 * All Room schema migrations for KairosDatabase.
 *
 * HOW TO ADD A MIGRATION:
 * 1. Bump `version` in KairosDatabase.kt
 * 2. Add `val MIGRATION_X_Y = object : Migration(X, Y) { override fun migrate(db) { ... } }` below
 * 3. Add it to ALL_MIGRATIONS
 * 4. Run the app — Room applies it automatically on next open
 *
 * NEVER skip a version. Every increment needs an explicit Migration.
 * Test with MigrationTestHelper in androidTest before releasing.
 *
 * Common SQL patterns:
 *   ALTER TABLE foo ADD COLUMN bar TEXT                      (nullable column)
 *   ALTER TABLE foo ADD COLUMN bar INTEGER NOT NULL DEFAULT 0
 *   CREATE TABLE new_foo (...); INSERT INTO new_foo SELECT ... FROM foo; DROP TABLE foo; ALTER TABLE new_foo RENAME TO foo;
 */
object Migrations {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE case_media ADD COLUMN original_file_name TEXT")
        }
    }

    /** All migrations in chronological order. Passed to addMigrations() in DataModule. */
    val ALL_MIGRATIONS: Array<Migration> = arrayOf(
        MIGRATION_1_2,
    )
}
