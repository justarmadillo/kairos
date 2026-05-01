package com.kairos.data.repository;

import com.kairos.data.db.KairosDatabase;
import com.kairos.data.db.dao.ConsultationSessionDao;
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
public final class ConsultationRepositoryImpl_Factory implements Factory<ConsultationRepositoryImpl> {
  private final Provider<ConsultationSessionDao> daoProvider;

  private final Provider<KairosDatabase> dbProvider;

  public ConsultationRepositoryImpl_Factory(Provider<ConsultationSessionDao> daoProvider,
      Provider<KairosDatabase> dbProvider) {
    this.daoProvider = daoProvider;
    this.dbProvider = dbProvider;
  }

  @Override
  public ConsultationRepositoryImpl get() {
    return newInstance(daoProvider.get(), dbProvider.get());
  }

  public static ConsultationRepositoryImpl_Factory create(
      javax.inject.Provider<ConsultationSessionDao> daoProvider,
      javax.inject.Provider<KairosDatabase> dbProvider) {
    return new ConsultationRepositoryImpl_Factory(Providers.asDaggerProvider(daoProvider), Providers.asDaggerProvider(dbProvider));
  }

  public static ConsultationRepositoryImpl_Factory create(
      Provider<ConsultationSessionDao> daoProvider, Provider<KairosDatabase> dbProvider) {
    return new ConsultationRepositoryImpl_Factory(daoProvider, dbProvider);
  }

  public static ConsultationRepositoryImpl newInstance(ConsultationSessionDao dao,
      KairosDatabase db) {
    return new ConsultationRepositoryImpl(dao, db);
  }
}
