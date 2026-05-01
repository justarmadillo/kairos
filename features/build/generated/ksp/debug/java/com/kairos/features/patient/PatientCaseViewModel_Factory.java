package com.kairos.features.patient;

import com.kairos.core.media.AudioRecorderEngine;
import com.kairos.core.media.MediaFileManager;
import com.kairos.core.repository.CaseRepository;
import com.kairos.core.repository.DiagnosisRepository;
import com.kairos.core.repository.MediaRepository;
import com.kairos.core.repository.PatientRepository;
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
public final class PatientCaseViewModel_Factory implements Factory<PatientCaseViewModel> {
  private final Provider<PatientRepository> patientRepoProvider;

  private final Provider<CaseRepository> caseRepoProvider;

  private final Provider<DiagnosisRepository> diagnosisRepoProvider;

  private final Provider<MediaRepository> mediaRepoProvider;

  private final Provider<MediaFileManager> mediaFileManagerProvider;

  private final Provider<AudioRecorderEngine> audioEngineProvider;

  public PatientCaseViewModel_Factory(Provider<PatientRepository> patientRepoProvider,
      Provider<CaseRepository> caseRepoProvider,
      Provider<DiagnosisRepository> diagnosisRepoProvider,
      Provider<MediaRepository> mediaRepoProvider,
      Provider<MediaFileManager> mediaFileManagerProvider,
      Provider<AudioRecorderEngine> audioEngineProvider) {
    this.patientRepoProvider = patientRepoProvider;
    this.caseRepoProvider = caseRepoProvider;
    this.diagnosisRepoProvider = diagnosisRepoProvider;
    this.mediaRepoProvider = mediaRepoProvider;
    this.mediaFileManagerProvider = mediaFileManagerProvider;
    this.audioEngineProvider = audioEngineProvider;
  }

  @Override
  public PatientCaseViewModel get() {
    return newInstance(patientRepoProvider.get(), caseRepoProvider.get(), diagnosisRepoProvider.get(), mediaRepoProvider.get(), mediaFileManagerProvider.get(), audioEngineProvider.get());
  }

  public static PatientCaseViewModel_Factory create(
      javax.inject.Provider<PatientRepository> patientRepoProvider,
      javax.inject.Provider<CaseRepository> caseRepoProvider,
      javax.inject.Provider<DiagnosisRepository> diagnosisRepoProvider,
      javax.inject.Provider<MediaRepository> mediaRepoProvider,
      javax.inject.Provider<MediaFileManager> mediaFileManagerProvider,
      javax.inject.Provider<AudioRecorderEngine> audioEngineProvider) {
    return new PatientCaseViewModel_Factory(Providers.asDaggerProvider(patientRepoProvider), Providers.asDaggerProvider(caseRepoProvider), Providers.asDaggerProvider(diagnosisRepoProvider), Providers.asDaggerProvider(mediaRepoProvider), Providers.asDaggerProvider(mediaFileManagerProvider), Providers.asDaggerProvider(audioEngineProvider));
  }

  public static PatientCaseViewModel_Factory create(Provider<PatientRepository> patientRepoProvider,
      Provider<CaseRepository> caseRepoProvider,
      Provider<DiagnosisRepository> diagnosisRepoProvider,
      Provider<MediaRepository> mediaRepoProvider,
      Provider<MediaFileManager> mediaFileManagerProvider,
      Provider<AudioRecorderEngine> audioEngineProvider) {
    return new PatientCaseViewModel_Factory(patientRepoProvider, caseRepoProvider, diagnosisRepoProvider, mediaRepoProvider, mediaFileManagerProvider, audioEngineProvider);
  }

  public static PatientCaseViewModel newInstance(PatientRepository patientRepo,
      CaseRepository caseRepo, DiagnosisRepository diagnosisRepo, MediaRepository mediaRepo,
      MediaFileManager mediaFileManager, AudioRecorderEngine audioEngine) {
    return new PatientCaseViewModel(patientRepo, caseRepo, diagnosisRepo, mediaRepo, mediaFileManager, audioEngine);
  }
}
