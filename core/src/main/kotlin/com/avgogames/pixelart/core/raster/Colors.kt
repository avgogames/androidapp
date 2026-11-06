package com.avgogames.pixelart.core.raster

import kotlin.math.roundToInt

/** Opaque ARGB from 0xRRGGBB. */
fun rgb(hex: Int): Int = (0xFF shl 24) or (hex and 0xFFFFFF)

fun red(color: Int): Int = (color shr 16) and 0xFF
fun green(color: Int): Int = (color shr 8) and 0xFF
fun blue(color: Int): Int = color and 0xFF

fun argb(r: Int, g: Int, b: Int): Int =
    (0xFF shl 24) or (r.coerceIn(0, 255) shl 16) or (g.coerceIn(0, 255) shl 8) or b.coerceIn(0, 255)

/** Relative luminance in 0..1 (Rec. 709 weights on gamma-encoded channels; good enough for sorting). */
fun luminance(color: Int): Float = (0.2126f * red(color) + 0.7152f * green(color) + 0.0722f * blue(color)) / 255f

/** Multiplies channels by [factor]; < 1 darkens, > 1 lightens. */
fun shade(color: Int, factor: Float): Int =
    argb((red(color) * factor).roundToInt(), (green(color) * factor).roundToInt(), (blue(color) * factor).roundToInt())

/** Linear blend from [a] (t = 0) to [b] (t = 1). */
fun mix(a: Int, b: Int, t: Float): Int = argb(
    (red(a) + (red(b) - red(a)) * t).roundToInt(),
    (green(a) + (green(b) - green(a)) * t).roundToInt(),
    (blue(a) + (blue(b) - blue(a)) * t).roundToInt(),
)
