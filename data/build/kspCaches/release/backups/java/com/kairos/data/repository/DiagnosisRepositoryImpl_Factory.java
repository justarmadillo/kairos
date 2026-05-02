package com.kairos.data.repository;

import com.kairos.core.repository.DataSafetyCoordinator;
import com.kairos.data.db.KairosDatabase;
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
public final class DiagnosisRepositoryImpl_Factory implements Factory<DiagnosisRepositoryImpl> {
  private final Provider<DiagnosisDao> daoProvider;

  private final Provider<KairosDatabase> dbProvider;

  private final Provider<DataSafetyCoordinator> dataSafetyCoordinatorProvider;

  public DiagnosisRepositoryImpl_Factory(Provider<DiagnosisDao> daoProvider,
      Provider<KairosDatabase> dbProvider,
      Provider<DataSafetyCoordinator> dataSafetyCoordinatorProvider) {
    this.daoProvider = daoProvider;
    this.dbProvider = dbProvider;
    this.dataSafetyCoordinatorProvider = dataSafetyCoordinatorProvider;
  }

  @Override
  public DiagnosisRepositoryImpl get() {
    return newInstance(daoProvider.get(), dbProvider.get(), dataSafetyCoordinatorProvider.get());
  }

  public static DiagnosisRepositoryImpl_Factory create(
      javax.inject.Provider<DiagnosisDao> daoProvider,
      javax.inject.Provider<KairosDatabase> dbProvider,
      javax.inject.Provider<DataSafetyCoordinator> dataSafetyCoordinatorProvider) {
    return new DiagnosisRepositoryImpl_Factory(Providers.asDaggerProvider(daoProvider), Providers.asDaggerProvider(dbProvider), Providers.asDaggerProvider(dataSafetyCoordinatorProvider));
  }

  public static DiagnosisRepositoryImpl_Factory create(Provider<DiagnosisDao> daoProvider,
      Provider<KairosDatabase> dbProvider,
      Provider<DataSafetyCoordinator> dataSafetyCoordinatorProvider) {
    return new DiagnosisRepositoryImpl_Factory(daoProvider, dbProvider, dataSafetyCoordinatorProvider);
  }

  public static DiagnosisRepositoryImpl newInstance(DiagnosisDao dao, KairosDatabase db,
      DataSafetyCoordinator dataSafetyCoordinator) {
    return new DiagnosisRepositoryImpl(dao, db, dataSafetyCoordinator);
  }
}
