package com.taha.kairos.data.mapper

import com.taha.kairos.core.model.ConsultationSession
import com.taha.kairos.data.db.entities.ConsultationSessionEntity
import com.taha.kairos.data.db.relations.SessionWithCount

fun SessionWithCount.toDomain(): ConsultationSession = ConsultationSession(
    id = id,
    date = date,
    caseCount = caseCount,
)

fun ConsultationSessionEntity.toDomain(): ConsultationSession = ConsultationSession(
    id = id,
    date = date,
    caseCount = 0,
    deletedAt = deletedAt,
)
