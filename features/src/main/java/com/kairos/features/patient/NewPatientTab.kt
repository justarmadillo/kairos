package com.kairos.features.patient

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.kairos.core.components.AudioRecorderModal
import com.kairos.core.components.DiagnosisAutocomplete
import com.kairos.core.components.MediaAttachmentSection
import com.kairos.core.components.MediaDisplayItem
import com.kairos.core.components.PhoneInputSection
import com.kairos.core.components.RichNotesEditor
import com.kairos.core.media.MediaFileManager
import com.kairos.core.model.MediaType
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun NewPatientTab(
    state: PatientCaseUiState,
    mediaFileManager: MediaFileManager,
    onNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onAddPhone: (String, String?) -> Unit,
    onRemovePhone: (com.kairos.core.model.PatientPhone) -> Unit,
    onCaseDateChange: (Long) -> Unit,
    onMechanismChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onDiagnosisQuery: (String) -> Unit,
    onSelectDiagnosis: (String) -> Unit,
    onRemoveDiagnosis: (String) -> Unit,
    onAttachFile: (java.io.File, MediaType, String?) -> Unit,
    onRemoveMedia: (Int) -> Unit,
    onSetPrimaryMedia: (Int) -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onCancelRecording: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val richTextState = rememberRichTextState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showAudioModal by remember { mutableStateOf(false) }

    // Sync rich text state with external notesHtml (e.g. on first load)
    remember(state.notesHtml) {
        if (richTextState.toHtml() != state.notesHtml && state.notesHtml.isEmpty()) {
            richTextState.setHtml("")
        }
    }

    // Audio permission
    val audioPermission = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    // Camera photo launcher — photo is written to a FileProvider temp file
    var pendingPhotoFile by remember { mutableStateOf<java.io.File?>(null) }
    val photoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            pendingPhotoFile?.let { onAttachFile(it, MediaType.IMAGE, null) }
        } else {
            pendingPhotoFile?.delete()
        }
        pendingPhotoFile = null
    }

    // Camera video launcher
    var pendingVideoFile by remember { mutableStateOf<java.io.File?>(null) }
    val videoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CaptureVideo()
    ) { success ->
        if (success) {
            pendingVideoFile?.let { onAttachFile(it, MediaType.VIDEO, null) }
        } else {
            pendingVideoFile?.delete()
        }
        pendingVideoFile = null
    }

    var pendingCameraCapture by remember { mutableStateOf<MediaType?>(null) }

    fun launchPhotoCapture() {
        val file = mediaFileManager.newCaseMediaFile(caseId = 0, type = MediaType.IMAGE)
        pendingPhotoFile = file
        photoLauncher.launch(mediaFileManager.contentUriFor(file))
    }

    fun launchVideoCapture() {
        val file = mediaFileManager.newCaseMediaFile(caseId = 0, type = MediaType.VIDEO)
        pendingVideoFile = file
        videoLauncher.launch(mediaFileManager.contentUriFor(file))
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        val capture = pendingCameraCapture
        pendingCameraCapture = null
        if (granted) {
            when (capture) {
                MediaType.IMAGE -> launchPhotoCapture()
                MediaType.VIDEO -> launchVideoCapture()
                else -> Unit
            }
        }
    }

    fun launchWithCameraPermission(type: MediaType) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA,
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            when (type) {
                MediaType.IMAGE -> launchPhotoCapture()
                MediaType.VIDEO -> launchVideoCapture()
                else -> Unit
            }
        } else {
            pendingCameraCapture = type
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Gallery multi-pick
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris: List<Uri> ->
        uris.forEach { uri ->
            val mimeType = context.contentResolver.getType(uri) ?: ""
            val mediaType = if (mimeType.startsWith("video")) MediaType.VIDEO else MediaType.IMAGE
            val file = mediaFileManager.newCaseMediaFile(caseId = 0, type = mediaType)
            context.contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output -> input.copyTo(output) }
            }
            onAttachFile(file, mediaType, null)
        }
    }

    // File picker (PDFs, ZIPs, docs, etc.)
    val fileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        uris.forEach { uri ->
            val displayName = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (idx >= 0) cursor.getString(idx) else null
                } else null
            } ?: "file"
            val ext = displayName.substringAfterLast('.', "").takeIf { it.isNotEmpty() }
            val file = mediaFileManager.newCaseMediaFile(caseId = 0, type = MediaType.FILE, ext)
            context.contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output -> input.copyTo(output) }
            }
            onAttachFile(file, MediaType.FILE, displayName)
        }
    }

    Column(
        modifier = modifier
            .imePadding()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(16.dp))

        // Name
        OutlinedTextField(
            value = state.name,
            onValueChange = onNameChange,
            label = { Text("Name *") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
        )
        Spacer(Modifier.height(12.dp))

        // Age
        OutlinedTextField(
            value = state.age,
            onValueChange = onAgeChange,
            label = { Text("Age") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
        )
        Spacer(Modifier.height(12.dp))

        // Phones
        PhoneInputSection(
            phones = state.phones,
            onAdd = onAddPhone,
            onRemove = onRemovePhone,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))

        // Case date
        val dateLabel = remember(state.caseDate) {
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                .format(Date(state.caseDate))
        }
        OutlinedButton(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
        ) {
            Text("Case date: $dateLabel")
        }
        Spacer(Modifier.height(12.dp))

        // Mechanism
        OutlinedTextField(
            value = state.mechanism,
            onValueChange = onMechanismChange,
            label = { Text("Mechanism of injury") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
        )
        Spacer(Modifier.height(12.dp))

        // Diagnoses autocomplete
        DiagnosisAutocomplete(
            query = state.diagnosisQuery,
            suggestions = state.diagnosisSuggestions,
            selected = state.diagnoses,
            onQueryChange = onDiagnosisQuery,
            onSelect = onSelectDiagnosis,
            onRemove = onRemoveDiagnosis,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))

        // Notes
        Text("Notes", style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(4.dp))
        RichNotesEditor(
            state = richTextState,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Clinical notes…") },
        )
        // Keep VM in sync whenever richText changes
        androidx.compose.runtime.LaunchedEffect(richTextState.annotatedString) {
            onNotesChange(richTextState.toHtml())
        }
        Spacer(Modifier.height(12.dp))

        // Media
        Text("Attachments", style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(8.dp))

        val displayItems = state.pendingMedia.map { pm ->
            MediaDisplayItem(
                localId = pm.localId,
                filePath = pm.sourceFile.absolutePath,
                mediaType = pm.mediaType,
                durationMs = pm.durationMs,
                isPrimary = pm.isPrimary,
                originalFileName = pm.originalFileName,
            )
        }

        MediaAttachmentSection(
            items = displayItems,
            onTakePhoto = { launchWithCameraPermission(MediaType.IMAGE) },
            onTakeVideo = { launchWithCameraPermission(MediaType.VIDEO) },
            onPickFromGallery = {
                galleryLauncher.launch(
                    androidx.activity.result.PickVisualMediaRequest(
                        androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageAndVideo
                    )
                )
            },
            onRecordAudio = {
                if (audioPermission.status.isGranted) {
                    showAudioModal = true
                } else {
                    audioPermission.launchPermissionRequest()
                }
            },
            onPickFile = { fileLauncher.launch(arrayOf("*/*")) },
            onRemove = onRemoveMedia,
            onSetPrimary = onSetPrimaryMedia,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(24.dp))
    }

    // Date picker dialog
    if (showDatePicker) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = state.caseDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { onCaseDateChange(it) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            },
        ) {
            DatePicker(state = pickerState)
        }
    }

    // Audio recorder modal
    if (showAudioModal || state.isRecording) {
        AudioRecorderModal(
            isRecording = state.isRecording,
            elapsedMs = state.recordingElapsedMs,
            onStart = {
                onStartRecording()
                showAudioModal = false
            },
            onStop = {
                onStopRecording()
                showAudioModal = false
            },
            onCancel = {
                onCancelRecording()
                showAudioModal = false
            },
        )
    }
}
