package com.avgogames.pixelart.core.model

/**
 * What the player has painted on one artwork. Immutable: every edit returns a new instance,
 * which keeps undo/redo and UI state snapshots trivial.
 *
 * [painted] holds, per cell, the palette index the player applied or [UNPAINTED].
 * A painted index that differs from the artwork's target is a visible mistake.
 */
class ColoringProgress private constructor(
    val artId: String,
    private val painted: IntArray,
    /** Count of cells whose painted index matches the target. */
    val correctCount: Int,
    val totalCount: Int,
) {
    fun paintedAt(cell: Int): Int = painted[cell]

    fun isCorrect(art: PixelArt, cell: Int): Boolean = painted[cell] == art.targetAt(cell)

    val fraction: Float get() = if (totalCount == 0) 0f else correctCount.toFloat() / totalCount

    val isComplete: Boolean get() = totalCount > 0 && correctCount == totalCount

    val isStarted: Boolean get() = painted.any { it != UNPAINTED }

    /**
     * Applies [changes] (cell -> new painted index or [UNPAINTED]) and returns the new progress.
     * Returns `this` when nothing changes so callers can cheaply skip work.
     */
    fun with(art: PixelArt, changes: Map<Int, Int>): ColoringProgress {
        if (changes.isEmpty()) return this
        var copy: IntArray? = null
        var correct = correctCount
        for ((cell, value) in changes) {
            val old = painted[cell]
            if (old == value) continue
            val target = art.targetAt(cell)
            if (old == target) correct--
            if (value == target) correct++
            val array = copy ?: painted.copyOf().also { copy = it }
            array[cell] = value
        }
        val newArray = copy ?: return this
        return ColoringProgress(artId, newArray, correct, totalCount)
    }

    /** Count of correctly painted cells per palette index. */
    fun correctCountPerColor(art: PixelArt): IntArray {
        val counts = IntArray(art.palette.size)
        for (cell in painted.indices) {
            val target = art.targetAt(cell)
            if (painted[cell] == target) counts[target]++
        }
        return counts
    }

    fun paintedCopy(): IntArray = painted.copyOf()

    override fun equals(other: Any?): Boolean =
        other is ColoringProgress && other.artId == artId && other.painted.contentEquals(painted)

    override fun hashCode(): Int = 31 * artId.hashCode() + painted.contentHashCode()

    companion object {
        const val UNPAINTED = -1

        fun empty(art: PixelArt): ColoringProgress =
            ColoringProgress(art.id, IntArray(art.cellCount) { UNPAINTED }, 0, art.cellCount)

        /** Rebuilds progress from stored cells, dropping any index the palette no longer has. */
        fun restore(art: PixelArt, stored: IntArray): ColoringProgress {
            if (stored.size != art.cellCount) return empty(art)
            val painted = IntArray(stored.size) { cell ->
                val value = stored[cell]
                if (value in art.palette.indices) value else UNPAINTED
            }
            var correct = 0
            for (cell in painted.indices) if (painted[cell] == art.targetAt(cell)) correct++
            return ColoringProgress(art.id, painted, correct, art.cellCount)
        }
    }
}
