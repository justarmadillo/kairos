package com.kairos.features.shifts

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kairos.core.components.EmptyState
import com.kairos.core.components.KairosTopBar
import com.kairos.core.model.Shift
import com.kairos.core.theme.LocalKairosExtraColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ShiftsListScreen(
    onAddPatient: () -> Unit = {},
    onNavigateToDetail: (shiftId: Long) -> Unit = {},
    viewModel: ShiftsViewModel = hiltViewModel(),
) {
    val state by viewModel.ui.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    // Undo snackbar on delete
    LaunchedEffect(state.recentlyDeletedShift) {
        val deleted = state.recentlyDeletedShift ?: return@LaunchedEffect
        val result = snackbar.showSnackbar(
            message = "Shift deleted",
            actionLabel = "Undo",
            duration = SnackbarDuration.Long,
        )
        if (result == SnackbarResult.ActionPerformed) {
            viewModel.undoDelete()
        } else {
            viewModel.clearUndo()
        }
    }

    Scaffold(
        topBar = {
            KairosTopBar(
                title = "Shifts",
                actions = {
                    androidx.compose.material3.IconButton(onClick = viewModel::openAddDialog) {
                        Icon(Icons.Default.Add, contentDescription = "Add shift")
                    }
                },
            )
        },
        floatingActionButton = {
            // FAB only shown when there are shifts (direct entry = via shift detail)
            // Kept minimal — main + is in top bar
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->

        if (!state.isLoading && state.shifts.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.LocalHospital,
                title = "No shifts yet",
                message = "Tap + to log your first shift",
                modifier = Modifier.padding(padding),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { Spacer(Modifier.height(8.dp)) }
                items(state.shifts, key = { it.id }) { shift ->
                    ShiftCard(
                        shift = shift,
                        onClick = { onNavigateToDetail(shift.id) },
                        onLongClick = { viewModel.softDelete(shift) },
                    )
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }

    if (state.showAddDialog) {
        AddShiftDialog(
            onDismiss = viewModel::closeAddDialog,
            onConfirm = { date, label ->
                viewModel.addShift(date, label) { newId ->
                    onNavigateToDetail(newId)
                }
            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ShiftCard(
    shift: Shift,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val extra = LocalKairosExtraColors.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        colors = CardDefaults.cardColors(containerColor = extra.surfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = MaterialTheme.shapes.large,
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
                        .format(Date(shift.date)),
                    style = MaterialTheme.typography.titleLarge,
                )
                if (!shift.label.isNullOrBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = shift.label ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            SuggestionChip(
                onClick = {},
                label = {
                    Text(
                        "${shift.caseCount} ${if (shift.caseCount == 1) "case" else "cases"}",
                        style = MaterialTheme.typography.labelMedium,
                    )
                },
            )
        }
    }
}
