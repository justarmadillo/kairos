package com.taha.kairos.data.di

import android.content.Context
import androidx.room.Room
import com.taha.kairos.core.authorization.DeviceAuthorizationRepository
import com.taha.kairos.core.repository.BackupRepository
import com.taha.kairos.core.repository.CaseRepository
import com.taha.kairos.core.repository.ConsultationRepository
import com.taha.kairos.core.repository.DataSafetyCoordinator
import com.taha.kairos.core.repository.DashboardRepository
import com.taha.kairos.core.repository.DiagnosisRepository
import com.taha.kairos.core.repository.MediaRepository
import com.taha.kairos.core.repository.PatientRepository
import com.taha.kairos.core.repository.SearchRepository
import com.taha.kairos.core.repository.SettingsRepository
import com.taha.kairos.core.repository.ShiftRepository
import com.taha.kairos.data.db.KairosDatabase
import com.taha.kairos.data.db.migrations.Migrations
import com.taha.kairos.data.db.dao.CaseDao
import com.taha.kairos.data.db.dao.CaseMediaDao
import com.taha.kairos.data.db.dao.ConsultationSessionDao
import com.taha.kairos.data.db.dao.DiagnosisDao
import com.taha.kairos.data.db.dao.PatientDao
import com.taha.kairos.data.db.dao.ShiftDao
import com.taha.kairos.data.backup.BackupEngine
import com.taha.kairos.data.backup.DataSafetyCoordinatorImpl
import com.taha.kairos.data.authorization.FirebaseDeviceAuthorizationRepository
import com.taha.kairos.data.repository.CaseRepositoryImpl
import com.taha.kairos.data.repository.ConsultationRepositoryImpl
import com.taha.kairos.data.repository.DashboardRepositoryImpl
import com.taha.kairos.data.repository.DiagnosisRepositoryImpl
import com.taha.kairos.data.repository.MediaRepositoryImpl
import com.taha.kairos.data.repository.PatientRepositoryImpl
import com.taha.kairos.data.repository.SearchRepositoryImpl
import com.taha.kairos.data.repository.SettingsRepositoryImpl
import com.taha.kairos.data.repository.ShiftRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KairosDatabase =
        Room.databaseBuilder(context, KairosDatabase::class.java, "kairos.db")
            .addMigrations(*Migrations.ALL_MIGRATIONS)
            .build()

    @Provides fun providePatientDao(db: KairosDatabase): PatientDao = db.patientDao()
    @Provides fun provideCaseDao(db: KairosDatabase): CaseDao = db.caseDao()
    @Provides fun provideDiagnosisDao(db: KairosDatabase): DiagnosisDao = db.diagnosisDao()
    @Provides fun provideCaseMediaDao(db: KairosDatabase): CaseMediaDao = db.caseMediaDao()
    @Provides fun provideShiftDao(db: KairosDatabase): ShiftDao = db.shiftDao()
    @Provides fun provideConsultationDao(db: KairosDatabase): ConsultationSessionDao =
        db.consultationSessionDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindDeviceAuthorizationRepository(
        impl: FirebaseDeviceAuthorizationRepository,
    ): DeviceAuthorizationRepository

    @Binds @Singleton
    abstract fun bindPatientRepository(impl: PatientRepositoryImpl): PatientRepository

    @Binds @Singleton
    abstract fun bindCaseRepository(impl: CaseRepositoryImpl): CaseRepository

    @Binds @Singleton
    abstract fun bindDiagnosisRepository(impl: DiagnosisRepositoryImpl): DiagnosisRepository

    @Binds @Singleton
    abstract fun bindMediaRepository(impl: MediaRepositoryImpl): MediaRepository

    @Binds @Singleton
    abstract fun bindShiftRepository(impl: ShiftRepositoryImpl): ShiftRepository

    @Binds @Singleton
    abstract fun bindConsultationRepository(impl: ConsultationRepositoryImpl): ConsultationRepository

    @Binds @Singleton
    abstract fun bindDashboardRepository(impl: DashboardRepositoryImpl): DashboardRepository

    @Binds @Singleton
    abstract fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository

    @Binds @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds @Singleton
    abstract fun bindBackupRepository(impl: BackupEngine): BackupRepository

    @Binds @Singleton
    abstract fun bindDataSafetyCoordinator(impl: DataSafetyCoordinatorImpl): DataSafetyCoordinator
}
