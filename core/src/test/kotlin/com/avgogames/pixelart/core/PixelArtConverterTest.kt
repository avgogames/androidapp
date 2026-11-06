package com.avgogames.pixelart.core

import com.avgogames.pixelart.core.convert.ColorQuantizer
import com.avgogames.pixelart.core.convert.ConversionOptions
import com.avgogames.pixelart.core.convert.PixelArtConverter
import com.avgogames.pixelart.core.model.Category
import com.avgogames.pixelart.core.raster.rgb
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PixelArtConverterTest {

    @Test
    fun keepsAspectRatioAndGridWidth() {
        val art = PixelArtConverter.convert("i", "Photo", gradient(400, 200), 400, 200, ConversionOptions(gridWidth = 40))
        assertEquals(40, art.width)
        assertEquals(20, art.height)
        assertEquals(Category.IMPORTED, art.category)
    }

    @Test
    fun respectsColorLimitAndUsesEveryColor() {
        val art = PixelArtConverter.convert("i", "Photo", gradient(300, 300), 300, 300, ConversionOptions(32, maxColors = 8))
        assertTrue(art.palette.size <= 8)
        assertTrue(art.cellCountPerColor.all { it > 0 })
    }

    @Test
    fun flatImageKeepsExactColors() {
        val red = rgb(0xFF0000); val blue = rgb(0x0000FF)
        val pixels = IntArray(64 * 64) { if ((it % 64) < 32) red else blue }
        val art = PixelArtConverter.convert("i", "Flag", pixels, 64, 64, ConversionOptions(16, 4))
        assertEquals(setOf(red, blue), art.palette.toSet())
        assertEquals(red, art.targetColorAt(0))
        assertEquals(blue, art.targetColorAt(15))
    }

    @Test
    fun transparentPixelsBecomeWhite() {
        val pixels = IntArray(32 * 32) // fully transparent black
        val art = PixelArtConverter.convert("i", "Empty", pixels, 32, 32, ConversionOptions(16, 4))
        assertEquals(listOf(rgb(0xFFFFFF)), art.palette)
    }

    @Test
    fun smallImagesAreNotUpscaled() {
        val art = PixelArtConverter.convert("i", "Tiny", gradient(20, 10), 20, 10, ConversionOptions(gridWidth = 48))
        assertEquals(20, art.width)
    }

    @Test
    fun nearestPicksClosestColor() {
        val palette = listOf(rgb(0x000000), rgb(0xFFFFFF), rgb(0xFF0000))
        assertEquals(2, ColorQuantizer.nearest(rgb(0xE01010), palette))
        assertEquals(1, ColorQuantizer.nearest(rgb(0xEEEEEE), palette))
    }

    private fun gradient(w: Int, h: Int) = IntArray(w * h) {
        val x = it % w; val y = it / w
        rgb(((x * 255 / w) shl 16) or ((y * 255 / h) shl 8) or 0x40)
    }
}
