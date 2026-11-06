package com.avgogames.pixelart.core.catalog

import com.avgogames.pixelart.core.model.Category
import com.avgogames.pixelart.core.model.PixelArt
import com.avgogames.pixelart.core.raster.Raster
import com.avgogames.pixelart.core.raster.mix
import com.avgogames.pixelart.core.raster.rgb
import com.avgogames.pixelart.core.raster.shade

/** Front-facing animal portraits assembled from symmetric face parts. */
internal object AnimalArt {
    private val INK = rgb(0x2B2233)
    private val WHITE = rgb(0xFFFFFF)
    private val PINK = rgb(0xF7A1B5)

    fun create(): List<PixelArt> = CatalogBuilder(Category.ANIMALS, "animals").apply {
        art("Ginger Cat") { s ->
            Face(s).run {
                backdrop(rgb(0xBDE7F2), rgb(0xA6DCEB))
                pointedEars(rgb(0xF4A04B), PINK)
                head(rgb(0xF4A04B))
                sym { rect(5, 15, 7, 15, rgb(0xD9792A)); rect(5, 18, 7, 18, rgb(0xD9792A)) }
                s.rect(14, 8, 17, 8, rgb(0xD9792A)); s.rect(15, 10, 16, 10, rgb(0xD9792A))
                muzzle(rgb(0xFFF1DE), 16, 22, 4.5f, 3f)
                eyes(rgb(0x3FA34D)); nose(PINK, 20); catMouth(22)
                whiskers(rgb(0xFFFFFF))
            }
        }
        art("Happy Puppy") { s ->
            Face(s).run {
                backdrop(rgb(0xFFE8A3), rgb(0xFFDD80))
                head(rgb(0xE8C9A0))
                s.ellipse(20.5f, 14f, 4f, 4f, rgb(0x9C6B44))
                floppyEars(rgb(0x9C6B44))
                muzzle(rgb(0xFFF5E6), 16, 22, 5f, 3.5f)
                eyes(INK); nose(INK, 19, wide = true)
                s.rect(15, 23, 16, 25, rgb(0xFF7A8A))
                blush(rgb(0xF5A9A0))
            }
        }
        art("Brown Bear") { s ->
            Face(s).run {
                backdrop(rgb(0xC9E8B8), rgb(0xB3DE9E))
                roundEars(rgb(0x8B5A3C), rgb(0xD9A27A))
                head(rgb(0x8B5A3C))
                muzzle(rgb(0xD9A27A), 16, 22, 5f, 3.5f)
                eyes(INK); nose(INK, 19, wide = true); smile(23)
            }
        }
        art("Snow Bunny") { s ->
            Face(s).run {
                backdrop(rgb(0xD6C8FF), rgb(0xC6B4FF))
                longEars(rgb(0xFFFFFF), PINK)
                head(rgb(0xFFFFFF), rx = 9f, ry = 8.5f, cy = 19f)
                eyes(rgb(0x7A3B5E), y = 18); nose(PINK, 21); catMouth(23)
                blush(rgb(0xFFC2D1), y = 22)
            }
        }
        art("Clever Fox") { s ->
            Face(s).run {
                backdrop(rgb(0xD8F0D2), rgb(0xC5E8BC))
                pointedEars(rgb(0xF07A2E), rgb(0x3A2A2A), tall = true)
                head(rgb(0xF07A2E))
                sym { polygon(WHITE, 6 to 17, 16 to 21, 16 to 27, 11 to 26) }
                eyes(INK); nose(INK, 24)
            }
        }
        art("Bamboo Panda") { s ->
            Face(s).run {
                backdrop(rgb(0xB8E6C9), rgb(0x9FDDB6))
                roundEars(INK, INK)
                head(WHITE)
                sym { ellipse(11.5f, 17f, 2.8f, 3.4f, INK) }
                eyes(INK, highlightOnly = true)
                nose(INK, 21); smile(23)
                s.rect(26, 22, 27, 31, rgb(0x6CBF4A)); s.rect(26, 25, 27, 25, rgb(0x4E9A35))
            }
        }
        art("Pink Piglet") { s ->
            Face(s).run {
                backdrop(rgb(0xFFF2B3), rgb(0xFFEA8F))
                sym { polygon(rgb(0xF59FB4), 6 to 13, 6 to 5, 12 to 9) }
                head(rgb(0xFFB8C8))
                s.ellipse(16, 21, 4.5f, 3f, rgb(0xF58FA8))
                s.rect(14, 20, 14, 21, rgb(0xC05A78)); s.rect(17, 20, 17, 21, rgb(0xC05A78))
                eyes(INK, y = 15); blush(rgb(0xF58FA8), y = 19)
            }
        }
        art("Little Mouse") { s ->
            Face(s).run {
                backdrop(rgb(0xFFE0C2), rgb(0xFFD3A8))
                sym { circle(7f, 9f, 5.5f, rgb(0xA9A9B8)); circle(7f, 9f, 3.5f, PINK) }
                head(rgb(0xA9A9B8), rx = 8.5f, ry = 8f, cy = 19f)
                eyes(INK, y = 17); nose(PINK, 22)
                whiskers(rgb(0x5A5A6A), y = 22)
            }
        }
        art("Night Owl") { s ->
            Face(s).run {
                backdrop(rgb(0x1E2A52), rgb(0x26356A))
                s.circle(26, 5, 2.5f, rgb(0xFDF6D8))
                head(rgb(0x8A6A4E), rx = 11f, ry = 11f, cy = 18f)
                sym { polygon(rgb(0x8A6A4E), 5 to 11, 6 to 3, 11 to 8) }
                s.ellipse(16, 24, 6f, 5f, rgb(0xC9A882))
                for (x in 12..19 step 3) s.hline(x, x + 1, 25, rgb(0xA8875F))
                sym { circle(11.5f, 15f, 4f, rgb(0xF5E6C8)); circle(11.5f, 15f, 2.2f, rgb(0xF2B630)) }
                sym { rect(11, 14, 12, 15, INK); set(11, 14, WHITE) }
                s.polygon(rgb(0xF29A2E), 14.5f to 18, 17.5f to 18, 16 to 21)
                s.rect(4, 29, 27, 31, rgb(0x5A3D24))
            }
        }
        art("Lily Frog") { s ->
            Face(s).run {
                backdrop(rgb(0x9ED8F0), rgb(0x86CDEA))
                s.ellipse(16, 21, 12f, 8f, rgb(0x5DBB46))
                sym { circle(9.5f, 12f, 4.5f, rgb(0x5DBB46)); circle(9.5f, 12f, 3f, WHITE); rect(9, 11, 10, 13, INK) }
                s.ellipse(16, 24, 8f, 4f, rgb(0xB5E39A))
                s.hline(11, 20, 21, rgb(0x2F6B2A)); s[10, 20] = rgb(0x2F6B2A); s[21, 20] = rgb(0x2F6B2A)
                blush(rgb(0xF2A0A0), y = 19)
                s.rect(0, 29, 31, 31, rgb(0x3D9A6E))
            }
        }
        art("Sleepy Koala") { s ->
            Face(s).run {
                backdrop(rgb(0xD9F2E6), rgb(0xC6EBD8))
                sym { circle(6.5f, 11f, 5.5f, rgb(0x9AA3AE)); circle(6.5f, 11f, 3.2f, rgb(0xE8EAEE)) }
                head(rgb(0x9AA3AE))
                s.ellipse(16, 20, 3f, 4f, rgb(0x3A3A48))
                sym { hline(10, 13, 16, INK) }
                blush(rgb(0xF2B8C6), y = 20)
            }
        }
        art("Tiger Cub") { s ->
            Face(s).run {
                backdrop(rgb(0xC8EFC0), rgb(0xB0E6A6))
                roundEars(rgb(0xF5A23C), WHITE, small = true)
                head(rgb(0xF5A23C))
                sym {
                    rect(6, 14, 8, 14, INK); rect(6, 17, 8, 17, INK)
                    rect(13, 8, 13, 10, INK)
                }
                s.rect(15, 8, 16, 11, INK)
                muzzle(WHITE, 16, 22, 5.5f, 3.5f)
                eyes(rgb(0x6BA33A)); nose(PINK, 20); catMouth(22)
            }
        }
        art("Lion King") { s ->
            Face(s).run {
                backdrop(rgb(0xFFE9A8), rgb(0xFFDE85))
                s.circle(16, 17, 14.5f, rgb(0xB5651D))
                for (a in 0 until 12) {
                    val angle = a * Math.PI / 6
                    s.circle(16 + 13 * kotlin.math.cos(angle).toFloat(), 17 + 13 * kotlin.math.sin(angle).toFloat(), 2.5f, rgb(0x9A4F12))
                }
                roundEars(rgb(0xF2B84B), rgb(0xD9932A), small = true)
                head(rgb(0xF2B84B), rx = 9f, ry = 9f)
                muzzle(rgb(0xFFE3A8), 16, 22, 4.5f, 3f)
                eyes(INK); nose(rgb(0x6B3A1E), 20); catMouth(22)
            }
        }
        art("Bandit Raccoon") { s ->
            Face(s).run {
                backdrop(rgb(0xC6DDF5), rgb(0xB2D0F0))
                pointedEars(rgb(0x7D7F8C), rgb(0x3A3A48))
                head(rgb(0x9A9CA8))
                s.polygon(rgb(0x3A3A48), 6 to 14, 26 to 14, 25 to 19, 17 to 18, 15 to 18, 7 to 19)
                muzzle(WHITE, 16, 22, 4.5f, 3f)
                sym { polygon(WHITE, 8 to 11, 14 to 12, 13 to 13, 8 to 13) }
                eyes(WHITE, highlightOnly = true, y = 16); nose(INK, 20); smile(23)
            }
        }
        art("Penguin Pal") { s ->
            Face(s).run {
                backdrop(rgb(0xD4F1FF), rgb(0xBCE8FF))
                repeat(10) { s[(it * 7 + 3) % 32, (it * 5 + 2) % 12] = WHITE }
                head(rgb(0x2D3447), rx = 11f, ry = 11f, cy = 18f)
                s.polygon(WHITE, 16 to 9, 25 to 14, 26 to 22, 22 to 28, 10 to 28, 6 to 22, 7 to 14)
                s.polygon(WHITE, 16 to 12, 16 to 9, 13 to 11)
                eyes(INK, y = 16)
                s.polygon(rgb(0xF5A623), 14 to 19, 18 to 19, 16 to 22)
                blush(rgb(0xF7B7C5), y = 20)
                s.rect(0, 29, 31, 31, WHITE)
            }
        }
        art("Spring Chick") { s ->
            Face(s).run {
                backdrop(rgb(0xBEEBC5), rgb(0xA8E3B2))
                s.ellipse(16, 18, 11f, 11f, rgb(0xFFD93B))
                s.polygon(rgb(0xFFD93B), 14 to 8, 15 to 4, 16.5f to 7, 18 to 3, 18.5f to 8)
                sym { ellipse(5f, 21f, 3f, 4f, rgb(0xF5C21B)) }
                eyes(INK, y = 16)
                s.polygon(rgb(0xF57F17), 14 to 19, 18 to 19, 16 to 22)
                blush(rgb(0xFFA8A8), y = 20)
                s.rect(0, 28, 31, 31, rgb(0x7CC85A))
            }
        }
        art("Dairy Cow") { s ->
            Face(s).run {
                backdrop(rgb(0xBFE6FF), rgb(0xA6DBFF))
                sym { polygon(rgb(0xF2E2C4), 8 to 8, 6 to 3, 11 to 7); ellipse(4f, 13f, 3.5f, 2f, WHITE) }
                head(WHITE, rx = 9.5f, ry = 10f)
                s.ellipse(11, 12, 3.5f, 3f, INK); s.ellipse(22, 16, 2.5f, 3f, INK)
                s.ellipse(16, 23, 7f, 4f, rgb(0xF7B6C2))
                s.rect(13, 23, 13, 24, rgb(0xC0607A)); s.rect(18, 23, 18, 24, rgb(0xC0607A))
                eyes(INK, y = 16)
            }
        }
        art("Cheeky Monkey") { s ->
            Face(s).run {
                backdrop(rgb(0xD8F5C0), rgb(0xC5EEA6))
                sym { circle(5f, 17f, 4f, rgb(0x8A5A3B)); circle(5f, 17f, 2.5f, rgb(0xF2C9A0)) }
                head(rgb(0x8A5A3B), rx = 10f, ry = 10f)
                s.ellipse(12.5f, 15f, 3.5f, 4f, rgb(0xF2C9A0)); s.ellipse(19.5f, 15f, 3.5f, 4f, rgb(0xF2C9A0))
                s.ellipse(16, 21.5f, 6f, 4f, rgb(0xF2C9A0))
                eyes(INK, y = 15)
                s[15, 20] = INK; s[16, 20] = INK
                s.hline(13, 18, 23, rgb(0x6B3A1E)); s[12, 22] = rgb(0x6B3A1E); s[19, 22] = rgb(0x6B3A1E)
            }
        }
        art("Chubby Hamster") { s ->
            Face(s).run {
                backdrop(rgb(0xFFE3EC), rgb(0xFFD4E1))
                roundEars(rgb(0xE8A86A), PINK, small = true)
                s.ellipse(16, 19, 12f, 10f, rgb(0xE8A86A))
                s.ellipse(16, 22, 9f, 6f, WHITE)
                s.polygon(WHITE, 15 to 9, 17 to 9, 18 to 16, 14 to 16)
                eyes(INK, y = 16); nose(PINK, 20); catMouth(21)
                blush(rgb(0xF7A1B5), y = 20)
            }
        }
        art("Moon Wolf") { s ->
            Face(s).run {
                backdrop(rgb(0x2C2F5B), rgb(0x3A3E73))
                s.circle(25, 6, 3.5f, rgb(0xFDF6D8))
                pointedEars(rgb(0x7E8AA3), rgb(0x4A5068), tall = true)
                head(rgb(0x7E8AA3))
                s.polygon(rgb(0xDDE3EE), 16 to 12, 22 to 19, 21 to 27, 11 to 27, 10 to 19)
                eyes(rgb(0x5BC0EB)); nose(INK, 22)
            }
        }
        art("Fluffy Sheep") { s ->
            Face(s).run {
                backdrop(rgb(0xB9E4A6), rgb(0xA4DA8E))
                for ((x, y) in listOf(8 to 10, 13 to 7, 19 to 7, 24 to 10, 6 to 16, 26 to 16, 9 to 22, 23 to 22, 16 to 8)) {
                    s.circle(x.toFloat(), y.toFloat(), 4.5f, rgb(0xF7F3EA))
                }
                sym { ellipse(7f, 17f, 3.5f, 2f, rgb(0x4A4048)) }
                s.ellipse(16, 19, 6.5f, 8f, rgb(0x4A4048))
                s.circle(16, 11, 4f, rgb(0xF7F3EA))
                eyes(WHITE, highlightOnly = true, y = 17, spread = 3)
                s.rect(15, 23, 16, 23, PINK)
            }
        }
        art("Forest Deer") { s ->
            Face(s).run {
                backdrop(rgb(0xE7F5D6), rgb(0xD7EDBF))
                sym {
                    line(10, 9, 6, 2, rgb(0x7A5230), 1); line(8, 6, 4, 5, rgb(0x7A5230), 1); line(9, 4, 11, 1, rgb(0x7A5230), 1)
                    ellipse(5f, 13f, 3.5f, 2f, rgb(0xB57A4A))
                }
                head(rgb(0xC98B55), rx = 8f, ry = 10f)
                s.ellipse(16, 23, 5f, 4f, rgb(0xF2DEC4))
                sym { set(10, 11, WHITE); set(9, 13, WHITE) }
                eyes(INK, y = 16, spread = 4); nose(INK, 22)
            }
        }
        art("Polar Bear") { s ->
            Face(s).run {
                backdrop(rgb(0x8FD3FF), rgb(0x76C8FF))
                repeat(12) { s[(it * 11 + 5) % 32, (it * 7 + 1) % 26] = WHITE }
                roundEars(rgb(0xF4F6FA), rgb(0xD9DEE8))
                head(rgb(0xF4F6FA))
                muzzle(rgb(0xE3E8F0), 16, 22, 5f, 3.5f)
                eyes(INK); nose(INK, 19, wide = true); smile(23)
                s.rect(0, 29, 31, 31, rgb(0xDDEFFF))
            }
        }
        art("Red Panda") { s ->
            Face(s).run {
                backdrop(rgb(0xF5E6C8), rgb(0xEFD9AE))
                pointedEars(rgb(0xC4502A), WHITE)
                head(rgb(0xC4502A))
                sym { ellipse(10f, 13f, 3f, 2f, WHITE); polygon(rgb(0x7A2E16), 9 to 16, 13 to 18, 12 to 22, 8 to 20) }
                muzzle(WHITE, 16, 22, 5f, 3.5f)
                eyes(INK); nose(INK, 20); catMouth(22)
            }
        }
        art("River Otter") { s ->
            Face(s).run {
                backdrop(rgb(0x7FC8E8), rgb(0x66BDE3))
                sym { circle(7f, 11f, 2.5f, rgb(0x7A5235)) }
                head(rgb(0x9B6B47), rx = 10f, ry = 9f)
                s.ellipse(16, 21, 7f, 5f, rgb(0xE8CBA6))
                eyes(INK, y = 15, spread = 5); nose(INK, 19, wide = true); catMouth(21)
                whiskers(rgb(0xFFFFFF), y = 21)
                for (x in 0..31) s.vline(x, 28 + (x / 3) % 2, 31, rgb(0x4FA8D6))
            }
        }
    }.build()

    /** Face part helpers. Everything is centred on x = 16; [sym] mirrors a drawing across it. */
    private class Face(val s: Raster) {
        private val mirror = MirrorRaster(s)

        fun sym(draw: MirrorRaster.() -> Unit) {
            mirror.flip = false; mirror.draw()
            mirror.flip = true; mirror.draw()
        }

        fun backdrop(color: Int, dots: Int) {
            s.fill(color)
            for (y in 1 until 32 step 6) for (x in (y / 6 % 2) * 3 + 1 until 32 step 6) s[x, y] = dots
        }

        fun head(color: Int, rx: Float = 10f, ry: Float = 9f, cy: Float = 18f) {
            s.ellipse(16, 30.5f, 9f, 4f, shade(color, 0.85f))
            s.ellipse(16, cy, rx, ry, color)
        }

        fun pointedEars(color: Int, inner: Int, tall: Boolean = false) {
            val top = if (tall) 1 else 4
            sym {
                polygon(color, 6 to 14, 8 to top, 15 to 10)
                polygon(inner, 8 to 12, 9 to top + 4, 12.5f to 10)
            }
        }

        fun roundEars(color: Int, inner: Int, small: Boolean = false) {
            val r = if (small) 3f else 4.5f
            sym {
                circle(8.5f, 10f, r, color)
                circle(8.5f, 10f, r * 0.5f, inner)
            }
        }

        fun longEars(color: Int, inner: Int) = sym {
            ellipse(11.5f, 6f, 2.8f, 7f, color)
            ellipse(11.5f, 6.5f, 1.2f, 5f, inner)
        }

        fun floppyEars(color: Int) = sym {
            ellipse(6.5f, 16f, 3.5f, 7f, color)
        }

        fun muzzle(color: Int, cx: Int, cy: Int, rx: Float, ry: Float) =
            s.ellipse(cx, cy, rx, ry, color)

        /** Two 2x2 eyes with a highlight. [highlightOnly] draws a white sparkle on dark patches. */
        fun eyes(color: Int, y: Int = 16, spread: Int = 5, highlightOnly: Boolean = false) {
            val leftX = 16 - spread - 1
            sym {
                if (!highlightOnly) rect(leftX, y, leftX + 1, y + 1, color)
                set(leftX, y, if (highlightOnly) color else WHITE)
            }
        }

        fun nose(color: Int, y: Int, wide: Boolean = false) {
            if (wide) {
                s.rect(14, y, 17, y + 1, color); s.rect(15, y + 2, 16, y + 2, color)
            } else {
                s.rect(15, y, 16, y, color); s.rect(15, y + 1, 16, y + 1, mix(color, INK, 0.3f))
            }
        }

        fun catMouth(y: Int) {
            s[15, y] = INK; s[16, y] = INK; s[14, y + 1] = INK; s[17, y + 1] = INK
        }

        fun smile(y: Int) {
            s.hline(14, 17, y, INK); s[13, y - 1] = INK; s[18, y - 1] = INK
        }

        fun blush(color: Int, y: Int = 20) = sym { rect(8, y, 9, y, color) }

        fun whiskers(color: Int, y: Int = 21) = sym {
            hline(2, 5, y - 1, color); hline(2, 5, y + 1, color)
        }
    }

    /** Draws onto [target], optionally reflected around the vertical centre line. */
    private class MirrorRaster(private val target: Raster) {
        var flip = false
        private fun mx(x: Int) = if (flip) target.width - 1 - x else x
        private fun mf(x: Float) = if (flip) target.width - x else x

        fun set(x: Int, y: Int, color: Int) { target[mx(x), y] = color }
        fun rect(x0: Int, y0: Int, x1: Int, y1: Int, color: Int) = target.rect(mx(x0), y0, mx(x1), y1, color)
        fun hline(x0: Int, x1: Int, y: Int, color: Int) = rect(x0, y, x1, y, color)
        fun line(x0: Int, y0: Int, x1: Int, y1: Int, color: Int, thickness: Int) =
            target.line(mx(x0), y0, mx(x1), y1, color, thickness)
        fun circle(cx: Float, cy: Float, r: Float, color: Int) = target.circle(mf(cx), cy, r, color)
        fun ellipse(cx: Float, cy: Float, rx: Float, ry: Float, color: Int) = target.ellipse(mf(cx), cy, rx, ry, color)
        fun polygon(color: Int, vararg points: Pair<Number, Number>) =
            target.polygon(color, *points.map { mf(it.first.toFloat()) to it.second }.toTypedArray())
    }
}
