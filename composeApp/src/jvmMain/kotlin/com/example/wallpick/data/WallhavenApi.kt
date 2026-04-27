package com.example.wallpick.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.io.File

object WallhavenApi {
    private val json = Json { ignoreUnknownKeys = true }

    val client = HttpClient(Java) {
        install(ContentNegotiation) { json(json) }
    }

    private const val BASE_URL = "https://wallhaven.cc/api/v1"

    suspend fun search(
        query: String = "",
        atleast: String = "",
        resolutions: String = "",
        colors: String = "",
        categories: String = "111",
        page: Int = 1,
        apiKey: String = ""
    ): WallhavenResponse {
        return client.get("$BASE_URL/search") {
            if (query.isNotBlank()) parameter("q", query)
            if (atleast.isNotBlank()) parameter("atleast", atleast)
            if (resolutions.isNotBlank()) parameter("resolutions", resolutions)
            if (colors.isNotBlank()) parameter("colors", colors)
            if (categories != "111") parameter("categories", categories)
            if (page > 1) parameter("page", page)
            if (apiKey.isNotBlank()) parameter("apikey", apiKey)
        }.body()
    }

    suspend fun downloadWallpaper(wallpaper: Wallpaper): File {
        val dir = WallpaperSetter.saveDir()
        val ext = wallpaper.path.substringAfterLast('.', "jpg")
        val file = File(dir, "${wallpaper.id}.$ext")
        if (!file.exists()) {
            val bytes: ByteArray = client.get(wallpaper.path).body()
            file.writeBytes(bytes)
        }
        return file
    }
}
