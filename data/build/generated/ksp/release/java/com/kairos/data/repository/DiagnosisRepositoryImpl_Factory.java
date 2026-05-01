package com.kairos.data.repository;

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

  public DiagnosisRepositoryImpl_Factory(Provider<DiagnosisDao> daoProvider,
      Provider<KairosDatabase> dbProvider) {
    this.daoProvider = daoProvider;
    this.dbProvider = dbProvider;
  }

  @Override
  public DiagnosisRepositoryImpl get() {
    return newInstance(daoProvider.get(), dbProvider.get());
  }

  public static DiagnosisRepositoryImpl_Factory create(
      javax.inject.Provider<DiagnosisDao> daoProvider,
      javax.inject.Provider<KairosDatabase> dbProvider) {
    return new DiagnosisRepositoryImpl_Factory(Providers.asDaggerProvider(daoProvider), Providers.asDaggerProvider(dbProvider));
  }

  public static DiagnosisRepositoryImpl_Factory create(Provider<DiagnosisDao> daoProvider,
      Provider<KairosDatabase> dbProvider) {
    return new DiagnosisRepositoryImpl_Factory(daoProvider, dbProvider);
  }

  public static DiagnosisRepositoryImpl newInstance(DiagnosisDao dao, KairosDatabase db) {
    return new DiagnosisRepositoryImpl(dao, db);
  }
}
