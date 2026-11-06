package com.avgogames.pixelart.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape

/** Palette taken from the editor reference: paper-white surfaces, one confident blue accent. */
object PixelColors {
    val Accent = Color(0xFF2F9BF0)
    val AccentDark = Color(0xFF1C7ED0)
    val Ink = Color(0xFF1F2430)
    val InkMuted = Color(0xFF6B7280)
    val Paper = Color(0xFFF3F4F7)
    val Surface = Color(0xFFFFFFFF)
    val Outline = Color(0xFFE3E6EC)
    val Success = Color(0xFF2EB872)
    val Warning = Color(0xFFE5484D)

    /** Unpainted cells: a light grey "paper" whose tone hints at the final shade. */
    val CellLight = Color(0xFFF7F8FA)
    val CellDark = Color(0xFFC5CAD3)
    val CellHighlight = Color(0xFF9CCBF5)
    val GridLine = Color(0x1F1F2430)
}

private val colors = lightColorScheme(
    primary = PixelColors.Accent,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDEEFF),
    onPrimaryContainer = PixelColors.AccentDark,
    secondary = Color(0xFFFF6B8B),
    onSecondary = Color.White,
    background = PixelColors.Paper,
    onBackground = PixelColors.Ink,
    surface = PixelColors.Surface,
    onSurface = PixelColors.Ink,
    surfaceVariant = Color(0xFFEDEFF3),
    onSurfaceVariant = PixelColors.InkMuted,
    outline = PixelColors.Outline,
    error = PixelColors.Warning,
)

private val typography = Typography(
    headlineSmall = TextStyle(fontWeight = FontWeight.Black, fontSize = 26.sp, letterSpacing = (-0.5).sp),
    titleMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp),
    titleSmall = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 12.sp, letterSpacing = 0.6.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp),
)

private val shapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
)

@Composable
fun PixelPaintTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = colors, typography = typography, shapes = shapes, content = content)
}
