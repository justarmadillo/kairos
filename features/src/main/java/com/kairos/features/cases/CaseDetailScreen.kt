package com.kairos.features.cases

import android.content.Intent
import android.net.Uri
import android.content.ClipData
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.kairos.core.components.AudioPlayerItem
import com.kairos.core.model.Case
import com.kairos.core.model.MediaItem
import com.kairos.core.model.MediaType
import java.io.File
import com.kairos.core.theme.LocalKairosExtraColors
import com.kairos.core.theme.PaletteOnDark
import com.kairos.core.theme.PaletteSurfaceDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val DetailThumbnailAspectRatio = 0.78f

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CaseDetailScreen(
    onNavigateBack: () -> Unit,
    onEditCase: (caseId: Long) -> Unit,
    onNavigateToCaseFeed: (diagnosisId: Long, diagnosisName: String) -> Unit,
    onOpenImageViewer: (caseId: Long, mediaIndex: Int) -> Unit,
    viewModel: CaseDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.ui.collectAsStateWithLifecycle()
    val case = state.case
    val context = LocalContext.current
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbar.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(state.pdfToShare) {
        val file = state.pdfToShare ?: return@LaunchedEffect
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            clipData = ClipData.newUri(context.contentResolver, file.name, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share PDF"))
        viewModel.clearPdfShare()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(case?.patient?.name ?: "")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state.isExportingPdf) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .size(24.dp),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        IconButton(onClick = viewModel::exportPdf) {
                            Icon(Icons.Default.Share, contentDescription = "Share PDF")
                        }
                    }
                    IconButton(onClick = { case?.let { onEditCase(it.id) } }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { viewModel.softDelete(onNavigateBack) }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        if (case == null) return@Scaffold

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(12.dp))

            // Patient header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = case.patient?.name ?: "",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f),
                )
                case.patient?.age?.let { age ->
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Age $age",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LocalKairosExtraColors.current.onSurfaceMuted,
                    )
                }
            }

            // Case date
            Spacer(Modifier.height(4.dp))
            Text(
                text = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault())
                    .format(Date(case.caseDate)),
                style = MaterialTheme.typography.bodyMedium,
                color = LocalKairosExtraColors.current.onSurfaceMuted,
            )

            // Phones
            case.patient?.phones?.takeIf { it.isNotEmpty() }?.let { phones ->
                Spacer(Modifier.height(12.dp))
                phones.forEach { phone ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                context.startActivity(
                                    Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phone.number}"))
                                )
                            }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Default.Phone,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = phone.number + if (!phone.label.isNullOrBlank()) "  ·  ${phone.label}" else "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp))

            // Diagnoses
            if (case.diagnoses.isNotEmpty()) {
                Text("Diagnoses", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    case.diagnoses.forEach { d ->
                        SuggestionChip(
                            onClick = { onNavigateToCaseFeed(d.id, d.name) },
                            label = { Text(d.name) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                labelColor = MaterialTheme.colorScheme.onSurface,
                            ),
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
            }

            // Mechanism
            if (!case.mechanism.isNullOrBlank()) {
                Text("Mechanism", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                Text(case.mechanism ?: "", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(14.dp))
            }

            // Notes
            if (!case.notesHtml.isNullOrBlank()) {
                Text("Notes", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(8.dp))
                // Render HTML notes as plain text for now (rich rendering optional)
                val plainText = remember(case.notesHtml) {
                    android.text.Html.fromHtml(case.notesHtml, android.text.Html.FROM_HTML_MODE_COMPACT)
                        .toString().trim()
                }
                Text(plainText, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(14.dp))
            }

            // Images / videos
            val visualMedia = case.media.filter { it.mediaType != MediaType.AUDIO }
            if (visualMedia.isNotEmpty()) {
                val columns = 3
                val gridSpacing = 6.dp
                HorizontalDivider(modifier = Modifier.padding(bottom = 14.dp))
                Text("Images & Videos", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(8.dp))
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val cellWidth = (maxWidth - gridSpacing * (columns - 1)) / columns.toFloat()
                    Column(
                        verticalArrangement = Arrangement.spacedBy(gridSpacing),
                    ) {
                        visualMedia.chunked(columns).forEach { rowMedia ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(gridSpacing),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                rowMedia.forEach { media ->
                                    val index = visualMedia.indexOf(media)
                                    VisualMediaThumbnail(
                                        media = media,
                                        onClick = { onOpenImageViewer(case.id, index) },
                                        modifier = Modifier.width(cellWidth),
                                    )
                                }
                                repeat(columns - rowMedia.size) {
                                    Spacer(Modifier.width(cellWidth))
                                }
                            }
                        }
                    }
                }
            }

            // Audio
            val audioMedia = case.media.filter { it.mediaType == MediaType.AUDIO }
            if (audioMedia.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Text("Voice Notes", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(8.dp))
                audioMedia.forEachIndexed { index, media ->
                    AudioPlayerItem(
                        filePath = media.filePath,
                        durationMs = media.durationMs,
                        index = index,
                        onDelete = { viewModel.deleteMedia(media.id) },
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun VisualMediaThumbnail(
    media: MediaItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(DetailThumbnailAspectRatio)
            .clip(MaterialTheme.shapes.large)
            .background(PaletteSurfaceDark)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = File(media.filePath),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.matchParentSize(),
        )

        if (media.mediaType == MediaType.VIDEO) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(PaletteSurfaceDark.copy(alpha = 0.55f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Video",
                    tint = PaletteOnDark,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}
