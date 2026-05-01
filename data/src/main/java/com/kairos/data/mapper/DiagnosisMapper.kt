package com.kairos.data.mapper

import com.kairos.core.model.Diagnosis
import com.kairos.data.db.relations.DiagnosisWithCount

fun DiagnosisWithCount.toDomain(): Diagnosis = Diagnosis(
    id = id,
    name = name,
    caseCount = caseCount,
)
