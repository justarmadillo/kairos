package com.kairos.data.di

import android.content.Context
import androidx.room.Room
import com.kairos.core.repository.BackupRepository
import com.kairos.core.repository.CaseRepository
import com.kairos.core.repository.ConsultationRepository
import com.kairos.core.repository.DataSafetyCoordinator
import com.kairos.core.repository.DiagnosisRepository
import com.kairos.core.repository.MediaRepository
import com.kairos.core.repository.PatientRepository
import com.kairos.core.repository.SettingsRepository
import com.kairos.core.repository.ShiftRepository
import com.kairos.data.db.KairosDatabase
import com.kairos.data.db.migrations.Migrations
import com.kairos.data.db.dao.CaseDao
import com.kairos.data.db.dao.CaseMediaDao
import com.kairos.data.db.dao.ConsultationSessionDao
import com.kairos.data.db.dao.DiagnosisDao
import com.kairos.data.db.dao.PatientDao
import com.kairos.data.db.dao.ShiftDao
import com.kairos.data.backup.BackupEngine
import com.kairos.data.backup.DataSafetyCoordinatorImpl
import com.kairos.data.repository.CaseRepositoryImpl
import com.kairos.data.repository.ConsultationRepositoryImpl
import com.kairos.data.repository.DiagnosisRepositoryImpl
import com.kairos.data.repository.MediaRepositoryImpl
import com.kairos.data.repository.PatientRepositoryImpl
import com.kairos.data.repository.SettingsRepositoryImpl
import com.kairos.data.repository.ShiftRepositoryImpl
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
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds @Singleton
    abstract fun bindBackupRepository(impl: BackupEngine): BackupRepository

    @Binds @Singleton
    abstract fun bindDataSafetyCoordinator(impl: DataSafetyCoordinatorImpl): DataSafetyCoordinator
}
