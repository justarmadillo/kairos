package com.kairos.features.settings;

import com.kairos.core.repository.CaseRepository;
import com.kairos.core.repository.ConsultationRepository;
import com.kairos.core.repository.PatientRepository;
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
public final class TrashViewModel_Factory implements Factory<TrashViewModel> {
  private final Provider<PatientRepository> patientRepoProvider;

  private final Provider<CaseRepository> caseRepoProvider;

  private final Provider<ShiftRepository> shiftRepoProvider;

  private final Provider<ConsultationRepository> consultationRepoProvider;

  public TrashViewModel_Factory(Provider<PatientRepository> patientRepoProvider,
      Provider<CaseRepository> caseRepoProvider, Provider<ShiftRepository> shiftRepoProvider,
      Provider<ConsultationRepository> consultationRepoProvider) {
    this.patientRepoProvider = patientRepoProvider;
    this.caseRepoProvider = caseRepoProvider;
    this.shiftRepoProvider = shiftRepoProvider;
    this.consultationRepoProvider = consultationRepoProvider;
  }

  @Override
  public TrashViewModel get() {
    return newInstance(patientRepoProvider.get(), caseRepoProvider.get(), shiftRepoProvider.get(), consultationRepoProvider.get());
  }

  public static TrashViewModel_Factory create(
      javax.inject.Provider<PatientRepository> patientRepoProvider,
      javax.inject.Provider<CaseRepository> caseRepoProvider,
      javax.inject.Provider<ShiftRepository> shiftRepoProvider,
      javax.inject.Provider<ConsultationRepository> consultationRepoProvider) {
    return new TrashViewModel_Factory(Providers.asDaggerProvider(patientRepoProvider), Providers.asDaggerProvider(caseRepoProvider), Providers.asDaggerProvider(shiftRepoProvider), Providers.asDaggerProvider(consultationRepoProvider));
  }

  public static TrashViewModel_Factory create(Provider<PatientRepository> patientRepoProvider,
      Provider<CaseRepository> caseRepoProvider, Provider<ShiftRepository> shiftRepoProvider,
      Provider<ConsultationRepository> consultationRepoProvider) {
    return new TrashViewModel_Factory(patientRepoProvider, caseRepoProvider, shiftRepoProvider, consultationRepoProvider);
  }

  public static TrashViewModel newInstance(PatientRepository patientRepo, CaseRepository caseRepo,
      ShiftRepository shiftRepo, ConsultationRepository consultationRepo) {
    return new TrashViewModel(patientRepo, caseRepo, shiftRepo, consultationRepo);
  }
}
