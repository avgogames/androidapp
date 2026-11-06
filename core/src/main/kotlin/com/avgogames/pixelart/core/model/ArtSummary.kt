package com.avgogames.pixelart.core.model

/** What the gallery shows for one artwork: the art itself plus the saved painting state. */
data class ArtSummary(
    val art: PixelArt,
    val progress: ColoringProgress,
)
