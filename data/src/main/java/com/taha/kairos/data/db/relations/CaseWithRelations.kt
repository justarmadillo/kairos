package com.taha.kairos.data.db.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.taha.kairos.data.db.entities.CaseDiagnosisCrossRef
import com.taha.kairos.data.db.entities.CaseEntity
import com.taha.kairos.data.db.entities.CaseMediaEntity
import com.taha.kairos.data.db.entities.DiagnosisEntity
import com.taha.kairos.data.db.entities.PatientEntity

data class CaseWithRelations(
    @Embedded val case: CaseEntity,

    @Relation(parentColumn = "patient_id", entityColumn = "id", entity = PatientEntity::class)
    val patient: PatientWithPhones?,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = CaseDiagnosisCrossRef::class,
            parentColumn = "case_id",
            entityColumn = "diagnosis_id"
        )
    )
    val diagnoses: List<DiagnosisEntity>,

    @Relation(parentColumn = "id", entityColumn = "case_id")
    val media: List<CaseMediaEntity>
)
