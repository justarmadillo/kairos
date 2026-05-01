package com.kairos.data.repository

import com.kairos.core.model.Shift
import com.kairos.core.repository.ShiftRepository
import com.kairos.data.db.dao.ShiftDao
import com.kairos.data.mapper.toDomain
import com.kairos.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShiftRepositoryImpl @Inject constructor(
    private val dao: ShiftDao,
) : ShiftRepository {

    override suspend fun upsert(shift: Shift): Long {
        val now = System.currentTimeMillis()
        return if (shift.id == 0L) {
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

    override suspend fun softDelete(id: Long) =
        dao.softDelete(id, System.currentTimeMillis())

    override suspend fun restore(id: Long) =
        dao.restore(id)

    override fun observeTrashed(): Flow<List<Shift>> =
        dao.observeTrashed().map { list -> list.map { it.toDomain() } }
}
