package com.kairos.features.settings;

import com.kairos.core.repository.BackupRepository;
import com.kairos.core.repository.SettingsRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<SettingsRepository> repoProvider;

  private final Provider<BackupRepository> backupEngineProvider;

  public SettingsViewModel_Factory(Provider<SettingsRepository> repoProvider,
      Provider<BackupRepository> backupEngineProvider) {
    this.repoProvider = repoProvider;
    this.backupEngineProvider = backupEngineProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(repoProvider.get(), backupEngineProvider.get());
  }

  public static SettingsViewModel_Factory create(
      javax.inject.Provider<SettingsRepository> repoProvider,
      javax.inject.Provider<BackupRepository> backupEngineProvider) {
    return new SettingsViewModel_Factory(Providers.asDaggerProvider(repoProvider), Providers.asDaggerProvider(backupEngineProvider));
  }

  public static SettingsViewModel_Factory create(Provider<SettingsRepository> repoProvider,
      Provider<BackupRepository> backupEngineProvider) {
    return new SettingsViewModel_Factory(repoProvider, backupEngineProvider);
  }

  public static SettingsViewModel newInstance(SettingsRepository repo,
      BackupRepository backupEngine) {
    return new SettingsViewModel(repo, backupEngine);
  }
}
