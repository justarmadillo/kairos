package com.kairos.core.theme

import androidx.compose.ui.graphics.Color

val PaletteTerracotta = Color(0xFFC15F3C)
val PaletteWhite = Color(0xFFFFFFFF)
val PaletteLinen = Color(0xFFF4F3EE)
val PaletteStone = Color(0xFFB1ADA1)
val PaletteBlack = Color(0xFF171411)

// App palette from the provided reference.
val LightBackground = PaletteLinen
val LightSurface = PaletteLinen
val LightSurfaceCard = PaletteWhite
val LightOnBackground = PaletteBlack
val LightOnSurfaceMuted = PaletteStone
val LightAccent = PaletteTerracotta
val LightOnAccent = PaletteWhite
val LightSelectedDark = PaletteTerracotta
val LightDivider = PaletteStone.copy(alpha = 0.35f)
val LightError = PaletteTerracotta

// Dark mode keeps the requested palette as accents while preserving readability.
val DarkBackground = PaletteBlack
val DarkSurface = PaletteBlack
val DarkSurfaceCard = Color(0xFF2D2A26)
val DarkOnBackground = PaletteLinen
val DarkOnSurfaceMuted = PaletteStone
val DarkAccent = PaletteTerracotta
val DarkOnAccent = PaletteWhite
val DarkSelectedDark = PaletteTerracotta
val DarkDivider = PaletteStone.copy(alpha = 0.45f)
val DarkError = PaletteTerracotta
