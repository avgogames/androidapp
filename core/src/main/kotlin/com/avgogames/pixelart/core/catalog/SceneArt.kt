package com.avgogames.pixelart.core.catalog

import com.avgogames.pixelart.core.model.Category
import com.avgogames.pixelart.core.model.PixelArt
import com.avgogames.pixelart.core.raster.Raster
import com.avgogames.pixelart.core.raster.mix
import com.avgogames.pixelart.core.raster.rgb
import com.avgogames.pixelart.core.raster.shade
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sin
import kotlin.random.Random

/** Landscapes built from layered helpers: sky, light, far ridges, near ground, props. */
internal object SceneArt {
    fun create(): List<PixelArt> = CatalogBuilder(Category.SCENES, "scenes").apply {
        art("Sunny Meadow") { s ->
            Scene(s, 1).run {
                sky(rgb(0x5BB8F5), rgb(0xBDE6FF), horizon = 20)
                sun(25f, 6f, 3.5f, rgb(0xFFE066))
                cloud(8f, 7f, rgb(0xFFFFFF))
                hills(18, 2.5f, 0.35f, rgb(0x7BC96F))
                hills(23, 2f, 0.5f, rgb(0x4FA44A))
                flowers(14, 25, 31, listOf(rgb(0xFF6B9A), rgb(0xFFE14D), rgb(0xFFFFFF)))
            }
        }
        art("Sunset Lake") { s ->
            Scene(s, 2).run {
                sky(rgb(0x5B3A8C), rgb(0xFF9E5E), horizon = 19, bands = 5)
                sun(16f, 17f, 5f, rgb(0xFFD25E))
                mountains(19, 8, 3, rgb(0x3D2856))
                water(19, rgb(0x6A4A9E), rgb(0xFFB070))
                reflectionColumn(16, 20, 30, rgb(0xFFD25E))
            }
        }
        art("Starry Hills") { s ->
            Scene(s, 3).run {
                sky(rgb(0x0B1236), rgb(0x2C3E8C), horizon = 22)
                stars(28, rgb(0xFFF7C2), maxY = 20)
                moon(24f, 7f, 3.5f, rgb(0xF4F1D0))
                hills(20, 3f, 0.3f, rgb(0x1E2F5E))
                hills(25, 2f, 0.45f, rgb(0x14203F))
                house(6, 26, 7, 5, rgb(0x3A2F4F), rgb(0x241A33), rgb(0xFFD966))
            }
        }
        art("Snowy Peaks") { s ->
            Scene(s, 4).run {
                sky(rgb(0x7EC8F2), rgb(0xE3F4FF), horizon = 22)
                cloud(24f, 5f, rgb(0xFFFFFF))
                mountains(24, 17, 3, rgb(0x6F7FA8), snow = rgb(0xFFFFFF))
                mountains(26, 9, 4, rgb(0x4F5F86), snow = rgb(0xE8F0FF))
                ground(27, rgb(0xEEF4FB))
                pine(4, 31, 8, rgb(0x2F6B4F))
                pine(27, 31, 7, rgb(0x2F6B4F))
            }
        }
        art("Desert Dunes") { s ->
            Scene(s, 5).run {
                sky(rgb(0xFFB55E), rgb(0xFFE7A8), horizon = 18)
                sun(9f, 8f, 4f, rgb(0xFFF3C4))
                hills(17, 3f, 0.25f, rgb(0xE59A4B))
                hills(22, 3f, 0.35f, rgb(0xD1813A))
                hills(27, 2f, 0.3f, rgb(0xB86A2C))
                cactus(24, 27, rgb(0x3F8F4A))
            }
        }
        art("Tropical Beach") { s ->
            Scene(s, 6).run {
                sky(rgb(0x38B6FF), rgb(0xA8E6FF), horizon = 18)
                sun(26f, 5f, 3f, rgb(0xFFEE70))
                water(18, rgb(0x1E9BD7), rgb(0xBFF3FF))
                s.polygon(rgb(0xF6D58E), 0 to 24, 14 to 22, 32 to 26, 32 to 32, 0 to 32)
                palm(7, 27, 13)
            }
        }
        art("Pine Forest") { s ->
            Scene(s, 7).run {
                sky(rgb(0x9AD4E8), rgb(0xE6F6EF), horizon = 18)
                mountains(20, 9, 3, rgb(0x8AA9B8))
                ground(25, rgb(0x5C8C3E))
                for ((x, h) in listOf(2 to 12, 8 to 15, 14 to 11, 20 to 16, 26 to 13, 31 to 10)) {
                    pine(x, 28, h, rgb(0x24573A))
                }
                ground(29, rgb(0x46702F))
            }
        }
        art("Autumn Valley") { s ->
            Scene(s, 8).run {
                sky(rgb(0xF2C48D), rgb(0xFCE9CF), horizon = 18)
                hills(16, 3f, 0.3f, rgb(0xC9783B))
                hills(22, 2f, 0.4f, rgb(0xA35A2A))
                roundTree(6, 28, 5, rgb(0xE0602A))
                roundTree(16, 27, 4, rgb(0xF2A23A))
                roundTree(26, 29, 5, rgb(0xC0392B))
                ground(29, rgb(0x7A4A24))
            }
        }
        art("Moonlit Sea") { s ->
            Scene(s, 9).run {
                sky(rgb(0x0E1A3A), rgb(0x34507F), horizon = 20)
                stars(18, rgb(0xFFFFFF), maxY = 18)
                moon(16f, 9f, 5f, rgb(0xFDF6D8))
                water(20, rgb(0x16305C), rgb(0x5F7FB4))
                reflectionColumn(16, 21, 31, rgb(0xFDF6D8))
            }
        }
        art("Lighthouse Cove") { s ->
            Scene(s, 10).run {
                sky(rgb(0x72C5F0), rgb(0xD4F0FF), horizon = 20)
                cloud(9f, 6f, rgb(0xFFFFFF))
                water(20, rgb(0x2A86C8), rgb(0xCDEFFF))
                s.polygon(rgb(0x6E6A62), 14 to 32, 18 to 21, 32 to 19, 32 to 32)
                lighthouse(23, 21)
            }
        }
        art("Cozy Cabin") { s ->
            Scene(s, 11).run {
                sky(rgb(0x9DC6E8), rgb(0xE9F3FB), horizon = 20)
                mountains(22, 12, 3, rgb(0xA4B4CC), snow = rgb(0xFFFFFF))
                ground(23, rgb(0xF4F8FC))
                house(9, 29, 13, 8, rgb(0x8B5A33), rgb(0xFFFFFF), rgb(0xFFC94A))
                pine(4, 30, 10, rgb(0x2E6A4A))
                pine(27, 30, 11, rgb(0x2E6A4A))
            }
        }
        art("Blossom Hill") { s ->
            Scene(s, 12).run {
                sky(rgb(0xBFE3FF), rgb(0xFFF0F6), horizon = 20)
                hills(21, 3f, 0.3f, rgb(0x9CD67E))
                roundTree(16, 26, 7, rgb(0xFFB3CF), trunk = rgb(0x6E4B3A))
                petals(12, rgb(0xFF8FB8))
                ground(28, rgb(0x7CC063))
            }
        }
        art("Volcano Isle") { s ->
            Scene(s, 13).run {
                sky(rgb(0xFF7B54), rgb(0xFFD08A), horizon = 21)
                s.polygon(rgb(0x5A3B36), 4 to 22, 13 to 8, 19 to 8, 28 to 22)
                s.polygon(rgb(0xFF4A1C), 13 to 8, 19 to 8, 18 to 12, 16 to 10, 14 to 13)
                cloud(16f, 4f, rgb(0x8A7E7A))
                water(22, rgb(0x2F7FB5), rgb(0x9FD9F5))
                palm(26, 27, 7)
            }
        }
        art("Northern Lights") { s ->
            Scene(s, 14).run {
                sky(rgb(0x061229), rgb(0x123056), horizon = 22)
                stars(20, rgb(0xFFFFFF), maxY = 21)
                aurora(rgb(0x4DFFB0), rgb(0x8C6BFF))
                mountains(24, 9, 3, rgb(0x1B2B45), snow = rgb(0xCFE3FF))
                ground(26, rgb(0xDCE8F5))
                pine(6, 31, 8, rgb(0x0F2A26))
            }
        }
        art("Misty Mountains") { s ->
            Scene(s, 15).run {
                sky(rgb(0xC7D6DE), rgb(0xEEF3F5), horizon = 22)
                mountains(14, 12, 2, rgb(0xA9BAC4))
                mist(14, rgb(0xDDE6EA))
                mountains(21, 12, 3, rgb(0x7F96A3))
                mist(21, rgb(0xC6D3D9))
                mountains(28, 10, 4, rgb(0x55707F))
                ground(29, rgb(0x3F5763))
            }
        }
        art("Canyon Dusk") { s ->
            Scene(s, 16).run {
                sky(rgb(0x6C3E7A), rgb(0xF59A6B), horizon = 16)
                sun(16f, 15f, 4f, rgb(0xFFCF70))
                s.rect(0, 12, 7, 31, rgb(0xB5552D)); s.rect(0, 12, 7, 13, rgb(0xD06C3A))
                s.rect(24, 10, 31, 31, rgb(0xA94B28)); s.rect(24, 10, 31, 11, rgb(0xCB6535))
                s.rect(8, 17, 23, 31, rgb(0xC9653A)); s.rect(8, 17, 23, 18, rgb(0xE07D49))
                s.rect(12, 24, 19, 31, rgb(0x8E3F22))
            }
        }
        art("Spring Fields") { s ->
            Scene(s, 17).run {
                sky(rgb(0x7FD0FF), rgb(0xE0F6FF), horizon = 16)
                cloud(22f, 6f, rgb(0xFFFFFF))
                fields(16, listOf(rgb(0xB8E07A), rgb(0xFFE36E), rgb(0x8CCB5E), rgb(0xFF9FB8), rgb(0x6FB34D)))
                house(23, 19, 6, 4, rgb(0xFFFFFF), rgb(0xD9483B), rgb(0x5BA3D9))
            }
        }
        art("Winter Village") { s ->
            Scene(s, 18).run {
                sky(rgb(0x3E5C8A), rgb(0x9DB8DA), horizon = 22)
                stars(10, rgb(0xFFFFFF), maxY = 12)
                ground(23, rgb(0xF0F5FC))
                house(2, 28, 9, 6, rgb(0xB0563A), rgb(0xFFFFFF), rgb(0xFFD35C))
                house(13, 26, 8, 6, rgb(0x5A7FA8), rgb(0xFFFFFF), rgb(0xFFD35C))
                house(23, 29, 8, 6, rgb(0x6D8F5A), rgb(0xFFFFFF), rgb(0xFFD35C))
                snowflakes(22, rgb(0xFFFFFF))
            }
        }
        art("Ocean Sunrise") { s ->
            Scene(s, 19).run {
                sky(rgb(0xFF8A65), rgb(0xFFE29A), horizon = 17, bands = 5)
                sun(16f, 17f, 6f, rgb(0xFFF1B8))
                water(17, rgb(0x3C6E9E), rgb(0xFFC48A))
                reflectionColumn(16, 18, 31, rgb(0xFFF1B8))
                bird(6, 6); bird(10, 9); bird(24, 5)
            }
        }
        art("Lavender Rows") { s ->
            Scene(s, 20).run {
                sky(rgb(0xA7C7F2), rgb(0xF6E8FF), horizon = 15)
                sun(6f, 6f, 3f, rgb(0xFFF0A0))
                hills(15, 1.5f, 0.3f, rgb(0x8BB86B))
                rows(16, rgb(0x8E6BC9), rgb(0x6F9A50))
                roundTree(26, 16, 3, rgb(0x5E8C3E))
            }
        }
        art("Twilight City") { s ->
            Scene(s, 21).run {
                sky(rgb(0x2A1B4D), rgb(0xE07AA0), horizon = 24, bands = 5)
                stars(12, rgb(0xFFF3C0), maxY = 10)
                skyline(rgb(0x241A3A), rgb(0xFFD66B))
            }
        }
        art("Red Barn Farm") { s ->
            Scene(s, 22).run {
                sky(rgb(0x8ED1F7), rgb(0xE5F6FF), horizon = 18)
                hills(17, 2f, 0.3f, rgb(0x8FCB6A))
                ground(22, rgb(0x6AAE48))
                barn(12, 27)
                fence(28, rgb(0xFFFFFF))
                roundTree(4, 22, 4, rgb(0x3F8A3A))
            }
        }
        art("Balloon Festival") { s ->
            Scene(s, 23).run {
                sky(rgb(0x5FB9F0), rgb(0xD9F1FF), horizon = 26)
                cloud(23f, 23f, rgb(0xFFFFFF))
                balloon(9, 9, rgb(0xFF5A5F), rgb(0xFFD15C))
                balloon(23, 6, rgb(0x5C7CFA), rgb(0xFFFFFF))
                balloon(18, 18, rgb(0x52C77E), rgb(0xFFE66D))
                hills(28, 1.5f, 0.4f, rgb(0x6DBE5A))
            }
        }
        art("Hidden Waterfall") { s ->
            Scene(s, 24).run {
                sky(rgb(0x9FD8F2), rgb(0xE8F8FF), horizon = 12)
                s.rect(0, 6, 11, 31, rgb(0x5D6B5A)); s.rect(20, 4, 31, 31, rgb(0x4F5D4C))
                s.rect(0, 6, 11, 8, rgb(0x4E9A47)); s.rect(20, 4, 31, 6, rgb(0x4E9A47))
                s.rect(12, 7, 19, 26, rgb(0x7FD3F5))
                for (y in 8..25 step 3) { s.vline(13 + y % 5, y, y + 1, rgb(0xE8FAFF)) }
                water(26, rgb(0x3A9AD0), rgb(0xE8FAFF))
                roundTree(4, 10, 4, rgb(0x3B7F35)); roundTree(27, 8, 4, rgb(0x3B7F35))
            }
        }
        art("Rainbow Valley") { s ->
            Scene(s, 25).run {
                sky(rgb(0x8BC9F0), rgb(0xE7F6FF), horizon = 22)
                rainbow()
                cloud(5f, 17f, rgb(0xFFFFFF)); cloud(27f, 17f, rgb(0xFFFFFF))
                hills(22, 2.5f, 0.35f, rgb(0x7DCB64))
                hills(27, 2f, 0.5f, rgb(0x55A84A))
            }
        }
    }.build()
}

/** Layer helpers for one scene. [seed] keeps star and flower placement stable between runs. */
private class Scene(val s: Raster, seed: Int) {
    private val random = Random(seed * 7919)

    fun sky(top: Int, bottom: Int, horizon: Int, bands: Int = 4) {
        val bandHeight = max(1, horizon / bands)
        for (y in 0 until s.height) {
            val band = (y / bandHeight).coerceAtMost(bands - 1)
            s.hline(0, s.width - 1, y, mix(top, bottom, band / (bands - 1f)))
        }
    }

    fun sun(cx: Float, cy: Float, r: Float, color: Int) {
        s.circle(cx, cy, r + 1.2f, mix(color, s[cx.toInt().coerceIn(0, 31), (cy - r - 1.5f).toInt().coerceIn(0, 31)], 0.55f))
        s.circle(cx, cy, r, color)
    }

    fun moon(cx: Float, cy: Float, r: Float, color: Int) {
        s.circle(cx, cy, r, color)
        val crater = shade(color, 0.86f)
        s[(cx - 1).toInt(), (cy - 1).toInt()] = crater
        s[(cx + 1).toInt(), (cy + 1).toInt()] = crater
        s[(cx + 1).toInt(), (cy + 1).toInt() - 1] = crater
    }

    fun stars(count: Int, color: Int, maxY: Int) = repeat(count) {
        s[random.nextInt(s.width), random.nextInt(maxY)] = color
    }

    fun cloud(cx: Float, cy: Float, color: Int) {
        val shadow = shade(color, 0.88f)
        s.ellipse(cx, cy + 1f, 6f, 2.2f, shadow)
        s.ellipse(cx - 2f, cy, 3f, 2.2f, color)
        s.ellipse(cx + 1.5f, cy - 0.8f, 3.2f, 2.6f, color)
        s.ellipse(cx + 4f, cy + 0.5f, 2f, 1.6f, color)
    }

    /** Layered ridge: sum of a few random peaks; [snow] caps the top rows of each peak. */
    fun mountains(baseY: Int, peakHeight: Int, peaks: Int, color: Int, snow: Int? = null) {
        val centers = List(peaks) { (it + 0.5f) * s.width / peaks + random.nextInt(-3, 4) }
        val heights = List(peaks) { peakHeight - random.nextInt(0, max(1, peakHeight / 3)) }
        val shadow = shade(color, 0.85f)
        for (x in 0 until s.width) {
            var top = s.height
            var nearest = 0
            for (i in centers.indices) {
                val y = baseY - heights[i] + (abs(x + 0.5f - centers[i]) * 1.1f).toInt()
                if (y < top) { top = y; nearest = i }
            }
            top = top.coerceAtMost(baseY)
            s.vline(x, top, s.height - 1, if (x + 0.5f > centers[nearest]) shadow else color)
            if (snow != null && top < baseY - heights[nearest] + 3) {
                s.vline(x, top, baseY - heights[nearest] + 2, snow)
            }
        }
    }

    fun hills(baseY: Int, amplitude: Float, frequency: Float, color: Int) {
        val phase = random.nextFloat() * 6f
        for (x in 0 until s.width) {
            val top = (baseY + sin(x * frequency + phase) * amplitude).toInt()
            s.vline(x, top, s.height - 1, color)
        }
    }

    fun ground(fromY: Int, color: Int) = s.rect(0, fromY, s.width - 1, s.height - 1, color)

    fun water(fromY: Int, color: Int, sparkle: Int) {
        s.rect(0, fromY, s.width - 1, s.height - 1, color)
        s.rect(0, fromY + (s.height - fromY) / 2, s.width - 1, s.height - 1, shade(color, 0.85f))
        repeat((s.height - fromY) * 2) {
            val y = random.nextInt(fromY + 1, s.height)
            val x = random.nextInt(s.width - 3)
            s.hline(x, x + random.nextInt(1, 3), y, sparkle)
        }
    }

    fun reflectionColumn(cx: Int, fromY: Int, toY: Int, color: Int) {
        for (y in fromY..toY) {
            val half = max(0, 3 - (y - fromY) / 3)
            if ((y - fromY) % 2 == 0) s.hline(cx - half - 1, cx + half, y, color)
        }
    }

    fun mist(y: Int, color: Int) {
        s.rect(0, y - 1, s.width - 1, y, color)
        for (x in 0 until s.width step 5) s.hline(x, x + 2, y - 2, color)
    }

    fun pine(x: Int, baseY: Int, height: Int, color: Int) {
        val dark = shade(color, 0.75f)
        s.rect(x, baseY - 2, x, baseY, rgb(0x5B3A1E))
        val tiers = 3
        for (t in 0 until tiers) {
            val tierTop = baseY - height + t * height / (tiers + 1)
            val tierBottom = tierTop + height / 2
            val half = 1.5f + t * 1.2f
            s.polygon(color, x + 0.5f to tierTop, x + 0.5f + half to tierBottom, x + 0.5f - half to tierBottom)
            s.polygon(dark, x + 0.5f to tierTop, x + 0.5f + half to tierBottom, x + 0.5f to tierBottom)
        }
    }

    fun roundTree(x: Int, baseY: Int, r: Int, color: Int, trunk: Int = rgb(0x6B4226)) {
        s.rect(x, baseY - r, x + 1, baseY, trunk)
        s.circle(x + 1f, baseY - r - r * 0.6f, r.toFloat(), color)
        s.circle(x + 1f - r * 0.35f, baseY - r - r * 0.9f, r * 0.45f, mix(color, rgb(0xFFFFFF), 0.25f))
    }

    fun palm(x: Int, baseY: Int, height: Int) {
        val trunk = rgb(0x9B6B3F)
        for (i in 0..height) s[x + i / 5, baseY - i] = trunk
        for (i in 0..height step 3) s[x + i / 5, baseY - i] = shade(trunk, 0.8f)
        val tx = x + height / 5
        val ty = baseY - height
        val leaf = rgb(0x2E9E4F)
        s.line(tx, ty, tx - 5, ty + 3, leaf, 2)
        s.line(tx, ty, tx + 5, ty + 3, leaf, 2)
        s.line(tx, ty, tx - 4, ty - 2, leaf, 1)
        s.line(tx, ty, tx + 4, ty - 2, leaf, 1)
        s.line(tx, ty, tx + 1, ty + 5, leaf, 1)
        s.circle(tx + 0.5f, ty + 1.5f, 1f, rgb(0x6B4A2B))
    }

    fun cactus(x: Int, baseY: Int, color: Int) {
        s.rect(x, baseY - 9, x + 1, baseY, color)
        s.rect(x - 3, baseY - 6, x - 2, baseY - 3, color); s.hline(x - 3, x, baseY - 3, color)
        s.rect(x + 3, baseY - 8, x + 4, baseY - 5, color); s.hline(x + 1, x + 4, baseY - 5, color)
        s.vline(x + 1, baseY - 9, baseY, shade(color, 0.8f))
    }

    fun house(x: Int, baseY: Int, w: Int, h: Int, wall: Int, roof: Int, window: Int) {
        s.rect(x, baseY - h, x + w - 1, baseY, wall)
        s.polygon(roof, x - 1 to baseY - h + 0.5f, x + w / 2f to baseY - h - w / 2f, x + w + 1 to baseY - h + 0.5f)
        s.rect(x + 1, baseY - h + 2, x + 2, baseY - h + 3, window)
        if (w >= 7) s.rect(x + w - 3, baseY - h + 2, x + w - 2, baseY - h + 3, window)
        s.rect(x + w / 2 - 1 + (w + 1) % 2, baseY - 2, x + w / 2, baseY, shade(wall, 0.6f))
    }

    fun lighthouse(x: Int, baseY: Int) {
        val red = rgb(0xE04040); val white = rgb(0xFFFFFF)
        for (y in 0..12) {
            val half = 2 + y / 6
            s.hline(x - half, x + half, baseY - 12 + y, if ((y / 3) % 2 == 0) red else white)
        }
        s.rect(x - 2, baseY - 15, x + 2, baseY - 13, rgb(0xFFE36B))
        s.polygon(rgb(0x3A3A48), x - 3 to baseY - 15, x + 0.5f to baseY - 19, x + 4 to baseY - 15)
        s.line(x + 3, baseY - 14, x + 8, baseY - 16, rgb(0xFFF6C2))
        s.line(x - 3, baseY - 14, x - 8, baseY - 16, rgb(0xFFF6C2))
    }

    fun flowers(count: Int, minY: Int, maxY: Int, colors: List<Int>) = repeat(count) {
        val x = random.nextInt(1, s.width - 1)
        val y = random.nextInt(minY, maxY)
        val c = colors[it % colors.size]
        s[x, y] = c; s[x - 1, y] = c; s[x + 1, y] = c; s[x, y - 1] = c
        s[x, y] = rgb(0xFFF2A8)
    }

    fun petals(count: Int, color: Int) = repeat(count) {
        s[random.nextInt(s.width), random.nextInt(8, s.height - 4)] = color
    }

    fun snowflakes(count: Int, color: Int) = repeat(count) {
        s[random.nextInt(s.width), random.nextInt(0, 22)] = color
    }

    fun aurora(a: Int, b: Int) {
        for (x in 0 until s.width) {
            val y = (8 + sin(x * 0.35f) * 3f).toInt()
            s.vline(x, y, y + 2, a)
            s.vline(x, y + 3, y + 4, mix(a, b, 0.6f))
            if (x % 3 == 0) s.vline(x, y - 2, y - 1, b)
        }
    }

    fun fields(fromY: Int, colors: List<Int>) {
        val rows = colors.size
        for (y in fromY until s.height) {
            for (x in 0 until s.width) {
                val band = ((y - fromY) * rows / (s.height - fromY) + (x + y) / 14) % rows
                s[x, y] = colors[band]
            }
        }
    }

    fun rows(fromY: Int, flower: Int, leaf: Int) {
        for (y in fromY until s.height) for (x in 0 until s.width) {
            val stripe = (x - 16) * (y - fromY + 2) / (s.height - fromY + 6)
            s[x, y] = if (((x + stripe) / 2) % 3 == 0) leaf else if ((y + x) % 5 == 0) shade(flower, 0.8f) else flower
        }
    }

    fun skyline(building: Int, light: Int) {
        var x = 0
        while (x < s.width) {
            val w = random.nextInt(3, 6)
            val top = random.nextInt(10, 22)
            val c = if (random.nextBoolean()) building else shade(building, 1.25f)
            s.rect(x, top, x + w - 1, s.height - 1, c)
            for (wy in top + 2 until s.height - 1 step 2) for (wx in x + 1 until x + w - 1 step 2) {
                if (random.nextInt(3) > 0) s[wx, wy] = light
            }
            x += w
        }
    }

    fun barn(x: Int, baseY: Int) {
        val red = rgb(0xC0392B); val white = rgb(0xFFFFFF)
        s.rect(x, baseY - 7, x + 9, baseY, red)
        s.polygon(rgb(0x7B2A20), x - 1 to baseY - 6.5f, x + 2 to baseY - 10, x + 8 to baseY - 10, x + 11 to baseY - 6.5f)
        s.rect(x + 3, baseY - 4, x + 6, baseY, white)
        s.line(x + 3, baseY - 4, x + 6, baseY, red); s.line(x + 6, baseY - 4, x + 3, baseY, red)
        s.rect(x + 4, baseY - 8, x + 5, baseY - 7, white)
    }

    fun fence(y: Int, color: Int) {
        s.hline(0, s.width - 1, y, color)
        s.hline(0, s.width - 1, y + 2, color)
        for (x in 1 until s.width step 4) s.vline(x, y - 1, y + 3, color)
    }

    fun balloon(cx: Int, cy: Int, color: Int, stripe: Int) {
        s.ellipse(cx + 0.5f, cy.toFloat(), 4f, 4.5f, color)
        s.rect(cx, cy - 4, cx, cy + 3, stripe)
        s.polygon(color, cx - 3 to cy + 2, cx + 4 to cy + 2, cx + 1.5f to cy + 6, cx - 0.5f to cy + 6)
        s.vline(cx - 1, cy + 6, cy + 7, rgb(0x5B4636)); s.vline(cx + 1, cy + 6, cy + 7, rgb(0x5B4636))
        s.rect(cx - 1, cy + 8, cx + 1, cy + 9, rgb(0x9B6B3F))
    }

    fun bird(x: Int, y: Int) {
        val c = rgb(0x3A2A4A)
        s[x, y] = c; s[x + 1, y + 1] = c; s[x + 2, y] = c; s[x - 1, y - 1] = c; s[x + 3, y - 1] = c
    }

    fun rainbow() {
        val colors = listOf(0xFF5C5C, 0xFFA94D, 0xFFE066, 0x69DB7C, 0x4DABF7, 0x9775FA).map(::rgb)
        for ((i, c) in colors.withIndex()) {
            val r = 15f - i
            for (y in 0 until 19) for (x in 0 until s.width) {
                val dx = x + 0.5f - 16f; val dy = y + 0.5f - 23f
                val d = kotlin.math.sqrt(dx * dx + dy * dy)
                if (d <= r && d > r - 1f) s[x, y] = c
            }
        }
    }
}
