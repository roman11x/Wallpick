package com.example.wallpick.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.wallpick.LocalWallpickImageLoader
import com.example.wallpick.data.Wallpaper

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WallpaperCard(
    wallpaper: Wallpaper,
    onHover: (Wallpaper) -> Unit,
    onClick: (Wallpaper) -> Unit
) {
    val imageLoader = LocalWallpickImageLoader.current

    Card(
        modifier = Modifier
            .aspectRatio(16f / 9f)
            .onPointerEvent(PointerEventType.Enter) { onHover(wallpaper) }
            .clickable { onClick(wallpaper) },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        AsyncImage(
            model = wallpaper.thumbs.small,
            contentDescription = null,
            imageLoader = imageLoader,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}
