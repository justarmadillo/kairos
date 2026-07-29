package com.taha.kairos.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SpaceDashboard
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.SpaceDashboard
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class TopLevelDestination(
    val route: String,
    val label: String,
    val iconActive: ImageVector,
    val iconInactive: ImageVector
) {
    Dashboard(
        route = "dashboard",
        label = "Dashboard",
        iconActive = Icons.Rounded.SpaceDashboard,
        iconInactive = Icons.Outlined.SpaceDashboard
    ),
    Shifts(
        route = "shifts",
        label = "Shifts",
        iconActive = Icons.Rounded.LocalHospital,
        iconInactive = Icons.Outlined.LocalHospital
    ),
    Consultation(
        route = "consultation",
        label = "Consultation",
        iconActive = Icons.Rounded.CalendarMonth,
        iconInactive = Icons.Outlined.CalendarMonth
    ),
    Cases(
        route = "cases",
        label = "Cases",
        iconActive = Icons.Rounded.MedicalServices,
        iconInactive = Icons.Outlined.MedicalServices
    ),
    Settings(
        route = "settings",
        label = "Settings",
        iconActive = Icons.Rounded.Settings,
        iconInactive = Icons.Outlined.Settings
    )
}
