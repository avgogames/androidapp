package com.avgogames.pixelart.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.avgogames.pixelart.PixelArtApplication
import com.avgogames.pixelart.di.AppContainer

/** Resolves the app container inside a `viewModelFactory { initializer { ... } }` block. */
fun CreationExtras.appContainer(): AppContainer =
    (checkNotNull(this[APPLICATION_KEY]) as PixelArtApplication).container
