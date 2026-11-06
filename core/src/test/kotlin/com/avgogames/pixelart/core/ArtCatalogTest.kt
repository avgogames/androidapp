package com.avgogames.pixelart.core

import com.avgogames.pixelart.core.catalog.ArtCatalog
import com.avgogames.pixelart.core.model.Category
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ArtCatalogTest {
    private val arts = ArtCatalog.all()

    @Test
    fun hasOneHundredArtworks() = assertEquals(100, arts.size)

    @Test
    fun hasTwentyFivePerBundledCategory() {
        for (category in Category.entries - Category.IMPORTED) {
            assertEquals(category.name, 25, ArtCatalog.byCategory(category).size)
        }
    }

    @Test
    fun idsAndTitlesAreUnique() {
        assertEquals(arts.size, arts.map { it.id }.toSet().size)
        assertEquals(arts.size, arts.map { it.title }.toSet().size)
    }

    @Test
    fun everyArtworkIsPlayable() {
        for (art in arts) {
            assertTrue("${art.title} palette ${art.palette.size}", art.palette.size in 4..24)
            assertTrue("${art.title} has an unused color", art.cellCountPerColor.all { it > 0 })
        }
    }

    @Test
    fun artworksAreVisuallyDistinct() {
        val fingerprints = arts.map { art -> List(art.cellCount) { art.targetColorAt(it) } }
        assertEquals(arts.size, fingerprints.toSet().size)
    }

    @Test
    fun generationIsDeterministic() {
        val again = ArtCatalog.find("scenes_03")!!
        assertEquals(arts.first { it.id == "scenes_03" }, again)
    }
}
