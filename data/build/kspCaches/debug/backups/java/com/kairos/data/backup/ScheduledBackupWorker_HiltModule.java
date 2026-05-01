package com.kairos.data.backup;

import androidx.hilt.work.WorkerAssistedFactory;
import androidx.work.ListenableWorker;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import javax.annotation.processing.Generated;

@Generated("androidx.hilt.AndroidXHiltProcessor")
@Module
@InstallIn(SingletonComponent.class)
@OriginatingElement(
    topLevelClass = ScheduledBackupWorker.class
)
public interface ScheduledBackupWorker_HiltModule {
  @Binds
  @IntoMap
  @StringKey("com.kairos.data.backup.ScheduledBackupWorker")
  WorkerAssistedFactory<? extends ListenableWorker> bind(
      ScheduledBackupWorker_AssistedFactory factory);
}
