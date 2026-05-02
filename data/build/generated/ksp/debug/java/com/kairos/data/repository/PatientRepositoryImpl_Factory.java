package com.kairos.data.repository;

import com.kairos.core.repository.DataSafetyCoordinator;
import com.kairos.data.db.KairosDatabase;
import com.kairos.data.db.dao.PatientDao;
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
public final class PatientRepositoryImpl_Factory implements Factory<PatientRepositoryImpl> {
  private final Provider<PatientDao> daoProvider;

  private final Provider<KairosDatabase> dbProvider;

  private final Provider<DataSafetyCoordinator> dataSafetyCoordinatorProvider;

  public PatientRepositoryImpl_Factory(Provider<PatientDao> daoProvider,
      Provider<KairosDatabase> dbProvider,
      Provider<DataSafetyCoordinator> dataSafetyCoordinatorProvider) {
    this.daoProvider = daoProvider;
    this.dbProvider = dbProvider;
    this.dataSafetyCoordinatorProvider = dataSafetyCoordinatorProvider;
  }

  @Override
  public PatientRepositoryImpl get() {
    return newInstance(daoProvider.get(), dbProvider.get(), dataSafetyCoordinatorProvider.get());
  }

  public static PatientRepositoryImpl_Factory create(javax.inject.Provider<PatientDao> daoProvider,
      javax.inject.Provider<KairosDatabase> dbProvider,
      javax.inject.Provider<DataSafetyCoordinator> dataSafetyCoordinatorProvider) {
    return new PatientRepositoryImpl_Factory(Providers.asDaggerProvider(daoProvider), Providers.asDaggerProvider(dbProvider), Providers.asDaggerProvider(dataSafetyCoordinatorProvider));
  }

  public static PatientRepositoryImpl_Factory create(Provider<PatientDao> daoProvider,
      Provider<KairosDatabase> dbProvider,
      Provider<DataSafetyCoordinator> dataSafetyCoordinatorProvider) {
    return new PatientRepositoryImpl_Factory(daoProvider, dbProvider, dataSafetyCoordinatorProvider);
  }

  public static PatientRepositoryImpl newInstance(PatientDao dao, KairosDatabase db,
      DataSafetyCoordinator dataSafetyCoordinator) {
    return new PatientRepositoryImpl(dao, db, dataSafetyCoordinator);
  }
}
