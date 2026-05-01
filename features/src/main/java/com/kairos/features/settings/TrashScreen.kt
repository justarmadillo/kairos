package com.kairos.features.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kairos.core.components.EmptyState
import com.kairos.core.model.Case
import com.kairos.core.model.ConsultationSession
import com.kairos.core.model.Patient
import com.kairos.core.model.Shift
import com.kairos.core.theme.LocalKairosExtraColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(
    onNavigateBack: () -> Unit,
    viewModel: TrashViewModel = hiltViewModel(),
) {
    val state by viewModel.ui.collectAsStateWithLifecycle()
    val isEmpty = state.patients.isEmpty() && state.cases.isEmpty() &&
            state.shifts.isEmpty() && state.sessions.isEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trash") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        if (!state.isLoading && isEmpty) {
            EmptyState(
                icon = Icons.Default.RestoreFromTrash,
                title = "Trash is empty",
                message = "Deleted items appear here for 30 days",
                modifier = Modifier.padding(padding),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
            ) {
                if (state.shifts.isNotEmpty()) {
                    item { TrashSectionHeader("Shifts") }
                    items(state.shifts, key = { "shift_${it.id}" }) { shift ->
                        ShiftTrashRow(shift = shift, onRestore = { viewModel.restoreShift(shift.id) })
                    }
                }
                if (state.sessions.isNotEmpty()) {
                    item { TrashSectionHeader("Consultation Sessions") }
                    items(state.sessions, key = { "session_${it.id}" }) { session ->
                        SessionTrashRow(session = session, onRestore = { viewModel.restoreSession(session.id) })
                    }
                }
                if (state.cases.isNotEmpty()) {
                    item { TrashSectionHeader("Cases") }
                    items(state.cases, key = { "case_${it.id}" }) { case ->
                        CaseTrashRow(case = case, onRestore = { viewModel.restoreCase(case.id) })
                    }
                }
                if (state.patients.isNotEmpty()) {
                    item { TrashSectionHeader("Patients") }
                    items(state.patients, key = { "patient_${it.id}" }) { patient ->
                        PatientTrashRow(patient = patient, onRestore = { viewModel.restorePatient(patient.id) })
                    }
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun TrashSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
    )
}

@Composable
private fun TrashRow(
    title: String,
    subtitle: String,
    daysRemaining: Int,
    onRestore: () -> Unit,
) {
    val extra = LocalKairosExtraColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = extra.onSurfaceMuted)
            Text(
                if (daysRemaining > 0) "$daysRemaining days until purge" else "Purging soon",
                style = MaterialTheme.typography.labelSmall,
                color = if (daysRemaining <= 3) MaterialTheme.colorScheme.error else extra.onSurfaceMuted,
            )
        }
        OutlinedButton(onClick = onRestore) { Text("Restore") }
    }
}

@Composable
private fun ShiftTrashRow(shift: Shift, onRestore: () -> Unit) {
    val label = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(shift.date))
    TrashRow(
        title = shift.label?.let { "$label — $it" } ?: label,
        subtitle = "Shift",
        daysRemaining = daysUntilPurge(shift.deletedAt ?: System.currentTimeMillis()),
        onRestore = onRestore,
    )
}

@Composable
private fun SessionTrashRow(session: ConsultationSession, onRestore: () -> Unit) {
    val label = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(session.date))
    TrashRow(
        title = label,
        subtitle = "Consultation session",
        daysRemaining = daysUntilPurge(session.deletedAt ?: System.currentTimeMillis()),
        onRestore = onRestore,
    )
}

@Composable
private fun CaseTrashRow(case: Case, onRestore: () -> Unit) {
    TrashRow(
        title = case.patient?.name ?: "Unknown patient",
        subtitle = case.diagnoses.take(2).joinToString(", ") { it.name },
        daysRemaining = daysUntilPurge(case.deletedAt ?: case.updatedAt),
        onRestore = onRestore,
    )
}

@Composable
private fun PatientTrashRow(patient: Patient, onRestore: () -> Unit) {
    TrashRow(
        title = patient.name,
        subtitle = patient.age?.let { "Age $it" } ?: "Patient",
        daysRemaining = daysUntilPurge(patient.deletedAt ?: patient.updatedAt),
        onRestore = onRestore,
    )
}

private fun daysUntilPurge(deletedAtMs: Long): Int {
    val purgeAt = deletedAtMs + TimeUnit.DAYS.toMillis(30)
    val remaining = purgeAt - System.currentTimeMillis()
    return (TimeUnit.MILLISECONDS.toDays(remaining)).toInt().coerceAtLeast(0)
}
