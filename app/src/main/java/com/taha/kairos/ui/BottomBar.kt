package com.taha.kairos.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.taha.kairos.core.theme.LocalKairosExtraColors
import com.taha.kairos.navigation.TopLevelDestination

@Composable
fun BottomBar(
    currentRoute: String?,
    onTabSelected: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val extras = LocalKairosExtraColors.current
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 0.dp
    ) {
        TopLevelDestination.entries.forEach { dest ->
            val selected = dest.route == currentRoute
            NavigationBarItem(
                selected = selected,
                onClick = { if (!selected) onTabSelected(dest) },
                icon = {
                    Icon(
                        imageVector = if (selected) dest.iconActive else dest.iconInactive,
                        contentDescription = dest.label,
                        modifier = Modifier.padding(2.dp)
                    )
                },
                label = { Text(dest.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = extras.selectedDark,
                    selectedTextColor = extras.selectedDark,
                    unselectedIconColor = extras.onSurfaceMuted,
                    unselectedTextColor = extras.onSurfaceMuted,
                    indicatorColor = extras.surfaceCard
                )
            )
        }
    }
}
