package com.avgogames.pixelart.core.convert

import com.avgogames.pixelart.core.model.Category
import com.avgogames.pixelart.core.model.PixelArt
import com.avgogames.pixelart.core.raster.argb
import com.avgogames.pixelart.core.raster.blue
import com.avgogames.pixelart.core.raster.green
import com.avgogames.pixelart.core.raster.luminance
import com.avgogames.pixelart.core.raster.red
import kotlin.math.roundToInt

/** Settings the import screen exposes. */
data class ConversionOptions(
    /** Columns in the resulting grid; rows follow the source aspect ratio. */
    val gridWidth: Int = 32,
    /** Upper bound on palette size. Fewer colors are used when the image has fewer. */
    val maxColors: Int = 16,
) {
    init {
        require(gridWidth in MIN_GRID..MAX_GRID) { "gridWidth must be in $MIN_GRID..$MAX_GRID" }
        require(maxColors in MIN_COLORS..MAX_COLORS) { "maxColors must be in $MIN_COLORS..$MAX_COLORS" }
    }

    companion object {
        const val MIN_GRID = 16
        const val MAX_GRID = 64
        const val MIN_COLORS = 2
        const val MAX_COLORS = 32
    }
}

/**
 * Turns decoded image pixels into a color-by-number [PixelArt]: box-filter downscale to the
 * grid, composite transparency over white, quantize to a palette, then index every cell.
 * Pure Kotlin so it runs (and is tested) off-device; Android only supplies the ARGB pixels.
 */
object PixelArtConverter {

    fun convert(
        id: String,
        title: String,
        pixels: IntArray,
        sourceWidth: Int,
        sourceHeight: Int,
        options: ConversionOptions = ConversionOptions(),
    ): PixelArt {
        require(sourceWidth > 0 && sourceHeight > 0) { "Empty image" }
        require(pixels.size == sourceWidth * sourceHeight) { "Pixel buffer does not match size" }

        val width = minOf(options.gridWidth, sourceWidth)
        val height = (sourceHeight.toFloat() * width / sourceWidth).roundToInt()
            .coerceIn(1, ConversionOptions.MAX_GRID * 2)
        val small = downscale(pixels, sourceWidth, sourceHeight, width, height)

        val palette = ColorQuantizer.quantize(small, options.maxColors)
            .sortedBy { luminance(it) }
        val cells = IntArray(small.size) { ColorQuantizer.nearest(small[it], palette) }

        // Drop palette entries nothing maps to, keeping indices dense.
        val used = cells.distinct().sorted()
        val remap = IntArray(palette.size) { -1 }.also { map -> used.forEachIndexed { newIndex, old -> map[old] = newIndex } }
        return PixelArt(
            id = id,
            title = title,
            category = Category.IMPORTED,
            width = width,
            height = height,
            palette = used.map { palette[it] },
            cells = IntArray(cells.size) { remap[cells[it]] },
        )
    }

    /**
     * Area-average downscale: each output cell is the mean of the source pixels it covers,
     * which keeps thin features and avoids the shimmer of nearest-neighbour sampling.
     */
    internal fun downscale(src: IntArray, sw: Int, sh: Int, dw: Int, dh: Int): IntArray {
        val out = IntArray(dw * dh)
        for (oy in 0 until dh) {
            val y0 = oy * sh / dh
            val y1 = maxOf(y0 + 1, (oy + 1) * sh / dh)
            for (ox in 0 until dw) {
                val x0 = ox * sw / dw
                val x1 = maxOf(x0 + 1, (ox + 1) * sw / dw)
                var r = 0L; var g = 0L; var b = 0L; var n = 0
                for (y in y0 until y1) for (x in x0 until x1) {
                    val c = src[y * sw + x]
                    val a = (c ushr 24) and 0xFF
                    // Composite over white so transparent PNG areas become paper, not black.
                    r += (red(c) * a + 255 * (255 - a)) / 255
                    g += (green(c) * a + 255 * (255 - a)) / 255
                    b += (blue(c) * a + 255 * (255 - a)) / 255
                    n++
                }
                out[oy * dw + ox] = argb((r / n).toInt(), (g / n).toInt(), (b / n).toInt())
            }
        }
        return out
    }
}
