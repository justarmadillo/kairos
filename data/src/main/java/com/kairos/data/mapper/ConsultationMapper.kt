package com.kairos.data.mapper

import com.kairos.core.model.ConsultationSession
import com.kairos.data.db.entities.ConsultationSessionEntity
import com.kairos.data.db.relations.SessionWithCount

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
