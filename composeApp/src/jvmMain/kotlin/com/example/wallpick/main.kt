package com.example.wallpick

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.example.wallpick.data.WallhavenApi

fun main() {
    SingletonImageLoader.setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory(httpClient = WallhavenApi.client)) }
            .build()
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "wallpick",
        ) {
            App()
        }
    }
}
