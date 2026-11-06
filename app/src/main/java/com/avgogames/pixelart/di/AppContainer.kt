package com.avgogames.pixelart.di

import android.content.Context
import com.avgogames.pixelart.core.data.ArtRepository
import com.avgogames.pixelart.data.ImageLoader
import java.io.File

/**
 * Manual dependency injection. The graph is two objects, so a DI framework would add build
 * time and indirection without buying anything yet; swap in Hilt when the graph grows.
 */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    val repository: ArtRepository by lazy { ArtRepository(File(appContext.filesDir, "pixelart")) }

    val imageLoader: ImageLoader by lazy { ImageLoader(appContext.contentResolver) }
}
