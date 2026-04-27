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
        ProcessBuilder("gsettings", "set", "org.gnome.desktop.background", "picture-uri-dark", uri)
            .start().waitFor()
    }

    private fun setWindows(path: String) {
        // Escape single quotes for PowerShell single-quoted string
        val escaped = path.replace("'", "''")
        // Write a .ps1 file to avoid -Command quoting issues with Add-Type here-strings
        val script = """
Add-Type -Name WallpickWP -Namespace WallpickNS -ErrorAction SilentlyContinue -MemberDefinition '[DllImport("user32.dll",CharSet=CharSet.Auto)]public static extern int SystemParametersInfo(int a,int b,string c,int d);'
[WallpickNS.WallpickWP]::SystemParametersInfo(20, 0, '$escaped', 3)
        """.trimIndent()
        val temp = File(System.getProperty("java.io.tmpdir"), "wallpick_set.ps1")
        temp.writeText(script, Charsets.UTF_8)
        ProcessBuilder(
            "powershell", "-NoProfile", "-NonInteractive",
            "-ExecutionPolicy", "Bypass",
            "-File", temp.absolutePath
        ).start().waitFor()
        temp.delete()
    }

    fun saveDir(): File {
        val os = System.getProperty("os.name").lowercase()
        val home = System.getProperty("user.home")
        val dir = if ("windows" in os) File(home, "Pictures/wallpick") else File(home, ".local/share/wallpick")
        dir.mkdirs()
        return dir
    }
}
