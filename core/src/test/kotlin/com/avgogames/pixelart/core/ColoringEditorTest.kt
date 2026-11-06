package com.avgogames.pixelart.core

import com.avgogames.pixelart.core.editor.ColoringEditor
import com.avgogames.pixelart.core.editor.EditHistory
import com.avgogames.pixelart.core.editor.Tool
import com.avgogames.pixelart.core.model.Category
import com.avgogames.pixelart.core.model.ColoringProgress
import com.avgogames.pixelart.core.model.PixelArt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ColoringEditorTest {
    // 4x3:
    // 0 0 1 1
    // 0 1 1 0
    // 0 0 0 0
    private val art = PixelArt(
        "e", "Editor", Category.SCENES, 4, 3, listOf(0xFF000000.toInt(), -1),
        intArrayOf(0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0),
    )
    private val editor = ColoringEditor(art)
    private val empty = ColoringProgress.empty(art)

    @Test
    fun pencilPaintsAnyColorButSparesCorrectCells() {
        assertEquals(mapOf(0 to 1), editor.changesFor(Tool.PENCIL, 0, 1, 1, empty))
        val done = empty.with(art, mapOf(0 to 0))
        assertTrue(editor.changesFor(Tool.PENCIL, 0, 1, 1, done).isEmpty())
    }

    @Test
    fun brushOnlyPaintsMatchingNumbers() {
        val changes = editor.changesFor(Tool.BRUSH, 5, 1, 3, empty)
        assertEquals(setOf(2, 5, 6), changes.keys)
        assertTrue(changes.values.all { it == 1 })
    }

    @Test
    fun bucketFillsConnectedRegionOnly() {
        // Cell 7 joins the main region through the bottom row.
        assertEquals(setOf(0, 1, 4, 8, 9, 10, 11, 7), editor.changesFor(Tool.BUCKET, 0, 0, 1, empty).keys)
        assertEquals(setOf(2, 3, 5, 6), editor.floodRegion(6).toSet())
    }

    @Test
    fun eraserClearsOnlyPaintedCells() {
        val painted = empty.with(art, mapOf(0 to 0, 1 to 1))
        assertEquals(mapOf(0 to -1, 1 to -1), editor.changesFor(Tool.ERASER, 0, 0, 2, painted))
    }

    @Test
    fun squareIsClippedAtEdges() {
        assertEquals(listOf(0, 1, 4, 5), editor.square(0, 3).sorted())
        assertEquals(listOf(5), editor.square(5, 1))
    }

    @Test
    fun cellsBetweenHasNoGaps() {
        assertEquals(listOf(0, 1, 2, 3), editor.cellsBetween(0, 3))
        assertEquals(3, editor.cellsBetween(0, 10).size)
    }

    @Test
    fun historyUndoesAndRedoes() {
        val history = EditHistory(limit = 2)
        history.record(EditHistory.Edit(mapOf(0 to -1), mapOf(0 to 0)))
        history.record(EditHistory.Edit(mapOf(1 to -1), mapOf(1 to 0)))
        history.record(EditHistory.Edit(mapOf(2 to -1), mapOf(2 to 1)))
        assertEquals(mapOf(2 to -1), history.undo())
        assertEquals(mapOf(2 to 1), history.redo())
        history.undo(); history.undo()
        assertFalse(history.canUndo) // oldest edit fell off the bounded stack
        assertNull(history.undo())
    }
}
