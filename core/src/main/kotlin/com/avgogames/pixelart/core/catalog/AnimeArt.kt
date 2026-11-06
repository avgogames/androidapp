package com.avgogames.pixelart.core.catalog

import com.avgogames.pixelart.core.model.Category
import com.avgogames.pixelart.core.model.PixelArt
import com.avgogames.pixelart.core.raster.Raster
import com.avgogames.pixelart.core.raster.mix
import com.avgogames.pixelart.core.raster.rgb
import com.avgogames.pixelart.core.raster.shade

/**
 * Original anime-style chibi portraits. Each is a combination of hair style, accessory,
 * expression and colors; none depicts an existing character.
 */
internal object AnimeArt {
    private val INK = rgb(0x2B2233)
    private val WHITE = rgb(0xFFFFFF)
    private val SKIN_LIGHT = rgb(0xFFE0CC)
    private val SKIN_WARM = rgb(0xF6C9A6)
    private val SKIN_TAN = rgb(0xD9A27A)
    private val SKIN_DEEP = rgb(0x9C6B4E)

    private enum class Hair { SHORT, LONG, TWIN_TAILS, PONYTAIL, SPIKY, BOB, BUNS, SIDE_PART }
    private enum class Accessory { NONE, BOW, CAT_EARS, HEADPHONES, GLASSES, STAR_CLIP, HEADBAND, FLOWER, WITCH_HAT, CROWN, CAP }
    private enum class Mood { SMILE, OPEN, WINK, CALM }
    private enum class Pattern { DOTS, STARS, HEARTS, STRIPES, PLAIN }

    private class Look(
        val title: String,
        val hair: Hair,
        val hairColor: Int,
        val eyeColor: Int,
        val skin: Int,
        val outfit: Int,
        val background: Int,
        val accessory: Accessory = Accessory.NONE,
        val accent: Int = rgb(0xFF5A7A),
        val mood: Mood = Mood.SMILE,
        val pattern: Pattern = Pattern.DOTS,
    )

    private val looks = listOf(
        Look("Sakura Student", Hair.LONG, rgb(0xF59AC0), rgb(0x3E7BD6), SKIN_LIGHT, rgb(0x2E3A66), rgb(0xFFE6EF), Accessory.BOW, rgb(0xE8344F)),
        Look("Spiky Hero", Hair.SPIKY, rgb(0xF2C230), rgb(0x2F9E5A), SKIN_WARM, rgb(0xE8572A), rgb(0xCDEBFF), Accessory.HEADBAND, rgb(0x2B59C3), Mood.OPEN, Pattern.STRIPES),
        Look("Twin Tail Idol", Hair.TWIN_TAILS, rgb(0x3ED1C8), rgb(0x2E9AA8), SKIN_LIGHT, rgb(0x2B2B3A), rgb(0xE3F9F7), Accessory.STAR_CLIP, rgb(0xFF5AA5), Mood.WINK, Pattern.STARS),
        Look("Star Mage", Hair.LONG, rgb(0x8E6BE8), rgb(0xF2B630), SKIN_LIGHT, rgb(0x4A3A8C), rgb(0x1E1B45), Accessory.WITCH_HAT, rgb(0x2E2560), Mood.CALM, Pattern.STARS),
        Look("Cat Ear Cafe", Hair.BOB, rgb(0x2B2B38), rgb(0xD94A6A), SKIN_LIGHT, rgb(0xFFFFFF), rgb(0xFFEFD6), Accessory.CAT_EARS, rgb(0x2B2B38), Mood.SMILE, Pattern.HEARTS),
        Look("DJ Kaito", Hair.SHORT, rgb(0x2D5BD3), rgb(0x2D5BD3), SKIN_WARM, rgb(0x1F1F2B), rgb(0xFFE36E), Accessory.HEADPHONES, rgb(0xFF4D6D), Mood.OPEN, Pattern.STRIPES),
        Look("Bookworm Hana", Hair.PONYTAIL, rgb(0x7A4A2B), rgb(0x6B8E3A), SKIN_LIGHT, rgb(0x6FA8DC), rgb(0xE8F2E0), Accessory.GLASSES, rgb(0x4A3A3A), Mood.CALM),
        Look("Princess Rin", Hair.LONG, rgb(0xFFD966), rgb(0x3FA3D6), SKIN_LIGHT, rgb(0xFF8FB8), rgb(0xFCE4F2), Accessory.CROWN, rgb(0xF2B630), Mood.SMILE, Pattern.HEARTS),
        Look("Skater Ren", Hair.SIDE_PART, rgb(0xE8E8F0), rgb(0xD9342B), SKIN_TAN, rgb(0x2E9E8F), rgb(0xFFD3B5), Accessory.CAP, rgb(0xD9342B), Mood.WINK, Pattern.PLAIN),
        Look("Bun Chef Mei", Hair.BUNS, rgb(0x2B2B38), rgb(0xA0522D), SKIN_LIGHT, rgb(0xFFFFFF), rgb(0xFFE0D6), Accessory.NONE, rgb(0xE8344F), Mood.OPEN),
        Look("Flower Yui", Hair.BOB, rgb(0xFF9A4D), rgb(0x2F9E5A), SKIN_LIGHT, rgb(0xF4D35E), rgb(0xE0F5D0), Accessory.FLOWER, rgb(0xFF6FA0), Mood.SMILE, Pattern.DOTS),
        Look("Ninja Sora", Hair.SPIKY, rgb(0x2B2B38), rgb(0x5B6B8C), SKIN_WARM, rgb(0x34354A), rgb(0xBFD7EA), Accessory.HEADBAND, rgb(0x8C93A8), Mood.CALM, Pattern.PLAIN),
        Look("Cosmic Luna", Hair.TWIN_TAILS, rgb(0xC9C9F2), rgb(0x8A5BD6), SKIN_LIGHT, rgb(0x3E3A78), rgb(0x241F4F), Accessory.STAR_CLIP, rgb(0xF2D04B), Mood.SMILE, Pattern.STARS),
        Look("Sporty Aoi", Hair.PONYTAIL, rgb(0x3A6FD8), rgb(0x3A6FD8), SKIN_TAN, rgb(0xFF6B4A), rgb(0xFFF2C2), Accessory.HEADBAND, rgb(0xFFFFFF), Mood.OPEN, Pattern.STRIPES),
        Look("Gamer Kenji", Hair.SHORT, rgb(0x6B3A2B), rgb(0x2B2233), SKIN_DEEP, rgb(0x5BCB6B), rgb(0x2B2F4A), Accessory.HEADPHONES, rgb(0x5BCB6B), Mood.SMILE, Pattern.DOTS),
        Look("Mint Witch", Hair.LONG, rgb(0x6FD6B0), rgb(0xD94A8C), SKIN_LIGHT, rgb(0x2B2B38), rgb(0xFFE5F0), Accessory.WITCH_HAT, rgb(0x2B2B38), Mood.WINK, Pattern.HEARTS),
        Look("Scholar Haru", Hair.SIDE_PART, rgb(0x2B2B38), rgb(0x6B4A2B), SKIN_WARM, rgb(0x8C2B3A), rgb(0xF2E6D6), Accessory.GLASSES, rgb(0x2B2B38), Mood.CALM, Pattern.PLAIN),
        Look("Bunny Bow Emi", Hair.TWIN_TAILS, rgb(0xFFE8A3), rgb(0xE8576F), SKIN_LIGHT, rgb(0xFFB3C8), rgb(0xE6F0FF), Accessory.BOW, rgb(0xFF7AA0), Mood.SMILE, Pattern.HEARTS),
        Look("Fire Kid Taro", Hair.SPIKY, rgb(0xE8342B), rgb(0xF29A2E), SKIN_TAN, rgb(0x2B2B38), rgb(0xFFE0A8), Accessory.NONE, rgb(0xF29A2E), Mood.OPEN, Pattern.STRIPES),
        Look("Snow Queen Yuki", Hair.LONG, rgb(0xEAF4FF), rgb(0x4FB3F2), SKIN_LIGHT, rgb(0x6FA8DC), rgb(0x9BC9EE), Accessory.CROWN, rgb(0xBFE6FF), Mood.CALM, Pattern.STARS),
        Look("Kitty Mika", Hair.SHORT, rgb(0xF2A0C8), rgb(0x3FA35A), SKIN_WARM, rgb(0x6F5BD6), rgb(0xFFF6D6), Accessory.CAT_EARS, rgb(0xF2A0C8), Mood.WINK, Pattern.DOTS),
        Look("Pilot Nao", Hair.BOB, rgb(0x5A3A2B), rgb(0x4A7FD6), SKIN_DEEP, rgb(0x6B8E3A), rgb(0xBFE6FF), Accessory.CAP, rgb(0x6B8E3A), Mood.SMILE, Pattern.PLAIN),
        Look("Garden Kiko", Hair.BUNS, rgb(0xFF8F5A), rgb(0x3FA35A), SKIN_LIGHT, rgb(0x9BD06B), rgb(0xFFF0E0), Accessory.FLOWER, rgb(0xFFFFFF), Mood.SMILE, Pattern.DOTS),
        Look("Rock Star Jun", Hair.SPIKY, rgb(0x8E3BD6), rgb(0x8E3BD6), SKIN_LIGHT, rgb(0x2B2B38), rgb(0xFF5A7A), Accessory.HEADPHONES, rgb(0xC9CED6), Mood.OPEN, Pattern.STARS),
        Look("Sunny Natsu", Hair.PONYTAIL, rgb(0xFFC94A), rgb(0xD97A2B), SKIN_TAN, rgb(0x3FB8E8), rgb(0xFFF7B8), Accessory.FLOWER, rgb(0xFF5A5A), Mood.WINK, Pattern.STRIPES),
    )

    fun create(): List<PixelArt> = CatalogBuilder(Category.ANIME, "anime").apply {
        for (look in looks) art(look.title) { s -> Portrait(s, look).draw() }
    }.build()

    private class Portrait(val s: Raster, val look: Look) {
        private val hairShade = shade(look.hairColor, 0.78f)
        private val hairLight = mix(look.hairColor, WHITE, 0.4f)

        fun draw() {
            backdrop()
            backHair()
            body()
            s.ellipse(16, 16, 9f, 9.5f, look.skin)
            s.rect(7, 16, 8, 17, look.skin); s.rect(23, 16, 24, 17, look.skin)
            frontHair()
            face()
            accessory()
        }

        private fun backdrop() {
            s.fill(look.background)
            val mark = mix(look.background, if (luminanceOf(look.background) > 0.5f) look.accent else WHITE, 0.35f)
            when (look.pattern) {
                Pattern.DOTS -> for (y in 2 until 32 step 5) for (x in (y % 2) * 2 + 1 until 32 step 5) s[x, y] = mark
                Pattern.STARS -> for ((x, y) in listOf(3 to 3, 27 to 4, 5 to 22, 28 to 20, 24 to 12, 2 to 12)) {
                    s[x, y] = mark; s[x - 1, y] = mark; s[x + 1, y] = mark; s[x, y - 1] = mark; s[x, y + 1] = mark
                }
                Pattern.HEARTS -> for ((x, y) in listOf(3 to 4, 27 to 6, 4 to 20, 27 to 21)) {
                    s.rect(x - 1, y, x, y, mark); s.rect(x + 2, y, x + 3, y, mark)
                    s.rect(x - 1, y + 1, x + 3, y + 1, mark); s.rect(x, y + 2, x + 2, y + 2, mark); s[x + 1, y + 3] = mark
                }
                Pattern.STRIPES -> for (x in -32 until 32 step 6) s.line(x, 31, x + 31, 0, mark, 2)
                Pattern.PLAIN -> Unit
            }
        }

        private fun backHair() {
            when (look.hair) {
                Hair.LONG -> {
                    s.ellipse(16, 15, 11.5f, 11f, look.hairColor)
                    s.rect(5, 15, 26, 29, look.hairColor)
                    s.vline(6, 18, 28, hairShade); s.vline(25, 18, 28, hairShade)
                }
                Hair.TWIN_TAILS -> {
                    s.ellipse(4.5f, 19f, 3.5f, 9f, look.hairColor); s.ellipse(27.5f, 19f, 3.5f, 9f, look.hairColor)
                    s.vline(4, 13, 26, hairShade); s.vline(27, 13, 26, hairShade)
                }
                Hair.PONYTAIL -> {
                    s.ellipse(26f, 17f, 3.5f, 8f, look.hairColor); s.vline(26, 12, 23, hairShade)
                }
                Hair.BUNS -> {
                    s.circle(8f, 5f, 4f, look.hairColor); s.circle(24f, 5f, 4f, look.hairColor)
                    s.circle(7f, 4f, 1.5f, hairLight); s.circle(23f, 4f, 1.5f, hairLight)
                }
                else -> Unit
            }
        }

        private fun body() {
            s.ellipse(16, 32, 12f, 7f, look.outfit)
            s.rect(14, 24, 17, 26, shade(look.skin, 0.9f))
            s.polygon(WHITE, 12 to 26, 16 to 29, 20 to 26, 18 to 26, 16 to 27.5f, 14 to 26)
            s[16, 29] = look.accent; s[15, 29] = look.accent; s[16, 30] = look.accent
        }

        private fun frontHair() {
            // Crown of the head.
            for (y in 0..11) for (x in 0..31) {
                val dx = (x + 0.5f - 16f) / 10.2f
                val dy = (y + 0.5f - 15.5f) / 10.5f
                if (dx * dx + dy * dy <= 1f) s[x, y] = look.hairColor
            }
            // Bangs: a jagged fringe; spiky hair gets longer, sharper points.
            val fringe = when (look.hair) {
                Hair.SPIKY -> intArrayOf(13, 11, 14, 12, 15, 12, 14, 11, 15, 12, 14, 11, 15, 12, 14, 11, 13, 12)
                Hair.SIDE_PART -> intArrayOf(16, 15, 15, 14, 14, 13, 13, 13, 12, 12, 12, 12, 12, 13, 13, 14, 15, 16)
                Hair.BOB -> intArrayOf(14, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 14)
                else -> intArrayOf(15, 14, 13, 12, 13, 14, 13, 12, 12, 13, 14, 13, 12, 13, 14, 13, 14, 15)
            }
            for (i in fringe.indices) s.vline(7 + i, 10, fringe[i], look.hairColor)
            // Side locks frame the face.
            val lockBottom = when (look.hair) {
                Hair.BOB -> 21
                Hair.SHORT, Hair.SPIKY, Hair.SIDE_PART -> 16
                else -> 22
            }
            s.rect(6, 10, 7, lockBottom, look.hairColor); s.rect(24, 10, 25, lockBottom, look.hairColor)
            s.vline(7, 13, lockBottom, hairShade); s.vline(24, 13, lockBottom, hairShade)
            if (look.hair == Hair.SPIKY) {
                s.polygon(look.hairColor, 5 to 9, 1 to 4, 9 to 6)
                s.polygon(look.hairColor, 27 to 9, 31 to 4, 23 to 6)
                s.polygon(look.hairColor, 13 to 6, 15 to 0, 18 to 6)
            }
            // Shine.
            s.hline(10, 13, 7, hairLight); s.hline(9, 10, 8, hairLight)
        }

        private fun face() {
            val iris = look.eyeColor
            val irisDark = shade(iris, 0.6f)
            val irisLight = mix(iris, WHITE, 0.35f)
            for (flip in listOf(false, true)) {
                fun x(v: Int) = if (flip) 31 - v else v
                if (look.mood == Mood.WINK && flip) {
                    s.hline(x(12), x(9), 17, INK); s[x(9), 16] = INK
                    continue
                }
                if (look.mood == Mood.CALM) {
                    s.hline(x(9), x(13), 16, INK)
                    s.rect(x(10), 17, x(12), 18, irisDark); s.rect(x(10), 19, x(12), 19, iris)
                    s[x(10), 17] = WHITE
                    continue
                }
                s.hline(x(9), x(13), 15, INK)
                s.rect(x(10), 16, x(12), 16, irisDark)
                s.rect(x(10), 17, x(12), 18, iris)
                s.rect(x(10), 19, x(12), 19, irisLight)
                s[x(10), 16] = WHITE; s[x(10), 17] = WHITE
            }
            s.rect(8, 20, 9, 20, rgb(0xF7A1B5)); s.rect(22, 20, 23, 20, rgb(0xF7A1B5))
            when (look.mood) {
                Mood.OPEN -> { s.rect(15, 22, 16, 23, rgb(0xC0394F)); s.hline(15, 16, 23, rgb(0xFF8FA3)) }
                Mood.CALM -> s.hline(15, 16, 22, rgb(0xC07A6A))
                else -> { s[14, 21] = rgb(0xC0394F); s.hline(15, 16, 22, rgb(0xC0394F)); s[17, 21] = rgb(0xC0394F) }
            }
        }

        private fun accessory() {
            val a = look.accent
            when (look.accessory) {
                Accessory.NONE -> Unit
                Accessory.BOW -> {
                    s.polygon(a, 18 to 2, 22 to 4.5f, 18 to 7); s.polygon(a, 26 to 2, 22 to 4.5f, 26 to 7)
                    s.rect(21, 4, 22, 5, shade(a, 0.75f))
                }
                Accessory.CAT_EARS -> {
                    s.polygon(look.hairColor, 5 to 9, 6 to 0, 12 to 5); s.polygon(look.hairColor, 27 to 9, 26 to 0, 20 to 5)
                    s.polygon(rgb(0xF7A1B5), 7 to 6, 7 to 3, 10 to 5); s.polygon(rgb(0xF7A1B5), 25 to 6, 25 to 3, 22 to 5)
                }
                Accessory.HEADPHONES -> {
                    s.rect(4, 13, 6, 19, a); s.rect(25, 13, 27, 19, a)
                    for (x in 5..26) {
                        val dx = (x + 0.5f - 16f) / 11f
                        val y = (14f - 13f * kotlin.math.sqrt((1f - dx * dx).coerceAtLeast(0f))).toInt()
                        s[x, y] = shade(a, 0.7f); s[x, y + 1] = shade(a, 0.7f)
                    }
                }
                Accessory.GLASSES -> {
                    for (cx in listOf(9, 19)) {
                        s.hline(cx, cx + 4, 15, a); s.hline(cx, cx + 4, 19, a)
                        s.vline(cx, 15, 19, a); s.vline(cx + 4, 15, 19, a)
                    }
                    s.hline(14, 17, 16, a)
                }
                Accessory.STAR_CLIP -> {
                    val x = 22; val y = 7
                    s.rect(x - 1, y, x + 1, y, a); s.vline(x, y - 1, y + 1, a)
                    s[x - 2, y] = a; s[x + 2, y] = a; s[x, y - 2] = a; s[x, y + 2] = a
                }
                Accessory.HEADBAND -> {
                    s.rect(7, 9, 24, 10, a)
                    s.rect(14, 9, 17, 10, mix(a, WHITE, 0.45f))
                }
                Accessory.FLOWER -> {
                    val c = 23f; val cy = 7f
                    for ((dx, dy) in listOf(-1.5f to 0f, 1.5f to 0f, 0f to -1.5f, 0f to 1.5f)) s.circle(c + dx, cy + dy, 1.4f, a)
                    s.rect(22, 6, 23, 7, rgb(0xF2C230))
                }
                Accessory.WITCH_HAT -> {
                    s.ellipse(16, 8, 14f, 2.2f, a)
                    s.polygon(a, 9 to 8, 16 to -4, 23 to 8)
                    s.polygon(a, 16 to -4, 22 to -1, 19 to 2)
                    s.rect(9, 6, 22, 7, look.accent.let { mix(look.eyeColor, WHITE, 0.2f) })
                }
                Accessory.CROWN -> {
                    s.polygon(a, 10 to 7, 10 to 1, 13 to 4, 16 to 0, 19 to 4, 22 to 1, 22 to 7)
                    s[16, 4] = rgb(0xE8344F); s[12, 5] = rgb(0x3FA3D6); s[20, 5] = rgb(0x3FA3D6)
                }
                Accessory.CAP -> {
                    s.ellipse(16, 8, 10f, 6f, a)
                    s.rect(6, 8, 25, 10, a)
                    s.rect(16, 10, 30, 11, shade(a, 0.75f))
                    s.rect(14, 4, 17, 6, WHITE)
                }
            }
        }

        private fun luminanceOf(c: Int) = com.avgogames.pixelart.core.raster.luminance(c)
    }
}
