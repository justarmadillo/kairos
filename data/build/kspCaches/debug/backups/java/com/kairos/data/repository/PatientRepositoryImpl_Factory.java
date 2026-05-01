package com.kairos.data.repository;

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

  public PatientRepositoryImpl_Factory(Provider<PatientDao> daoProvider,
      Provider<KairosDatabase> dbProvider) {
    this.daoProvider = daoProvider;
    this.dbProvider = dbProvider;
  }

  @Override
  public PatientRepositoryImpl get() {
    return newInstance(daoProvider.get(), dbProvider.get());
  }

  public static PatientRepositoryImpl_Factory create(javax.inject.Provider<PatientDao> daoProvider,
      javax.inject.Provider<KairosDatabase> dbProvider) {
    return new PatientRepositoryImpl_Factory(Providers.asDaggerProvider(daoProvider), Providers.asDaggerProvider(dbProvider));
  }

  public static PatientRepositoryImpl_Factory create(Provider<PatientDao> daoProvider,
      Provider<KairosDatabase> dbProvider) {
    return new PatientRepositoryImpl_Factory(daoProvider, dbProvider);
  }

  public static PatientRepositoryImpl newInstance(PatientDao dao, KairosDatabase db) {
    return new PatientRepositoryImpl(dao, db);
  }
}
