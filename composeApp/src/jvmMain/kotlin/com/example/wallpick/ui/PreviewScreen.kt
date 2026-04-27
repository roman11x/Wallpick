package com.example.wallpick.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.wallpick.data.Wallpaper
import com.example.wallpick.data.WallhavenApi
import com.example.wallpick.data.WallpaperSetter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    wallpaper: Wallpaper,
    onBack: () -> Unit,
    onColorClick: (String) -> Unit
) {
    var isDownloading by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(wallpaper.resolution) },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("← Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = wallpaper.path,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Spacer(Modifier.height(12.dp))

            // colour swatches
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                wallpaper.colors.forEach { hex ->
                    val color = runCatching {
                        val h = hex.trimStart('#').padStart(6, '0')
                        Color(0xFF000000 or h.toLong(16))
                    }.getOrDefault(Color.Gray)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color)
                            .clickable { onColorClick(hex) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            statusMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = {
                    scope.launch {
                        isDownloading = true
                        statusMessage = "Downloading…"
                        runCatching {
                            val file = withContext(Dispatchers.IO) {
                                WallhavenApi.downloadWallpaper(wallpaper)
                            }
                            withContext(Dispatchers.IO) {
                                WallpaperSetter.set(file.absolutePath)
                            }
                            statusMessage = "Wallpaper set!"
                        }.onFailure {
                            statusMessage = "Failed: ${it.message}"
                        }
                        isDownloading = false
                    }
                },
                enabled = !isDownloading,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                if (isDownloading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text("Set Wallpaper")
            }
        }
    }
}
