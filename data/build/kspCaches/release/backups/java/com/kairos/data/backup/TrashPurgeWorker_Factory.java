package com.kairos.data.backup;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.kairos.core.media.MediaFileManager;
import com.kairos.core.repository.DataSafetyCoordinator;
import com.kairos.data.db.dao.CaseDao;
import com.kairos.data.db.dao.CaseMediaDao;
import com.kairos.data.db.dao.ConsultationSessionDao;
import com.kairos.data.db.dao.PatientDao;
import com.kairos.data.db.dao.ShiftDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class TrashPurgeWorker_Factory {
  private final Provider<PatientDao> patientDaoProvider;

  private final Provider<CaseDao> caseDaoProvider;

  private final Provider<CaseMediaDao> caseMediaDaoProvider;

  private final Provider<ShiftDao> shiftDaoProvider;

  private final Provider<ConsultationSessionDao> sessionDaoProvider;

  private final Provider<MediaFileManager> mediaFileManagerProvider;

  private final Provider<DataSafetyCoordinator> dataSafetyCoordinatorProvider;

  public TrashPurgeWorker_Factory(Provider<PatientDao> patientDaoProvider,
      Provider<CaseDao> caseDaoProvider, Provider<CaseMediaDao> caseMediaDaoProvider,
      Provider<ShiftDao> shiftDaoProvider, Provider<ConsultationSessionDao> sessionDaoProvider,
      Provider<MediaFileManager> mediaFileManagerProvider,
      Provider<DataSafetyCoordinator> dataSafetyCoordinatorProvider) {
    this.patientDaoProvider = patientDaoProvider;
    this.caseDaoProvider = caseDaoProvider;
    this.caseMediaDaoProvider = caseMediaDaoProvider;
    this.shiftDaoProvider = shiftDaoProvider;
    this.sessionDaoProvider = sessionDaoProvider;
    this.mediaFileManagerProvider = mediaFileManagerProvider;
    this.dataSafetyCoordinatorProvider = dataSafetyCoordinatorProvider;
  }

  public TrashPurgeWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, patientDaoProvider.get(), caseDaoProvider.get(), caseMediaDaoProvider.get(), shiftDaoProvider.get(), sessionDaoProvider.get(), mediaFileManagerProvider.get(), dataSafetyCoordinatorProvider.get());
  }

  public static TrashPurgeWorker_Factory create(
      javax.inject.Provider<PatientDao> patientDaoProvider,
      javax.inject.Provider<CaseDao> caseDaoProvider,
      javax.inject.Provider<CaseMediaDao> caseMediaDaoProvider,
      javax.inject.Provider<ShiftDao> shiftDaoProvider,
      javax.inject.Provider<ConsultationSessionDao> sessionDaoProvider,
      javax.inject.Provider<MediaFileManager> mediaFileManagerProvider,
      javax.inject.Provider<DataSafetyCoordinator> dataSafetyCoordinatorProvider) {
    return new TrashPurgeWorker_Factory(Providers.asDaggerProvider(patientDaoProvider), Providers.asDaggerProvider(caseDaoProvider), Providers.asDaggerProvider(caseMediaDaoProvider), Providers.asDaggerProvider(shiftDaoProvider), Providers.asDaggerProvider(sessionDaoProvider), Providers.asDaggerProvider(mediaFileManagerProvider), Providers.asDaggerProvider(dataSafetyCoordinatorProvider));
  }

  public static TrashPurgeWorker_Factory create(Provider<PatientDao> patientDaoProvider,
      Provider<CaseDao> caseDaoProvider, Provider<CaseMediaDao> caseMediaDaoProvider,
      Provider<ShiftDao> shiftDaoProvider, Provider<ConsultationSessionDao> sessionDaoProvider,
      Provider<MediaFileManager> mediaFileManagerProvider,
      Provider<DataSafetyCoordinator> dataSafetyCoordinatorProvider) {
    return new TrashPurgeWorker_Factory(patientDaoProvider, caseDaoProvider, caseMediaDaoProvider, shiftDaoProvider, sessionDaoProvider, mediaFileManagerProvider, dataSafetyCoordinatorProvider);
  }

  public static TrashPurgeWorker newInstance(Context context, WorkerParameters params,
      PatientDao patientDao, CaseDao caseDao, CaseMediaDao caseMediaDao, ShiftDao shiftDao,
      ConsultationSessionDao sessionDao, MediaFileManager mediaFileManager,
      DataSafetyCoordinator dataSafetyCoordinator) {
    return new TrashPurgeWorker(context, params, patientDao, caseDao, caseMediaDao, shiftDao, sessionDao, mediaFileManager, dataSafetyCoordinator);
  }
}
