package com.avgogames.pixelart.core

import com.avgogames.pixelart.core.catalog.ArtCatalog
import com.avgogames.pixelart.core.convert.PixelArtConverter
import com.avgogames.pixelart.core.data.ArtRepository
import com.avgogames.pixelart.core.raster.rgb
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ArtRepositoryTest {
    @get:Rule val folder = TemporaryFolder()

    private fun repository(scheduler: kotlinx.coroutines.test.TestCoroutineScheduler) =
        ArtRepository(folder.root, ArtCatalog.all().take(3), StandardTestDispatcher(scheduler))

    @Test
    fun progressSurvivesAReload() = runTest {
        val art = ArtCatalog.all().first()
        val first = repository(testScheduler)
        val painted = first.progress(art).with(art, mapOf(0 to art.targetAt(0), 1 to art.targetAt(1)))
        first.saveProgress(painted)

        val second = repository(testScheduler)
        assertEquals(2, second.progress(art).correctCount)
        assertEquals(painted, second.progress(art))
    }

    @Test
    fun resetClearsSavedProgress() = runTest {
        val art = ArtCatalog.all().first()
        val repo = repository(testScheduler)
        repo.saveProgress(repo.progress(art).with(art, mapOf(0 to art.targetAt(0))))
        repo.resetProgress(art)

        assertFalse(repo.progress(art).isStarted)
        assertFalse(repository(testScheduler).progress(art).isStarted)
    }

    @Test
    fun importedArtIsListedFirstAndPersisted() = runTest {
        val pixels = IntArray(64 * 64) { if (it % 2 == 0) rgb(0x336699) else rgb(0xFFCC00) }
        val imported = PixelArtConverter.convert("import_1", "Holiday", pixels, 64, 64)
        repository(testScheduler).saveImported(imported)

        val reloaded = repository(testScheduler)
        val summaries = reloaded.summaries.first()
        assertEquals("import_1", summaries.first().art.id)
        assertEquals(imported, reloaded.art("import_1"))
        assertEquals(4, summaries.size)

        reloaded.deleteImported("import_1")
        assertNull(reloaded.art("import_1"))
        assertTrue(reloaded.summaries.first().none { it.art.id == "import_1" })
    }
}
