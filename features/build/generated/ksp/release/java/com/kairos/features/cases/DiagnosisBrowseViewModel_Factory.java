package com.kairos.features.cases;

import com.kairos.core.repository.DiagnosisRepository;
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
public final class DiagnosisBrowseViewModel_Factory implements Factory<DiagnosisBrowseViewModel> {
  private final Provider<DiagnosisRepository> diagnosisRepoProvider;

  public DiagnosisBrowseViewModel_Factory(Provider<DiagnosisRepository> diagnosisRepoProvider) {
    this.diagnosisRepoProvider = diagnosisRepoProvider;
  }

  @Override
  public DiagnosisBrowseViewModel get() {
    return newInstance(diagnosisRepoProvider.get());
  }

  public static DiagnosisBrowseViewModel_Factory create(
      javax.inject.Provider<DiagnosisRepository> diagnosisRepoProvider) {
    return new DiagnosisBrowseViewModel_Factory(Providers.asDaggerProvider(diagnosisRepoProvider));
  }

  public static DiagnosisBrowseViewModel_Factory create(
      Provider<DiagnosisRepository> diagnosisRepoProvider) {
    return new DiagnosisBrowseViewModel_Factory(diagnosisRepoProvider);
  }

  public static DiagnosisBrowseViewModel newInstance(DiagnosisRepository diagnosisRepo) {
    return new DiagnosisBrowseViewModel(diagnosisRepo);
  }
}
