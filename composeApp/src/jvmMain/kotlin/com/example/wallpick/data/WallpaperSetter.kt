package com.example.wallpick.data

import java.io.File

object WallpaperSetter {
    fun set(absolutePath: String) {
        val os = System.getProperty("os.name").lowercase()
        val de = System.getenv("XDG_CURRENT_DESKTOP")?.lowercase() ?: ""
        when {
            "gnome" in de -> setGnome(absolutePath)
            "windows" in os -> setWindows(absolutePath)
        }
    }

    private fun setGnome(path: String) {
        val uri = "file://$path"
        ProcessBuilder("gsettings", "set", "org.gnome.desktop.background", "picture-uri", uri)
            .start().waitFor()
        // cover both light and dark mode
        ProcessBuilder("gsettings", "set", "org.gnome.desktop.background", "picture-uri-dark", uri)
            .start().waitFor()
    }

    private fun setWindows(path: String) {
        val escaped = path.replace("'", "''")
        val ps = """
Add-Type @"
using System.Runtime.InteropServices;
public class WP {
  [DllImport("user32.dll", CharSet=CharSet.Auto)]
  public static extern int SystemParametersInfo(int a, int b, string c, int d);
}
"@
[WP]::SystemParametersInfo(0x0014, 0, '$escaped', 0x03)
        """.trimIndent()
        ProcessBuilder("powershell", "-command", ps).start().waitFor()
    }

    fun saveDir(): File {
        val os = System.getProperty("os.name").lowercase()
        val home = System.getProperty("user.home")
        val dir = if ("windows" in os) {
            File(home, "Pictures/wallpick")
        } else {
            File(home, ".local/share/wallpick")
        }
        dir.mkdirs()
        return dir
    }
}
