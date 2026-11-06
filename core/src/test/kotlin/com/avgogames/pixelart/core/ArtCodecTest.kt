package com.avgogames.pixelart.core

import com.avgogames.pixelart.core.catalog.ArtCatalog
import com.avgogames.pixelart.core.data.ArtCodec
import com.avgogames.pixelart.core.model.ColoringProgress
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class ArtCodecTest {
    @Test
    fun artRoundTrips() {
        for (art in ArtCatalog.all()) {
            val bytes = ByteArrayOutputStream().also { ArtCodec.writeArt(art, it) }.toByteArray()
            assertEquals(art, ArtCodec.readArt(ByteArrayInputStream(bytes)))
        }
    }

    @Test
    fun progressRoundTripsIncludingUnpainted() {
        val art = ArtCatalog.all()[42]
        val progress = ColoringProgress.empty(art).with(art, mapOf(0 to art.targetAt(0), 5 to 0))
        val bytes = ByteArrayOutputStream().also { ArtCodec.writeProgress(progress, it) }.toByteArray()
        val (id, painted) = ArtCodec.readProgress(ByteArrayInputStream(bytes))
        assertEquals(art.id, id)
        assertArrayEquals(progress.paintedCopy(), painted)
    }
}
