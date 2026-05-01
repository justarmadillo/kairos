package com.kairos.features.cases

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kairos.core.components.EmptyState
import com.kairos.core.components.KairosTopBar
import com.kairos.core.model.Diagnosis
import com.kairos.core.model.DiagnosisSortMode
import com.kairos.core.theme.LocalKairosExtraColors

@Composable
fun DiagnosisBrowseScreen(
    onNavigateToCaseFeed: (diagnosisId: Long, diagnosisName: String) -> Unit = { _, _ -> },
    onAddCase: () -> Unit = {},
    viewModel: DiagnosisBrowseViewModel = hiltViewModel(),
) {
    val state by viewModel.ui.collectAsStateWithLifecycle()
    var showSortMenu by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newDiagnosisName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            KairosTopBar(
                title = "Cases",
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add diagnosis")
                    }
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort")
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false },
                    ) {
                        DiagnosisSortMode.entries.forEach { mode ->
                            DropdownMenuItem(
                                text = { Text(mode.label()) },
                                onClick = {
                                    viewModel.setSortMode(mode)
                                    showSortMenu = false
                                },
                            )
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddCase) {
                Icon(Icons.Default.Add, contentDescription = "Add case")
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::setQuery,
                label = { Text("Search diagnoses") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))

            if (!state.isLoading && state.diagnoses.isEmpty()) {
                EmptyState(
                    icon = Icons.Outlined.MedicalServices,
                    title = "No diagnoses yet",
                    message = "Tap + to add a case, or add a diagnosis from the toolbar",
                )
            } else {
                LazyColumn {
                    items(state.diagnoses, key = { it.id }) { diagnosis ->
                        DiagnosisRow(
                            diagnosis = diagnosis,
                            onClick = { onNavigateToCaseFeed(diagnosis.id, diagnosis.name) },
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add diagnosis") },
            text = {
                OutlinedTextField(
                    value = newDiagnosisName,
                    onValueChange = { newDiagnosisName = it },
                    label = { Text("Diagnosis") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.addDiagnosis(newDiagnosisName)
                        newDiagnosisName = ""
                        showAddDialog = false
                    },
                    enabled = newDiagnosisName.isNotBlank(),
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun DiagnosisRow(
    diagnosis: Diagnosis,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val extra = LocalKairosExtraColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = diagnosis.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f).padding(end = 12.dp),
        )
        Text(
            text = diagnosis.caseCount.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = extra.onSurfaceMuted,
        )
    }
}

private fun DiagnosisSortMode.label() = when (this) {
    DiagnosisSortMode.ALPHABETICAL -> "Alphabetical"
    DiagnosisSortMode.MOST_USED -> "Most used"
    DiagnosisSortMode.RECENT -> "Recently added"
}
