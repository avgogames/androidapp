package com.avgogames.pixelart.core.data

import com.avgogames.pixelart.core.model.Category
import com.avgogames.pixelart.core.model.ColoringProgress
import com.avgogames.pixelart.core.model.PixelArt
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * Compact versioned binary formats for imported artworks and painting progress.
 * One byte per cell is enough because palettes are capped at [PixelArt.MAX_COLORS].
 */
object ArtCodec {
    private const val ART_MAGIC = 0x50584131 // "PXA1"
    private const val PROGRESS_MAGIC = 0x50585031 // "PXP1"

    fun writeArt(art: PixelArt, out: OutputStream) {
        DataOutputStream(out).run {
            writeInt(ART_MAGIC)
            writeUTF(art.id)
            writeUTF(art.title)
            writeUTF(art.category.name)
            writeShort(art.width)
            writeShort(art.height)
            writeByte(art.palette.size)
            art.palette.forEach(::writeInt)
            write(ByteArray(art.cellCount) { art.targetAt(it).toByte() })
            flush()
        }
    }

    fun readArt(input: InputStream): PixelArt {
        val data = DataInputStream(input)
        if (data.readInt() != ART_MAGIC) throw IOException("Not an artwork file")
        val id = data.readUTF()
        val title = data.readUTF()
        val categoryName = data.readUTF()
        val category = Category.entries.firstOrNull { it.name == categoryName } ?: Category.IMPORTED
        val width = data.readUnsignedShort()
        val height = data.readUnsignedShort()
        val palette = List(data.readUnsignedByte()) { data.readInt() }
        val bytes = ByteArray(width * height).also(data::readFully)
        return PixelArt(id, title, category, width, height, palette, IntArray(bytes.size) { bytes[it].toInt() and 0xFF })
    }

    fun writeProgress(progress: ColoringProgress, out: OutputStream) {
        DataOutputStream(out).run {
            writeInt(PROGRESS_MAGIC)
            writeUTF(progress.artId)
            val painted = progress.paintedCopy()
            writeInt(painted.size)
            // Shift by one so UNPAINTED (-1) is stored as 0.
            write(ByteArray(painted.size) { (painted[it] + 1).toByte() })
            flush()
        }
    }

    /** Returns the stored artwork id and raw painted indices; validate with [ColoringProgress.restore]. */
    fun readProgress(input: InputStream): Pair<String, IntArray> {
        val data = DataInputStream(input)
        if (data.readInt() != PROGRESS_MAGIC) throw IOException("Not a progress file")
        val artId = data.readUTF()
        val bytes = ByteArray(data.readInt()).also(data::readFully)
        return artId to IntArray(bytes.size) { (bytes[it].toInt() and 0xFF) - 1 }
    }
}
