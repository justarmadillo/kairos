package com.kairos.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.kairos.core.model.MediaType
import com.kairos.core.theme.PaletteOnDark
import com.kairos.core.theme.PaletteSurfaceDark
import java.io.File

// Minimal descriptor usable both for PendingMedia (pre-save) and saved MediaItem
data class MediaDisplayItem(
    val localId: Int,
    val filePath: String,
    val mediaType: MediaType,
    val durationMs: Long? = null,
    val isPrimary: Boolean = false,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MediaAttachmentSection(
    items: List<MediaDisplayItem>,
    onTakePhoto: () -> Unit,
    onTakeVideo: () -> Unit,
    onPickFromGallery: () -> Unit,
    onRecordAudio: () -> Unit,
    onRemove: (Int) -> Unit,
    onSetPrimary: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val visual = items.filter { it.mediaType != MediaType.AUDIO }
    val audio = items.filter { it.mediaType == MediaType.AUDIO }

    androidx.compose.foundation.layout.Column(modifier = modifier) {
        // Action chips
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            AssistChip(
                onClick = onTakePhoto,
                label = { Text("Take photo") },
                leadingIcon = { Icon(Icons.Default.CameraAlt, null, Modifier.size(18.dp)) },
            )
            AssistChip(
                onClick = onTakeVideo,
                label = { Text("Take video") },
                leadingIcon = { Icon(Icons.Default.Videocam, null, Modifier.size(18.dp)) },
            )
            AssistChip(
                onClick = onPickFromGallery,
                label = { Text("Gallery") },
                leadingIcon = { Icon(Icons.Default.PhotoLibrary, null, Modifier.size(18.dp)) },
            )
            AssistChip(
                onClick = onRecordAudio,
                label = { Text("Voice note") },
                leadingIcon = { Icon(Icons.Default.Mic, null, Modifier.size(18.dp)) },
            )
        }

        // Image/video grid
        if (visual.isNotEmpty()) {
            val rowCount = (visual.size + 2) / 3
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height((rowCount * 112).dp.coerceAtMost(336.dp)),
                userScrollEnabled = false,
            ) {
                items(visual, key = { it.localId }) { item ->
                    VisualThumbnail(
                        item = item,
                        onRemove = { onRemove(item.localId) },
                        onSetPrimary = { onSetPrimary(item.localId) },
                    )
                }
            }
        }

        // Audio items
        audio.forEachIndexed { index, item ->
            AudioPlayerItem(
                filePath = item.filePath,
                durationMs = item.durationMs,
                index = index,
                onDelete = { onRemove(item.localId) },
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun VisualThumbnail(
    item: MediaDisplayItem,
    onRemove: () -> Unit,
    onSetPrimary: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = if (item.isPrimary) 2.dp else 0.dp,
                color = if (item.isPrimary) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = MaterialTheme.shapes.large,
            )
            .clickable(onClick = onSetPrimary),
    ) {
        AsyncImage(
            model = File(item.filePath),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
        )

        if (item.mediaType == MediaType.VIDEO) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
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

        // Remove button
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(28.dp)
                .background(PaletteSurfaceDark.copy(alpha = 0.5f), MaterialTheme.shapes.small),
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Remove",
                tint = PaletteOnDark,
                modifier = Modifier.size(16.dp),
            )
        }

        // Primary indicator
        if (item.isPrimary) {
            Icon(
                Icons.Default.Star,
                contentDescription = "Primary image",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .size(16.dp),
            )
        }
    }
}
