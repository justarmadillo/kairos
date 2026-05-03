package com.kairos.features.search

import android.text.Html
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kairos.core.components.EmptyState
import com.kairos.core.repository.SearchResult
import com.kairos.core.theme.LocalKairosExtraColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onCaseClick: (Long) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val state by viewModel.ui.collectAsStateWithLifecycle()
    val focusRequester = FocusRequester()

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::setQuery,
                label = { Text("Name, phone, age, diagnosis, notes...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (state.query.isNotEmpty()) {
                        IconButton(onClick = viewModel::clearQuery) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                shape = MaterialTheme.shapes.medium,
            )

            Spacer(Modifier.height(12.dp))

            when {
                state.query.isBlank() -> {
                    EmptyState(
                        icon = Icons.Outlined.SearchOff,
                        title = "Search everything",
                        message = "Try a patient name, number, age, diagnosis, mechanism, or note",
                        modifier = Modifier.weight(1f),
                    )
                }
                state.results.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Outlined.SearchOff,
                        title = "No matches",
                        message = "No patient cases match this search",
                        modifier = Modifier.weight(1f),
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(state.results, key = { it.caseId }) { result ->
                            SearchResultCard(
                                result = result,
                                onClick = { onCaseClick(result.caseId) },
                            )
                        }
                        item { Spacer(Modifier.height(12.dp)) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SearchResultCard(
    result: SearchResult,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val extra = LocalKairosExtraColors.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = extra.surfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = result.patientName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                result.patientAge?.let { age ->
                    Text(
                        text = "Age $age",
                        style = MaterialTheme.typography.bodySmall,
                        color = extra.onSurfaceMuted,
                    )
                }
            }

            Spacer(Modifier.height(2.dp))
            Text(
                text = result.caseDate.shortDate(),
                style = MaterialTheme.typography.bodySmall,
                color = extra.onSurfaceMuted,
            )

            if (result.diagnosisNames.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    result.diagnosisNames.take(4).forEach { diagnosis ->
                        SuggestionChip(
                            onClick = {},
                            label = {
                                Text(
                                    text = diagnosis,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                        )
                    }
                }
            }

            val detailLines = buildList {
                if (result.phoneNumbers.isNotEmpty()) add(result.phoneNumbers.joinToString("  •  "))
                if (!result.mechanism.isNullOrBlank()) add("Mechanism: ${result.mechanism}")
                result.notesHtml?.toPlainText()?.takeIf { it.isNotBlank() }?.let { add(it) }
            }

            detailLines.take(2).forEach { line ->
                Spacer(Modifier.height(6.dp))
                Text(
                    text = line,
                    style = MaterialTheme.typography.bodySmall,
                    color = extra.onSurfaceMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private fun Long.shortDate(): String =
    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(this))

private fun String.toPlainText(): String =
    Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT).toString().trim()
