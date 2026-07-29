package com.taha.kairos.core.theme

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
    primaryContainer = PaletteSurfaceCard,
    onPrimaryContainer = PaletteInk,
    inversePrimary = PaletteCoralActive,
    secondary = PaletteSurfaceDark,
    onSecondary = LightBackground,
    secondaryContainer = PaletteSurfaceCreamStrong,
    onSecondaryContainer = PaletteInk,
    tertiary = PaletteAccentAmber,
    onTertiary = PaletteInk,
    tertiaryContainer = PaletteSurfaceSoft,
    onTertiaryContainer = PaletteInk,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnBackground,
    surfaceVariant = PaletteSurfaceCard,
    onSurfaceVariant = LightOnSurfaceMuted,
    surfaceTint = PaletteCoral,
    inverseSurface = PaletteSurfaceDark,
    inverseOnSurface = PaletteOnDark,
    outline = LightDivider,
    outlineVariant = PaletteHairlineSoft,
    scrim = PaletteSurfaceDark,
    error = LightError,
    onError = LightOnAccent,
    errorContainer = PaletteCoralDisabled,
    onErrorContainer = PaletteError,
    surfaceBright = PaletteCanvas,
    surfaceDim = PaletteSurfaceSoft,
    surfaceContainer = PaletteSurfaceSoft,
    surfaceContainerHigh = PaletteSurfaceCard,
    surfaceContainerHighest = PaletteSurfaceCreamStrong,
    surfaceContainerLow = PaletteCanvas,
    surfaceContainerLowest = PaletteCanvas,
)

private val DarkScheme = darkColorScheme(
    primary = DarkAccent,
    onPrimary = DarkOnAccent,
    primaryContainer = PaletteSurfaceDarkElevated,
    onPrimaryContainer = PaletteOnDark,
    inversePrimary = PaletteSurfaceCard,
    secondary = DarkSelectedDark,
    onSecondary = PaletteOnPrimary,
    secondaryContainer = PaletteSurfaceDarkSoft,
    onSecondaryContainer = PaletteOnDark,
    tertiary = PaletteAccentAmber,
    onTertiary = PaletteSurfaceDark,
    tertiaryContainer = PaletteSurfaceDarkElevated,
    onTertiaryContainer = PaletteOnDark,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnBackground,
    surfaceVariant = PaletteSurfaceDarkElevated,
    onSurfaceVariant = DarkOnSurfaceMuted,
    surfaceTint = PaletteCoral,
    inverseSurface = PaletteCanvas,
    inverseOnSurface = PaletteInk,
    outline = DarkDivider,
    outlineVariant = DarkDivider,
    scrim = PaletteInk,
    error = DarkError,
    onError = DarkOnAccent,
    errorContainer = PaletteSurfaceDarkElevated,
    onErrorContainer = PaletteOnDark,
    surfaceBright = PaletteSurfaceDarkElevated,
    surfaceDim = PaletteSurfaceDark,
    surfaceContainer = PaletteSurfaceDark,
    surfaceContainerHigh = DarkSurfaceCard,
    surfaceContainerHighest = DarkSurfaceCard,
    surfaceContainerLow = PaletteSurfaceDark,
    surfaceContainerLowest = PaletteSurfaceDark,
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
