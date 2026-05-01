package com.kairos.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

private val LightScheme = lightColorScheme(
    primary = LightAccent,
    onPrimary = LightOnAccent,
    primaryContainer = PaletteStone,
    onPrimaryContainer = PaletteTerracotta,
    inversePrimary = PaletteStone,
    secondary = LightSelectedDark,
    onSecondary = LightBackground,
    secondaryContainer = PaletteLinen,
    onSecondaryContainer = PaletteBlack,
    tertiary = PaletteStone,
    onTertiary = PaletteWhite,
    tertiaryContainer = PaletteLinen,
    onTertiaryContainer = PaletteBlack,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnBackground,
    surfaceVariant = PaletteWhite,
    onSurfaceVariant = LightOnSurfaceMuted,
    surfaceTint = PaletteTerracotta,
    inverseSurface = PaletteBlack,
    inverseOnSurface = PaletteWhite,
    outline = LightDivider,
    outlineVariant = LightDivider,
    scrim = PaletteStone,
    error = LightError,
    onError = LightOnAccent,
    errorContainer = PaletteLinen,
    onErrorContainer = PaletteTerracotta,
    surfaceBright = PaletteWhite,
    surfaceDim = PaletteLinen,
    surfaceContainer = PaletteLinen,
    surfaceContainerHigh = PaletteLinen,
    surfaceContainerHighest = PaletteWhite,
    surfaceContainerLow = PaletteLinen,
    surfaceContainerLowest = PaletteLinen,
)

private val DarkScheme = darkColorScheme(
    primary = DarkAccent,
    onPrimary = DarkOnAccent,
    primaryContainer = PaletteStone,
    onPrimaryContainer = PaletteWhite,
    inversePrimary = PaletteLinen,
    secondary = DarkSelectedDark,
    onSecondary = PaletteWhite,
    secondaryContainer = PaletteStone,
    onSecondaryContainer = PaletteWhite,
    tertiary = PaletteLinen,
    onTertiary = PaletteBlack,
    tertiaryContainer = PaletteStone,
    onTertiaryContainer = PaletteWhite,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnBackground,
    surfaceVariant = PaletteStone,
    onSurfaceVariant = DarkOnSurfaceMuted,
    surfaceTint = PaletteTerracotta,
    inverseSurface = PaletteLinen,
    inverseOnSurface = PaletteBlack,
    outline = DarkDivider,
    outlineVariant = DarkDivider,
    scrim = PaletteStone,
    error = DarkError,
    onError = DarkOnAccent,
    errorContainer = PaletteStone,
    onErrorContainer = PaletteWhite,
    surfaceBright = PaletteStone,
    surfaceDim = PaletteBlack,
    surfaceContainer = PaletteBlack,
    surfaceContainerHigh = DarkSurfaceCard,
    surfaceContainerHighest = DarkSurfaceCard,
    surfaceContainerLow = PaletteBlack,
    surfaceContainerLowest = PaletteBlack,
)

/** App-specific tokens not covered by Material3 ColorScheme. */
data class KairosExtraColors(
    val surfaceCard: androidx.compose.ui.graphics.Color,
    val onSurfaceMuted: androidx.compose.ui.graphics.Color,
    val selectedDark: androidx.compose.ui.graphics.Color,
    val divider: androidx.compose.ui.graphics.Color
)

val LocalKairosExtraColors = staticCompositionLocalOf {
    KairosExtraColors(
        surfaceCard = LightSurfaceCard,
        onSurfaceMuted = LightOnSurfaceMuted,
        selectedDark = LightSelectedDark,
        divider = LightDivider
    )
}

@Composable
fun KairosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val scheme = if (darkTheme) DarkScheme else LightScheme
    val extras = if (darkTheme) {
        KairosExtraColors(DarkSurfaceCard, DarkOnSurfaceMuted, DarkSelectedDark, DarkDivider)
    } else {
        KairosExtraColors(LightSurfaceCard, LightOnSurfaceMuted, LightSelectedDark, LightDivider)
    }

    CompositionLocalProvider(LocalKairosExtraColors provides extras) {
        MaterialTheme(
            colorScheme = scheme,
            typography = KairosTypography,
            shapes = KairosShapes,
            content = content
        )
    }
}
