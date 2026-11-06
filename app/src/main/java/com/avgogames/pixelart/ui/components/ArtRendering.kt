package com.avgogames.pixelart.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.avgogames.pixelart.core.model.ColoringProgress
import com.avgogames.pixelart.core.model.PixelArt
import com.avgogames.pixelart.core.raster.luminance
import com.avgogames.pixelart.ui.theme.PixelColors
import kotlin.math.roundToInt

/** The grey "unpainted" tone for a target color: darker targets read as darker paper. */
fun unpaintedTone(targetArgb: Int): Color =
    lerp(PixelColors.CellLight, PixelColors.CellDark, (1f - luminance(targetArgb)) * 0.85f)

/**
 * One pixel per cell: finished cells in full color, the rest as paper tones. Building a bitmap
 * once per progress change is far cheaper than drawing 1–4k rects per gallery card per frame.
 */
fun renderArtBitmap(art: PixelArt, progress: ColoringProgress?, revealAll: Boolean = false): ImageBitmap {
    val pixels = IntArray(art.cellCount) { cell ->
        val target = art.targetColorAt(cell)
        when {
            revealAll -> target
            progress != null && progress.isCorrect(art, cell) -> target
            else -> unpaintedTone(target).toArgb()
        }
    }
    return Bitmap.createBitmap(pixels, art.width, art.height, Bitmap.Config.ARGB_8888).asImageBitmap()
}

/** Crisp, nearest-neighbour preview of an artwork scaled to fit its bounds. */
@Composable
fun PixelArtImage(
    art: PixelArt,
    progress: ColoringProgress?,
    modifier: Modifier = Modifier,
    revealAll: Boolean = false,
) {
    val bitmap = remember(art, progress, revealAll) { renderArtBitmap(art, progress, revealAll) }
    Canvas(modifier) {
        val cell = minOf(size.width / art.width, size.height / art.height)
        val w = (cell * art.width).roundToInt()
        val h = (cell * art.height).roundToInt()
        drawImage(
            image = bitmap,
            dstOffset = IntOffset(((size.width - w) / 2).roundToInt(), ((size.height - h) / 2).roundToInt()),
            dstSize = IntSize(w, h),
            filterQuality = FilterQuality.None,
        )
    }
}
