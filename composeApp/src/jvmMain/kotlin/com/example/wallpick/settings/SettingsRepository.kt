package com.example.wallpick.settings

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class Settings(
    val apiKey: String = "",
    val defaultResolution: String = "",
    val defaultPurity: String = "100"
)

object SettingsRepository {
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    private val configFile: File by lazy {
        val os = System.getProperty("os.name").lowercase()
        val home = System.getProperty("user.home")
        val dir = when {
            "windows" in os -> File(System.getenv("APPDATA") ?: "$home/AppData/Roaming", "wallpick")
            else -> File(home, ".config/wallpick")
        }
        dir.mkdirs()
        File(dir, "config.json")
    }

    fun load(): Settings {
        if (!configFile.exists()) return Settings()
        return runCatching {
            json.decodeFromString<Settings>(configFile.readText())
        }.getOrDefault(Settings())
    }

    fun save(settings: Settings) {
        runCatching { configFile.writeText(json.encodeToString(settings)) }
    }
}
