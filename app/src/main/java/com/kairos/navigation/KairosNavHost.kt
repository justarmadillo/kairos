package com.kairos.navigation

import android.net.Uri
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kairos.features.cases.CaseDetailScreen
import com.kairos.features.cases.CaseFeedScreen
import com.kairos.features.cases.DiagnosisBrowseScreen
import com.kairos.features.cases.ImageViewerScreen
import com.kairos.features.consultation.ConsultationCalendarScreen
import com.kairos.features.dashboard.DashboardScreen
import com.kairos.features.patient.PatientCaseScreen
import com.kairos.features.settings.SettingsScreen
import com.kairos.features.settings.TrashScreen
import com.kairos.features.shifts.ShiftDetailScreen
import com.kairos.features.shifts.ShiftsListScreen

const val ROUTE_PATIENT_CASE = "patient_case"

@Composable
fun KairosNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = TopLevelDestination.Dashboard.route,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        composable(TopLevelDestination.Dashboard.route) {
            DashboardScreen(
                onCaseClick = { caseId -> navController.navigate("case_detail/$caseId") },
            )
        }

        composable(TopLevelDestination.Shifts.route) {
            ShiftsListScreen(
                onAddPatient = { navController.navigate(ROUTE_PATIENT_CASE) },
                onNavigateToDetail = { id -> navController.navigate("shift_detail/$id") },
            )
        }

        composable(
            route = "shift_detail/{shiftId}",
            arguments = listOf(navArgument("shiftId") { type = NavType.LongType }),
        ) {
            ShiftDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onAddPatient = { shiftId ->
                    navController.navigate("$ROUTE_PATIENT_CASE?shiftId=$shiftId")
                },
                onCaseClick = { caseId -> navController.navigate("case_detail/$caseId") },
            )
        }
        composable(TopLevelDestination.Consultation.route) {
            ConsultationCalendarScreen(
                onAddPatient = { sessionId ->
                    navController.navigate("$ROUTE_PATIENT_CASE?sessionId=$sessionId")
                },
                onCaseClick = { caseId -> navController.navigate("case_detail/$caseId") },
            )
        }
        composable(TopLevelDestination.Cases.route) {
            DiagnosisBrowseScreen(
                onNavigateToCaseFeed = { id, name ->
                    navController.navigate("case_feed/$id?name=${Uri.encode(name)}")
                },
                onAddCase = { navController.navigate(ROUTE_PATIENT_CASE) },
            )
        }

        composable(
            route = "case_feed/{diagnosisId}?name={diagnosisName}",
            arguments = listOf(
                navArgument("diagnosisId") { type = NavType.LongType },
                navArgument("diagnosisName") { defaultValue = "" },
            ),
        ) {
            CaseFeedScreen(
                onNavigateBack = { navController.popBackStack() },
                onCaseClick = { caseId -> navController.navigate("case_detail/$caseId") },
            )
        }

        composable(
            route = "case_detail/{caseId}",
            arguments = listOf(navArgument("caseId") { type = NavType.LongType }),
        ) {
            CaseDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onEditCase = { caseId ->
                    navController.navigate("$ROUTE_PATIENT_CASE?caseId=$caseId")
                },
                onNavigateToCaseFeed = { id, name ->
                    navController.navigate("case_feed/$id?name=${Uri.encode(name)}")
                },
                onOpenImageViewer = { caseId, index ->
                    navController.navigate("image_viewer/$caseId?index=$index")
                },
            )
        }

        composable(
            route = "image_viewer/{caseId}?index={index}",
            arguments = listOf(
                navArgument("caseId") { type = NavType.LongType },
                navArgument("index") { type = NavType.IntType; defaultValue = 0 },
            ),
        ) { backStack ->
            ImageViewerScreen(
                initialIndex = backStack.arguments?.getInt("index") ?: 0,
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(TopLevelDestination.Settings.route) {
            SettingsScreen(onNavigateToTrash = { navController.navigate("trash") })
        }
        composable("trash") {
            TrashScreen(onNavigateBack = { navController.popBackStack() })
        }

        // Patient/Case entry screen — optional shiftId, sessionId, or caseId (edit)
        composable(
            route = "$ROUTE_PATIENT_CASE?shiftId={shiftId}&sessionId={sessionId}&caseId={caseId}",
            arguments = listOf(
                navArgument("shiftId") { type = NavType.LongType; defaultValue = -1L },
                navArgument("sessionId") { type = NavType.LongType; defaultValue = -1L },
                navArgument("caseId") { type = NavType.LongType; defaultValue = -1L },
            ),
        ) { backStack ->
            val shiftId = backStack.arguments?.getLong("shiftId")?.takeIf { it != -1L }
            val sessionId = backStack.arguments?.getLong("sessionId")?.takeIf { it != -1L }
            val caseId = backStack.arguments?.getLong("caseId")?.takeIf { it != -1L }
            PatientCaseScreen(
                linkShiftId = shiftId,
                linkSessionId = sessionId,
                editCaseId = caseId,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
