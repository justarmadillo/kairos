package com.kairos.features.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kairos.core.components.KairosTopBar
import com.kairos.core.model.BackupSchedule
import com.kairos.core.model.DiagnosisSortMode
import com.kairos.core.model.ThemeMode
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToTrash: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val backupUi by viewModel.backupUi.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var showRestoreConfirm by remember { mutableStateOf(false) }
    var pendingRestoreUri by remember { mutableStateOf<String?>(null) }
    var showRestartDialog by remember { mutableStateOf(false) }

    LaunchedEffect(backupUi.lastMessage) {
        backupUi.lastMessage?.let {
            snackbar.showSnackbar(it)
            viewModel.clearBackupMessage()
        }
    }

    LaunchedEffect(backupUi.restoreCompleted) {
        if (backupUi.restoreCompleted) showRestartDialog = true
    }

    // SAF folder picker — take persistable permission so access survives reboots
    val folderPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or
                android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
            )
            viewModel.setBackupFolder(it.toString())
        }
    }

    // SAF file picker for restore
    val restorePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            pendingRestoreUri = it.toString()
            showRestoreConfirm = true
        }
    }

    Scaffold(
        topBar = { KairosTopBar(title = "Settings") },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            SectionHeader("Consultation")

            // Consultation day of week
            DropdownSetting(
                label = "Consultation day",
                current = settings.consultationDayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                options = DayOfWeek.entries.map {
                    it.getDisplayName(TextStyle.FULL, Locale.getDefault()) to it
                },
                onSelect = { viewModel.setConsultationDay(it) },
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            SectionHeader("Appearance")

            DropdownSetting(
                label = "Theme",
                current = settings.themeMode.label(),
                options = ThemeMode.entries.map { it.label() to it },
                onSelect = { viewModel.setTheme(it) },
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            SectionHeader("Cases")

            DropdownSetting(
                label = "Default diagnosis sort",
                current = settings.diagnosisSortMode.label(),
                options = DiagnosisSortMode.entries.map { it.label() to it },
                onSelect = { viewModel.setDiagnosisSort(it) },
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            SectionHeader("Backup")

            ListItem(
                headlineContent = { Text("Backup folder") },
                supportingContent = {
                    Text(
                        settings.backupFolderUri?.takeLast(40)?.let { "…$it" } ?: "Not set",
                        style = MaterialTheme.typography.bodySmall,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { folderPicker.launch(null) },
            )

            DropdownSetting(
                label = "Auto-backup schedule",
                current = settings.backupSchedule.label(),
                options = BackupSchedule.entries.map { it.label() to it },
                onSelect = { viewModel.setBackupSchedule(it) },
            )

            // Export now
            ListItem(
                headlineContent = { Text("Export now") },
                supportingContent = { Text(if (settings.backupFolderUri == null) "Select a folder first" else "Backup to selected folder") },
                trailingContent = {
                    if (backupUi.isExporting) {
                        CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                    } else {
                        Button(
                            onClick = viewModel::exportNow,
                            enabled = settings.backupFolderUri != null,
                            shape = MaterialTheme.shapes.medium,
                        ) { Text("Export") }
                    }
                },
            )

            // Restore from backup
            ListItem(
                headlineContent = { Text("Restore from backup") },
                supportingContent = { Text("Select a .zip backup file") },
                trailingContent = {
                    if (backupUi.isRestoring) {
                        CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                    } else {
                        Button(
                            onClick = { restorePicker.launch(arrayOf("application/zip", "*/*")) },
                            enabled = !backupUi.isExporting,
                            shape = MaterialTheme.shapes.medium,
                        ) { Text("Restore") }
                    }
                },
            )

            settings.backupLastRunAt?.let { ts ->
                ListItem(
                    headlineContent = { Text("Last backup") },
                    supportingContent = {
                        val date = java.text.SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
                            .format(java.util.Date(ts))
                        val status = if (settings.backupLastRunSuccess == true) "✓" else "✗"
                        Text("$status  $date")
                    },
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            SectionHeader("Data")

            ListItem(
                headlineContent = { Text("Optimize database") },
                supportingContent = { Text("Reclaim space after bulk deletes") },
                trailingContent = {
                    if (backupUi.isVacuuming) {
                        CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                    } else {
                        Button(
                            onClick = viewModel::vacuumDatabase,
                            enabled = !backupUi.isExporting && !backupUi.isRestoring,
                            shape = MaterialTheme.shapes.medium,
                        ) { Text("Optimize") }
                    }
                },
            )

            ListItem(
                headlineContent = { Text("Trash") },
                supportingContent = { Text("Restore or permanently delete items") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNavigateToTrash),
            )
        }
    }

    // Confirm before restore (warns about data loss)
    if (showRestoreConfirm) {
        AlertDialog(
            onDismissRequest = { showRestoreConfirm = false; pendingRestoreUri = null },
            title = { Text("Restore backup?") },
            text = { Text("All current data will be replaced by the backup. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showRestoreConfirm = false
                    pendingRestoreUri?.let { viewModel.restoreBackup(it) }
                    pendingRestoreUri = null
                }) { Text("Restore") }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreConfirm = false; pendingRestoreUri = null }) {
                    Text("Cancel")
                }
            },
        )
    }

    // After successful restore: prompt user to restart
    if (showRestartDialog) {
        AlertDialog(
            onDismissRequest = { showRestartDialog = false; viewModel.clearRestoreCompleted() },
            title = { Text("Restore complete") },
            text = { Text("Please restart the app to apply the restored data.") },
            confirmButton = {
                TextButton(onClick = { showRestartDialog = false; viewModel.clearRestoreCompleted() }) {
                    Text("OK")
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> DropdownSetting(
    label: String,
    current: String,
    options: List<Pair<String, T>>,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        OutlinedTextField(
            value = current,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
            shape = MaterialTheme.shapes.medium,
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { (displayName, value) ->
                DropdownMenuItem(
                    text = { Text(displayName) },
                    onClick = {
                        onSelect(value)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp),
    )
}

private fun ThemeMode.label() = when (this) {
    ThemeMode.LIGHT -> "Light"
    ThemeMode.DARK -> "Dark"
    ThemeMode.SYSTEM -> "System default"
}

private fun DiagnosisSortMode.label() = when (this) {
    DiagnosisSortMode.ALPHABETICAL -> "Alphabetical"
    DiagnosisSortMode.MOST_USED -> "Most used"
    DiagnosisSortMode.RECENT -> "Recently added"
}

private fun BackupSchedule.label() = when (this) {
    BackupSchedule.OFF -> "Off"
    BackupSchedule.DAILY -> "Daily"
    BackupSchedule.WEEKLY -> "Weekly"
    BackupSchedule.MONTHLY -> "Monthly"
}
