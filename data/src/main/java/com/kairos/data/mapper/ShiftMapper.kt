package com.kairos.data.mapper

import com.kairos.core.model.Shift
import com.kairos.data.db.entities.ShiftEntity
import com.kairos.data.db.relations.ShiftWithCount

fun ShiftWithCount.toDomain(): Shift = Shift(
    id = id,
    label = label,
    date = date,
    caseCount = caseCount,
    createdAt = createdAt,
)

fun ShiftEntity.toDomain(): Shift = Shift(
    id = id,
    label = label,
    date = date,
    caseCount = 0,
    createdAt = createdAt,
    deletedAt = deletedAt,
)

fun Shift.toEntity(now: Long): ShiftEntity = ShiftEntity(
    id = id,
    label = label,
    date = date,
    createdAt = if (id == 0L) now else createdAt,
)
