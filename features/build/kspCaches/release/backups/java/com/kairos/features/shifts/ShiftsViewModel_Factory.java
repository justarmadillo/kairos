package com.kairos.features.shifts;

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
public final class ShiftsViewModel_Factory implements Factory<ShiftsViewModel> {
  private final Provider<ShiftRepository> repoProvider;

  public ShiftsViewModel_Factory(Provider<ShiftRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public ShiftsViewModel get() {
    return newInstance(repoProvider.get());
  }

  public static ShiftsViewModel_Factory create(
      javax.inject.Provider<ShiftRepository> repoProvider) {
    return new ShiftsViewModel_Factory(Providers.asDaggerProvider(repoProvider));
  }

  public static ShiftsViewModel_Factory create(Provider<ShiftRepository> repoProvider) {
    return new ShiftsViewModel_Factory(repoProvider);
  }

  public static ShiftsViewModel newInstance(ShiftRepository repo) {
    return new ShiftsViewModel(repo);
  }
}
