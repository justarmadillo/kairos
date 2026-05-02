package com.kairos.features.patient

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kairos.core.media.MediaFileManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientCaseScreen(
    linkShiftId: Long? = null,
    linkSessionId: Long? = null,
    editCaseId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: PatientCaseViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val mediaFileManager = viewModel.mediaFileManager

    // Pre-fill form when editing an existing case
    LaunchedEffect(editCaseId) {
        editCaseId?.let { viewModel.loadCase(it) }
    }
    val snackbar = remember { SnackbarHostState() }
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    // Navigate back after successful save
    LaunchedEffect(state.saved) {
        if (state.saved) onNavigateBack()
    }

    // Show errors in snackbar
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbar.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val label = when {
                        state.selectedPatient != null -> "New case for ${state.selectedPatient!!.name}"
                        pagerState.currentPage == 0 -> "New patient"
                        else -> "Existing patient"
                    }
                    Text(label)
                },
                windowInsets = WindowInsets(0.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    } else {
                        Button(
                            onClick = { viewModel.save(linkShiftId, linkSessionId) },
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.padding(end = 8.dp),
                        ) {
                            Text("Save")
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { innerPadding ->

        // Hide tabs if an existing patient is selected (form collapses to new-case-for-patient)
        if (state.selectedPatient != null) {
            Box(modifier = Modifier.padding(innerPadding)) {
                NewCaseForExistingPatient(
                    state = state,
                    mediaFileManager = mediaFileManager,
                    viewModel = viewModel,
                )
            }
        } else {
            Column(modifier = Modifier.padding(innerPadding)) {
                TabRow(selectedTabIndex = pagerState.currentPage) {
                    Tab(
                        selected = pagerState.currentPage == 0,
                        onClick = { scope.launch { pagerState.animateScrollToPage(0) } },
                        text = { Text("New") },
                    )
                    Tab(
                        selected = pagerState.currentPage == 1,
                        onClick = { scope.launch { pagerState.animateScrollToPage(1) } },
                        text = { Text("Existing") },
                    )
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                ) { page ->
                    when (page) {
                        0 -> NewPatientTab(
                            state = state,
                            mediaFileManager = mediaFileManager,
                            onNameChange = viewModel::setName,
                            onAgeChange = viewModel::setAge,
                            onAddPhone = viewModel::addPhone,
                            onRemovePhone = viewModel::removePhone,
                            onCaseDateChange = viewModel::setCaseDate,
                            onMechanismChange = viewModel::setMechanism,
                            onNotesChange = viewModel::setNotes,
                            onDiagnosisQuery = viewModel::setDiagnosisQuery,
                            onSelectDiagnosis = viewModel::selectDiagnosis,
                            onRemoveDiagnosis = viewModel::removeDiagnosis,
                            onAttachFile = viewModel::attachFile,
                            onRemoveMedia = viewModel::removePendingMedia,
                            onSetPrimaryMedia = viewModel::setPrimaryMedia,
                            onStartRecording = viewModel::startRecording,
                            onStopRecording = viewModel::stopRecording,
                            onCancelRecording = viewModel::cancelRecording,
                            modifier = Modifier.verticalScroll(rememberScrollState()),
                        )
                        1 -> ExistingPatientTab(
                            state = state,
                            onQueryChange = viewModel::setSearchQuery,
                            onSelectPatient = viewModel::selectExistingPatient,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }
    }
}

/** Form displayed after selecting an existing patient — only case fields are editable. */
@Composable
private fun NewCaseForExistingPatient(
    state: PatientCaseUiState,
    mediaFileManager: MediaFileManager,
    viewModel: PatientCaseViewModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        NewPatientTab(
            state = state,
            mediaFileManager = mediaFileManager,
            onNameChange = {},          // locked — patient is selected
            onAgeChange = {},
            onAddPhone = { _, _ -> },
            onRemovePhone = {},
            onCaseDateChange = viewModel::setCaseDate,
            onMechanismChange = viewModel::setMechanism,
            onNotesChange = viewModel::setNotes,
            onDiagnosisQuery = viewModel::setDiagnosisQuery,
            onSelectDiagnosis = viewModel::selectDiagnosis,
            onRemoveDiagnosis = viewModel::removeDiagnosis,
            onAttachFile = viewModel::attachFile,
            onRemoveMedia = viewModel::removePendingMedia,
            onSetPrimaryMedia = viewModel::setPrimaryMedia,
            onStartRecording = viewModel::startRecording,
            onStopRecording = viewModel::stopRecording,
            onCancelRecording = viewModel::cancelRecording,
        )
    }
}
