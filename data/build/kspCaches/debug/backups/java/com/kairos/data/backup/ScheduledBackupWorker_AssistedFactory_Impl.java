package com.kairos.data.backup;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class ScheduledBackupWorker_AssistedFactory_Impl implements ScheduledBackupWorker_AssistedFactory {
  private final ScheduledBackupWorker_Factory delegateFactory;

  ScheduledBackupWorker_AssistedFactory_Impl(ScheduledBackupWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public ScheduledBackupWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<ScheduledBackupWorker_AssistedFactory> create(
      ScheduledBackupWorker_Factory delegateFactory) {
    return InstanceFactory.create(new ScheduledBackupWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<ScheduledBackupWorker_AssistedFactory> createFactoryProvider(
      ScheduledBackupWorker_Factory delegateFactory) {
    return InstanceFactory.create(new ScheduledBackupWorker_AssistedFactory_Impl(delegateFactory));
  }
}
