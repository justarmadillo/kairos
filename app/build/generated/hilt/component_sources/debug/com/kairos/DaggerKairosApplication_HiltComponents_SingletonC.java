package com.kairos;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.hilt.work.WorkerAssistedFactory;
import androidx.hilt.work.WorkerFactoryModule_ProvideFactoryFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.kairos.core.media.AudioRecorderEngine;
import com.kairos.core.media.MediaFileManager;
import com.kairos.data.backup.BackupEngine;
import com.kairos.data.backup.DataSafetyCoordinatorImpl;
import com.kairos.data.backup.ScheduledBackupWorker;
import com.kairos.data.backup.ScheduledBackupWorker_AssistedFactory;
import com.kairos.data.backup.TrashPurgeWorker;
import com.kairos.data.backup.TrashPurgeWorker_AssistedFactory;
import com.kairos.data.backup.WorkerScheduler;
import com.kairos.data.db.KairosDatabase;
import com.kairos.data.db.dao.CaseDao;
import com.kairos.data.db.dao.CaseMediaDao;
import com.kairos.data.db.dao.ConsultationSessionDao;
import com.kairos.data.db.dao.DiagnosisDao;
import com.kairos.data.db.dao.PatientDao;
import com.kairos.data.db.dao.ShiftDao;
import com.kairos.data.di.DatabaseModule_ProvideCaseDaoFactory;
import com.kairos.data.di.DatabaseModule_ProvideCaseMediaDaoFactory;
import com.kairos.data.di.DatabaseModule_ProvideConsultationDaoFactory;
import com.kairos.data.di.DatabaseModule_ProvideDatabaseFactory;
import com.kairos.data.di.DatabaseModule_ProvideDiagnosisDaoFactory;
import com.kairos.data.di.DatabaseModule_ProvidePatientDaoFactory;
import com.kairos.data.di.DatabaseModule_ProvideShiftDaoFactory;
import com.kairos.data.repository.CaseRepositoryImpl;
import com.kairos.data.repository.ConsultationRepositoryImpl;
import com.kairos.data.repository.DiagnosisRepositoryImpl;
import com.kairos.data.repository.MediaRepositoryImpl;
import com.kairos.data.repository.PatientRepositoryImpl;
import com.kairos.data.repository.SettingsRepositoryImpl;
import com.kairos.data.repository.ShiftRepositoryImpl;
import com.kairos.data.settings.PreferencesStore;
import com.kairos.features.cases.CaseDetailViewModel;
import com.kairos.features.cases.CaseDetailViewModel_HiltModules;
import com.kairos.features.cases.CaseDetailViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.kairos.features.cases.CaseDetailViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.kairos.features.cases.CaseFeedViewModel;
import com.kairos.features.cases.CaseFeedViewModel_HiltModules;
import com.kairos.features.cases.CaseFeedViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.kairos.features.cases.CaseFeedViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.kairos.features.cases.DiagnosisBrowseViewModel;
import com.kairos.features.cases.DiagnosisBrowseViewModel_HiltModules;
import com.kairos.features.cases.DiagnosisBrowseViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.kairos.features.cases.DiagnosisBrowseViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.kairos.features.consultation.ConsultationViewModel;
import com.kairos.features.consultation.ConsultationViewModel_HiltModules;
import com.kairos.features.consultation.ConsultationViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.kairos.features.consultation.ConsultationViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.kairos.features.patient.PatientCaseViewModel;
import com.kairos.features.patient.PatientCaseViewModel_HiltModules;
import com.kairos.features.patient.PatientCaseViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.kairos.features.patient.PatientCaseViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.kairos.features.settings.SettingsViewModel;
import com.kairos.features.settings.SettingsViewModel_HiltModules;
import com.kairos.features.settings.SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.kairos.features.settings.SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.kairos.features.settings.TrashViewModel;
import com.kairos.features.settings.TrashViewModel_HiltModules;
import com.kairos.features.settings.TrashViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.kairos.features.settings.TrashViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.kairos.features.shifts.ShiftDetailViewModel;
import com.kairos.features.shifts.ShiftDetailViewModel_HiltModules;
import com.kairos.features.shifts.ShiftDetailViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.kairos.features.shifts.ShiftDetailViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.kairos.features.shifts.ShiftsViewModel;
import com.kairos.features.shifts.ShiftsViewModel_HiltModules;
import com.kairos.features.shifts.ShiftsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.kairos.features.shifts.ShiftsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.SingleCheck;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerKairosApplication_HiltComponents_SingletonC {
  private DaggerKairosApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public KairosApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements KairosApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public KairosApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements KairosApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public KairosApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements KairosApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public KairosApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements KairosApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public KairosApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements KairosApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public KairosApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements KairosApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public KairosApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements KairosApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public KairosApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends KairosApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends KairosApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends KairosApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends KairosApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity arg0) {
      injectMainActivity2(arg0);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(ImmutableMap.<String, Boolean>builderWithExpectedSize(9).put(CaseDetailViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, CaseDetailViewModel_HiltModules.KeyModule.provide()).put(CaseFeedViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, CaseFeedViewModel_HiltModules.KeyModule.provide()).put(ConsultationViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, ConsultationViewModel_HiltModules.KeyModule.provide()).put(DiagnosisBrowseViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, DiagnosisBrowseViewModel_HiltModules.KeyModule.provide()).put(PatientCaseViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, PatientCaseViewModel_HiltModules.KeyModule.provide()).put(SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SettingsViewModel_HiltModules.KeyModule.provide()).put(ShiftDetailViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, ShiftDetailViewModel_HiltModules.KeyModule.provide()).put(ShiftsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, ShiftsViewModel_HiltModules.KeyModule.provide()).put(TrashViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, TrashViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectSettingsRepository(instance, singletonCImpl.settingsRepositoryImplProvider.get());
      return instance;
    }
  }

  private static final class ViewModelCImpl extends KairosApplication_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<CaseDetailViewModel> caseDetailViewModelProvider;

    private Provider<CaseFeedViewModel> caseFeedViewModelProvider;

    private Provider<ConsultationViewModel> consultationViewModelProvider;

    private Provider<DiagnosisBrowseViewModel> diagnosisBrowseViewModelProvider;

    private Provider<PatientCaseViewModel> patientCaseViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private Provider<ShiftDetailViewModel> shiftDetailViewModelProvider;

    private Provider<ShiftsViewModel> shiftsViewModelProvider;

    private Provider<TrashViewModel> trashViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.caseDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.caseFeedViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.consultationViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.diagnosisBrowseViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.patientCaseViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.shiftDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.shiftsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.trashViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(ImmutableMap.<String, javax.inject.Provider<ViewModel>>builderWithExpectedSize(9).put(CaseDetailViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) caseDetailViewModelProvider)).put(CaseFeedViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) caseFeedViewModelProvider)).put(ConsultationViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) consultationViewModelProvider)).put(DiagnosisBrowseViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) diagnosisBrowseViewModelProvider)).put(PatientCaseViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) patientCaseViewModelProvider)).put(SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) settingsViewModelProvider)).put(ShiftDetailViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) shiftDetailViewModelProvider)).put(ShiftsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) shiftsViewModelProvider)).put(TrashViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) trashViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.kairos.features.cases.CaseDetailViewModel 
          return (T) new CaseDetailViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.caseRepositoryImplProvider.get(), singletonCImpl.mediaRepositoryImplProvider.get());

          case 1: // com.kairos.features.cases.CaseFeedViewModel 
          return (T) new CaseFeedViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.caseRepositoryImplProvider.get());

          case 2: // com.kairos.features.consultation.ConsultationViewModel 
          return (T) new ConsultationViewModel(singletonCImpl.settingsRepositoryImplProvider.get(), singletonCImpl.consultationRepositoryImplProvider.get(), singletonCImpl.caseRepositoryImplProvider.get());

          case 3: // com.kairos.features.cases.DiagnosisBrowseViewModel 
          return (T) new DiagnosisBrowseViewModel(singletonCImpl.diagnosisRepositoryImplProvider.get());

          case 4: // com.kairos.features.patient.PatientCaseViewModel 
          return (T) new PatientCaseViewModel(singletonCImpl.patientRepositoryImplProvider.get(), singletonCImpl.caseRepositoryImplProvider.get(), singletonCImpl.diagnosisRepositoryImplProvider.get(), singletonCImpl.mediaRepositoryImplProvider.get(), singletonCImpl.mediaFileManagerProvider.get(), singletonCImpl.audioRecorderEngineProvider.get(), singletonCImpl.dataSafetyCoordinatorImplProvider.get());

          case 5: // com.kairos.features.settings.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.settingsRepositoryImplProvider.get(), singletonCImpl.backupEngineProvider.get());

          case 6: // com.kairos.features.shifts.ShiftDetailViewModel 
          return (T) new ShiftDetailViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.shiftRepositoryImplProvider.get(), singletonCImpl.caseRepositoryImplProvider.get());

          case 7: // com.kairos.features.shifts.ShiftsViewModel 
          return (T) new ShiftsViewModel(singletonCImpl.shiftRepositoryImplProvider.get());

          case 8: // com.kairos.features.settings.TrashViewModel 
          return (T) new TrashViewModel(singletonCImpl.patientRepositoryImplProvider.get(), singletonCImpl.caseRepositoryImplProvider.get(), singletonCImpl.shiftRepositoryImplProvider.get(), singletonCImpl.consultationRepositoryImplProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends KairosApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends KairosApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends KairosApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<KairosDatabase> provideDatabaseProvider;

    private Provider<MediaFileManager> mediaFileManagerProvider;

    private Provider<DataSafetyCoordinatorImpl> dataSafetyCoordinatorImplProvider;

    private Provider<BackupEngine> backupEngineProvider;

    private Provider<PreferencesStore> preferencesStoreProvider;

    private Provider<SettingsRepositoryImpl> settingsRepositoryImplProvider;

    private Provider<ScheduledBackupWorker_AssistedFactory> scheduledBackupWorker_AssistedFactoryProvider;

    private Provider<TrashPurgeWorker_AssistedFactory> trashPurgeWorker_AssistedFactoryProvider;

    private Provider<WorkerScheduler> workerSchedulerProvider;

    private Provider<CaseRepositoryImpl> caseRepositoryImplProvider;

    private Provider<MediaRepositoryImpl> mediaRepositoryImplProvider;

    private Provider<ConsultationRepositoryImpl> consultationRepositoryImplProvider;

    private Provider<DiagnosisRepositoryImpl> diagnosisRepositoryImplProvider;

    private Provider<PatientRepositoryImpl> patientRepositoryImplProvider;

    private Provider<AudioRecorderEngine> audioRecorderEngineProvider;

    private Provider<ShiftRepositoryImpl> shiftRepositoryImplProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private PatientDao patientDao() {
      return DatabaseModule_ProvidePatientDaoFactory.providePatientDao(provideDatabaseProvider.get());
    }

    private CaseDao caseDao() {
      return DatabaseModule_ProvideCaseDaoFactory.provideCaseDao(provideDatabaseProvider.get());
    }

    private CaseMediaDao caseMediaDao() {
      return DatabaseModule_ProvideCaseMediaDaoFactory.provideCaseMediaDao(provideDatabaseProvider.get());
    }

    private DiagnosisDao diagnosisDao() {
      return DatabaseModule_ProvideDiagnosisDaoFactory.provideDiagnosisDao(provideDatabaseProvider.get());
    }

    private ShiftDao shiftDao() {
      return DatabaseModule_ProvideShiftDaoFactory.provideShiftDao(provideDatabaseProvider.get());
    }

    private ConsultationSessionDao consultationSessionDao() {
      return DatabaseModule_ProvideConsultationDaoFactory.provideConsultationDao(provideDatabaseProvider.get());
    }

    private Map<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>> mapOfStringAndProviderOfWorkerAssistedFactoryOf(
        ) {
      return ImmutableMap.<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>>of("com.kairos.data.backup.ScheduledBackupWorker", ((Provider) scheduledBackupWorker_AssistedFactoryProvider), "com.kairos.data.backup.TrashPurgeWorker", ((Provider) trashPurgeWorker_AssistedFactoryProvider));
    }

    private HiltWorkerFactory hiltWorkerFactory() {
      return WorkerFactoryModule_ProvideFactoryFactory.provideFactory(mapOfStringAndProviderOfWorkerAssistedFactoryOf());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<KairosDatabase>(singletonCImpl, 2));
      this.mediaFileManagerProvider = DoubleCheck.provider(new SwitchingProvider<MediaFileManager>(singletonCImpl, 3));
      this.dataSafetyCoordinatorImplProvider = DoubleCheck.provider(new SwitchingProvider<DataSafetyCoordinatorImpl>(singletonCImpl, 4));
      this.backupEngineProvider = DoubleCheck.provider(new SwitchingProvider<BackupEngine>(singletonCImpl, 1));
      this.preferencesStoreProvider = DoubleCheck.provider(new SwitchingProvider<PreferencesStore>(singletonCImpl, 6));
      this.settingsRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<SettingsRepositoryImpl>(singletonCImpl, 5));
      this.scheduledBackupWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<ScheduledBackupWorker_AssistedFactory>(singletonCImpl, 0));
      this.trashPurgeWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<TrashPurgeWorker_AssistedFactory>(singletonCImpl, 7));
      this.workerSchedulerProvider = DoubleCheck.provider(new SwitchingProvider<WorkerScheduler>(singletonCImpl, 8));
      this.caseRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<CaseRepositoryImpl>(singletonCImpl, 9));
      this.mediaRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<MediaRepositoryImpl>(singletonCImpl, 10));
      this.consultationRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<ConsultationRepositoryImpl>(singletonCImpl, 11));
      this.diagnosisRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<DiagnosisRepositoryImpl>(singletonCImpl, 12));
      this.patientRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<PatientRepositoryImpl>(singletonCImpl, 13));
      this.audioRecorderEngineProvider = DoubleCheck.provider(new SwitchingProvider<AudioRecorderEngine>(singletonCImpl, 14));
      this.shiftRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<ShiftRepositoryImpl>(singletonCImpl, 15));
    }

    @Override
    public void injectKairosApplication(KairosApplication arg0) {
      injectKairosApplication2(arg0);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private KairosApplication injectKairosApplication2(KairosApplication instance) {
      KairosApplication_MembersInjector.injectWorkerFactory(instance, hiltWorkerFactory());
      KairosApplication_MembersInjector.injectWorkerScheduler(instance, workerSchedulerProvider.get());
      KairosApplication_MembersInjector.injectSettingsRepository(instance, settingsRepositoryImplProvider.get());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.kairos.data.backup.ScheduledBackupWorker_AssistedFactory 
          return (T) new ScheduledBackupWorker_AssistedFactory() {
            @Override
            public ScheduledBackupWorker create(Context context, WorkerParameters params) {
              return new ScheduledBackupWorker(context, params, singletonCImpl.backupEngineProvider.get(), singletonCImpl.settingsRepositoryImplProvider.get());
            }
          };

          case 1: // com.kairos.data.backup.BackupEngine 
          return (T) new BackupEngine(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideDatabaseProvider.get(), singletonCImpl.mediaFileManagerProvider.get(), singletonCImpl.dataSafetyCoordinatorImplProvider.get());

          case 2: // com.kairos.data.db.KairosDatabase 
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // com.kairos.core.media.MediaFileManager 
          return (T) new MediaFileManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 4: // com.kairos.data.backup.DataSafetyCoordinatorImpl 
          return (T) new DataSafetyCoordinatorImpl();

          case 5: // com.kairos.data.repository.SettingsRepositoryImpl 
          return (T) new SettingsRepositoryImpl(singletonCImpl.preferencesStoreProvider.get());

          case 6: // com.kairos.data.settings.PreferencesStore 
          return (T) new PreferencesStore(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 7: // com.kairos.data.backup.TrashPurgeWorker_AssistedFactory 
          return (T) new TrashPurgeWorker_AssistedFactory() {
            @Override
            public TrashPurgeWorker create(Context context2, WorkerParameters params2) {
              return new TrashPurgeWorker(context2, params2, singletonCImpl.patientDao(), singletonCImpl.caseDao(), singletonCImpl.caseMediaDao(), singletonCImpl.diagnosisDao(), singletonCImpl.shiftDao(), singletonCImpl.consultationSessionDao(), singletonCImpl.mediaFileManagerProvider.get(), singletonCImpl.dataSafetyCoordinatorImplProvider.get());
            }
          };

          case 8: // com.kairos.data.backup.WorkerScheduler 
          return (T) new WorkerScheduler(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 9: // com.kairos.data.repository.CaseRepositoryImpl 
          return (T) new CaseRepositoryImpl(singletonCImpl.caseDao(), singletonCImpl.diagnosisDao(), singletonCImpl.provideDatabaseProvider.get(), singletonCImpl.mediaFileManagerProvider.get(), singletonCImpl.dataSafetyCoordinatorImplProvider.get());

          case 10: // com.kairos.data.repository.MediaRepositoryImpl 
          return (T) new MediaRepositoryImpl(singletonCImpl.caseMediaDao(), singletonCImpl.provideDatabaseProvider.get(), singletonCImpl.mediaFileManagerProvider.get(), singletonCImpl.dataSafetyCoordinatorImplProvider.get());

          case 11: // com.kairos.data.repository.ConsultationRepositoryImpl 
          return (T) new ConsultationRepositoryImpl(singletonCImpl.consultationSessionDao(), singletonCImpl.provideDatabaseProvider.get(), singletonCImpl.dataSafetyCoordinatorImplProvider.get());

          case 12: // com.kairos.data.repository.DiagnosisRepositoryImpl 
          return (T) new DiagnosisRepositoryImpl(singletonCImpl.diagnosisDao(), singletonCImpl.provideDatabaseProvider.get(), singletonCImpl.dataSafetyCoordinatorImplProvider.get());

          case 13: // com.kairos.data.repository.PatientRepositoryImpl 
          return (T) new PatientRepositoryImpl(singletonCImpl.patientDao(), singletonCImpl.provideDatabaseProvider.get(), singletonCImpl.dataSafetyCoordinatorImplProvider.get());

          case 14: // com.kairos.core.media.AudioRecorderEngine 
          return (T) new AudioRecorderEngine(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 15: // com.kairos.data.repository.ShiftRepositoryImpl 
          return (T) new ShiftRepositoryImpl(singletonCImpl.shiftDao(), singletonCImpl.dataSafetyCoordinatorImplProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
