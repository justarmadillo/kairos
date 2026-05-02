package com.kairos.data.repository;

import com.kairos.core.repository.DataSafetyCoordinator;
import com.kairos.data.db.dao.ShiftDao;
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
public final class ShiftRepositoryImpl_Factory implements Factory<ShiftRepositoryImpl> {
  private final Provider<ShiftDao> daoProvider;

  private final Provider<DataSafetyCoordinator> dataSafetyCoordinatorProvider;

  public ShiftRepositoryImpl_Factory(Provider<ShiftDao> daoProvider,
      Provider<DataSafetyCoordinator> dataSafetyCoordinatorProvider) {
    this.daoProvider = daoProvider;
    this.dataSafetyCoordinatorProvider = dataSafetyCoordinatorProvider;
  }

  @Override
  public ShiftRepositoryImpl get() {
    return newInstance(daoProvider.get(), dataSafetyCoordinatorProvider.get());
  }

  public static ShiftRepositoryImpl_Factory create(javax.inject.Provider<ShiftDao> daoProvider,
      javax.inject.Provider<DataSafetyCoordinator> dataSafetyCoordinatorProvider) {
    return new ShiftRepositoryImpl_Factory(Providers.asDaggerProvider(daoProvider), Providers.asDaggerProvider(dataSafetyCoordinatorProvider));
  }

  public static ShiftRepositoryImpl_Factory create(Provider<ShiftDao> daoProvider,
      Provider<DataSafetyCoordinator> dataSafetyCoordinatorProvider) {
    return new ShiftRepositoryImpl_Factory(daoProvider, dataSafetyCoordinatorProvider);
  }

  public static ShiftRepositoryImpl newInstance(ShiftDao dao,
      DataSafetyCoordinator dataSafetyCoordinator) {
    return new ShiftRepositoryImpl(dao, dataSafetyCoordinator);
  }
}
