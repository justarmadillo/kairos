package com.taha.kairos.core.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.taha.kairos.core.theme.LocalKairosExtraColors
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RichNotesEditor(
    state: RichTextState,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
) {
    val extra = LocalKairosExtraColors.current
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .bringIntoViewRequester(bringIntoViewRequester)
            .border(
                width = 1.dp,
                color = extra.divider,
                shape = MaterialTheme.shapes.medium,
            )
    ) {
        RichTextEditor(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        scope.launch {
                            delay(250)
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                }
                .heightIn(min = 120.dp, max = 320.dp)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            colors = RichTextEditorDefaults.richTextEditorColors(
                containerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
            ),
            placeholder = placeholder,
        )
        RichTextEditorToolbar(state = state)
    }
}

@Composable
private fun RichTextEditorToolbar(
    state: RichTextState,
    modifier: Modifier = Modifier,
) {
    val spanStyle = state.currentSpanStyle
    val isBold = spanStyle.fontWeight == FontWeight.Bold
    val isItalic = spanStyle.fontStyle == FontStyle.Italic
    val isUnderline = spanStyle.textDecoration == TextDecoration.Underline

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = LocalKairosExtraColors.current.divider.copy(alpha = 0.4f),
    ) {
        Row(modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)) {
            ToolbarToggleButton(
                icon = Icons.Default.FormatBold,
                contentDescription = "Bold",
                active = isBold,
                onClick = { state.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold)) },
            )
            ToolbarToggleButton(
                icon = Icons.Default.FormatItalic,
                contentDescription = "Italic",
                active = isItalic,
                onClick = { state.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic)) },
            )
            ToolbarToggleButton(
                icon = Icons.Default.FormatUnderlined,
                contentDescription = "Underline",
                active = isUnderline,
                onClick = { state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline)) },
            )
            ToolbarToggleButton(
                icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                contentDescription = "Bullet list",
                active = state.isUnorderedList,
                onClick = { state.toggleUnorderedList() },
            )
            ToolbarToggleButton(
                icon = Icons.Default.FormatListNumbered,
                contentDescription = "Numbered list",
                active = state.isOrderedList,
                onClick = { state.toggleOrderedList() },
            )
        }
    }
}

@Composable
private fun ToolbarToggleButton(
    icon: ImageVector,
    contentDescription: String,
    active: Boolean,
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (active) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface,
        )
    }
}
