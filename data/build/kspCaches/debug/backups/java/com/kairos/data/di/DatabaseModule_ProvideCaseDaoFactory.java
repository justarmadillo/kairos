package com.kairos.data.di;

import com.kairos.data.db.KairosDatabase;
import com.kairos.data.db.dao.CaseDao;
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
public final class DatabaseModule_ProvideCaseDaoFactory implements Factory<CaseDao> {
  private final Provider<KairosDatabase> dbProvider;

  public DatabaseModule_ProvideCaseDaoFactory(Provider<KairosDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public CaseDao get() {
    return provideCaseDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideCaseDaoFactory create(
      javax.inject.Provider<KairosDatabase> dbProvider) {
    return new DatabaseModule_ProvideCaseDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static DatabaseModule_ProvideCaseDaoFactory create(Provider<KairosDatabase> dbProvider) {
    return new DatabaseModule_ProvideCaseDaoFactory(dbProvider);
  }

  public static CaseDao provideCaseDao(KairosDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideCaseDao(db));
  }
}
