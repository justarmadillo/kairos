package com.kairos.`data`.db

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.kairos.`data`.db.dao.CaseDao
import com.kairos.`data`.db.dao.CaseDao_Impl
import com.kairos.`data`.db.dao.CaseMediaDao
import com.kairos.`data`.db.dao.CaseMediaDao_Impl
import com.kairos.`data`.db.dao.ConsultationSessionDao
import com.kairos.`data`.db.dao.ConsultationSessionDao_Impl
import com.kairos.`data`.db.dao.DiagnosisDao
import com.kairos.`data`.db.dao.DiagnosisDao_Impl
import com.kairos.`data`.db.dao.PatientDao
import com.kairos.`data`.db.dao.PatientDao_Impl
import com.kairos.`data`.db.dao.ShiftDao
import com.kairos.`data`.db.dao.ShiftDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class KairosDatabase_Impl : KairosDatabase() {
  private val _patientDao: Lazy<PatientDao> = lazy {
    PatientDao_Impl(this)
  }

  private val _caseDao: Lazy<CaseDao> = lazy {
    CaseDao_Impl(this)
  }

  private val _diagnosisDao: Lazy<DiagnosisDao> = lazy {
    DiagnosisDao_Impl(this)
  }

  private val _caseMediaDao: Lazy<CaseMediaDao> = lazy {
    CaseMediaDao_Impl(this)
  }

  private val _shiftDao: Lazy<ShiftDao> = lazy {
    ShiftDao_Impl(this)
  }

  private val _consultationSessionDao: Lazy<ConsultationSessionDao> = lazy {
    ConsultationSessionDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1,
        "a3ea9b795784c49850a473c04d0ab2ae", "314ce963743b03931462a869161cb769") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `patients` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `age` INTEGER, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `is_deleted` INTEGER NOT NULL, `deleted_at` INTEGER, `remote_id` TEXT, `sync_state` TEXT NOT NULL, `last_synced_at` INTEGER)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `patient_phones` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `patient_id` INTEGER NOT NULL, `number` TEXT NOT NULL, `label` TEXT, FOREIGN KEY(`patient_id`) REFERENCES `patients`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_patient_phones_patient_id` ON `patient_phones` (`patient_id`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `cases` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `patient_id` INTEGER NOT NULL, `case_date` INTEGER NOT NULL, `mechanism` TEXT, `notes_html` TEXT, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `is_deleted` INTEGER NOT NULL, `deleted_at` INTEGER, `remote_id` TEXT, `sync_state` TEXT NOT NULL, `last_synced_at` INTEGER, FOREIGN KEY(`patient_id`) REFERENCES `patients`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_cases_patient_id` ON `cases` (`patient_id`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_cases_case_date` ON `cases` (`case_date`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_cases_is_deleted` ON `cases` (`is_deleted`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `case_diagnoses` (`case_id` INTEGER NOT NULL, `diagnosis_id` INTEGER NOT NULL, PRIMARY KEY(`case_id`, `diagnosis_id`), FOREIGN KEY(`case_id`) REFERENCES `cases`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`diagnosis_id`) REFERENCES `diagnoses`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_case_diagnoses_diagnosis_id` ON `case_diagnoses` (`diagnosis_id`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `case_media` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `case_id` INTEGER NOT NULL, `file_path` TEXT NOT NULL, `media_type` TEXT NOT NULL, `duration_ms` INTEGER, `is_primary` INTEGER NOT NULL, `created_at` INTEGER NOT NULL, FOREIGN KEY(`case_id`) REFERENCES `cases`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_case_media_case_id` ON `case_media` (`case_id`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `diagnoses` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `remote_id` TEXT, `sync_state` TEXT NOT NULL)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_diagnoses_name` ON `diagnoses` (`name`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `shifts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `label` TEXT, `date` INTEGER NOT NULL, `created_at` INTEGER NOT NULL, `is_deleted` INTEGER NOT NULL, `deleted_at` INTEGER, `remote_id` TEXT, `sync_state` TEXT NOT NULL, `last_synced_at` INTEGER)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_shifts_date` ON `shifts` (`date`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_shifts_is_deleted` ON `shifts` (`is_deleted`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `shift_cases` (`shift_id` INTEGER NOT NULL, `case_id` INTEGER NOT NULL, PRIMARY KEY(`shift_id`, `case_id`), FOREIGN KEY(`shift_id`) REFERENCES `shifts`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`case_id`) REFERENCES `cases`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_shift_cases_case_id` ON `shift_cases` (`case_id`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `consultation_sessions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` INTEGER NOT NULL, `created_at` INTEGER NOT NULL, `is_deleted` INTEGER NOT NULL, `deleted_at` INTEGER, `remote_id` TEXT, `sync_state` TEXT NOT NULL, `last_synced_at` INTEGER)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_consultation_sessions_date` ON `consultation_sessions` (`date`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_consultation_sessions_is_deleted` ON `consultation_sessions` (`is_deleted`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `consultation_cases` (`session_id` INTEGER NOT NULL, `case_id` INTEGER NOT NULL, PRIMARY KEY(`session_id`, `case_id`), FOREIGN KEY(`session_id`) REFERENCES `consultation_sessions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`case_id`) REFERENCES `cases`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_consultation_cases_case_id` ON `consultation_cases` (`case_id`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'a3ea9b795784c49850a473c04d0ab2ae')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `patients`")
        connection.execSQL("DROP TABLE IF EXISTS `patient_phones`")
        connection.execSQL("DROP TABLE IF EXISTS `cases`")
        connection.execSQL("DROP TABLE IF EXISTS `case_diagnoses`")
        connection.execSQL("DROP TABLE IF EXISTS `case_media`")
        connection.execSQL("DROP TABLE IF EXISTS `diagnoses`")
        connection.execSQL("DROP TABLE IF EXISTS `shifts`")
        connection.execSQL("DROP TABLE IF EXISTS `shift_cases`")
        connection.execSQL("DROP TABLE IF EXISTS `consultation_sessions`")
        connection.execSQL("DROP TABLE IF EXISTS `consultation_cases`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        connection.execSQL("PRAGMA foreign_keys = ON")
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsPatients: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsPatients.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPatients.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPatients.put("age", TableInfo.Column("age", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPatients.put("created_at", TableInfo.Column("created_at", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPatients.put("updated_at", TableInfo.Column("updated_at", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPatients.put("is_deleted", TableInfo.Column("is_deleted", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPatients.put("deleted_at", TableInfo.Column("deleted_at", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPatients.put("remote_id", TableInfo.Column("remote_id", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPatients.put("sync_state", TableInfo.Column("sync_state", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPatients.put("last_synced_at", TableInfo.Column("last_synced_at", "INTEGER", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPatients: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesPatients: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoPatients: TableInfo = TableInfo("patients", _columnsPatients, _foreignKeysPatients,
            _indicesPatients)
        val _existingPatients: TableInfo = read(connection, "patients")
        if (!_infoPatients.equals(_existingPatients)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |patients(com.kairos.data.db.entities.PatientEntity).
              | Expected:
              |""".trimMargin() + _infoPatients + """
              |
              | Found:
              |""".trimMargin() + _existingPatients)
        }
        val _columnsPatientPhones: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsPatientPhones.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPatientPhones.put("patient_id", TableInfo.Column("patient_id", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPatientPhones.put("number", TableInfo.Column("number", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPatientPhones.put("label", TableInfo.Column("label", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPatientPhones: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysPatientPhones.add(TableInfo.ForeignKey("patients", "CASCADE", "NO ACTION",
            listOf("patient_id"), listOf("id")))
        val _indicesPatientPhones: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesPatientPhones.add(TableInfo.Index("index_patient_phones_patient_id", false,
            listOf("patient_id"), listOf("ASC")))
        val _infoPatientPhones: TableInfo = TableInfo("patient_phones", _columnsPatientPhones,
            _foreignKeysPatientPhones, _indicesPatientPhones)
        val _existingPatientPhones: TableInfo = read(connection, "patient_phones")
        if (!_infoPatientPhones.equals(_existingPatientPhones)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |patient_phones(com.kairos.data.db.entities.PatientPhoneEntity).
              | Expected:
              |""".trimMargin() + _infoPatientPhones + """
              |
              | Found:
              |""".trimMargin() + _existingPatientPhones)
        }
        val _columnsCases: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCases.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCases.put("patient_id", TableInfo.Column("patient_id", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCases.put("case_date", TableInfo.Column("case_date", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCases.put("mechanism", TableInfo.Column("mechanism", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCases.put("notes_html", TableInfo.Column("notes_html", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCases.put("created_at", TableInfo.Column("created_at", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCases.put("updated_at", TableInfo.Column("updated_at", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCases.put("is_deleted", TableInfo.Column("is_deleted", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCases.put("deleted_at", TableInfo.Column("deleted_at", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCases.put("remote_id", TableInfo.Column("remote_id", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCases.put("sync_state", TableInfo.Column("sync_state", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCases.put("last_synced_at", TableInfo.Column("last_synced_at", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCases: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysCases.add(TableInfo.ForeignKey("patients", "CASCADE", "NO ACTION",
            listOf("patient_id"), listOf("id")))
        val _indicesCases: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesCases.add(TableInfo.Index("index_cases_patient_id", false, listOf("patient_id"),
            listOf("ASC")))
        _indicesCases.add(TableInfo.Index("index_cases_case_date", false, listOf("case_date"),
            listOf("ASC")))
        _indicesCases.add(TableInfo.Index("index_cases_is_deleted", false, listOf("is_deleted"),
            listOf("ASC")))
        val _infoCases: TableInfo = TableInfo("cases", _columnsCases, _foreignKeysCases,
            _indicesCases)
        val _existingCases: TableInfo = read(connection, "cases")
        if (!_infoCases.equals(_existingCases)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |cases(com.kairos.data.db.entities.CaseEntity).
              | Expected:
              |""".trimMargin() + _infoCases + """
              |
              | Found:
              |""".trimMargin() + _existingCases)
        }
        val _columnsCaseDiagnoses: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCaseDiagnoses.put("case_id", TableInfo.Column("case_id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCaseDiagnoses.put("diagnosis_id", TableInfo.Column("diagnosis_id", "INTEGER", true,
            2, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCaseDiagnoses: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysCaseDiagnoses.add(TableInfo.ForeignKey("cases", "CASCADE", "NO ACTION",
            listOf("case_id"), listOf("id")))
        _foreignKeysCaseDiagnoses.add(TableInfo.ForeignKey("diagnoses", "CASCADE", "NO ACTION",
            listOf("diagnosis_id"), listOf("id")))
        val _indicesCaseDiagnoses: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesCaseDiagnoses.add(TableInfo.Index("index_case_diagnoses_diagnosis_id", false,
            listOf("diagnosis_id"), listOf("ASC")))
        val _infoCaseDiagnoses: TableInfo = TableInfo("case_diagnoses", _columnsCaseDiagnoses,
            _foreignKeysCaseDiagnoses, _indicesCaseDiagnoses)
        val _existingCaseDiagnoses: TableInfo = read(connection, "case_diagnoses")
        if (!_infoCaseDiagnoses.equals(_existingCaseDiagnoses)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |case_diagnoses(com.kairos.data.db.entities.CaseDiagnosisCrossRef).
              | Expected:
              |""".trimMargin() + _infoCaseDiagnoses + """
              |
              | Found:
              |""".trimMargin() + _existingCaseDiagnoses)
        }
        val _columnsCaseMedia: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCaseMedia.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCaseMedia.put("case_id", TableInfo.Column("case_id", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCaseMedia.put("file_path", TableInfo.Column("file_path", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCaseMedia.put("media_type", TableInfo.Column("media_type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCaseMedia.put("duration_ms", TableInfo.Column("duration_ms", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCaseMedia.put("is_primary", TableInfo.Column("is_primary", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCaseMedia.put("created_at", TableInfo.Column("created_at", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCaseMedia: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysCaseMedia.add(TableInfo.ForeignKey("cases", "CASCADE", "NO ACTION",
            listOf("case_id"), listOf("id")))
        val _indicesCaseMedia: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesCaseMedia.add(TableInfo.Index("index_case_media_case_id", false, listOf("case_id"),
            listOf("ASC")))
        val _infoCaseMedia: TableInfo = TableInfo("case_media", _columnsCaseMedia,
            _foreignKeysCaseMedia, _indicesCaseMedia)
        val _existingCaseMedia: TableInfo = read(connection, "case_media")
        if (!_infoCaseMedia.equals(_existingCaseMedia)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |case_media(com.kairos.data.db.entities.CaseMediaEntity).
              | Expected:
              |""".trimMargin() + _infoCaseMedia + """
              |
              | Found:
              |""".trimMargin() + _existingCaseMedia)
        }
        val _columnsDiagnoses: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDiagnoses.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDiagnoses.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDiagnoses.put("created_at", TableInfo.Column("created_at", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDiagnoses.put("remote_id", TableInfo.Column("remote_id", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDiagnoses.put("sync_state", TableInfo.Column("sync_state", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDiagnoses: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesDiagnoses: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesDiagnoses.add(TableInfo.Index("index_diagnoses_name", true, listOf("name"),
            listOf("ASC")))
        val _infoDiagnoses: TableInfo = TableInfo("diagnoses", _columnsDiagnoses,
            _foreignKeysDiagnoses, _indicesDiagnoses)
        val _existingDiagnoses: TableInfo = read(connection, "diagnoses")
        if (!_infoDiagnoses.equals(_existingDiagnoses)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |diagnoses(com.kairos.data.db.entities.DiagnosisEntity).
              | Expected:
              |""".trimMargin() + _infoDiagnoses + """
              |
              | Found:
              |""".trimMargin() + _existingDiagnoses)
        }
        val _columnsShifts: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsShifts.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShifts.put("label", TableInfo.Column("label", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShifts.put("date", TableInfo.Column("date", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShifts.put("created_at", TableInfo.Column("created_at", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShifts.put("is_deleted", TableInfo.Column("is_deleted", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShifts.put("deleted_at", TableInfo.Column("deleted_at", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShifts.put("remote_id", TableInfo.Column("remote_id", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShifts.put("sync_state", TableInfo.Column("sync_state", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShifts.put("last_synced_at", TableInfo.Column("last_synced_at", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysShifts: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesShifts: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesShifts.add(TableInfo.Index("index_shifts_date", false, listOf("date"),
            listOf("ASC")))
        _indicesShifts.add(TableInfo.Index("index_shifts_is_deleted", false, listOf("is_deleted"),
            listOf("ASC")))
        val _infoShifts: TableInfo = TableInfo("shifts", _columnsShifts, _foreignKeysShifts,
            _indicesShifts)
        val _existingShifts: TableInfo = read(connection, "shifts")
        if (!_infoShifts.equals(_existingShifts)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |shifts(com.kairos.data.db.entities.ShiftEntity).
              | Expected:
              |""".trimMargin() + _infoShifts + """
              |
              | Found:
              |""".trimMargin() + _existingShifts)
        }
        val _columnsShiftCases: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsShiftCases.put("shift_id", TableInfo.Column("shift_id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShiftCases.put("case_id", TableInfo.Column("case_id", "INTEGER", true, 2, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysShiftCases: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysShiftCases.add(TableInfo.ForeignKey("shifts", "CASCADE", "NO ACTION",
            listOf("shift_id"), listOf("id")))
        _foreignKeysShiftCases.add(TableInfo.ForeignKey("cases", "CASCADE", "NO ACTION",
            listOf("case_id"), listOf("id")))
        val _indicesShiftCases: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesShiftCases.add(TableInfo.Index("index_shift_cases_case_id", false,
            listOf("case_id"), listOf("ASC")))
        val _infoShiftCases: TableInfo = TableInfo("shift_cases", _columnsShiftCases,
            _foreignKeysShiftCases, _indicesShiftCases)
        val _existingShiftCases: TableInfo = read(connection, "shift_cases")
        if (!_infoShiftCases.equals(_existingShiftCases)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |shift_cases(com.kairos.data.db.entities.ShiftCaseCrossRef).
              | Expected:
              |""".trimMargin() + _infoShiftCases + """
              |
              | Found:
              |""".trimMargin() + _existingShiftCases)
        }
        val _columnsConsultationSessions: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsConsultationSessions.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsConsultationSessions.put("date", TableInfo.Column("date", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsConsultationSessions.put("created_at", TableInfo.Column("created_at", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsConsultationSessions.put("is_deleted", TableInfo.Column("is_deleted", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsConsultationSessions.put("deleted_at", TableInfo.Column("deleted_at", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsConsultationSessions.put("remote_id", TableInfo.Column("remote_id", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsConsultationSessions.put("sync_state", TableInfo.Column("sync_state", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsConsultationSessions.put("last_synced_at", TableInfo.Column("last_synced_at",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysConsultationSessions: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesConsultationSessions: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesConsultationSessions.add(TableInfo.Index("index_consultation_sessions_date", false,
            listOf("date"), listOf("ASC")))
        _indicesConsultationSessions.add(TableInfo.Index("index_consultation_sessions_is_deleted",
            false, listOf("is_deleted"), listOf("ASC")))
        val _infoConsultationSessions: TableInfo = TableInfo("consultation_sessions",
            _columnsConsultationSessions, _foreignKeysConsultationSessions,
            _indicesConsultationSessions)
        val _existingConsultationSessions: TableInfo = read(connection, "consultation_sessions")
        if (!_infoConsultationSessions.equals(_existingConsultationSessions)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |consultation_sessions(com.kairos.data.db.entities.ConsultationSessionEntity).
              | Expected:
              |""".trimMargin() + _infoConsultationSessions + """
              |
              | Found:
              |""".trimMargin() + _existingConsultationSessions)
        }
        val _columnsConsultationCases: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsConsultationCases.put("session_id", TableInfo.Column("session_id", "INTEGER", true,
            1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsConsultationCases.put("case_id", TableInfo.Column("case_id", "INTEGER", true, 2,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysConsultationCases: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysConsultationCases.add(TableInfo.ForeignKey("consultation_sessions", "CASCADE",
            "NO ACTION", listOf("session_id"), listOf("id")))
        _foreignKeysConsultationCases.add(TableInfo.ForeignKey("cases", "CASCADE", "NO ACTION",
            listOf("case_id"), listOf("id")))
        val _indicesConsultationCases: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesConsultationCases.add(TableInfo.Index("index_consultation_cases_case_id", false,
            listOf("case_id"), listOf("ASC")))
        val _infoConsultationCases: TableInfo = TableInfo("consultation_cases",
            _columnsConsultationCases, _foreignKeysConsultationCases, _indicesConsultationCases)
        val _existingConsultationCases: TableInfo = read(connection, "consultation_cases")
        if (!_infoConsultationCases.equals(_existingConsultationCases)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |consultation_cases(com.kairos.data.db.entities.ConsultationCaseCrossRef).
              | Expected:
              |""".trimMargin() + _infoConsultationCases + """
              |
              | Found:
              |""".trimMargin() + _existingConsultationCases)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "patients", "patient_phones",
        "cases", "case_diagnoses", "case_media", "diagnoses", "shifts", "shift_cases",
        "consultation_sessions", "consultation_cases")
  }

  public override fun clearAllTables() {
    super.performClear(true, "patients", "patient_phones", "cases", "case_diagnoses", "case_media",
        "diagnoses", "shifts", "shift_cases", "consultation_sessions", "consultation_cases")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(PatientDao::class, PatientDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(CaseDao::class, CaseDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(DiagnosisDao::class, DiagnosisDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(CaseMediaDao::class, CaseMediaDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ShiftDao::class, ShiftDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ConsultationSessionDao::class,
        ConsultationSessionDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun patientDao(): PatientDao = _patientDao.value

  public override fun caseDao(): CaseDao = _caseDao.value

  public override fun diagnosisDao(): DiagnosisDao = _diagnosisDao.value

  public override fun caseMediaDao(): CaseMediaDao = _caseMediaDao.value

  public override fun shiftDao(): ShiftDao = _shiftDao.value

  public override fun consultationSessionDao(): ConsultationSessionDao =
      _consultationSessionDao.value
}
