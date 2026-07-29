package com.taha.kairos.core.model

data class Patient(
    val id: Long = 0,
    val name: String,
    val age: Int? = null,
    val phones: List<PatientPhone> = emptyList(),
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val deletedAt: Long? = null,
)

data class PatientPhone(
    val id: Long = 0,
    val number: String,
    val label: String? = null
)
