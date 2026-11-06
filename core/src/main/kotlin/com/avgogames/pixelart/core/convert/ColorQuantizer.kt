package com.avgogames.pixelart.core.convert

import com.avgogames.pixelart.core.raster.argb
import com.avgogames.pixelart.core.raster.blue
import com.avgogames.pixelart.core.raster.green
import com.avgogames.pixelart.core.raster.red

/**
 * Reduces an image to a small palette: median cut picks well-spread starting colors, then a few
 * k-means passes pull each color to the centre of the pixels it represents. Deterministic for
 * the same input, so re-converting a photo with the same settings gives the same artwork.
 */
object ColorQuantizer {

    fun quantize(pixels: IntArray, maxColors: Int, iterations: Int = 6): List<Int> {
        require(maxColors >= 1) { "maxColors must be >= 1" }
        val distinct = pixels.distinct()
        if (distinct.size <= maxColors) return distinct
        var palette = medianCut(pixels, maxColors)
        repeat(iterations) { palette = refine(pixels, palette) }
        return palette.distinct()
    }

    /** Index of the palette color perceptually closest to [color]. */
    fun nearest(color: Int, palette: List<Int>): Int {
        var best = 0
        var bestDistance = Long.MAX_VALUE
        for (i in palette.indices) {
            val d = distance(color, palette[i])
            if (d < bestDistance) { bestDistance = d; best = i }
        }
        return best
    }

    /**
     * "Redmean" weighted RGB distance: cheap, and far closer to perceived difference than plain
     * Euclidean RGB, especially for skin tones and greens.
     */
    fun distance(a: Int, b: Int): Long {
        val rMean = (red(a) + red(b)) / 2
        val dr = (red(a) - red(b)).toLong()
        val dg = (green(a) - green(b)).toLong()
        val db = (blue(a) - blue(b)).toLong()
        return (((512 + rMean) * dr * dr) shr 8) + 4 * dg * dg + (((767 - rMean) * db * db) shr 8)
    }

    private fun medianCut(pixels: IntArray, count: Int): List<Int> {
        val boxes = mutableListOf(pixels.copyOf())
        while (boxes.size < count) {
            // Split the box with the widest single channel range.
            var widest = -1
            var widestRange = 0
            var widestChannel = 0
            for ((index, box) in boxes.withIndex()) {
                if (box.size < 2) continue
                for (channel in 0..2) {
                    var min = 255; var max = 0
                    for (c in box) { val v = channelOf(c, channel); if (v < min) min = v; if (v > max) max = v }
                    if (max - min > widestRange) { widestRange = max - min; widest = index; widestChannel = channel }
                }
            }
            if (widest < 0) break
            val box = boxes.removeAt(widest)
            val sorted = box.sortedBy { channelOf(it, widestChannel) }
            val mid = sorted.size / 2
            boxes += sorted.subList(0, mid).toIntArray()
            boxes += sorted.subList(mid, sorted.size).toIntArray()
        }
        return boxes.filter { it.isNotEmpty() }.map(::average)
    }

    private fun refine(pixels: IntArray, palette: List<Int>): List<Int> {
        val r = LongArray(palette.size); val g = LongArray(palette.size); val b = LongArray(palette.size)
        val n = IntArray(palette.size)
        for (c in pixels) {
            val i = nearest(c, palette)
            r[i] += red(c).toLong(); g[i] += green(c).toLong(); b[i] += blue(c).toLong(); n[i]++
        }
        return palette.indices.map { i ->
            if (n[i] == 0) palette[i] else argb((r[i] / n[i]).toInt(), (g[i] / n[i]).toInt(), (b[i] / n[i]).toInt())
        }
    }

    private fun average(colors: IntArray): Int {
        var r = 0L; var g = 0L; var b = 0L
        for (c in colors) { r += red(c); g += green(c); b += blue(c) }
        return argb((r / colors.size).toInt(), (g / colors.size).toInt(), (b / colors.size).toInt())
    }

    private fun channelOf(color: Int, channel: Int) = when (channel) {
        0 -> red(color)
        1 -> green(color)
        else -> blue(color)
    }
}
