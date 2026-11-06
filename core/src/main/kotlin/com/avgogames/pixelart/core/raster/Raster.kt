package com.avgogames.pixelart.core.raster

import com.avgogames.pixelart.core.model.Category
import com.avgogames.pixelart.core.model.PixelArt
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

/**
 * A tiny software canvas used to author the bundled artworks procedurally.
 * Coordinates outside the canvas are clipped silently so shapes can overhang edges.
 */
class Raster(val width: Int, val height: Int, background: Int = 0xFFFFFFFF.toInt()) {
    private val pixels = IntArray(width * height) { background }

    operator fun get(x: Int, y: Int): Int = pixels[y * width + x]

    operator fun set(x: Int, y: Int, color: Int) {
        if (x in 0 until width && y in 0 until height) pixels[y * width + x] = color
    }

    fun fill(color: Int) = pixels.fill(color)

    /** Inclusive rectangle. */
    fun rect(x0: Int, y0: Int, x1: Int, y1: Int, color: Int) {
        for (y in max(0, min(y0, y1))..min(height - 1, max(y0, y1))) {
            for (x in max(0, min(x0, x1))..min(width - 1, max(x0, x1))) pixels[y * width + x] = color
        }
    }

    fun hline(x0: Int, x1: Int, y: Int, color: Int) = rect(x0, y, x1, y, color)

    fun vline(x: Int, y0: Int, y1: Int, color: Int) = rect(x, y0, x, y1, color)

    /**
     * Filled ellipse in continuous coordinates: pixel (x, y) covers [x, x+1) x [y, y+1) and is
     * filled when its centre lies inside. cx = 16 centres a shape on a 32-wide canvas.
     */
    fun ellipse(cx: Number, cy: Number, rx: Number, ry: Number, color: Int) {
        val fcx = cx.toFloat(); val fcy = cy.toFloat(); val frx = rx.toFloat(); val fry = ry.toFloat()
        val minY = floor(fcy - fry).toInt()
        val maxY = floor(fcy + fry).toInt()
        val minX = floor(fcx - frx).toInt()
        val maxX = floor(fcx + frx).toInt()
        for (y in minY..maxY) for (x in minX..maxX) {
            val dx = (x + 0.5f - fcx) / frx
            val dy = (y + 0.5f - fcy) / fry
            if (dx * dx + dy * dy <= 1.0f) this[x, y] = color
        }
    }

    fun circle(cx: Number, cy: Number, r: Number, color: Int) = ellipse(cx, cy, r, r, color)

    /** Filled polygon using even-odd scanline sampling at pixel centres. */
    fun polygon(color: Int, vararg points: Pair<Number, Number>) {
        val xs = points.map { it.first.toFloat() }
        val ys = points.map { it.second.toFloat() }
        val minY = max(0, floor(ys.min()).toInt())
        val maxY = min(height - 1, ys.max().toInt())
        for (y in minY..maxY) {
            val sy = y + 0.5f
            val crossings = mutableListOf<Float>()
            for (i in xs.indices) {
                val j = (i + 1) % xs.size
                val ax = xs[i]; val ay = ys[i]; val bx = xs[j]; val by = ys[j]
                if ((ay <= sy && by > sy) || (by <= sy && ay > sy)) {
                    crossings += ax + (sy - ay) / (by - ay) * (bx - ax)
                }
            }
            crossings.sort()
            var k = 0
            while (k + 1 < crossings.size) {
                val start = ceil(crossings[k] - 0.5f).toInt().coerceAtLeast(0)
                val end = floor(crossings[k + 1] - 0.5f).toInt().coerceAtMost(width - 1)
                for (x in start..end) pixels[y * width + x] = color
                k += 2
            }
        }
    }

    /** Bresenham line, [thickness] grows the brush as a square. */
    fun line(x0: Int, y0: Int, x1: Int, y1: Int, color: Int, thickness: Int = 1) {
        var x = x0
        var y = y0
        val dx = abs(x1 - x0)
        val dy = -abs(y1 - y0)
        val sx = if (x0 < x1) 1 else -1
        val sy = if (y0 < y1) 1 else -1
        var err = dx + dy
        while (true) {
            if (thickness <= 1) this[x, y] = color else rect(x, y, x + thickness - 1, y + thickness - 1, color)
            if (x == x1 && y == y1) break
            val e2 = 2 * err
            if (e2 >= dy) { err += dy; x += sx }
            if (e2 <= dx) { err += dx; y += sy }
        }
    }

    /** Replaces every pixel of [from] inside the bounds with [to]. */
    fun replace(from: Int, to: Int) {
        for (i in pixels.indices) if (pixels[i] == from) pixels[i] = to
    }

    /** Mirrors the left half onto the right half; handy for symmetrical faces. */
    fun mirrorLeftToRight() {
        for (y in 0 until height) for (x in 0 until width / 2) {
            pixels[y * width + (width - 1 - x)] = pixels[y * width + x]
        }
    }

    fun pixelsCopy(): IntArray = pixels.copyOf()

    /**
     * Converts the drawing to a [PixelArt], building the palette from the distinct colors
     * ordered from darkest to lightest so neighbouring numbers look related.
     */
    fun toPixelArt(id: String, title: String, category: Category): PixelArt {
        val distinct = pixels.distinct().sortedBy { luminance(it) }
        val indexOf = distinct.withIndex().associate { (index, color) -> color to index }
        return PixelArt(
            id = id,
            title = title,
            category = category,
            width = width,
            height = height,
            palette = distinct,
            cells = IntArray(pixels.size) { indexOf.getValue(pixels[it]) },
        )
    }
}
