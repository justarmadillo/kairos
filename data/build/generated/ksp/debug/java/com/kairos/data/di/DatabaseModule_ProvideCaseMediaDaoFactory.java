package com.kairos.data.di;

import com.kairos.data.db.KairosDatabase;
import com.kairos.data.db.dao.CaseMediaDao;
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
public final class DatabaseModule_ProvideCaseMediaDaoFactory implements Factory<CaseMediaDao> {
  private final Provider<KairosDatabase> dbProvider;

  public DatabaseModule_ProvideCaseMediaDaoFactory(Provider<KairosDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public CaseMediaDao get() {
    return provideCaseMediaDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideCaseMediaDaoFactory create(
      javax.inject.Provider<KairosDatabase> dbProvider) {
    return new DatabaseModule_ProvideCaseMediaDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static DatabaseModule_ProvideCaseMediaDaoFactory create(
      Provider<KairosDatabase> dbProvider) {
    return new DatabaseModule_ProvideCaseMediaDaoFactory(dbProvider);
  }

  public static CaseMediaDao provideCaseMediaDao(KairosDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideCaseMediaDao(db));
  }
}
