package com.avgogames.pixelart.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/** Icons the Material set lacks, drawn on the standard 24dp grid. */
object PixelIcons {
    val Eraser: ImageVector by lazy {
        ImageVector.Builder("Eraser", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.Black), pathFillType = PathFillType.EvenOdd) {
                // Tilted eraser block with a separate tip, plus a baseline.
                moveTo(15.1f, 3.6f)
                lineTo(20.4f, 8.9f)
                curveTo(21.2f, 9.7f, 21.2f, 10.9f, 20.4f, 11.7f)
                lineTo(12.6f, 19.5f)
                lineTo(8.4f, 19.5f)
                lineTo(3.6f, 14.7f)
                curveTo(2.8f, 13.9f, 2.8f, 12.7f, 3.6f, 11.9f)
                lineTo(12.3f, 3.6f)
                curveTo(13.1f, 2.8f, 14.3f, 2.8f, 15.1f, 3.6f)
                close()
                moveTo(9.2f, 17.5f)
                lineTo(11.8f, 17.5f)
                lineTo(13.4f, 15.9f)
                lineTo(8.1f, 10.6f)
                lineTo(5.0f, 13.3f)
                close()
                moveTo(3f, 21f)
                lineTo(21f, 21f)
                lineTo(21f, 22.5f)
                lineTo(3f, 22.5f)
                close()
            }
        }.build()
    }
}
