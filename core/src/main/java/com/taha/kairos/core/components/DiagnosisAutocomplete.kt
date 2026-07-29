package com.taha.kairos.core.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.taha.kairos.core.model.Diagnosis

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiagnosisAutocomplete(
    query: String,
    suggestions: List<Diagnosis>,
    selected: List<String>,
    onQueryChange: (String) -> Unit,
    onSelect: (String) -> Unit,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        // Selected chips
        if (selected.isNotEmpty()) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
            ) {
                selected.forEach { name ->
                    InputChip(
                        selected = true,
                        onClick = {},
                        label = { Text(name) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove $name",
                                modifier = Modifier.clickable { onRemove(name) },
                            )
                        },
                        modifier = Modifier.padding(end = 6.dp, bottom = 4.dp),
                    )
                }
            }
        }

        // Input field + inline suggestions
        Column {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                label = { Text("Diagnoses") },
                placeholder = { Text("Type to search or add…") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
            )

            if (suggestions.isNotEmpty() || query.isNotBlank()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    shape = MaterialTheme.shapes.small,
                    tonalElevation = 0.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp),
                    ) {
                        suggestions.forEach { d ->
                            DiagnosisSuggestionRow(
                                text = d.name,
                                onClick = { onSelect(d.name) },
                            )
                        }
                        // Allow creating new tag if query doesn't match existing exactly
                        if (query.isNotBlank() && suggestions.none { it.name.equals(query, ignoreCase = true) }) {
                            DiagnosisSuggestionRow(
                                text = "Add \"$query\"",
                                color = MaterialTheme.colorScheme.primary,
                                onClick = { onSelect(query) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DiagnosisSuggestionRow(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = color,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    )
}
