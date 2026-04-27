package com.example.wallpick

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.wallpick.data.Wallpaper
import com.example.wallpick.settings.Settings
import com.example.wallpick.settings.SettingsRepository
import com.example.wallpick.ui.PreviewScreen
import com.example.wallpick.ui.SearchScreen
import com.materialkolor.DynamicMaterialTheme

sealed class Screen {
    data class Search(val initialColor: String = "", val initialQuery: String = "") : Screen()
    data class Preview(val wallpaper: Wallpaper) : Screen()
}

@Composable
fun App() {
    val settings = remember { SettingsRepository.load() }
    var seedColor by remember { mutableStateOf(Color(0xFF6650A4)) }
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Search()) }

    DynamicMaterialTheme(seedColor = seedColor, animate = true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (val screen = currentScreen) {
                is Screen.Search -> SearchScreen(
                    initialQuery = screen.initialQuery,
                    initialColor = screen.initialColor,
                    settings = settings,
                    onWallpaperHover = { wallpaper ->
                        wallpaper.colors.firstOrNull()?.toComposeColor()?.let { seedColor = it }
                    },
                    onWallpaperClick = { wallpaper ->
                        currentScreen = Screen.Preview(wallpaper)
                    }
                )
                is Screen.Preview -> PreviewScreen(
                    wallpaper = screen.wallpaper,
                    onBack = { currentScreen = Screen.Search() },
                    onColorClick = { hex ->
                        currentScreen = Screen.Search(initialColor = hex)
                    }
                )
            }
        }
    }
}

fun String.toComposeColor(): Color = runCatching {
    val hex = trimStart('#').padStart(6, '0')
    Color(0xFF000000 or hex.toLong(16))
}.getOrDefault(Color(0xFF6650A4))
