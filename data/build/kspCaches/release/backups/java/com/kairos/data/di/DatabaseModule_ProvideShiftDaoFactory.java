package com.kairos.data.di;

import com.kairos.data.db.KairosDatabase;
import com.kairos.data.db.dao.ShiftDao;
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
public final class DatabaseModule_ProvideShiftDaoFactory implements Factory<ShiftDao> {
  private final Provider<KairosDatabase> dbProvider;

  public DatabaseModule_ProvideShiftDaoFactory(Provider<KairosDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ShiftDao get() {
    return provideShiftDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideShiftDaoFactory create(
      javax.inject.Provider<KairosDatabase> dbProvider) {
    return new DatabaseModule_ProvideShiftDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static DatabaseModule_ProvideShiftDaoFactory create(Provider<KairosDatabase> dbProvider) {
    return new DatabaseModule_ProvideShiftDaoFactory(dbProvider);
  }

  public static ShiftDao provideShiftDao(KairosDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideShiftDao(db));
  }
}
