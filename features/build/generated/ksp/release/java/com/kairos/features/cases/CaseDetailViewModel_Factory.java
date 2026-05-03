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

  private final Provider<CasePdfExporter> pdfExporterProvider;

  public CaseDetailViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<CaseRepository> caseRepoProvider, Provider<MediaRepository> mediaRepoProvider,
      Provider<CasePdfExporter> pdfExporterProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.caseRepoProvider = caseRepoProvider;
    this.mediaRepoProvider = mediaRepoProvider;
    this.pdfExporterProvider = pdfExporterProvider;
  }

  @Override
  public CaseDetailViewModel get() {
    return newInstance(savedStateHandleProvider.get(), caseRepoProvider.get(), mediaRepoProvider.get(), pdfExporterProvider.get());
  }

  public static CaseDetailViewModel_Factory create(
      javax.inject.Provider<SavedStateHandle> savedStateHandleProvider,
      javax.inject.Provider<CaseRepository> caseRepoProvider,
      javax.inject.Provider<MediaRepository> mediaRepoProvider,
      javax.inject.Provider<CasePdfExporter> pdfExporterProvider) {
    return new CaseDetailViewModel_Factory(Providers.asDaggerProvider(savedStateHandleProvider), Providers.asDaggerProvider(caseRepoProvider), Providers.asDaggerProvider(mediaRepoProvider), Providers.asDaggerProvider(pdfExporterProvider));
  }

  public static CaseDetailViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<CaseRepository> caseRepoProvider, Provider<MediaRepository> mediaRepoProvider,
      Provider<CasePdfExporter> pdfExporterProvider) {
    return new CaseDetailViewModel_Factory(savedStateHandleProvider, caseRepoProvider, mediaRepoProvider, pdfExporterProvider);
  }

  public static CaseDetailViewModel newInstance(SavedStateHandle savedStateHandle,
      CaseRepository caseRepo, MediaRepository mediaRepo, CasePdfExporter pdfExporter) {
    return new CaseDetailViewModel(savedStateHandle, caseRepo, mediaRepo, pdfExporter);
  }
}
