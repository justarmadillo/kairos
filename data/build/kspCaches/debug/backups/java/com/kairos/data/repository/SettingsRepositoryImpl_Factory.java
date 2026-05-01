package com.kairos.data.repository;

import com.kairos.data.settings.PreferencesStore;
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
public final class SettingsRepositoryImpl_Factory implements Factory<SettingsRepositoryImpl> {
  private final Provider<PreferencesStore> storeProvider;

  public SettingsRepositoryImpl_Factory(Provider<PreferencesStore> storeProvider) {
    this.storeProvider = storeProvider;
  }

  @Override
  public SettingsRepositoryImpl get() {
    return newInstance(storeProvider.get());
  }

  public static SettingsRepositoryImpl_Factory create(
      javax.inject.Provider<PreferencesStore> storeProvider) {
    return new SettingsRepositoryImpl_Factory(Providers.asDaggerProvider(storeProvider));
  }

  public static SettingsRepositoryImpl_Factory create(Provider<PreferencesStore> storeProvider) {
    return new SettingsRepositoryImpl_Factory(storeProvider);
  }

  public static SettingsRepositoryImpl newInstance(PreferencesStore store) {
    return new SettingsRepositoryImpl(store);
  }
}
