package com.kairos.data.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.kairos.data.db.entities.PatientEntity
import com.kairos.data.db.entities.PatientPhoneEntity

data class PatientWithPhones(
    @Embedded val patient: PatientEntity,
    @Relation(parentColumn = "id", entityColumn = "patient_id")
    val phones: List<PatientPhoneEntity>
)
