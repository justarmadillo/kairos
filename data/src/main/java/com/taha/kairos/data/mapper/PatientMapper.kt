package com.taha.kairos.data.mapper

import com.taha.kairos.core.model.Patient
import com.taha.kairos.core.model.PatientPhone
import com.taha.kairos.core.model.toCapitalizedPatientName
import com.taha.kairos.data.db.entities.PatientEntity
import com.taha.kairos.data.db.entities.PatientPhoneEntity
import com.taha.kairos.data.db.relations.PatientWithPhones

fun PatientWithPhones.toDomain(): Patient = Patient(
    id = patient.id,
    name = patient.name.toCapitalizedPatientName(),
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
    name = name.trim().toCapitalizedPatientName(),
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
