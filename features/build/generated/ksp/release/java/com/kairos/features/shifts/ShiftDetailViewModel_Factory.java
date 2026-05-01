package com.kairos.features.shifts;

import androidx.lifecycle.SavedStateHandle;
import com.kairos.core.repository.CaseRepository;
import com.kairos.core.repository.ShiftRepository;
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
public final class ShiftDetailViewModel_Factory implements Factory<ShiftDetailViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<ShiftRepository> shiftRepoProvider;

  private final Provider<CaseRepository> caseRepoProvider;

  public ShiftDetailViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<ShiftRepository> shiftRepoProvider, Provider<CaseRepository> caseRepoProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.shiftRepoProvider = shiftRepoProvider;
    this.caseRepoProvider = caseRepoProvider;
  }

  @Override
  public ShiftDetailViewModel get() {
    return newInstance(savedStateHandleProvider.get(), shiftRepoProvider.get(), caseRepoProvider.get());
  }

  public static ShiftDetailViewModel_Factory create(
      javax.inject.Provider<SavedStateHandle> savedStateHandleProvider,
      javax.inject.Provider<ShiftRepository> shiftRepoProvider,
      javax.inject.Provider<CaseRepository> caseRepoProvider) {
    return new ShiftDetailViewModel_Factory(Providers.asDaggerProvider(savedStateHandleProvider), Providers.asDaggerProvider(shiftRepoProvider), Providers.asDaggerProvider(caseRepoProvider));
  }

  public static ShiftDetailViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<ShiftRepository> shiftRepoProvider, Provider<CaseRepository> caseRepoProvider) {
    return new ShiftDetailViewModel_Factory(savedStateHandleProvider, shiftRepoProvider, caseRepoProvider);
  }

  public static ShiftDetailViewModel newInstance(SavedStateHandle savedStateHandle,
      ShiftRepository shiftRepo, CaseRepository caseRepo) {
    return new ShiftDetailViewModel(savedStateHandle, shiftRepo, caseRepo);
  }
}
