package com.avgogames.pixelart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.avgogames.pixelart.ui.navigation.PixelArtNavHost
import com.avgogames.pixelart.ui.theme.PixelPaintTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            PixelPaintTheme {
                PixelArtNavHost()
            }
        }
    }
}
