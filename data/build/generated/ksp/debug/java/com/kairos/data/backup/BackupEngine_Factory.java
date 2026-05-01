package com.kairos.data.backup;

import android.content.Context;
import com.kairos.core.media.MediaFileManager;
import com.kairos.data.db.KairosDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class BackupEngine_Factory implements Factory<BackupEngine> {
  private final Provider<Context> contextProvider;

  private final Provider<KairosDatabase> dbProvider;

  private final Provider<MediaFileManager> mediaFileManagerProvider;

  public BackupEngine_Factory(Provider<Context> contextProvider,
      Provider<KairosDatabase> dbProvider, Provider<MediaFileManager> mediaFileManagerProvider) {
    this.contextProvider = contextProvider;
    this.dbProvider = dbProvider;
    this.mediaFileManagerProvider = mediaFileManagerProvider;
  }

  @Override
  public BackupEngine get() {
    return newInstance(contextProvider.get(), dbProvider.get(), mediaFileManagerProvider.get());
  }

  public static BackupEngine_Factory create(javax.inject.Provider<Context> contextProvider,
      javax.inject.Provider<KairosDatabase> dbProvider,
      javax.inject.Provider<MediaFileManager> mediaFileManagerProvider) {
    return new BackupEngine_Factory(Providers.asDaggerProvider(contextProvider), Providers.asDaggerProvider(dbProvider), Providers.asDaggerProvider(mediaFileManagerProvider));
  }

  public static BackupEngine_Factory create(Provider<Context> contextProvider,
      Provider<KairosDatabase> dbProvider, Provider<MediaFileManager> mediaFileManagerProvider) {
    return new BackupEngine_Factory(contextProvider, dbProvider, mediaFileManagerProvider);
  }

  public static BackupEngine newInstance(Context context, KairosDatabase db,
      MediaFileManager mediaFileManager) {
    return new BackupEngine(context, db, mediaFileManager);
  }
}
