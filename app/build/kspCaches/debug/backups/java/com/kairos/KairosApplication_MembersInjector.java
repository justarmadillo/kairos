package com.kairos;

import androidx.hilt.work.HiltWorkerFactory;
import com.kairos.core.repository.SettingsRepository;
import com.kairos.data.backup.WorkerScheduler;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class KairosApplication_MembersInjector implements MembersInjector<KairosApplication> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  private final Provider<WorkerScheduler> workerSchedulerProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public KairosApplication_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider,
      Provider<WorkerScheduler> workerSchedulerProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
    this.workerSchedulerProvider = workerSchedulerProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  public static MembersInjector<KairosApplication> create(
      Provider<HiltWorkerFactory> workerFactoryProvider,
      Provider<WorkerScheduler> workerSchedulerProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new KairosApplication_MembersInjector(workerFactoryProvider, workerSchedulerProvider, settingsRepositoryProvider);
  }

  public static MembersInjector<KairosApplication> create(
      javax.inject.Provider<HiltWorkerFactory> workerFactoryProvider,
      javax.inject.Provider<WorkerScheduler> workerSchedulerProvider,
      javax.inject.Provider<SettingsRepository> settingsRepositoryProvider) {
    return new KairosApplication_MembersInjector(Providers.asDaggerProvider(workerFactoryProvider), Providers.asDaggerProvider(workerSchedulerProvider), Providers.asDaggerProvider(settingsRepositoryProvider));
  }

  @Override
  public void injectMembers(KairosApplication instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
    injectWorkerScheduler(instance, workerSchedulerProvider.get());
    injectSettingsRepository(instance, settingsRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.kairos.KairosApplication.workerFactory")
  public static void injectWorkerFactory(KairosApplication instance,
      HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }

  @InjectedFieldSignature("com.kairos.KairosApplication.workerScheduler")
  public static void injectWorkerScheduler(KairosApplication instance,
      WorkerScheduler workerScheduler) {
    instance.workerScheduler = workerScheduler;
  }

  @InjectedFieldSignature("com.kairos.KairosApplication.settingsRepository")
  public static void injectSettingsRepository(KairosApplication instance,
      SettingsRepository settingsRepository) {
    instance.settingsRepository = settingsRepository;
  }
}
