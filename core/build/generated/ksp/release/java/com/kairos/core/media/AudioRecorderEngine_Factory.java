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
public final class AudioRecorderEngine_Factory implements Factory<AudioRecorderEngine> {
  private final Provider<Context> contextProvider;

  public AudioRecorderEngine_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AudioRecorderEngine get() {
    return newInstance(contextProvider.get());
  }

  public static AudioRecorderEngine_Factory create(javax.inject.Provider<Context> contextProvider) {
    return new AudioRecorderEngine_Factory(Providers.asDaggerProvider(contextProvider));
  }

  public static AudioRecorderEngine_Factory create(Provider<Context> contextProvider) {
    return new AudioRecorderEngine_Factory(contextProvider);
  }

  public static AudioRecorderEngine newInstance(Context context) {
    return new AudioRecorderEngine(context);
  }
}
