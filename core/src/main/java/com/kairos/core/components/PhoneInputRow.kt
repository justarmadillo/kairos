package com.kairos.core.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kairos.core.model.PatientPhone

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PhoneInputSection(
    phones: List<PatientPhone>,
    onAdd: (number: String, label: String?) -> Unit,
    onRemove: (PatientPhone) -> Unit,
    modifier: Modifier = Modifier,
) {
    var draft by remember { mutableStateOf("") }

    androidx.compose.foundation.layout.Column(modifier = modifier) {
        // Existing phones as chips
        if (phones.isNotEmpty()) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
            ) {
                phones.forEach { phone ->
                    InputChip(
                        selected = false,
                        onClick = {},
                        label = { Text(phone.number) },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, null, Modifier.size(16.dp))
                        },
                        trailingIcon = {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove ${phone.number}",
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { onRemove(phone) },
                            )
                        },
                        modifier = Modifier.padding(end = 6.dp, bottom = 4.dp),
                    )
                }
            }
        }

        // Add row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                label = { Text("Phone number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = {
                    onAdd(draft, null)
                    draft = ""
                },
                enabled = draft.isNotBlank(),
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add phone")
            }
        }
    }
}
