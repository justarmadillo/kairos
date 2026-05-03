package com.kairos.features.cases

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import com.kairos.core.model.MediaItem
import com.kairos.core.model.MediaType
import com.kairos.core.theme.PaletteOnDark
import com.kairos.core.theme.PaletteSurfaceDark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import androidx.media3.common.MediaItem as ExoMediaItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageViewerScreen(
    initialIndex: Int = 0,
    onNavigateBack: () -> Unit,
    viewModel: CaseDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.ui.collectAsStateWithLifecycle()
    val visualMedia = state.case?.media?.filter {
        it.mediaType == MediaType.IMAGE || it.mediaType == MediaType.VIDEO
    } ?: emptyList()

    val pagerState = rememberPagerState(initialPage = initialIndex) { visualMedia.size }
    val snackbar = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PaletteSurfaceDark),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            val media = visualMedia[page]
            when (media.mediaType) {
                MediaType.VIDEO -> VideoPlayer(media = media, modifier = Modifier.fillMaxSize())
                else -> ZoomableImage(media = media, modifier = Modifier.fillMaxSize())
            }
        }

        // Top bar overlaid
        TopAppBar(
            title = {
                val current = if (visualMedia.isEmpty()) 0 else pagerState.currentPage + 1
                androidx.compose.material3.Text("$current / ${visualMedia.size}", color = PaletteOnDark)
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PaletteOnDark)
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        val media = visualMedia.getOrNull(pagerState.currentPage) ?: return@IconButton
                        scope.launch {
                            val saved = saveToGallery(context, File(media.filePath))
                            snackbar.showSnackbar(if (saved) "Saved to gallery" else "Save failed")
                        }
                    },
                ) {
                    Icon(Icons.Default.Download, contentDescription = "Save to gallery", tint = PaletteOnDark)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = PaletteSurfaceDark.copy(alpha = 0.72f)),
            modifier = Modifier.align(Alignment.TopCenter),
        )

        if (visualMedia.size > 1 && pagerState.currentPage > 0) {
            IconButton(
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 12.dp)
                    .size(48.dp)
                    .background(PaletteSurfaceDark.copy(alpha = 0.6f), CircleShape),
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous photo",
                    tint = PaletteOnDark,
                )
            }
        }

        if (visualMedia.size > 1 && pagerState.currentPage < visualMedia.lastIndex) {
            IconButton(
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp)
                    .size(48.dp)
                    .background(PaletteSurfaceDark.copy(alpha = 0.6f), CircleShape),
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next photo",
                    tint = PaletteOnDark,
                )
            }
        }

        SnackbarHost(
            hostState = snackbar,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
        )
    }
}

@Composable
private fun ZoomableImage(
    media: MediaItem,
    modifier: Modifier = Modifier,
) {
    var scale by remember(media.id) { mutableFloatStateOf(1f) }
    var offset by remember(media.id) { mutableStateOf(Offset.Zero) }
    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.5f, 5f)
        offset += panChange
    }

    AsyncImage(
        model = File(media.filePath),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .transformable(transformState)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y,
            ),
    )
}

@Composable
private fun VideoPlayer(
    media: MediaItem,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val player = remember(media.filePath) {
        ExoPlayer.Builder(context).build().also { p ->
            p.setMediaItem(ExoMediaItem.fromUri(Uri.fromFile(File(media.filePath))))
            p.prepare()
        }
    }

    DisposableEffect(player) {
        onDispose { player.release() }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                useController = true
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                this.player = player
            }
        },
        update = { view -> view.player = player },
        modifier = modifier,
    )
}

private suspend fun saveToGallery(context: Context, file: File): Boolean =
    withContext(Dispatchers.IO) {
        try {
            val mimeType = if (file.extension.lowercase() in listOf("mp4", "mov", "avi")) "video/mp4"
            else "image/jpeg"
            val isVideo = mimeType.startsWith("video")
            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (isVideo) MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                else MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                if (isVideo) MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                else MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            val values = ContentValues().apply {
                put(if (isVideo) MediaStore.Video.Media.DISPLAY_NAME else MediaStore.Images.Media.DISPLAY_NAME, file.name)
                put(if (isVideo) MediaStore.Video.Media.MIME_TYPE else MediaStore.Images.Media.MIME_TYPE, mimeType)
            }
            val uri = context.contentResolver.insert(collection, values) ?: return@withContext false
            context.contentResolver.openOutputStream(uri)?.use { out ->
                file.inputStream().copyTo(out)
            }
            true
        } catch (_: Exception) {
            false
        }
    }
