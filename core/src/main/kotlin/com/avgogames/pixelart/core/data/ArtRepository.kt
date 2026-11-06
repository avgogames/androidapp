package com.avgogames.pixelart.core.data

import com.avgogames.pixelart.core.catalog.ArtCatalog
import com.avgogames.pixelart.core.model.ArtSummary
import com.avgogames.pixelart.core.model.ColoringProgress
import com.avgogames.pixelart.core.model.PixelArt
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

/**
 * Single source of truth for artworks and painting progress.
 *
 * Built-in art comes from [ArtCatalog]; imported art and progress live as small binary files
 * under [rootDir]. Everything is loaded once into memory on first use, and every write goes to
 * memory first (so the UI updates instantly) and then to disk atomically.
 */
class ArtRepository(
    private val rootDir: File,
    private val builtIns: List<PixelArt> = ArtCatalog.all(),
    private val io: CoroutineDispatcher = Dispatchers.IO,
) {
    private val importedDir get() = File(rootDir, "imported")
    private val progressDir get() = File(rootDir, "progress")

    private val loadMutex = Mutex()
    private val writeMutex = Mutex()
    private val state = MutableStateFlow<Library?>(null)

    private data class Library(
        val arts: List<PixelArt>,
        val progress: Map<String, ColoringProgress>,
    ) {
        fun summaries() = arts.map { ArtSummary(it, progress[it.id] ?: ColoringProgress.empty(it)) }
    }

    /** Every artwork with its progress; imported art first so new imports are easy to find. */
    val summaries: Flow<List<ArtSummary>> = flow {
        ensureLoaded()
        emitAll(state.filterNotNull().map { it.summaries() })
    }

    suspend fun art(id: String): PixelArt? = ensureLoaded().arts.firstOrNull { it.id == id }

    suspend fun progress(art: PixelArt): ColoringProgress =
        ensureLoaded().progress[art.id] ?: ColoringProgress.empty(art)

    suspend fun saveProgress(progress: ColoringProgress) {
        ensureLoaded()
        state.update { it?.copy(progress = it.progress + (progress.artId to progress)) }
        writeMutex.withLock {
            withContext(io) {
                writeAtomically(File(progressDir, fileName(progress.artId, "progress"))) { ArtCodec.writeProgress(progress, it) }
            }
        }
    }

    /** Clears every painted cell of [art] and returns the blank progress. */
    suspend fun resetProgress(art: PixelArt): ColoringProgress {
        ensureLoaded()
        val empty = ColoringProgress.empty(art)
        state.update { it?.copy(progress = it.progress - art.id) }
        writeMutex.withLock {
            withContext(io) { File(progressDir, fileName(art.id, "progress")).delete() }
        }
        return empty
    }

    suspend fun saveImported(art: PixelArt) {
        ensureLoaded()
        writeMutex.withLock {
            withContext(io) { writeAtomically(File(importedDir, fileName(art.id, "art"))) { ArtCodec.writeArt(art, it) } }
        }
        state.update { it?.copy(arts = listOf(art) + it.arts.filterNot { existing -> existing.id == art.id }) }
    }

    suspend fun deleteImported(id: String) {
        ensureLoaded()
        writeMutex.withLock {
            withContext(io) {
                File(importedDir, fileName(id, "art")).delete()
                File(progressDir, fileName(id, "progress")).delete()
            }
        }
        state.update { library ->
            library?.copy(arts = library.arts.filterNot { it.id == id && it !in builtIns }, progress = library.progress - id)
        }
    }

    private suspend fun ensureLoaded(): Library {
        state.value?.let { return it }
        return loadMutex.withLock {
            state.value ?: withContext(io) { readLibrary() }.also { state.value = it }
        }
    }

    private fun readLibrary(): Library {
        val imported = importedDir.listFiles { file -> file.extension == "art" }.orEmpty()
            .sortedByDescending { it.lastModified() }
            .mapNotNull { file -> runCatching { file.inputStream().buffered().use(ArtCodec::readArt) }.getOrNull() }
        val arts = imported + builtIns
        val byId = arts.associateBy { it.id }
        val progress = progressDir.listFiles { file -> file.extension == "progress" }.orEmpty()
            .mapNotNull { file ->
                runCatching {
                    val (artId, painted) = file.inputStream().buffered().use(ArtCodec::readProgress)
                    val art = byId[artId] ?: return@runCatching null
                    ColoringProgress.restore(art, painted)
                }.getOrNull()
            }
            .associateBy { it.artId }
        return Library(arts, progress)
    }

    /** Write to a temp file then rename, so a crash mid-write never leaves a corrupt file. */
    private fun writeAtomically(target: File, write: (java.io.OutputStream) -> Unit) {
        target.parentFile?.mkdirs()
        val temp = File(target.parentFile, target.name + ".tmp")
        temp.outputStream().buffered().use(write)
        if (!temp.renameTo(target)) {
            target.delete()
            if (!temp.renameTo(target)) throw IOException("Could not save ${target.name}")
        }
    }

    private fun fileName(id: String, extension: String) =
        id.replace(Regex("[^A-Za-z0-9_-]"), "_") + "." + extension
}
