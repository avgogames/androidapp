package com.avgogames.pixelart.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.avgogames.pixelart.ui.coloring.ColoringRoute
import com.avgogames.pixelart.ui.gallery.GalleryRoute
import com.avgogames.pixelart.ui.importer.ImportRoute

/** Route strings live here so screens never construct paths themselves. */
object Routes {
    const val GALLERY = "gallery"
    const val IMPORT = "import"
    const val ART_ID = "artId"
    const val COLORING = "coloring/{$ART_ID}"

    fun coloring(artId: String) = "coloring/$artId"
}

@Composable
fun PixelArtNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.GALLERY, modifier = modifier) {
        composable(Routes.GALLERY) {
            GalleryRoute(
                onOpenArt = { navController.navigate(Routes.coloring(it)) },
                onImport = { navController.navigate(Routes.IMPORT) },
            )
        }
        composable(
            route = Routes.COLORING,
            arguments = listOf(navArgument(Routes.ART_ID) { type = NavType.StringType }),
        ) {
            ColoringRoute(onBack = { navController.popBackStack() })
        }
        composable(Routes.IMPORT) {
            ImportRoute(
                onBack = { navController.popBackStack() },
                onCreated = { artId ->
                    navController.navigate(Routes.coloring(artId)) {
                        popUpTo(Routes.GALLERY)
                    }
                },
            )
        }
    }
}
