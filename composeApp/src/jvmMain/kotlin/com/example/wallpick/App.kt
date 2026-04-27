package com.example.wallpick

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import coil3.ImageLoader
import coil3.compose.LocalPlatformContext
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.example.wallpick.components.WaveOverlay
import com.example.wallpick.data.Wallpaper
import com.example.wallpick.settings.SettingsRepository
import com.example.wallpick.ui.PreviewScreen
import com.example.wallpick.ui.SearchScreen
import com.materialkolor.DynamicMaterialTheme

val LocalWallpickImageLoader = staticCompositionLocalOf<ImageLoader> {
    error("No ImageLoader provided")
}

@Composable
fun App() {
    val context = LocalPlatformContext.current
    val imageLoader = remember(context) {
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory()) }
            .build()
    }

    val settings = remember { SettingsRepository.load() }
    var seedColor by remember { mutableStateOf(Color(0xFF6650A4)) }
    var waveKey by remember { mutableIntStateOf(0) }
    var waveOrigin by remember { mutableStateOf(Offset.Zero) }
    var waveColor by remember { mutableStateOf(Color(0xFF6650A4)) }
    // null = showing SearchScreen; non-null = showing PreviewScreen on top
    var previewWallpaper by remember { mutableStateOf<Wallpaper?>(null) }
    var pendingColorSearch by remember { mutableStateOf("") }

    CompositionLocalProvider(LocalWallpickImageLoader provides imageLoader) {
        DynamicMaterialTheme(seedColor = seedColor, animate = true) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Always in composition — preserves search results across navigation
                    SearchScreen(
                        settings = settings,
                        pendingColorSearch = pendingColorSearch,
                        onColorSearchConsumed = { pendingColorSearch = "" },
                        onWallpaperHover = { wallpaper, offset ->
                            wallpaper.colors.firstOrNull()?.toComposeColor()?.let { newColor ->
                                seedColor = newColor
                                waveColor = newColor
                                waveOrigin = offset
                                waveKey++
                            }
                        },
                        onWallpaperClick = { wallpaper ->
                            previewWallpaper = wallpaper
                        }
                    )

                    // Overlays the grid without removing it from composition
                    previewWallpaper?.let { wallpaper ->
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            PreviewScreen(
                                wallpaper = wallpaper,
                                onBack = { previewWallpaper = null },
                                onColorClick = { hex ->
                                    pendingColorSearch = hex
                                    previewWallpaper = null
                                }
                            )
                        }
                    }

                    // Wave ripple on top of everything — transparent to pointer events
                    WaveOverlay(waveKey = waveKey, origin = waveOrigin, color = waveColor)
                }
            }
        }
    }
}

fun String.toComposeColor(): Color = runCatching {
    val hex = trimStart('#').padStart(6, '0')
    Color(0xFF000000 or hex.toLong(16))
}.getOrDefault(Color(0xFF6650A4))
