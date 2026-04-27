package com.example.wallpick.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WallhavenResponse(
    val data: List<Wallpaper>,
    val meta: Meta
)

@Serializable
data class Wallpaper(
    val id: String,
    val path: String,
    val thumbs: Thumbs,
    val colors: List<String>,
    val resolution: String,
    @SerialName("file_size") val fileSize: Long = 0
)

@Serializable
data class Thumbs(
    val small: String,
    val large: String = "",
    val original: String = ""
)

@Serializable
data class Meta(
    @SerialName("current_page") val currentPage: Int,
    @SerialName("last_page") val lastPage: Int,
    val total: Int
)
