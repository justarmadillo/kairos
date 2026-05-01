package com.kairos.data.di;

import com.kairos.data.db.KairosDatabase;
import com.kairos.data.db.dao.DiagnosisDao;
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
public final class DatabaseModule_ProvideDiagnosisDaoFactory implements Factory<DiagnosisDao> {
  private final Provider<KairosDatabase> dbProvider;

  public DatabaseModule_ProvideDiagnosisDaoFactory(Provider<KairosDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public DiagnosisDao get() {
    return provideDiagnosisDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideDiagnosisDaoFactory create(
      javax.inject.Provider<KairosDatabase> dbProvider) {
    return new DatabaseModule_ProvideDiagnosisDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static DatabaseModule_ProvideDiagnosisDaoFactory create(
      Provider<KairosDatabase> dbProvider) {
    return new DatabaseModule_ProvideDiagnosisDaoFactory(dbProvider);
  }

  public static DiagnosisDao provideDiagnosisDao(KairosDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideDiagnosisDao(db));
  }
}
