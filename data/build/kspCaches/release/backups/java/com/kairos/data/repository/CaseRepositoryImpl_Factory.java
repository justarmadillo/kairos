package com.kairos.data.repository;

import com.kairos.core.media.MediaFileManager;
import com.kairos.core.repository.DataSafetyCoordinator;
import com.kairos.data.db.KairosDatabase;
import com.kairos.data.db.dao.CaseDao;
import com.kairos.data.db.dao.DiagnosisDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class CaseRepositoryImpl_Factory implements Factory<CaseRepositoryImpl> {
  private final Provider<CaseDao> caseDaoProvider;

  private final Provider<DiagnosisDao> diagnosisDaoProvider;

  private final Provider<KairosDatabase> dbProvider;

  private final Provider<MediaFileManager> mediaFileManagerProvider;

  private final Provider<DataSafetyCoordinator> dataSafetyCoordinatorProvider;

  public CaseRepositoryImpl_Factory(Provider<CaseDao> caseDaoProvider,
      Provider<DiagnosisDao> diagnosisDaoProvider, Provider<KairosDatabase> dbProvider,
      Provider<MediaFileManager> mediaFileManagerProvider,
      Provider<DataSafetyCoordinator> dataSafetyCoordinatorProvider) {
    this.caseDaoProvider = caseDaoProvider;
    this.diagnosisDaoProvider = diagnosisDaoProvider;
    this.dbProvider = dbProvider;
    this.mediaFileManagerProvider = mediaFileManagerProvider;
    this.dataSafetyCoordinatorProvider = dataSafetyCoordinatorProvider;
  }

  @Override
  public CaseRepositoryImpl get() {
    return newInstance(caseDaoProvider.get(), diagnosisDaoProvider.get(), dbProvider.get(), mediaFileManagerProvider.get(), dataSafetyCoordinatorProvider.get());
  }

  public static CaseRepositoryImpl_Factory create(javax.inject.Provider<CaseDao> caseDaoProvider,
      javax.inject.Provider<DiagnosisDao> diagnosisDaoProvider,
      javax.inject.Provider<KairosDatabase> dbProvider,
      javax.inject.Provider<MediaFileManager> mediaFileManagerProvider,
      javax.inject.Provider<DataSafetyCoordinator> dataSafetyCoordinatorProvider) {
    return new CaseRepositoryImpl_Factory(Providers.asDaggerProvider(caseDaoProvider), Providers.asDaggerProvider(diagnosisDaoProvider), Providers.asDaggerProvider(dbProvider), Providers.asDaggerProvider(mediaFileManagerProvider), Providers.asDaggerProvider(dataSafetyCoordinatorProvider));
  }

  public static CaseRepositoryImpl_Factory create(Provider<CaseDao> caseDaoProvider,
      Provider<DiagnosisDao> diagnosisDaoProvider, Provider<KairosDatabase> dbProvider,
      Provider<MediaFileManager> mediaFileManagerProvider,
      Provider<DataSafetyCoordinator> dataSafetyCoordinatorProvider) {
    return new CaseRepositoryImpl_Factory(caseDaoProvider, diagnosisDaoProvider, dbProvider, mediaFileManagerProvider, dataSafetyCoordinatorProvider);
  }

  public static CaseRepositoryImpl newInstance(CaseDao caseDao, DiagnosisDao diagnosisDao,
      KairosDatabase db, MediaFileManager mediaFileManager,
      DataSafetyCoordinator dataSafetyCoordinator) {
    return new CaseRepositoryImpl(caseDao, diagnosisDao, db, mediaFileManager, dataSafetyCoordinator);
  }
}
