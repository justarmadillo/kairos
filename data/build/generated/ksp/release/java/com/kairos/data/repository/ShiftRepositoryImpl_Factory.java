package com.kairos.data.repository;

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

  public ShiftRepositoryImpl_Factory(Provider<ShiftDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public ShiftRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static ShiftRepositoryImpl_Factory create(javax.inject.Provider<ShiftDao> daoProvider) {
    return new ShiftRepositoryImpl_Factory(Providers.asDaggerProvider(daoProvider));
  }

  public static ShiftRepositoryImpl_Factory create(Provider<ShiftDao> daoProvider) {
    return new ShiftRepositoryImpl_Factory(daoProvider);
  }

  public static ShiftRepositoryImpl newInstance(ShiftDao dao) {
    return new ShiftRepositoryImpl(dao);
  }
}
