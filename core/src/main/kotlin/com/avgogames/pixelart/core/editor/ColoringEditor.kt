package com.avgogames.pixelart.core.editor

import com.avgogames.pixelart.core.model.ColoringProgress
import com.avgogames.pixelart.core.model.PixelArt

/**
 * Pure editing rules for one artwork: which cells a tool touches and how undo/redo work.
 * Kept free of Android and coroutines so the rules are trivially unit-testable.
 */
class ColoringEditor(private val art: PixelArt) {

    /**
     * Cells (and their new painted values) a single touch at [cell] produces.
     * Correctly painted cells are never overwritten by painting tools, so a stray stroke
     * cannot undo finished work; the eraser is the explicit way to clear them.
     */
    fun changesFor(
        tool: Tool,
        cell: Int,
        color: Int,
        brushSize: Int,
        progress: ColoringProgress,
    ): Map<Int, Int> = when (tool) {
        Tool.PENCIL -> if (progress.isCorrect(art, cell)) emptyMap() else mapOf(cell to color)
        Tool.BRUSH -> square(cell, brushSize)
            .filter { art.targetAt(it) == color && !progress.isCorrect(art, it) }
            .associateWith { color }
        Tool.ERASER -> square(cell, brushSize)
            .filter { progress.paintedAt(it) != ColoringProgress.UNPAINTED }
            .associateWith { ColoringProgress.UNPAINTED }
        Tool.BUCKET -> floodRegion(cell)
            .filter { !progress.isCorrect(art, it) }
            .associateWith { color }
        Tool.PICKER, Tool.HAND -> emptyMap()
    }

    /** Cells in a [size] x [size] square centred on [cell], clipped to the artwork. */
    fun square(cell: Int, size: Int): List<Int> {
        val cx = cell % art.width
        val cy = cell / art.width
        val start = -(size - 1) / 2
        val result = ArrayList<Int>(size * size)
        for (dy in start until start + size) for (dx in start until start + size) {
            val x = cx + dx
            val y = cy + dy
            if (x in 0 until art.width && y in 0 until art.height) result += y * art.width + x
        }
        return result
    }

    /** 4-connected cells sharing [cell]'s target number. */
    fun floodRegion(cell: Int): List<Int> {
        val target = art.targetAt(cell)
        val seen = BooleanArray(art.cellCount)
        val stack = ArrayDeque<Int>().apply { add(cell) }
        val region = ArrayList<Int>()
        seen[cell] = true
        while (stack.isNotEmpty()) {
            val current = stack.removeLast()
            region += current
            val x = current % art.width
            val y = current / art.width
            fun visit(nx: Int, ny: Int) {
                if (nx !in 0 until art.width || ny !in 0 until art.height) return
                val next = ny * art.width + nx
                if (!seen[next] && art.targetAt(next) == target) {
                    seen[next] = true
                    stack.add(next)
                }
            }
            visit(x - 1, y); visit(x + 1, y); visit(x, y - 1); visit(x, y + 1)
        }
        return region
    }

    /** Cells crossed moving from [from] to [to], so fast drags leave no gaps. */
    fun cellsBetween(from: Int, to: Int): List<Int> {
        var x0 = from % art.width; var y0 = from / art.width
        val x1 = to % art.width; val y1 = to / art.width
        val dx = kotlin.math.abs(x1 - x0); val dy = -kotlin.math.abs(y1 - y0)
        val sx = if (x0 < x1) 1 else -1; val sy = if (y0 < y1) 1 else -1
        var err = dx + dy
        val cells = ArrayList<Int>()
        while (true) {
            cells += y0 * art.width + x0
            if (x0 == x1 && y0 == y1) break
            val e2 = 2 * err
            if (e2 >= dy) { err += dy; x0 += sx }
            if (e2 <= dx) { err += dx; y0 += sy }
        }
        return cells
    }
}
