package com.example.wallpick.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilterBar(
    resolution: String,
    onResolutionChange: (String) -> Unit,
    colorFilter: String,
    onColorFilterChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = resolution,
            onValueChange = onResolutionChange,
            label = { Text("Min resolution") },
            placeholder = { Text("1920x1080") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = colorFilter,
            onValueChange = onColorFilterChange,
            label = { Text("Colour filter") },
            placeholder = { Text("#6650A4") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
        )
    }
}
