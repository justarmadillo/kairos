package com.taha.kairos.features.patient

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.taha.kairos.core.model.Patient

@Composable
fun ExistingPatientTab(
    state: PatientCaseUiState,
    onQueryChange: (String) -> Unit,
    onSelectPatient: (Patient) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = onQueryChange,
            label = { Text("Search by name") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
        )

        Spacer(Modifier.height(12.dp))

        if (state.searchQuery.isBlank()) {
            Text(
                "Start typing to search patients",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        } else if (state.searchResults.isEmpty()) {
            Text(
                "No patients found",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        } else {
            LazyColumn {
                items(state.searchResults) { patient ->
                    PatientSearchRow(
                        patient = patient,
                        onClick = { onSelectPatient(patient) },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun PatientSearchRow(
    patient: Patient,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(patient.name, style = MaterialTheme.typography.titleSmall)
            if (patient.age != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    "Age ${patient.age}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
