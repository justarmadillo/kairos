package com.taha.kairos.features.consultation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taha.kairos.core.components.CaseCard
import com.taha.kairos.core.components.EmptyState
import com.taha.kairos.core.components.KairosTopBar
import com.taha.kairos.core.theme.LocalKairosExtraColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ConsultationCalendarScreen(
    onAddPatient: (sessionId: Long) -> Unit = {},
    onCaseClick: (caseId: Long) -> Unit = {},
    viewModel: ConsultationViewModel = hiltViewModel(),
) {
    val state by viewModel.ui.collectAsStateWithLifecycle()
    val cases by viewModel.cases.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    // Scroll date strip to selected date on first composition
    LaunchedEffect(state.dates, state.selectedDate) {
        if (state.dates.isEmpty()) return@LaunchedEffect
        val index = state.dates.indexOfFirst { it.date == state.selectedDate }
        if (index >= 0) listState.animateScrollToItem(index.coerceAtMost(state.dates.lastIndex))
    }

    Scaffold(
        topBar = { KairosTopBar(title = "Consultation") },
        floatingActionButton = {
            val isConsultDay = state.selectedDate.dayOfWeek == state.consultationDayOfWeek
            if (isConsultDay) {
                FloatingActionButton(
                    onClick = { viewModel.getOrCreateSessionForSelected(onAddPatient) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add patient")
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // Date strip
            LazyRow(
                state = listState,
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
            ) {
                items(state.dates, key = { it.date.toString() }) { item ->
                    DateCell(
                        item = item,
                        isSelected = item.date == state.selectedDate,
                        onClick = { viewModel.selectDate(item.date) },
                    )
                }
            }

            HorizontalDivider()

            // Selected date header
            val selectedDateLabel = state.selectedDate.format(
                DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.getDefault())
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = viewModel::selectPreviousConsultationDate) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous consultation date")
                }
                Text(
                    text = selectedDateLabel,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = viewModel::selectNextConsultationDate) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next consultation date")
                }
            }

            // Cases or empty state
            if (cases.isEmpty()) {
                val isConsultDay = state.selectedDate.dayOfWeek == state.consultationDayOfWeek
                EmptyState(
                    title = if (isConsultDay) "No patients on this day" else "Not a consultation day",
                    message = if (isConsultDay) "Tap + to add your first patient" else "Select a ${state.consultationDayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())} to add patients",
                    modifier = Modifier.weight(1f),
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item { Spacer(Modifier.height(4.dp)) }
                    items(cases, key = { it.id }) { case ->
                        CaseCard(
                            case = case,
                            onClick = { onCaseClick(case.id) },
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun DateCell(
    item: DateItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val extra = LocalKairosExtraColors.current
    val today = LocalDate.now()
    val isToday = item.date == today

    val bgColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday && item.isConsultationDay -> MaterialTheme.colorScheme.primaryContainer
        else -> androidx.compose.ui.graphics.Color.Transparent
    }

    Column(
        modifier = modifier
            .width(48.dp)
            .alpha(if (item.isConsultationDay) 1f else 0.35f)
            .clip(MaterialTheme.shapes.medium)
            .background(bgColor)
            .then(
                if (item.isConsultationDay) Modifier.clickable(onClick = onClick) else Modifier
            )
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        // Day-of-week abbrev (Mon, Tue…)
        Text(
            text = item.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else extra.onSurfaceMuted,
        )
        // Day number
        Text(
            text = item.date.dayOfMonth.toString(),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = if (item.isConsultationDay) FontWeight.Medium else FontWeight.Normal,
                fontSize = if (item.isConsultationDay) 18.sp else 15.sp,
            ),
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        // Month abbrev on 1st of month
        if (item.date.dayOfMonth == 1) {
            Text(
                text = item.date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                else extra.onSurfaceMuted,
            )
        }
        // Dot if session has cases
        if (item.session != null && item.session.caseCount > 0) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.primary
                    )
            )
        }
    }
}
