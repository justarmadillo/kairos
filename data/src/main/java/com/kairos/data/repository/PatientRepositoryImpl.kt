package com.kairos.data.repository

import androidx.room.withTransaction
import com.kairos.core.model.Patient
import com.kairos.core.repository.PatientRepository
import com.kairos.data.db.KairosDatabase
import com.kairos.data.db.dao.PatientDao
import com.kairos.data.mapper.toDomain
import com.kairos.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PatientRepositoryImpl @Inject constructor(
    private val dao: PatientDao,
    private val db: KairosDatabase,
) : PatientRepository {

    override suspend fun upsert(patient: Patient): Long {
        val now = System.currentTimeMillis()
        val entity = patient.toEntity(now)
        return db.withTransaction {
            if (patient.id == 0L) {
                val newId = dao.insert(entity)
                dao.deletePhonesFor(newId)
                if (patient.phones.isNotEmpty()) {
                    dao.insertPhones(patient.phones.map { it.toEntity(newId) })
                }
                newId
            } else {
                dao.update(entity)
                dao.deletePhonesFor(patient.id)
                if (patient.phones.isNotEmpty()) {
                    dao.insertPhones(patient.phones.map { it.toEntity(patient.id) })
                }
                patient.id
            }
        }
    }

    override suspend fun getById(id: Long): Patient? =
        dao.getById(id)?.toDomain()

    override fun observeById(id: Long): Flow<Patient?> =
        dao.observeById(id).map { it?.toDomain() }

    override fun search(query: String): Flow<List<Patient>> =
        dao.search(query).map { list -> list.map { it.toDomain() } }

    override suspend fun softDelete(id: Long) =
        dao.softDelete(id, System.currentTimeMillis())

    override suspend fun restore(id: Long) =
        dao.restore(id, System.currentTimeMillis())

    override fun observeTrashed(): Flow<List<Patient>> =
        dao.observeTrashed().map { list -> list.map { it.toDomain() } }
}
