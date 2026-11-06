package com.avgogames.pixelart

import android.app.Application
import com.avgogames.pixelart.di.AppContainer

/** Owns the app-wide dependency graph; screens reach it through their ViewModel factories. */
class PixelArtApplication : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}
