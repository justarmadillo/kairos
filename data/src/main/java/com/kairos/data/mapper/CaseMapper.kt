package com.kairos.data.mapper

import com.kairos.core.model.Case
import com.kairos.core.model.Diagnosis
import com.kairos.core.model.MediaItem
import com.kairos.core.model.MediaType
import com.kairos.data.db.entities.CaseEntity
import com.kairos.data.db.entities.CaseMediaEntity
import com.kairos.data.db.entities.DiagnosisEntity
import com.kairos.data.db.relations.CaseWithRelations

fun CaseWithRelations.toDomain(): Case = Case(
    id = case.id,
    patientId = case.patientId,
    patient = patient?.toDomain(),
    caseDate = case.caseDate,
    mechanism = case.mechanism,
    notesHtml = case.notesHtml,
    diagnoses = diagnoses.map { it.toDomain() },
    media = media.map { it.toDomain() },
    createdAt = case.createdAt,
    updatedAt = case.updatedAt,
    deletedAt = case.deletedAt,
)

fun DiagnosisEntity.toDomain(): Diagnosis = Diagnosis(
    id = id,
    name = name,
    caseCount = 0,
)

fun CaseMediaEntity.toDomain(): MediaItem = MediaItem(
    id = id,
    caseId = caseId,
    filePath = filePath,
    mediaType = MediaType.entries.find { it.name == mediaType } ?: MediaType.IMAGE,
    durationMs = durationMs,
    isPrimary = isPrimary,
    createdAt = createdAt,
)

fun Case.toEntity(now: Long): CaseEntity = CaseEntity(
    id = id,
    patientId = patientId,
    caseDate = caseDate,
    mechanism = mechanism,
    notesHtml = notesHtml,
    createdAt = if (id == 0L) now else createdAt,
    updatedAt = now,
)
