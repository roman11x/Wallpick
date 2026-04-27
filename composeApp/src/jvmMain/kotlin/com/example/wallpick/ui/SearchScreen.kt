package com.example.wallpick.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wallpick.components.FilterBar
import com.example.wallpick.components.WallpaperCard
import com.example.wallpick.data.Wallpaper
import com.example.wallpick.data.WallhavenApi
import com.example.wallpick.settings.Settings
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    initialQuery: String = "",
    initialColor: String = "",
    settings: Settings,
    onWallpaperHover: (Wallpaper) -> Unit,
    onWallpaperClick: (Wallpaper) -> Unit
) {
    val defaultResolution = remember {
        settings.defaultResolution.ifBlank {
            runCatching {
                val screen = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .defaultScreenDevice.displayMode
                "${screen.width}x${screen.height}"
            }.getOrDefault("1920x1080")
        }
    }

    var query by remember { mutableStateOf(initialQuery) }
    var resolution by remember { mutableStateOf(defaultResolution) }
    var colorFilter by remember { mutableStateOf(initialColor) }
    var wallpapers by remember { mutableStateOf<List<Wallpaper>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var page by remember { mutableIntStateOf(1) }
    var hasMore by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun doSearch(reset: Boolean = true) {
        scope.launch {
            if (reset) {
                page = 1
                wallpapers = emptyList()
            }
            isLoading = true
            error = null
            runCatching {
                WallhavenApi.search(
                    query = query,
                    atleast = resolution,
                    colors = colorFilter.trimStart('#'),
                    page = page,
                    apiKey = settings.apiKey
                )
            }.onSuccess { response ->
                wallpapers = if (reset) response.data else wallpapers + response.data
                hasMore = response.meta.currentPage < response.meta.lastPage
            }.onFailure {
                error = it.message ?: "Search failed"
            }
            isLoading = false
        }
    }

    // auto-search if arriving from a colour swatch tap
    LaunchedEffect(initialColor) {
        if (initialColor.isNotBlank()) doSearch()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // search bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search wallpapers") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = { doSearch() },
                enabled = !isLoading
            ) {
                Text("Search")
            }
        }

        FilterBar(
            resolution = resolution,
            onResolutionChange = { resolution = it },
            colorFilter = colorFilter,
            onColorFilterChange = { colorFilter = it }
        )

        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        if (isLoading && wallpapers.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (wallpapers.isEmpty() && !isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Search for wallpapers above",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(220.dp),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(wallpapers, key = { it.id }) { wallpaper ->
                    WallpaperCard(
                        wallpaper = wallpaper,
                        onHover = onWallpaperHover,
                        onClick = onWallpaperClick
                    )
                }

                if (hasMore) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator()
                            } else {
                                Button(onClick = {
                                    page++
                                    doSearch(reset = false)
                                }) {
                                    Text("Load more")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
