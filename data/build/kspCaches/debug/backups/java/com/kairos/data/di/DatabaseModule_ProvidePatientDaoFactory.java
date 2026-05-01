package com.kairos.data.di;

import com.kairos.data.db.KairosDatabase;
import com.kairos.data.db.dao.PatientDao;
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
public final class DatabaseModule_ProvidePatientDaoFactory implements Factory<PatientDao> {
  private final Provider<KairosDatabase> dbProvider;

  public DatabaseModule_ProvidePatientDaoFactory(Provider<KairosDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public PatientDao get() {
    return providePatientDao(dbProvider.get());
  }

  public static DatabaseModule_ProvidePatientDaoFactory create(
      javax.inject.Provider<KairosDatabase> dbProvider) {
    return new DatabaseModule_ProvidePatientDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static DatabaseModule_ProvidePatientDaoFactory create(
      Provider<KairosDatabase> dbProvider) {
    return new DatabaseModule_ProvidePatientDaoFactory(dbProvider);
  }

  public static PatientDao providePatientDao(KairosDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.providePatientDao(db));
  }
}
