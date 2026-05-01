package com.kairos.data.backup;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.kairos.core.repository.SettingsRepository;
import dagger.internal.DaggerGenerated;
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
public final class ScheduledBackupWorker_Factory {
  private final Provider<BackupEngine> backupEngineProvider;

  private final Provider<SettingsRepository> settingsRepoProvider;

  public ScheduledBackupWorker_Factory(Provider<BackupEngine> backupEngineProvider,
      Provider<SettingsRepository> settingsRepoProvider) {
    this.backupEngineProvider = backupEngineProvider;
    this.settingsRepoProvider = settingsRepoProvider;
  }

  public ScheduledBackupWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, backupEngineProvider.get(), settingsRepoProvider.get());
  }

  public static ScheduledBackupWorker_Factory create(
      javax.inject.Provider<BackupEngine> backupEngineProvider,
      javax.inject.Provider<SettingsRepository> settingsRepoProvider) {
    return new ScheduledBackupWorker_Factory(Providers.asDaggerProvider(backupEngineProvider), Providers.asDaggerProvider(settingsRepoProvider));
  }

  public static ScheduledBackupWorker_Factory create(Provider<BackupEngine> backupEngineProvider,
      Provider<SettingsRepository> settingsRepoProvider) {
    return new ScheduledBackupWorker_Factory(backupEngineProvider, settingsRepoProvider);
  }

  public static ScheduledBackupWorker newInstance(Context context, WorkerParameters params,
      BackupEngine backupEngine, SettingsRepository settingsRepo) {
    return new ScheduledBackupWorker(context, params, backupEngine, settingsRepo);
  }
}
