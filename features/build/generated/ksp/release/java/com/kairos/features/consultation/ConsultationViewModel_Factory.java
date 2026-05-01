package com.kairos.features.consultation;

import com.kairos.core.repository.CaseRepository;
import com.kairos.core.repository.ConsultationRepository;
import com.kairos.core.repository.SettingsRepository;
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
public final class ConsultationViewModel_Factory implements Factory<ConsultationViewModel> {
  private final Provider<SettingsRepository> settingsRepoProvider;

  private final Provider<ConsultationRepository> consultationRepoProvider;

  private final Provider<CaseRepository> caseRepoProvider;

  public ConsultationViewModel_Factory(Provider<SettingsRepository> settingsRepoProvider,
      Provider<ConsultationRepository> consultationRepoProvider,
      Provider<CaseRepository> caseRepoProvider) {
    this.settingsRepoProvider = settingsRepoProvider;
    this.consultationRepoProvider = consultationRepoProvider;
    this.caseRepoProvider = caseRepoProvider;
  }

  @Override
  public ConsultationViewModel get() {
    return newInstance(settingsRepoProvider.get(), consultationRepoProvider.get(), caseRepoProvider.get());
  }

  public static ConsultationViewModel_Factory create(
      javax.inject.Provider<SettingsRepository> settingsRepoProvider,
      javax.inject.Provider<ConsultationRepository> consultationRepoProvider,
      javax.inject.Provider<CaseRepository> caseRepoProvider) {
    return new ConsultationViewModel_Factory(Providers.asDaggerProvider(settingsRepoProvider), Providers.asDaggerProvider(consultationRepoProvider), Providers.asDaggerProvider(caseRepoProvider));
  }

  public static ConsultationViewModel_Factory create(
      Provider<SettingsRepository> settingsRepoProvider,
      Provider<ConsultationRepository> consultationRepoProvider,
      Provider<CaseRepository> caseRepoProvider) {
    return new ConsultationViewModel_Factory(settingsRepoProvider, consultationRepoProvider, caseRepoProvider);
  }

  public static ConsultationViewModel newInstance(SettingsRepository settingsRepo,
      ConsultationRepository consultationRepo, CaseRepository caseRepo) {
    return new ConsultationViewModel(settingsRepo, consultationRepo, caseRepo);
  }
}
