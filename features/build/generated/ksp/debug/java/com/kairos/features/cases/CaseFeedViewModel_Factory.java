package com.kairos.features.cases;

import androidx.lifecycle.SavedStateHandle;
import com.kairos.core.repository.CaseRepository;
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
public final class CaseFeedViewModel_Factory implements Factory<CaseFeedViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<CaseRepository> caseRepoProvider;

  public CaseFeedViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<CaseRepository> caseRepoProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.caseRepoProvider = caseRepoProvider;
  }

  @Override
  public CaseFeedViewModel get() {
    return newInstance(savedStateHandleProvider.get(), caseRepoProvider.get());
  }

  public static CaseFeedViewModel_Factory create(
      javax.inject.Provider<SavedStateHandle> savedStateHandleProvider,
      javax.inject.Provider<CaseRepository> caseRepoProvider) {
    return new CaseFeedViewModel_Factory(Providers.asDaggerProvider(savedStateHandleProvider), Providers.asDaggerProvider(caseRepoProvider));
  }

  public static CaseFeedViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<CaseRepository> caseRepoProvider) {
    return new CaseFeedViewModel_Factory(savedStateHandleProvider, caseRepoProvider);
  }

  public static CaseFeedViewModel newInstance(SavedStateHandle savedStateHandle,
      CaseRepository caseRepo) {
    return new CaseFeedViewModel(savedStateHandle, caseRepo);
  }
}
