# wallpick

A Compose Multiplatform desktop app for browsing [Wallhaven](https://wallhaven.cc) wallpapers and setting them as your system wallpaper. Built as a companion to [holefetch](https://github.com/roman11x/holefetch) — a fastfetch-like terminal tool that colours its output to match the dominant colours of your current wallpaper.

The two together make for a seamless demo: wallpick changes the wallpaper while holefetch updates its colour scheme in real-time.

> **Built entirely with [Claude Code](https://claude.ai/code)**

## Demo
<video src="https://github.com/user-attachments/assets/2ca5faa3-7f75-4826-aa41-8ea961a8fe28" controls width="100%"></video>


## Features

- Browse and search Wallhaven wallpapers with resolution and colour filters
- Material You live theming — the UI recolours as you hover thumbnails
- One-click wallpaper setting on GNOME and Windows 11

## Install

**Fedora / RPM-based Linux**
```
sudo dnf install ./wallpick-*.rpm
```

**Windows 11**

Run the `.msi` installer from the [releases page](https://github.com/roman11x/Wallpick/releases).

## Platforms

| Platform | Status |
|----------|--------|
| Fedora / GNOME | ✓ |
| Windows 11 | ✓ |
| KDE / macOS | Not supported |
