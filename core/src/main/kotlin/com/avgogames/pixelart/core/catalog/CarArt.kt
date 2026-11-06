package com.avgogames.pixelart.core.catalog

import com.avgogames.pixelart.core.model.Category
import com.avgogames.pixelart.core.model.PixelArt
import com.avgogames.pixelart.core.raster.Raster
import com.avgogames.pixelart.core.raster.mix
import com.avgogames.pixelart.core.raster.rgb
import com.avgogames.pixelart.core.raster.shade
import kotlin.random.Random

/** Side-view vehicles facing right, each drawn from a silhouette polygon plus details. */
internal object CarArt {
    private val GLASS = rgb(0xAEE3FF)
    private val TIRE = rgb(0x26262E)
    private val RIM = rgb(0xC9CED6)

    fun create(): List<PixelArt> = CatalogBuilder(Category.CARS, "cars").apply {
        art("City Sedan") { s ->
            Garage(s, 1).run {
                backdrop(Backdrop.DAY)
                body(rgb(0x3D7DD8), 3 to 25, 3 to 19, 9 to 18, 12 to 13, 21 to 13, 25 to 18, 29 to 19, 29 to 25)
                glass(12.8f to 14, 16 to 14, 16 to 18, 10.4f to 18)
                glass(17 to 14, 20.6f to 14, 23.6f to 18, 17 to 18)
                lights(29, 3, 19); wheel(8.5f, 3f); wheel(23.5f, 3f)
            }
        }
        art("Zippy Hatchback") { s ->
            Garage(s, 2).run {
                backdrop(Backdrop.CITY)
                body(rgb(0xF2C230), 4 to 25, 4 to 15, 7 to 12, 18 to 12, 23 to 17, 28 to 18, 28 to 25)
                glass(7.6f to 13, 12 to 13, 12 to 17, 5 to 17)
                glass(13 to 13, 17.6f to 13, 21.6f to 17, 13 to 17)
                lights(28, 4, 19); wheel(9f, 3f); wheel(23f, 3f)
            }
        }
        art("Sports Coupe") { s ->
            Garage(s, 3).run {
                backdrop(Backdrop.SUNSET)
                body(rgb(0xE23B3B), 2 to 25, 2 to 20, 8 to 18, 13 to 15, 19 to 15, 24 to 19, 30 to 20, 30 to 25)
                glass(13.6f to 16, 18.6f to 16, 21.8f to 19, 10.5f to 19)
                s.rect(2, 18, 4, 18, shade(rgb(0xE23B3B), 0.6f))
                stripe(21, rgb(0xFFFFFF), 6, 28)
                lights(30, 2, 21); wheel(7.5f, 3f); wheel(24.5f, 3f)
            }
        }
        art("Pickup Truck") { s ->
            Garage(s, 4).run {
                backdrop(Backdrop.DESERT)
                body(rgb(0x2E8B57), 2 to 25, 2 to 18, 16 to 18, 16 to 12, 23 to 12, 26 to 17, 30 to 18, 30 to 25)
                glass(17 to 13, 22.4f to 13, 24.8f to 17, 17 to 17)
                s.hline(2, 15, 18, shade(rgb(0x2E8B57), 0.7f))
                lights(30, 2, 20); wheel(7.5f, 3.5f); wheel(24.5f, 3.5f)
            }
        }
        art("Family Van") { s ->
            Garage(s, 5).run {
                backdrop(Backdrop.DAY)
                body(rgb(0xE9EDF2), 2 to 25, 2 to 12, 4 to 10, 21 to 10, 27 to 16, 30 to 17, 30 to 25)
                glass(4 to 12, 9 to 12, 9 to 16, 4 to 16)
                glass(10 to 12, 15 to 12, 15 to 16, 10 to 16)
                glass(16 to 12, 21 to 12, 25 to 16, 16 to 16)
                stripe(19, rgb(0x46A0E0), 2, 30)
                lights(30, 2, 18); wheel(7.5f, 3f); wheel(24.5f, 3f)
            }
        }
        art("City Bus") { s ->
            Garage(s, 6).run {
                backdrop(Backdrop.CITY)
                body(rgb(0x2F9E8F), 1 to 25, 1 to 9, 30 to 9, 31 to 11, 31 to 25)
                for (x in 2..22 step 5) glass(x to 11, x + 4 to 11, x + 4 to 16, x to 16)
                glass(27 to 11, 30 to 11, 30 to 17, 27 to 17)
                s.rect(23, 11, 25, 24, shade(rgb(0x2F9E8F), 0.7f))
                stripe(19, rgb(0xFFD84D), 1, 22)
                lights(31, 1, 21); wheel(6.5f, 3f); wheel(26.5f, 3f)
            }
        }
        art("Trail Jeep") { s ->
            Garage(s, 7).run {
                backdrop(Backdrop.FOREST)
                body(rgb(0x6B8E3A), 3 to 24, 3 to 15, 8 to 15, 9 to 10, 19 to 10, 19 to 15, 29 to 16, 29 to 24)
                glass(10 to 11, 13 to 11, 13 to 15, 10 to 15)
                glass(14 to 11, 18 to 11, 18 to 15, 14 to 15)
                s.rect(1, 15, 2, 20, TIRE)
                s.hline(3, 29, 19, shade(rgb(0x6B8E3A), 0.7f))
                lights(29, 3, 17); wheel(8.5f, 4f); wheel(23.5f, 4f)
            }
        }
        art("Taxi Cab") { s ->
            Garage(s, 8).run {
                backdrop(Backdrop.NIGHT)
                body(rgb(0xFFC928), 3 to 25, 3 to 19, 9 to 18, 12 to 13, 21 to 13, 25 to 18, 29 to 19, 29 to 25)
                glass(12.8f to 14, 16 to 14, 16 to 18, 10.4f to 18)
                glass(17 to 14, 20.6f to 14, 23.6f to 18, 17 to 18)
                s.rect(14, 11, 18, 12, rgb(0x2B2B2B)); s.rect(15, 11, 17, 11, rgb(0xFFF4B0))
                for (x in 4..28 step 2) s[x, 21] = rgb(0x2B2B2B)
                lights(29, 3, 19); wheel(8.5f, 3f); wheel(23.5f, 3f)
            }
        }
        art("Number 7 Racer") { s ->
            Garage(s, 9).run {
                backdrop(Backdrop.TRACK)
                body(rgb(0x1F6FEB), 2 to 25, 2 to 18, 5 to 18, 10 to 16, 13 to 14, 19 to 14, 23 to 18, 30 to 20, 30 to 25)
                glass(13.6f to 15, 18.6f to 15, 21.4f to 18, 11.5f to 18)
                s.rect(1, 14, 5, 15, rgb(0x1A1A1A)); s.vline(3, 16, 17, rgb(0x1A1A1A))
                s.circle(16f, 21f, 2.5f, rgb(0xFFFFFF))
                s.hline(15, 17, 20, rgb(0x1A1A1A)); s.line(17, 20, 15, 22, rgb(0x1A1A1A))
                lights(30, 2, 21); wheel(7f, 3f); wheel(25f, 3f)
            }
        }
        art("Retro Bubble Car") { s ->
            Garage(s, 10).run {
                backdrop(Backdrop.DAY)
                val c = rgb(0x8FD3C8)
                s.ellipse(16, 20, 13, 6, c)
                s.ellipse(15, 15, 8, 6, c)
                s.rect(3, 20, 29, 24, c)
                glass(10 to 12, 15 to 11, 15 to 16, 8 to 16)
                glass(16 to 11, 20 to 12, 22 to 16, 16 to 16)
                s.hline(4, 28, 19, rgb(0xFFFFFF))
                lights(29, 3, 20); wheel(9f, 3f); wheel(23f, 3f)
            }
        }
        art("Sunny Convertible") { s ->
            Garage(s, 11).run {
                backdrop(Backdrop.BEACH)
                body(rgb(0xFF7F50), 2 to 25, 2 to 18, 22 to 18, 24 to 15, 25 to 18, 30 to 19, 30 to 25)
                glass(22 to 14, 23 to 14, 24 to 17, 22 to 17)
                s.rect(11, 15, 12, 17, rgb(0x7A3E1E)); s.rect(8, 16, 11, 17, rgb(0x7A3E1E))
                s.circle(14f, 15f, 1.6f, rgb(0xF1C27D)); s.hline(13, 15, 13, rgb(0x5A3A22))
                stripe(21, rgb(0xFFF1DE), 3, 29)
                lights(30, 2, 20); wheel(7.5f, 3f); wheel(24.5f, 3f)
            }
        }
        art("Monster Truck") { s ->
            Garage(s, 12).run {
                backdrop(Backdrop.DESERT)
                body(rgb(0x8A2BE2), 3 to 18, 3 to 12, 13 to 12, 15 to 7, 22 to 7, 25 to 11, 29 to 12, 29 to 18)
                glass(15.8f to 8, 21.4f to 8, 23.6f to 11, 15 to 11)
                for (x in 6..26 step 4) s.line(x, 13, x + 2, 16, rgb(0xFFDD33))
                s.rect(8, 18, 24, 19, rgb(0x555555))
                lights(29, 3, 13); wheel(8.5f, 6f, cy = 23f); wheel(23.5f, 6f, cy = 23f)
            }
        }
        art("Ice Cream Van") { s ->
            Garage(s, 13).run {
                backdrop(Backdrop.DAY)
                body(rgb(0xFFF3F8), 2 to 25, 2 to 9, 22 to 9, 23 to 14, 29 to 16, 30 to 25)
                glass(23 to 11, 23 to 15, 27.5f to 15, 23 to 11)
                s.rect(4, 12, 17, 17, rgb(0x9AD8F0))
                s.rect(4, 12, 17, 12, rgb(0xFF8FB1))
                for (x in 4..17 step 3) s.rect(x, 9, x + 1, 10, rgb(0xFF8FB1))
                s.polygon(rgb(0xE8B26A), 9 to 5, 12 to 5, 10.5f to 9)
                s.circle(10.5f, 4.5f, 2f, rgb(0xFFB3C8))
                stripe(20, rgb(0xFF8FB1), 2, 30)
                lights(30, 2, 21); wheel(7.5f, 3f); wheel(24.5f, 3f)
            }
        }
        art("Fire Engine") { s ->
            Garage(s, 14).run {
                backdrop(Backdrop.CITY)
                body(rgb(0xD62828), 1 to 25, 1 to 13, 22 to 13, 22 to 10, 28 to 10, 31 to 15, 31 to 25)
                glass(23 to 11, 27.5f to 11, 29.8f to 15, 23 to 15)
                for (x in 2..20 step 3) s.vline(x, 11, 12, rgb(0xC0C0C0))
                s.hline(1, 21, 10, rgb(0xC0C0C0)); s.hline(1, 21, 12, rgb(0xC0C0C0))
                s.rect(3, 16, 19, 19, shade(rgb(0xD62828), 0.75f))
                s.rect(24, 8, 25, 9, rgb(0x3FA9F5))
                stripe(21, rgb(0xFFFFFF), 1, 31)
                lights(31, 1, 18); wheel(6.5f, 3f); wheel(17.5f, 3f); wheel(26.5f, 3f)
            }
        }
        art("Tow Truck") { s ->
            Garage(s, 15).run {
                backdrop(Backdrop.DAY)
                body(rgb(0xF29E1F), 2 to 25, 2 to 18, 18 to 18, 18 to 11, 25 to 11, 28 to 17, 30 to 18, 30 to 25)
                glass(19 to 12, 24.4f to 12, 26.8f to 17, 19 to 17)
                s.line(4, 11, 15, 17, rgb(0x555555), 2)
                s.vline(4, 11, 20, rgb(0x333333)); s.rect(3, 20, 5, 21, rgb(0x333333))
                s.rect(20, 9, 22, 10, rgb(0xFFD200))
                lights(30, 2, 20); wheel(8f, 3f); wheel(24.5f, 3f)
            }
        }
        art("Stretch Limo") { s ->
            Garage(s, 16).run {
                backdrop(Backdrop.NIGHT)
                body(rgb(0x1E1E28), 0 to 24, 0 to 18, 3 to 17, 5 to 14, 26 to 14, 28 to 17, 31 to 18, 31 to 24)
                for (x in 6..22 step 4) glass(x to 15, x + 3 to 15, x + 3 to 17, x to 17)
                glass(26 to 15, 26.5f to 15, 27.5f to 17, 26 to 17)
                stripe(19, rgb(0xC9CED6), 0, 31)
                lights(31, 0, 19); wheel(5f, 2.5f); wheel(27f, 2.5f)
            }
        }
        art("Camper Van") { s ->
            Garage(s, 17).run {
                backdrop(Backdrop.FOREST)
                body(rgb(0x6EC1E4), 2 to 25, 2 to 10, 25 to 10, 28 to 16, 30 to 17, 30 to 25)
                s.rect(2, 17, 30, 25, rgb(0xFFF6E5))
                s.polygon(rgb(0xFFF6E5), 2 to 17.5f, 30 to 17.5f, 30 to 25, 2 to 25)
                glass(4 to 12, 10 to 12, 10 to 16, 4 to 16)
                glass(19 to 12, 25 to 12, 27.4f to 16, 19 to 16)
                s.rect(12, 12, 16, 22, rgb(0x4E9AC0))
                s.rect(4, 7, 22, 9, rgb(0x9B6B3F))
                lights(30, 2, 18); wheel(7.5f, 3f); wheel(24.5f, 3f)
            }
        }
        art("Go-Kart") { s ->
            Garage(s, 18).run {
                backdrop(Backdrop.TRACK)
                body(rgb(0x2ECC71), 5 to 25, 5 to 22, 22 to 22, 27 to 23, 27 to 25)
                s.rect(10, 17, 12, 21, rgb(0x333333))
                s.circle(15f, 13f, 3.5f, rgb(0xFF4757))
                s.rect(14, 12, 18, 14, rgb(0xAEE3FF))
                s.rect(13, 17, 18, 21, rgb(0x2B59C3))
                s.line(18, 18, 22, 20, rgb(0xF1C27D))
                s.line(21, 19, 22, 17, rgb(0x333333))
                wheel(8f, 3f); wheel(24f, 3f)
            }
        }
        art("Formula Racer") { s ->
            Garage(s, 19).run {
                backdrop(Backdrop.TRACK)
                body(rgb(0xF0F0F0), 3 to 24, 3 to 21, 12 to 20, 14 to 17, 19 to 17, 21 to 20, 31 to 22, 31 to 24)
                s.rect(1, 15, 5, 16, rgb(0xE84118)); s.vline(3, 17, 20, rgb(0x333333))
                s.circle(16.5f, 16.5f, 2f, rgb(0xFFC312))
                s.hline(15, 18, 16, rgb(0x333333))
                stripe(22, rgb(0xE84118), 3, 31)
                wheel(6f, 3.5f); wheel(26f, 3.5f)
            }
        }
        art("Delivery Truck") { s ->
            Garage(s, 20).run {
                backdrop(Backdrop.CITY)
                body(rgb(0xFFFFFF), 1 to 25, 1 to 8, 21 to 8, 21 to 25)
                body(rgb(0x3867D6), 21 to 25, 21 to 12, 27 to 12, 30 to 17, 31 to 18, 31 to 25)
                glass(22 to 13, 26.4f to 13, 28.8f to 17, 22 to 17)
                s.rect(4, 11, 17, 18, rgb(0xFFB142))
                s.rect(6, 13, 15, 16, rgb(0xFFFFFF))
                s.hline(1, 21, 21, rgb(0xC9CED6))
                lights(31, 1, 20); wheel(6f, 3f); wheel(16f, 3f); wheel(26f, 3f)
            }
        }
        art("Farm Tractor") { s ->
            Garage(s, 21).run {
                backdrop(Backdrop.FARM)
                body(rgb(0x2E9E44), 3 to 22, 3 to 7, 13 to 7, 13 to 15, 28 to 15, 29 to 22)
                glass(5 to 9, 11 to 9, 11 to 14, 5 to 14)
                s.vline(24, 9, 14, rgb(0x333333)); s.rect(23, 8, 25, 8, rgb(0x333333))
                s.rect(16, 17, 26, 18, rgb(0xFFD32A))
                wheel(8f, 6f, cy = 22f); wheel(25f, 3.5f, cy = 24.5f)
            }
        }
        art("School Bus") { s ->
            Garage(s, 22).run {
                backdrop(Backdrop.DAY)
                body(rgb(0xFFB800), 1 to 25, 1 to 9, 25 to 9, 25 to 14, 30 to 15, 31 to 17, 31 to 25)
                for (x in 2..20 step 5) glass(x to 11, x + 3 to 11, x + 3 to 15, x to 15)
                glass(22 to 11, 24 to 11, 24 to 15, 22 to 15)
                s.hline(1, 31, 18, rgb(0x2B2B2B)); s.hline(1, 31, 20, rgb(0x2B2B2B))
                s.rect(26, 13, 27, 14, rgb(0xE84118))
                lights(31, 1, 21); wheel(6.5f, 3f); wheel(25.5f, 3f)
            }
        }
        art("Mud Rally Car") { s ->
            Garage(s, 23).run {
                backdrop(Backdrop.FOREST)
                body(rgb(0x0FB9B1), 3 to 24, 3 to 18, 7 to 17, 10 to 13, 20 to 13, 24 to 17, 29 to 18, 29 to 24)
                glass(10.8f to 14, 15 to 14, 15 to 17, 8.6f to 17)
                glass(16 to 14, 19.6f to 14, 22.4f to 17, 16 to 17)
                for (x in 4..28 step 3) s[x, 22 - x % 2] = rgb(0x7A5230)
                s.circle(12f, 20f, 1.8f, rgb(0xFFFFFF)); s[12, 20] = rgb(0xE84118)
                s.rect(12, 11, 17, 12, rgb(0x2B2B2B))
                lights(29, 3, 19); wheel(8.5f, 3.5f); wheel(23.5f, 3.5f)
            }
        }
        art("Muscle Car") { s ->
            Garage(s, 24).run {
                backdrop(Backdrop.SUNSET)
                body(rgb(0x3A3F58), 2 to 25, 2 to 18, 11 to 17, 14 to 13, 21 to 13, 23 to 17, 30 to 18, 30 to 25)
                glass(14.8f to 14, 20.4f to 14, 21.6f to 17, 12.6f to 17)
                s.rect(3, 19, 29, 19, rgb(0xFF5E3A)); s.rect(3, 21, 29, 21, rgb(0xFF5E3A))
                s.rect(24, 16, 27, 17, shade(rgb(0x3A3F58), 0.7f))
                lights(30, 2, 20); wheel(7.5f, 3.5f); wheel(24.5f, 3.5f)
            }
        }
        art("Electric Mini") { s ->
            Garage(s, 25).run {
                backdrop(Backdrop.CITY)
                body(rgb(0xA3E048), 7 to 25, 7 to 13, 10 to 10, 19 to 10, 23 to 15, 25 to 17, 25 to 25)
                glass(10.5f to 11, 18.6f to 11, 22.4f to 15, 8 to 15)
                s.vline(14, 11, 15, rgb(0xA3E048))
                s.rect(10, 19, 12, 20, rgb(0xFFFFFF)); s[11, 18] = rgb(0xFFFFFF); s[11, 21] = rgb(0xFFFFFF)
                lights(25, 7, 18); wheel(11f, 2.5f); wheel(21f, 2.5f)
            }
        }
    }.build()

    private enum class Backdrop { DAY, CITY, SUNSET, DESERT, FOREST, NIGHT, TRACK, BEACH, FARM }

    private class Garage(val s: Raster, seed: Int) {
        private val random = Random(seed * 104729)

        fun backdrop(kind: Backdrop) {
            val (top, bottom) = when (kind) {
                Backdrop.DAY, Backdrop.FARM -> rgb(0x7CC6F6) to rgb(0xD8F0FF)
                Backdrop.CITY -> rgb(0xA9C9E8) to rgb(0xE8F1FA)
                Backdrop.SUNSET -> rgb(0xFF7E67) to rgb(0xFFD59A)
                Backdrop.DESERT -> rgb(0xF6B26B) to rgb(0xFFE8B8)
                Backdrop.FOREST -> rgb(0x98D7C2) to rgb(0xE3F6EE)
                Backdrop.NIGHT -> rgb(0x101B3D) to rgb(0x30437A)
                Backdrop.TRACK -> rgb(0x8BD3F7) to rgb(0xE6F7FF)
                Backdrop.BEACH -> rgb(0x45B8F2) to rgb(0xB9EBFF)
            }
            for (y in 0..21) s.hline(0, 31, y, mix(top, bottom, (y / 6).coerceAtMost(3) / 3f))
            when (kind) {
                Backdrop.CITY -> {
                    var x = 0
                    while (x < 32) {
                        val w = random.nextInt(3, 6); val h = random.nextInt(6, 13)
                        s.rect(x, 21 - h, x + w - 1, 21, if ((x / 3) % 2 == 0) rgb(0xB8C4D6) else rgb(0x9FAFC6))
                        x += w + 1
                    }
                }
                Backdrop.NIGHT -> {
                    repeat(16) { s[random.nextInt(32), random.nextInt(14)] = rgb(0xFFF7C2) }
                    s.circle(26, 5, 2.5f, rgb(0xFDF6D8))
                }
                Backdrop.SUNSET -> s.circle(24, 14, 5f, rgb(0xFFE29A))
                Backdrop.DESERT -> {
                    s.polygon(rgb(0xE3A15C), 0 to 22, 0 to 17, 8 to 15, 16 to 18, 25 to 14, 32 to 17, 32 to 22)
                }
                Backdrop.FOREST -> for (x in 0..31 step 5) {
                    s.polygon(rgb(0x3E8E63), x - 2 to 22, x + 0.5f to 13 + (x % 3), x + 3 to 22)
                }
                Backdrop.TRACK -> for (x in 0..31) s.vline(x, 19, 21, if ((x / 2) % 2 == 0) rgb(0xE84118) else rgb(0xFFFFFF))
                Backdrop.BEACH -> s.rect(0, 18, 31, 21, rgb(0x1E9BD7))
                Backdrop.FARM -> s.polygon(rgb(0x8CCB5E), 0 to 22, 0 to 16, 12 to 18, 22 to 15, 32 to 17, 32 to 22)
                Backdrop.DAY -> {
                    s.ellipse(7, 5, 4, 1.8f, rgb(0xFFFFFF)); s.ellipse(25, 8, 3.5f, 1.5f, rgb(0xFFFFFF))
                }
            }
            val road = when (kind) {
                Backdrop.DESERT -> rgb(0xD9A066)
                Backdrop.BEACH -> rgb(0xF6D58E)
                Backdrop.FARM, Backdrop.FOREST -> rgb(0x7D5A3A)
                else -> rgb(0x5A5F6B)
            }
            s.rect(0, 22, 31, 31, road)
            s.rect(0, 22, 31, 22, shade(road, 1.15f))
            if (road == rgb(0x5A5F6B)) for (x in 0..31 step 6) s.hline(x, x + 2, 30, rgb(0xF5F5F5))
        }

        fun body(color: Int, vararg points: Pair<Number, Number>) {
            s.polygon(color, *points)
        }

        fun glass(vararg points: Pair<Number, Number>) {
            s.polygon(GLASS, *points)
        }

        fun stripe(y: Int, color: Int, x0: Int, x1: Int) = s.hline(x0, x1, y, color)

        /** Headlight at the front (right) and tail light at the back (left). */
        fun lights(frontX: Int, rearX: Int, y: Int) {
            s.rect(frontX - 1, y, frontX, y + 1, rgb(0xFFF3A6))
            s.rect(rearX, y, rearX + 1, y + 1, rgb(0xE84118))
        }

        fun wheel(cx: Float, r: Float, cy: Float = 28f - r) {
            s.circle(cx, cy, r + 0.7f, rgb(0x1A1A20))
            s.circle(cx, cy, r, TIRE)
            s.circle(cx, cy, r * 0.5f, RIM)
        }
    }
}
