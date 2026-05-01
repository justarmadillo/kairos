package com.kairos.core.media;

import android.content.Context;
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
public final class MediaFileManager_Factory implements Factory<MediaFileManager> {
  private final Provider<Context> contextProvider;

  public MediaFileManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public MediaFileManager get() {
    return newInstance(contextProvider.get());
  }

  public static MediaFileManager_Factory create(javax.inject.Provider<Context> contextProvider) {
    return new MediaFileManager_Factory(Providers.asDaggerProvider(contextProvider));
  }

  public static MediaFileManager_Factory create(Provider<Context> contextProvider) {
    return new MediaFileManager_Factory(contextProvider);
  }

  public static MediaFileManager newInstance(Context context) {
    return new MediaFileManager(context);
  }
}
