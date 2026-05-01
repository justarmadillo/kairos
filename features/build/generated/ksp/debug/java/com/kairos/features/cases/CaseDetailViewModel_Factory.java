package com.kairos.features.cases;

import androidx.lifecycle.SavedStateHandle;
import com.kairos.core.repository.CaseRepository;
import com.kairos.core.repository.MediaRepository;
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
public final class CaseDetailViewModel_Factory implements Factory<CaseDetailViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<CaseRepository> caseRepoProvider;

  private final Provider<MediaRepository> mediaRepoProvider;

  public CaseDetailViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<CaseRepository> caseRepoProvider, Provider<MediaRepository> mediaRepoProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.caseRepoProvider = caseRepoProvider;
    this.mediaRepoProvider = mediaRepoProvider;
  }

  @Override
  public CaseDetailViewModel get() {
    return newInstance(savedStateHandleProvider.get(), caseRepoProvider.get(), mediaRepoProvider.get());
  }

  public static CaseDetailViewModel_Factory create(
      javax.inject.Provider<SavedStateHandle> savedStateHandleProvider,
      javax.inject.Provider<CaseRepository> caseRepoProvider,
      javax.inject.Provider<MediaRepository> mediaRepoProvider) {
    return new CaseDetailViewModel_Factory(Providers.asDaggerProvider(savedStateHandleProvider), Providers.asDaggerProvider(caseRepoProvider), Providers.asDaggerProvider(mediaRepoProvider));
  }

  public static CaseDetailViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<CaseRepository> caseRepoProvider, Provider<MediaRepository> mediaRepoProvider) {
    return new CaseDetailViewModel_Factory(savedStateHandleProvider, caseRepoProvider, mediaRepoProvider);
  }

  public static CaseDetailViewModel newInstance(SavedStateHandle savedStateHandle,
      CaseRepository caseRepo, MediaRepository mediaRepo) {
    return new CaseDetailViewModel(savedStateHandle, caseRepo, mediaRepo);
  }
}
