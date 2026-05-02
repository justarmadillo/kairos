package com.kairos.core.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.kairos.core.model.Case
import com.kairos.core.model.MediaItem
import com.kairos.core.model.MediaType
import java.io.File
import com.kairos.core.theme.LocalKairosExtraColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun CaseCard(
    case: Case,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val extra = LocalKairosExtraColors.current
    val patient = case.patient
    val primaryMedia = case.media.firstOrNull { it.isPrimary } ?: case.media.firstOrNull()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            ),
        colors = CardDefaults.cardColors(containerColor = extra.surfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = MaterialTheme.shapes.large,
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            // Thumbnail
            CaseThumbnail(primaryMedia)
            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Patient name + age
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = patient?.name ?: "Unknown patient",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    if (patient?.age != null) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Age ${patient.age}",
                            style = MaterialTheme.typography.bodySmall,
                            color = extra.onSurfaceMuted,
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                // Case date
                Text(
                    text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                        .format(Date(case.caseDate)),
                    style = MaterialTheme.typography.bodySmall,
                    color = extra.onSurfaceMuted,
                )

                // Mechanism preview
                if (!case.mechanism.isNullOrBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = case.mechanism,
                        style = MaterialTheme.typography.bodySmall,
                        color = extra.onSurfaceMuted,
                        maxLines = 1,
                    )
                }

                // Diagnosis chips
                if (case.diagnoses.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        case.diagnoses.take(3).forEach { d ->
                            SuggestionChip(
                                onClick = {},
                                label = {
                                    Text(
                                        d.name,
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1,
                                    )
                                },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    labelColor = MaterialTheme.colorScheme.onSurface,
                                ),
                            )
                        }
                        if (case.diagnoses.size > 3) {
                            Text(
                                "+${case.diagnoses.size - 3}",
                                style = MaterialTheme.typography.labelSmall,
                                color = extra.onSurfaceMuted,
                                modifier = Modifier.align(Alignment.CenterVertically),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CaseThumbnail(
    media: MediaItem?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (media != null) {
            AsyncImage(
                model = File(media.filePath),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp),
            )
        }

        if (media?.mediaType == MediaType.VIDEO) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.55f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Video",
                    tint = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}
