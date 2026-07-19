package com.kairos.features.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kairos.core.components.KairosTopBar
import com.kairos.core.repository.RecentCase
import com.kairos.core.theme.LocalKairosExtraColors
import com.kairos.core.theme.PaletteSuccess
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import kotlin.math.abs

private val DashboardCardShape = RoundedCornerShape(24.dp)

@Composable
fun DashboardScreen(
    onCaseClick: (caseId: Long) -> Unit = {},
    onSearchClick: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.ui.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            KairosTopBar(
                title = "Dashboard",
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            state.backupWarning?.let { warning ->
                BackupWarningCard(message = warning)
            }

            HeroStatsRow(
                totalPatients = state.totalPatients,
                totalCases = state.totalCases,
                totalShifts = state.totalShifts,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                PeriodCard(
                    title = "This Week",
                    current = state.casesThisWeek,
                    previous = state.casesLastWeek,
                    modifier = Modifier.weight(1f),
                )
                PeriodCard(
                    title = "This Month",
                    current = state.casesThisMonth,
                    previous = state.casesLastMonth,
                    modifier = Modifier.weight(1f),
                )
            }

            MilestoneCard(
                milestone = state.milestone,
                celebration = state.milestoneCelebration,
            )

            RecentActivityCard(
                cases = state.recentCases,
                onCaseClick = onCaseClick,
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun BackupWarningCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = DashboardCardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
        }
    }
}

@Composable
private fun HeroStatsRow(
    totalPatients: Int,
    totalCases: Int,
    totalShifts: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        HeroStatCard(
            value = totalPatients,
            label = "patients",
            modifier = Modifier.weight(1f),
        )
        HeroStatCard(
            value = totalCases,
            label = "cases",
            modifier = Modifier.weight(1f),
        )
        HeroStatCard(
            value = totalShifts,
            label = "shifts",
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun HeroStatCard(
    value: Int,
    label: String,
    modifier: Modifier = Modifier,
) {
    val extra = LocalKairosExtraColors.current
    Card(
        modifier = modifier.heightIn(min = 96.dp),
        shape = DashboardCardShape,
        colors = CardDefaults.cardColors(containerColor = extra.surfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = extra.onSurfaceMuted,
                maxLines = 1,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun PeriodCard(
    title: String,
    current: Int,
    previous: Int,
    modifier: Modifier = Modifier,
) {
    val extra = LocalKairosExtraColors.current
    val delta = current - previous
    val deltaColor = when {
        delta > 0 -> PaletteSuccess
        delta < 0 -> MaterialTheme.colorScheme.error
        else -> extra.onSurfaceMuted
    }
    val deltaText = when {
        delta > 0 -> "↑${abs(delta)} vs last"
        delta < 0 -> "↓${abs(delta)} vs last"
        else -> "— vs last"
    }

    Card(
        modifier = modifier.heightIn(min = 132.dp),
        shape = DashboardCardShape,
        colors = CardDefaults.cardColors(containerColor = extra.surfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = "$current ${caseLabel(current)}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = deltaText,
                style = MaterialTheme.typography.labelLarge,
                color = deltaColor,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun MilestoneCard(
    milestone: MilestoneProgress,
    celebration: Int?,
    modifier: Modifier = Modifier,
) {
    val extra = LocalKairosExtraColors.current
    val denominator = milestone.target - milestone.previous
    val progress = if (denominator <= 0) {
        1f
    } else {
        ((milestone.current - milestone.previous).toFloat() / denominator).coerceIn(0f, 1f)
    }
    val remaining = (milestone.target - milestone.current).coerceAtLeast(0)
    val title = if (celebration != null) {
        "🎉 Milestone reached: $celebration cases!"
    } else {
        "🏆 Next: ${milestone.target} cases"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = DashboardCardShape,
        colors = CardDefaults.cardColors(containerColor = extra.surfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(999.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = extra.divider,
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${milestone.current}/${milestone.target}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "$remaining to go",
                    style = MaterialTheme.typography.labelLarge,
                    color = extra.onSurfaceMuted,
                )
            }
        }
    }
}

@Composable
private fun RecentActivityCard(
    cases: List<RecentCase>,
    onCaseClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val extra = LocalKairosExtraColors.current
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = DashboardCardShape,
        colors = CardDefaults.cardColors(containerColor = extra.surfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
        ) {
            Text(
                text = "Recent",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 18.dp),
            )
            Spacer(Modifier.height(4.dp))

            if (cases.isEmpty()) {
                Text(
                    text = "No recent cases yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = extra.onSurfaceMuted,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                )
            } else {
                cases.forEachIndexed { index, recentCase ->
                    RecentCaseRow(
                        recentCase = recentCase,
                        onClick = { onCaseClick(recentCase.caseId) },
                    )
                    if (index < cases.lastIndex) {
                        HorizontalDivider(
                            color = extra.divider.copy(alpha = 0.6f),
                            modifier = Modifier.padding(horizontal = 18.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentCaseRow(
    recentCase: RecentCase,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val extra = LocalKairosExtraColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = recentCase.patientName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "—",
                    style = MaterialTheme.typography.bodyMedium,
                    color = extra.onSurfaceMuted,
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = recentCase.diagnosisName ?: "No diagnosis",
                    style = MaterialTheme.typography.bodyMedium,
                    color = extra.onSurfaceMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = recentCase.caseDate.dashboardDateLabel(),
                style = MaterialTheme.typography.bodySmall,
                color = extra.onSurfaceMuted,
            )
        }
    }
}

private fun caseLabel(count: Int): String =
    if (count == 1) "case" else "cases"

private fun Long.dashboardDateLabel(): String {
    val zone = ZoneId.systemDefault()
    val date = Instant.ofEpochMilli(this).atZone(zone).toLocalDate()
    val today = LocalDate.now(zone)
    return when (date) {
        today -> "today"
        today.minusDays(1) -> "yesterday"
        else -> SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(this))
    }
}
