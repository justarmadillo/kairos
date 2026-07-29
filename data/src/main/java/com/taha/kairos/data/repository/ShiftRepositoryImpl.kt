package com.taha.kairos.data.repository

import com.taha.kairos.core.model.Shift
import com.taha.kairos.core.repository.DataSafetyCoordinator
import com.taha.kairos.core.repository.ShiftRepository
import com.taha.kairos.data.db.dao.ShiftDao
import com.taha.kairos.data.mapper.toDomain
import com.taha.kairos.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShiftRepositoryImpl @Inject constructor(
    private val dao: ShiftDao,
    private val dataSafetyCoordinator: DataSafetyCoordinator,
) : ShiftRepository {

    override suspend fun upsert(shift: Shift): Long = dataSafetyCoordinator.withDataLock {
        val now = System.currentTimeMillis()
        if (shift.id == 0L) {
            dao.insert(shift.toEntity(now))
        } else {
            dao.update(shift.toEntity(now))
            shift.id
        }
    }

    override suspend fun getById(id: Long): Shift? =
        dao.getById(id)?.toDomain()

    override fun observeAll(): Flow<List<Shift>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeById(id: Long): Flow<Shift?> =
        dao.observeById(id).map { it?.toDomain() }

    override suspend fun softDelete(id: Long) = dataSafetyCoordinator.withDataLock {
        dao.softDelete(id, System.currentTimeMillis())
    }

    override suspend fun restore(id: Long) = dataSafetyCoordinator.withDataLock {
        dao.restore(id)
    }

    override fun observeTrashed(): Flow<List<Shift>> =
        dao.observeTrashed().map { list -> list.map { it.toDomain() } }
}
