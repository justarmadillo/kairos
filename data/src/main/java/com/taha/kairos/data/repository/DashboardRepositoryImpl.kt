package com.taha.kairos.data.repository

import com.taha.kairos.core.model.toCapitalizedPatientName
import com.taha.kairos.core.repository.DashboardRepository
import com.taha.kairos.core.repository.RecentCase
import com.taha.kairos.data.db.dao.CaseDao
import com.taha.kairos.data.db.dao.PatientDao
import com.taha.kairos.data.db.dao.ShiftDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepositoryImpl @Inject constructor(
    private val patientDao: PatientDao,
    private val caseDao: CaseDao,
    private val shiftDao: ShiftDao,
) : DashboardRepository {

    override fun observeTotalPatients(): Flow<Int> =
        patientDao.observeTotalPatients()

    override fun observeTotalCases(): Flow<Int> =
        caseDao.observeTotalCases()

    override fun observeTotalShifts(): Flow<Int> =
        shiftDao.observeTotalShifts()

    override fun observeRecentCases(): Flow<List<RecentCase>> =
        caseDao.observeRecentCases().map { rows ->
            rows.map { row ->
                RecentCase(
                    caseId = row.caseId,
                    patientName = row.patientName.toCapitalizedPatientName(),
                    diagnosisName = row.diagnosisName,
                    caseDate = row.caseDate,
                )
            }
        }

    override suspend fun countCasesInRange(startMs: Long, endMs: Long): Int =
        caseDao.countCasesInRange(startMs, endMs)
}
