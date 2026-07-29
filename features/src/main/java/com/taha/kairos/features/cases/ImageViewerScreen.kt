package com.taha.kairos.features.cases

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem as ExoMediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import com.taha.kairos.core.model.MediaItem
import com.taha.kairos.core.model.MediaType
import com.taha.kairos.core.theme.PaletteOnDark
import com.taha.kairos.core.theme.PaletteSurfaceDark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.abs
import kotlin.math.min

private const val MIN_IMAGE_SCALE = 1f
private const val DOUBLE_TAP_IMAGE_SCALE = 2.5f
private const val MAX_IMAGE_SCALE = 5f
private const val ZOOM_EPSILON = 0.01f
private const val VIDEO_CONTROL_REGION_START = 0.76f
private const val MIN_SWIPE_SLOP_MULTIPLIER = 4f
private const val MIN_VIDEO_SWIPE_FRACTION = 0.12f

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

    val pagerState = rememberPagerState { visualMedia.size }
    var initialPageApplied by remember(initialIndex) { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val saveCurrentMedia: () -> Unit = {
        val media = visualMedia.getOrNull(pagerState.currentPage)
        if (media != null) {
            scope.launch {
                val saved = saveToGallery(context, media)
                snackbar.showSnackbar(if (saved) "Saved to gallery" else "Save failed")
            }
        }
    }
    val legacyStoragePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            saveCurrentMedia()
        } else {
            scope.launch { snackbar.showSnackbar("Storage permission is required") }
        }
    }

    LaunchedEffect(visualMedia.size, initialIndex) {
        if (!initialPageApplied && visualMedia.isNotEmpty()) {
            pagerState.scrollToPage(initialIndex.coerceIn(0, visualMedia.lastIndex))
            initialPageApplied = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PaletteSurfaceDark),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            key = { page -> visualMedia[page].id },
            userScrollEnabled = pagerState.isScrollInProgress ||
                visualMedia.getOrNull(pagerState.currentPage)?.mediaType != MediaType.VIDEO,
        ) { page ->
            val media = visualMedia[page]
            when (media.mediaType) {
                MediaType.VIDEO -> VideoPlayer(
                    media = media,
                    isActive = page == pagerState.currentPage,
                    onSwipePrevious = {
                        if (page > 0) {
                            scope.launch { pagerState.animateScrollToPage(page - 1) }
                        }
                    },
                    onSwipeNext = {
                        if (page < visualMedia.lastIndex) {
                            scope.launch { pagerState.animateScrollToPage(page + 1) }
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                )
                else -> ZoomableImage(
                    media = media,
                    isActive = page == pagerState.currentPage,
                    modifier = Modifier.fillMaxSize(),
                )
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
                        val needsLegacyPermission =
                            Build.VERSION.SDK_INT <= Build.VERSION_CODES.P &&
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                ) != PackageManager.PERMISSION_GRANTED
                        if (needsLegacyPermission) {
                            legacyStoragePermissionLauncher.launch(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            )
                        } else {
                            saveCurrentMedia()
                        }
                    },
                ) {
                    Icon(Icons.Default.Download, contentDescription = "Save to gallery", tint = PaletteOnDark)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = PaletteSurfaceDark.copy(alpha = 0.72f)),
            modifier = Modifier.align(Alignment.TopCenter),
        )

        SnackbarHost(
            hostState = snackbar,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
        )
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun ZoomableImage(
    media: MediaItem,
    isActive: Boolean,
    modifier: Modifier = Modifier,
) {
    var scale by remember(media.id) { mutableFloatStateOf(MIN_IMAGE_SCALE) }
    var offset by remember(media.id) { mutableStateOf(Offset.Zero) }
    var viewportSize by remember(media.id) { mutableStateOf(IntSize.Zero) }
    var sourceImageSize by remember(media.id) { mutableStateOf(IntSize.Zero) }

    fun resetZoom() {
        scale = MIN_IMAGE_SCALE
        offset = Offset.Zero
    }

    LaunchedEffect(isActive) {
        if (!isActive) resetZoom()
    }

    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        val newScale = (scale * zoomChange).coerceIn(MIN_IMAGE_SCALE, MAX_IMAGE_SCALE)
        scale = newScale
        offset = if (newScale <= MIN_IMAGE_SCALE + ZOOM_EPSILON) {
            Offset.Zero
        } else {
            (offset + panChange).coerceToZoomBounds(
                viewportSize = viewportSize,
                sourceImageSize = sourceImageSize,
                scale = newScale,
            )
        }
    }

    Box(
        modifier = modifier
            .clipToBounds()
            .onSizeChanged { viewportSize = it }
            .pointerInput(media.id, viewportSize, sourceImageSize, scale) {
                detectTapGestures(
                    onDoubleTap = { tapPosition ->
                        if (scale > MIN_IMAGE_SCALE + ZOOM_EPSILON) {
                            resetZoom()
                        } else {
                            val targetScale = DOUBLE_TAP_IMAGE_SCALE
                            val center = Offset(
                                x = viewportSize.width / 2f,
                                y = viewportSize.height / 2f,
                            )
                            scale = targetScale
                            offset = ((center - tapPosition) * (targetScale - MIN_IMAGE_SCALE))
                                .coerceToZoomBounds(
                                    viewportSize = viewportSize,
                                    sourceImageSize = sourceImageSize,
                                    scale = targetScale,
                                )
                        }
                    },
                )
            }
            .transformable(
                state = transformState,
                canPan = { panChange ->
                    canConsumeImagePan(
                        panChange = panChange,
                        offset = offset,
                        viewportSize = viewportSize,
                        sourceImageSize = sourceImageSize,
                        scale = scale,
                    )
                },
                lockRotationOnZoomPan = true,
            ),
    ) {
        AsyncImage(
            model = File(media.filePath),
            contentDescription = media.originalFileName ?: "Case image",
            contentScale = ContentScale.Fit,
            onSuccess = { state ->
                sourceImageSize = IntSize(
                    width = state.result.image.width,
                    height = state.result.image.height,
                )
                offset = offset.coerceToZoomBounds(
                    viewportSize = viewportSize,
                    sourceImageSize = sourceImageSize,
                    scale = scale,
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y,
                    clip = true,
                ),
        )
    }
}

@Composable
@SuppressLint("ClickableViewAccessibility")
@androidx.annotation.OptIn(UnstableApi::class)
private fun VideoPlayer(
    media: MediaItem,
    isActive: Boolean,
    onSwipePrevious: () -> Unit,
    onSwipeNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnSwipePrevious = rememberUpdatedState(onSwipePrevious)
    val currentOnSwipeNext = rememberUpdatedState(onSwipeNext)
    val player = remember(media.filePath) {
        ExoPlayer.Builder(context).build().also { p ->
            p.setMediaItem(ExoMediaItem.fromUri(Uri.fromFile(File(media.filePath))))
            p.prepare()
        }
    }

    LaunchedEffect(player, isActive) {
        if (!isActive) player.pause()
    }

    DisposableEffect(player, lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
                player.pause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            player.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                useController = true
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                this.player = player
                installGallerySwipeNavigation(
                    onSwipePrevious = { currentOnSwipePrevious.value() },
                    onSwipeNext = { currentOnSwipeNext.value() },
                )
            }
        },
        update = { view -> view.player = player },
        modifier = modifier,
    )
}

@SuppressLint("ClickableViewAccessibility")
private fun PlayerView.installGallerySwipeNavigation(
    onSwipePrevious: () -> Unit,
    onSwipeNext: () -> Unit,
) {
    val touchSlop = ViewConfiguration.get(context).scaledTouchSlop.toFloat()
    var downX = 0f
    var downY = 0f
    var isPagingSwipe = false
    var startedOnControls = false

    setOnTouchListener { view, event ->
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                isPagingSwipe = false
                // Keep the lower controller/seek area fully interactive.
                startedOnControls = event.y >= view.height * VIDEO_CONTROL_REGION_START
                false
            }

            MotionEvent.ACTION_MOVE -> {
                if (startedOnControls) return@setOnTouchListener false

                val distanceX = event.x - downX
                val distanceY = event.y - downY
                if (!isPagingSwipe &&
                    abs(distanceX) > touchSlop &&
                    abs(distanceX) > abs(distanceY)
                ) {
                    isPagingSwipe = true
                    view.parent?.requestDisallowInterceptTouchEvent(true)
                }
                isPagingSwipe
            }

            MotionEvent.ACTION_UP -> {
                if (!isPagingSwipe) return@setOnTouchListener false

                val distanceX = event.x - downX
                val minimumDistance = maxOf(
                    touchSlop * MIN_SWIPE_SLOP_MULTIPLIER,
                    view.width * MIN_VIDEO_SWIPE_FRACTION,
                )
                if (abs(distanceX) >= minimumDistance) {
                    if (distanceX < 0f) onSwipeNext() else onSwipePrevious()
                }
                isPagingSwipe = false
                true
            }

            MotionEvent.ACTION_CANCEL -> {
                val wasPaging = isPagingSwipe
                isPagingSwipe = false
                wasPaging
            }

            else -> isPagingSwipe
        }
    }
}

private fun canConsumeImagePan(
    panChange: Offset,
    offset: Offset,
    viewportSize: IntSize,
    sourceImageSize: IntSize,
    scale: Float,
): Boolean {
    if (scale <= MIN_IMAGE_SCALE + ZOOM_EPSILON) return false

    // Vertical movement belongs to the zoomed image. Horizontal movement at an
    // image edge is left for the pager so the next/previous item remains swipeable.
    if (abs(panChange.y) >= abs(panChange.x)) return true

    val maxX = zoomBounds(
        viewportSize = viewportSize,
        sourceImageSize = sourceImageSize,
        scale = scale,
    ).x
    if (maxX <= ZOOM_EPSILON) return false

    return when {
        panChange.x > 0f -> offset.x < maxX - ZOOM_EPSILON
        panChange.x < 0f -> offset.x > -maxX + ZOOM_EPSILON
        else -> true
    }
}

private fun Offset.coerceToZoomBounds(
    viewportSize: IntSize,
    sourceImageSize: IntSize,
    scale: Float,
): Offset {
    val bounds = zoomBounds(
        viewportSize = viewportSize,
        sourceImageSize = sourceImageSize,
        scale = scale,
    )
    return Offset(
        x = x.coerceIn(-bounds.x, bounds.x),
        y = y.coerceIn(-bounds.y, bounds.y),
    )
}

private fun zoomBounds(
    viewportSize: IntSize,
    sourceImageSize: IntSize,
    scale: Float,
): Offset {
    if (viewportSize.width <= 0 || viewportSize.height <= 0) return Offset.Zero

    val sourceWidth = sourceImageSize.width.takeIf { it > 0 } ?: viewportSize.width
    val sourceHeight = sourceImageSize.height.takeIf { it > 0 } ?: viewportSize.height
    val fitScale = min(
        viewportSize.width.toFloat() / sourceWidth.toFloat(),
        viewportSize.height.toFloat() / sourceHeight.toFloat(),
    )
    val fittedWidth = sourceWidth * fitScale
    val fittedHeight = sourceHeight * fitScale

    return Offset(
        x = ((fittedWidth * scale - viewportSize.width) / 2f).coerceAtLeast(0f),
        y = ((fittedHeight * scale - viewportSize.height) / 2f).coerceAtLeast(0f),
    )
}

private suspend fun saveToGallery(context: Context, media: MediaItem): Boolean =
    withContext(Dispatchers.IO) {
        val file = File(media.filePath)
        var insertedUri: Uri? = null
        try {
            if (!file.isFile) return@withContext false

            val isVideo = media.mediaType == MediaType.VIDEO
            val mimeType = MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(file.extension.lowercase())
                ?.takeIf { candidate ->
                    (isVideo && candidate.startsWith("video/")) ||
                        (!isVideo && candidate.startsWith("image/"))
                }
                ?: if (isVideo) "video/mp4" else "image/jpeg"
            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (isVideo) MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                else MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                if (isVideo) MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                else MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, media.originalFileName ?: file.name)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        if (isVideo) {
                            Environment.DIRECTORY_MOVIES + "/Kairos"
                        } else {
                            Environment.DIRECTORY_PICTURES + "/Kairos"
                        },
                    )
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }
            val uri = context.contentResolver.insert(collection, values)
                ?: return@withContext false
            insertedUri = uri
            val output = context.contentResolver.openOutputStream(uri, "w")
                ?: error("Gallery output is unavailable")
            output.use { out ->
                file.inputStream().copyTo(out)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val updated = context.contentResolver.update(
                    uri,
                    ContentValues().apply {
                        put(MediaStore.MediaColumns.IS_PENDING, 0)
                    },
                    null,
                    null,
                )
                check(updated == 1) { "Gallery item could not be finalized" }
            }
            true
        } catch (_: Exception) {
            insertedUri?.let { uri ->
                runCatching { context.contentResolver.delete(uri, null, null) }
            }
            false
        }
    }
