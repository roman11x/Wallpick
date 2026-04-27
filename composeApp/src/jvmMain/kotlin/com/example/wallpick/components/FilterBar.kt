package com.example.wallpick.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class FilterState(
    val resolution: String = "",
    val useExactResolution: Boolean = false,
    val colorFilter: String = "",
    val generalEnabled: Boolean = true,
    val animeEnabled: Boolean = true,
    val peopleEnabled: Boolean = false
) {
    val categoriesString: String
        get() = "${b(generalEnabled)}${b(animeEnabled)}${b(peopleEnabled)}"

    private fun b(v: Boolean) = if (v) "1" else "0"
}

// Wallhaven's colour filter accepts a single hex value.
// These presets map to colours that produce good search results.
private val colorPresets = listOf(
    "" to "Any colour",
    "#cc0000" to "Red",
    "#cc4400" to "Orange-red",
    "#cc8800" to "Orange",
    "#cccc00" to "Yellow",
    "#66cc00" to "Yellow-green",
    "#00aa00" to "Green",
    "#00aaaa" to "Teal",
    "#0066cc" to "Blue",
    "#0000cc" to "Dark blue",
    "#6600cc" to "Purple",
    "#cc00cc" to "Magenta",
    "#cc0066" to "Pink",
    "#884411" to "Brown",
    "#888888" to "Gray",
    "#cccccc" to "Light gray",
    "#ffffff" to "White",
    "#000000" to "Black",
)

private fun parseColor(hex: String): Color? = runCatching {
    val h = hex.trimStart('#')
    if (h.length != 6) return null
    Color(0xFF000000L or h.toLong(16))
}.getOrNull()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColourComboBox(value: String, onValueChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val swatchColor = remember(value) { parseColor(value) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("Colour") },
            placeholder = { Text("Any") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            leadingIcon = {
                val c = swatchColor
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(c ?: Color.Transparent)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                )
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            colorPresets.forEach { (hex, name) ->
                val itemColor = if (hex.isNotEmpty()) parseColor(hex) else null
                DropdownMenuItem(
                    text = { Text(name) },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(itemColor ?: Color.Transparent)
                                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        )
                    },
                    onClick = {
                        onValueChange(hex)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(state: FilterState, onStateChange: (FilterState) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    val label = buildList {
        if (state.generalEnabled) add("General")
        if (state.animeEnabled) add("Anime")
        if (state.peopleEnabled) add("People")
    }.let { parts ->
        when {
            parts.isEmpty() || parts.size == 3 -> "All categories"
            else -> parts.joinToString(", ")
        }
    }

    // The Wallhaven API has exactly 3 fixed categories — General, Anime, People.
    // They are documented constants, not fetched from the API.
    val categories = listOf(
        Triple("General", state.generalEnabled) { onStateChange(state.copy(generalEnabled = !state.generalEnabled)) },
        Triple("Anime",   state.animeEnabled)   { onStateChange(state.copy(animeEnabled   = !state.animeEnabled)) },
        Triple("People",  state.peopleEnabled)  { onStateChange(state.copy(peopleEnabled  = !state.peopleEnabled)) }
    )

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = label,
            onValueChange = {},
            readOnly = true,
            label = { Text("Categories") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            categories.forEach { (name, checked, toggle) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    leadingIcon = {
                        Checkbox(checked = checked, onCheckedChange = null)
                    },
                    onClick = { toggle() }
                    // intentionally not closing so multiple categories can be toggled
                )
            }
        }
    }
}

@Composable
fun FilterBar(state: FilterState, onStateChange: (FilterState) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Resolution row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconToggleButton(
                checked = state.useExactResolution,
                onCheckedChange = { onStateChange(state.copy(useExactResolution = it)) }
            ) {
                Text(
                    text = if (state.useExactResolution) "=" else "≥",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            OutlinedTextField(
                value = state.resolution,
                onValueChange = { onStateChange(state.copy(resolution = it)) },
                label = { Text(if (state.useExactResolution) "Exact resolution" else "Min resolution") },
                placeholder = { Text("1920x1080") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            )
        }

        // Colour + Category row
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.weight(1f)) {
                ColourComboBox(
                    value = state.colorFilter,
                    onValueChange = { onStateChange(state.copy(colorFilter = it)) }
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                CategoryDropdown(state = state, onStateChange = onStateChange)
            }
        }
    }
}
