package com.avgogames.pixelart.data

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/** Decoded ARGB pixels, ready for [com.avgogames.pixelart.core.convert.PixelArtConverter]. */
class DecodedImage(val pixels: IntArray, val width: Int, val height: Int)

/**
 * Decodes a picked image into a bounded ARGB buffer. Images are downsampled while decoding so a
 * 50 MP photo never needs to fit in memory; the converter only needs a few hundred pixels.
 */
class ImageLoader(private val resolver: ContentResolver) {

    suspend fun load(uri: Uri, maxDimension: Int = MAX_DIMENSION): DecodedImage = withContext(Dispatchers.IO) {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            decodeWithImageDecoder(uri, maxDimension)
        } else {
            decodeWithBitmapFactory(uri, maxDimension)
        }
        try {
            val pixels = IntArray(bitmap.width * bitmap.height)
            bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
            DecodedImage(pixels, bitmap.width, bitmap.height)
        } finally {
            bitmap.recycle()
        }
    }

    private fun decodeWithImageDecoder(uri: Uri, maxDimension: Int): Bitmap {
        val source = ImageDecoder.createSource(resolver, uri)
        // ImageDecoder also applies EXIF rotation, so portrait photos stay upright.
        return ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            val (w, h) = info.size.width to info.size.height
            val scale = minOf(1f, maxDimension.toFloat() / maxOf(w, h))
            decoder.setTargetSize(maxOf(1, (w * scale).toInt()), maxOf(1, (h * scale).toInt()))
        }
    }

    private fun decodeWithBitmapFactory(uri: Uri, maxDimension: Int): Bitmap {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) throw IOException("Unreadable image")
        var sample = 1
        while (maxOf(bounds.outWidth, bounds.outHeight) / (sample * 2) >= maxDimension) sample *= 2
        val options = BitmapFactory.Options().apply {
            inSampleSize = sample
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        return resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
            ?: throw IOException("Unreadable image")
    }

    private companion object {
        const val MAX_DIMENSION = 512
    }
}
