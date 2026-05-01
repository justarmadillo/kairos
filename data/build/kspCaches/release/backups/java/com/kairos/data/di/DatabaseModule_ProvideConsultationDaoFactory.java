package com.kairos.data.di;

import com.kairos.data.db.KairosDatabase;
import com.kairos.data.db.dao.ConsultationSessionDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideConsultationDaoFactory implements Factory<ConsultationSessionDao> {
  private final Provider<KairosDatabase> dbProvider;

  public DatabaseModule_ProvideConsultationDaoFactory(Provider<KairosDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ConsultationSessionDao get() {
    return provideConsultationDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideConsultationDaoFactory create(
      javax.inject.Provider<KairosDatabase> dbProvider) {
    return new DatabaseModule_ProvideConsultationDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static DatabaseModule_ProvideConsultationDaoFactory create(
      Provider<KairosDatabase> dbProvider) {
    return new DatabaseModule_ProvideConsultationDaoFactory(dbProvider);
  }

  public static ConsultationSessionDao provideConsultationDao(KairosDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideConsultationDao(db));
  }
}
