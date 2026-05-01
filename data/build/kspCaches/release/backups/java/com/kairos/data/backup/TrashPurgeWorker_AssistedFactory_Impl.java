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
public final class TrashPurgeWorker_AssistedFactory_Impl implements TrashPurgeWorker_AssistedFactory {
  private final TrashPurgeWorker_Factory delegateFactory;

  TrashPurgeWorker_AssistedFactory_Impl(TrashPurgeWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public TrashPurgeWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<TrashPurgeWorker_AssistedFactory> create(
      TrashPurgeWorker_Factory delegateFactory) {
    return InstanceFactory.create(new TrashPurgeWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<TrashPurgeWorker_AssistedFactory> createFactoryProvider(
      TrashPurgeWorker_Factory delegateFactory) {
    return InstanceFactory.create(new TrashPurgeWorker_AssistedFactory_Impl(delegateFactory));
  }
}
