package com.avgogames.pixelart.core.catalog

import com.avgogames.pixelart.core.model.Category
import com.avgogames.pixelart.core.model.PixelArt

/**
 * The 100 bundled artworks (25 per category). Every piece is original and authored in code
 * with [com.avgogames.pixelart.core.raster.Raster], so the APK ships no image assets and the
 * catalog is deterministic: the same id always produces the same cells, which keeps saved
 * progress valid across app updates.
 */
object ArtCatalog {
    const val SIZE = 32

    private val cached: List<PixelArt> by lazy {
        AnimeArt.create() + CarArt.create() + AnimalArt.create() + SceneArt.create()
    }

    fun all(): List<PixelArt> = cached

    fun byCategory(category: Category): List<PixelArt> = cached.filter { it.category == category }

    fun find(id: String): PixelArt? = cached.firstOrNull { it.id == id }
}

/** Builds one category's artworks, numbering ids like `scenes_07` in declaration order. */
internal class CatalogBuilder(private val category: Category, private val prefix: String) {
    private val arts = mutableListOf<PixelArt>()

    fun art(title: String, draw: (com.avgogames.pixelart.core.raster.Raster) -> Unit) {
        val raster = com.avgogames.pixelart.core.raster.Raster(ArtCatalog.SIZE, ArtCatalog.SIZE)
        draw(raster)
        val id = "%s_%02d".format(prefix, arts.size + 1)
        arts += raster.toPixelArt(id, title, category)
    }

    fun build(): List<PixelArt> = arts.toList()
}
