package com.kairos.data.mapper

import com.kairos.core.model.Patient
import com.kairos.core.model.PatientPhone
import com.kairos.data.db.entities.PatientEntity
import com.kairos.data.db.entities.PatientPhoneEntity
import com.kairos.data.db.relations.PatientWithPhones

fun PatientWithPhones.toDomain(): Patient = Patient(
    id = patient.id,
    name = patient.name,
    age = patient.age,
    phones = phones.map { it.toDomain() },
    createdAt = patient.createdAt,
    updatedAt = patient.updatedAt,
    deletedAt = patient.deletedAt,
)

fun PatientPhoneEntity.toDomain(): PatientPhone = PatientPhone(
    id = id,
    number = number,
    label = label,
)

fun Patient.toEntity(now: Long): PatientEntity = PatientEntity(
    id = id,
    name = name,
    age = age,
    createdAt = if (id == 0L) now else createdAt,
    updatedAt = now,
)

fun PatientPhone.toEntity(patientId: Long): PatientPhoneEntity = PatientPhoneEntity(
    id = id,
    patientId = patientId,
    number = number,
    label = label,
)
