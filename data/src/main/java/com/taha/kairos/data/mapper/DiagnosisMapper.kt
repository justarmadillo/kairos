package com.taha.kairos.data.mapper

import com.taha.kairos.core.model.Diagnosis
import com.taha.kairos.data.db.relations.DiagnosisWithCount

fun DiagnosisWithCount.toDomain(): Diagnosis = Diagnosis(
    id = id,
    name = name,
    caseCount = caseCount,
)
