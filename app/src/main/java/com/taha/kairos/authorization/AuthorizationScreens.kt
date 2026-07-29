package com.taha.kairos.authorization

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun AuthorizationLaunchScreen(
    deviceId: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DeviceIdCard(deviceId = deviceId)
            Spacer(Modifier.height(24.dp))
            CircularProgressIndicator()
        }
    }
}

/**
 * Locked-state UI. The parent owns ActivityResultContracts.OpenDocumentTree and
 * should pass its launcher through [onOpenDocumentTree]. When a URI is returned,
 * pass uri.toString() to AuthorizationGateViewModel.exportData.
 */
@Composable
fun AuthorizationLockedScreen(
    deviceId: String,
    locked: AuthorizationAccessState.Locked,
    exportState: AuthorizationExportState,
    onRetry: () -> Unit,
    onOpenDocumentTree: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DeviceIdCard(deviceId = deviceId)

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onRetry,
            enabled = !locked.isRetrying,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (locked.isRetrying) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Text("Check authorization")
            }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onOpenDocumentTree,
            enabled = exportState != AuthorizationExportState.Exporting,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Export data")
        }
        Spacer(Modifier.height(12.dp))

        when (exportState) {
            AuthorizationExportState.Idle -> Unit
            AuthorizationExportState.Exporting -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                )
            }

            is AuthorizationExportState.Success -> ExportResultText(
                message = "Export complete",
                isError = false,
            )

            is AuthorizationExportState.Failure -> ExportResultText(
                message = "Export failed",
                isError = true,
            )
        }
    }
}

@Composable
private fun DeviceIdCard(
    deviceId: String,
    modifier: Modifier = Modifier,
) {
    val clipboard = LocalClipboardManager.current
    var copied by remember(deviceId) { mutableStateOf(false) }

    LaunchedEffect(copied) {
        if (copied) {
            delay(2_000L)
            copied = false
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 8.dp)) {
            Text(
                text = "Device ID",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            SelectionContainer {
                Text(
                    text = deviceId,
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(end = 16.dp),
                )
            }
            TextButton(
                onClick = {
                    clipboard.setText(AnnotatedString(deviceId))
                    copied = true
                },
                modifier = Modifier.align(Alignment.End),
            ) {
                Text(if (copied) "Copied" else "Copy device ID")
            }
        }
    }
}

@Composable
private fun ExportResultText(
    message: String,
    isError: Boolean,
) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = if (isError) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.primary
        },
        modifier = Modifier.fillMaxWidth(),
    )
}
