package com.avgogames.pixelart.core

import com.avgogames.pixelart.core.model.Category
import com.avgogames.pixelart.core.model.ColoringProgress
import com.avgogames.pixelart.core.model.PixelArt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ColoringProgressTest {
    // 2x2: [0, 1]
    //      [1, 1]
    private val art = PixelArt("t", "Test", Category.SCENES, 2, 2, listOf(0xFF000000.toInt(), -1), intArrayOf(0, 1, 1, 1))

    @Test
    fun correctPaintCountsTowardsCompletion() {
        val progress = ColoringProgress.empty(art).with(art, mapOf(0 to 0, 1 to 1, 2 to 1))
        assertEquals(3, progress.correctCount)
        assertFalse(progress.isComplete)
        assertTrue(progress.with(art, mapOf(3 to 1)).isComplete)
    }

    @Test
    fun wrongPaintIsKeptButNotCounted() {
        val progress = ColoringProgress.empty(art).with(art, mapOf(0 to 1))
        assertEquals(0, progress.correctCount)
        assertEquals(1, progress.paintedAt(0))
        assertTrue(progress.isStarted)
    }

    @Test
    fun repaintingFixesMistakesAndErasingUndoesCredit() {
        val wrong = ColoringProgress.empty(art).with(art, mapOf(0 to 1))
        val fixed = wrong.with(art, mapOf(0 to 0))
        assertEquals(1, fixed.correctCount)
        val erased = fixed.with(art, mapOf(0 to ColoringProgress.UNPAINTED))
        assertEquals(0, erased.correctCount)
    }

    @Test
    fun noOpChangesReturnSameInstance() {
        val progress = ColoringProgress.empty(art)
        assertSame(progress, progress.with(art, mapOf(0 to ColoringProgress.UNPAINTED)))
    }

    @Test
    fun restoreDropsOutOfRangeIndices() {
        val restored = ColoringProgress.restore(art, intArrayOf(0, 7, 1, -1))
        assertEquals(ColoringProgress.UNPAINTED, restored.paintedAt(1))
        assertEquals(2, restored.correctCount)
    }

    @Test
    fun perColorCounts() {
        val progress = ColoringProgress.empty(art).with(art, mapOf(0 to 0, 1 to 1, 2 to 0))
        assertEquals(listOf(1, 1), progress.correctCountPerColor(art).toList())
    }
}
