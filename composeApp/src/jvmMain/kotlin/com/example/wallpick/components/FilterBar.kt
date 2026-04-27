package com.example.wallpick.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBar(state: FilterState, onStateChange: (FilterState) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // resolution mode toggle
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

            OutlinedTextField(
                value = state.colorFilter,
                onValueChange = { onStateChange(state.copy(colorFilter = it)) },
                label = { Text("Colour") },
                placeholder = { Text("#6650A4") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = state.generalEnabled,
                onClick = { onStateChange(state.copy(generalEnabled = !state.generalEnabled)) },
                label = { Text("General") }
            )
            FilterChip(
                selected = state.animeEnabled,
                onClick = { onStateChange(state.copy(animeEnabled = !state.animeEnabled)) },
                label = { Text("Anime") }
            )
            FilterChip(
                selected = state.peopleEnabled,
                onClick = { onStateChange(state.copy(peopleEnabled = !state.peopleEnabled)) },
                label = { Text("People") }
            )
        }
    }
}
