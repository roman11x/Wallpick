package com.example.wallpick.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch

@Composable
fun WaveOverlay(waveKey: Int, origin: Offset, color: Color) {
    val radius = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(waveKey) {
        if (waveKey == 0) return@LaunchedEffect
        radius.snapTo(0f)
        alpha.snapTo(0.5f)
        launch {
            radius.animateTo(
                targetValue = 2800f,
                animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 700)
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val r = radius.value
        val a = alpha.value
        if (a > 0f && r > 0f) {
            drawCircle(color = color.copy(alpha = a), radius = r, center = origin)
        }
    }
}
