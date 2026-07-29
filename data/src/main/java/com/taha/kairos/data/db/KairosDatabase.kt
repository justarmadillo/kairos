package com.taha.kairos.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.taha.kairos.data.db.dao.CaseDao
import com.taha.kairos.data.db.dao.CaseMediaDao
import com.taha.kairos.data.db.dao.ConsultationSessionDao
import com.taha.kairos.data.db.dao.DiagnosisDao
import com.taha.kairos.data.db.dao.PatientDao
import com.taha.kairos.data.db.dao.ShiftDao
import com.taha.kairos.data.db.entities.CaseDiagnosisCrossRef
import com.taha.kairos.data.db.entities.CaseEntity
import com.taha.kairos.data.db.entities.CaseMediaEntity
import com.taha.kairos.data.db.entities.ConsultationCaseCrossRef
import com.taha.kairos.data.db.entities.ConsultationSessionEntity
import com.taha.kairos.data.db.entities.DiagnosisEntity
import com.taha.kairos.data.db.entities.PatientEntity
import com.taha.kairos.data.db.entities.PatientPhoneEntity
import com.taha.kairos.data.db.entities.ShiftCaseCrossRef
import com.taha.kairos.data.db.entities.ShiftEntity

@Database(
    entities = [
        PatientEntity::class,
        PatientPhoneEntity::class,
        CaseEntity::class,
        CaseDiagnosisCrossRef::class,
        CaseMediaEntity::class,
        DiagnosisEntity::class,
        ShiftEntity::class,
        ShiftCaseCrossRef::class,
        ConsultationSessionEntity::class,
        ConsultationCaseCrossRef::class,
    ],
    version = 2,
    exportSchema = true,
)
abstract class KairosDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun caseDao(): CaseDao
    abstract fun diagnosisDao(): DiagnosisDao
    abstract fun caseMediaDao(): CaseMediaDao
    abstract fun shiftDao(): ShiftDao
    abstract fun consultationSessionDao(): ConsultationSessionDao
}
