package com.avgogames.pixelart.core.model

/**
 * An immutable color-by-number artwork.
 *
 * Every cell holds an index into [palette]; the player paints a cell correctly by
 * applying the palette color with the same index. Palette colors are packed ARGB ints.
 */
class PixelArt(
    val id: String,
    val title: String,
    val category: Category,
    val width: Int,
    val height: Int,
    palette: List<Int>,
    cells: IntArray,
) {
    val palette: List<Int> = palette.toList()
    private val cells: IntArray = cells.copyOf()

    /** Number of cells that use each palette color, indexed like [palette]. */
    val cellCountPerColor: IntArray

    init {
        require(width > 0 && height > 0) { "Size must be positive, was ${width}x$height" }
        require(cells.size == width * height) { "Expected ${width * height} cells, got ${cells.size}" }
        require(palette.isNotEmpty() && palette.size <= MAX_COLORS) {
            "Palette must have 1..$MAX_COLORS colors, had ${palette.size}"
        }
        val counts = IntArray(palette.size)
        for (index in cells) {
            require(index in palette.indices) { "Cell color index $index outside palette" }
            counts[index]++
        }
        cellCountPerColor = counts
    }

    val cellCount: Int get() = width * height

    /** Palette index the cell at [cell] (row-major) should be painted with. */
    fun targetAt(cell: Int): Int = cells[cell]

    fun targetAt(x: Int, y: Int): Int = cells[y * width + x]

    fun targetColorAt(cell: Int): Int = palette[cells[cell]]

    /** Defensive copy of the row-major cell indices, for encoding. */
    fun cellsCopy(): IntArray = cells.copyOf()

    override fun equals(other: Any?): Boolean =
        other is PixelArt && other.id == id && other.width == width && other.height == height &&
            other.palette == palette && other.cells.contentEquals(cells) &&
            other.title == title && other.category == category

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "PixelArt($id, '$title', $category, ${width}x$height, ${palette.size} colors)"

    companion object {
        /** Upper bound keeps the palette bar usable and lets progress be stored in one byte per cell. */
        const val MAX_COLORS = 48
    }
}
