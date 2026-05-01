package com.kairos.data.repository;

import com.kairos.core.media.MediaFileManager;
import com.kairos.data.db.KairosDatabase;
import com.kairos.data.db.dao.CaseMediaDao;
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
public final class MediaRepositoryImpl_Factory implements Factory<MediaRepositoryImpl> {
  private final Provider<CaseMediaDao> daoProvider;

  private final Provider<KairosDatabase> dbProvider;

  private final Provider<MediaFileManager> mediaFileManagerProvider;

  public MediaRepositoryImpl_Factory(Provider<CaseMediaDao> daoProvider,
      Provider<KairosDatabase> dbProvider, Provider<MediaFileManager> mediaFileManagerProvider) {
    this.daoProvider = daoProvider;
    this.dbProvider = dbProvider;
    this.mediaFileManagerProvider = mediaFileManagerProvider;
  }

  @Override
  public MediaRepositoryImpl get() {
    return newInstance(daoProvider.get(), dbProvider.get(), mediaFileManagerProvider.get());
  }

  public static MediaRepositoryImpl_Factory create(javax.inject.Provider<CaseMediaDao> daoProvider,
      javax.inject.Provider<KairosDatabase> dbProvider,
      javax.inject.Provider<MediaFileManager> mediaFileManagerProvider) {
    return new MediaRepositoryImpl_Factory(Providers.asDaggerProvider(daoProvider), Providers.asDaggerProvider(dbProvider), Providers.asDaggerProvider(mediaFileManagerProvider));
  }

  public static MediaRepositoryImpl_Factory create(Provider<CaseMediaDao> daoProvider,
      Provider<KairosDatabase> dbProvider, Provider<MediaFileManager> mediaFileManagerProvider) {
    return new MediaRepositoryImpl_Factory(daoProvider, dbProvider, mediaFileManagerProvider);
  }

  public static MediaRepositoryImpl newInstance(CaseMediaDao dao, KairosDatabase db,
      MediaFileManager mediaFileManager) {
    return new MediaRepositoryImpl(dao, db, mediaFileManager);
  }
}
