package com.taha.kairos.core.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AudioRecorderModal(
    isRecording: Boolean,
    elapsedMs: Long,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(if (isRecording) "Recording…" else "Record Voice Note") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (isRecording) {
                    Text(
                        text = formatDuration(elapsedMs),
                        style = MaterialTheme.typography.displaySmall,
                    )
                    Spacer(Modifier.height(24.dp))
                } else {
                    Text(
                        text = "Tap the mic to start recording",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(24.dp))
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (isRecording) {
                        FilledIconButton(
                            onClick = onStop,
                            modifier = Modifier.size(72.dp),
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                            ),
                        ) {
                            Icon(
                                Icons.Default.Stop,
                                contentDescription = "Stop recording",
                                modifier = Modifier.size(36.dp),
                            )
                        }
                    } else {
                        FilledIconButton(
                            onClick = onStart,
                            modifier = Modifier.size(72.dp),
                            shape = CircleShape,
                        ) {
                            Icon(
                                Icons.Default.Mic,
                                contentDescription = "Start recording",
                                modifier = Modifier.size(36.dp),
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onCancel) {
                Icon(Icons.Default.Close, contentDescription = null)
                Text("Cancel")
            }
        },
        modifier = modifier,
    )
}

internal fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
