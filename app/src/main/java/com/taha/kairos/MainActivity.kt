package com.taha.kairos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.taha.kairos.authorization.AuthorizationAccessState
import com.taha.kairos.authorization.AuthorizationGateViewModel
import com.taha.kairos.authorization.AuthorizationLaunchScreen
import com.taha.kairos.authorization.AuthorizationLockedScreen
import com.taha.kairos.core.model.ThemeMode
import com.taha.kairos.core.repository.SettingsRepository
import com.taha.kairos.core.theme.KairosTheme
import com.taha.kairos.navigation.KairosNavHost
import com.taha.kairos.navigation.ROUTE_PATIENT_CASE
import com.taha.kairos.navigation.TopLevelDestination
import com.taha.kairos.ui.BottomBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var settingsRepository: SettingsRepository
    private val authorizationViewModel: AuthorizationGateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(0, 0),
            navigationBarStyle = SystemBarStyle.auto(0, 0),
        )
        setContent {
            val settings by settingsRepository.observeSettings()
                .collectAsStateWithLifecycle(
                    initialValue = com.taha.kairos.core.model.AppSettings()
                )

            val darkTheme = when (settings.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            KairosTheme(darkTheme = darkTheme) {
                DeviceAuthorizationGate(viewModel = authorizationViewModel) {
                    AuthorizedAppContent()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        authorizationViewModel.onAppResumed()
    }

    @Composable
    private fun DeviceAuthorizationGate(
        viewModel: AuthorizationGateViewModel,
        authorizedContent: @Composable () -> Unit,
    ) {
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        var minimumLaunchDisplayFinished by rememberSaveable { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            delay(MINIMUM_DEVICE_ID_DISPLAY_MS)
            minimumLaunchDisplayFinished = true
        }

        val exportFolderPicker = rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocumentTree(),
        ) { uri ->
            uri?.let {
                runCatching {
                    contentResolver.takePersistableUriPermission(
                        it,
                        android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
                    )
                }
                viewModel.exportData(it.toString())
            }
        }

        when {
            !minimumLaunchDisplayFinished ||
                state.access == AuthorizationAccessState.InitialChecking -> {
                AuthorizationLaunchScreen(deviceId = state.deviceId)
            }

            state.access is AuthorizationAccessState.Granted -> {
                authorizedContent()
            }

            state.access is AuthorizationAccessState.Locked -> {
                AuthorizationLockedScreen(
                    deviceId = state.deviceId,
                    locked = state.access as AuthorizationAccessState.Locked,
                    exportState = state.export,
                    onRetry = viewModel::retry,
                    onOpenDocumentTree = { exportFolderPicker.launch(null) },
                )
            }
        }
    }

    @Composable
    private fun AuthorizedAppContent() {
        val navController = rememberNavController()
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route
        val showBottomBar = TopLevelDestination.entries.any { it.route == currentRoute }

        // Widget deep-link: consume once (rememberSaveable survives rotation)
        var widgetDestinationHandled by rememberSaveable { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            val destination = intent.getStringExtra(EXTRA_WIDGET_DESTINATION)
            if (!widgetDestinationHandled && destination in ALLOWED_WIDGET_DESTINATIONS) {
                widgetDestinationHandled = true
                navController.navigate(destination!!) { launchSingleTop = true }
            }
        }

        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    BottomBar(
                        currentRoute = currentRoute,
                        onTabSelected = { dest ->
                            navController.navigate(dest.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
            },
        ) { padding ->
            KairosNavHost(
                navController = navController,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
                    ),
            )
        }
    }

    companion object {
        const val EXTRA_WIDGET_DESTINATION = "com.taha.kairos.widget.DESTINATION"
        private const val MINIMUM_DEVICE_ID_DISPLAY_MS = 1_500L

        // Whitelist: the activity is exported, so never navigate to arbitrary extras
        private val ALLOWED_WIDGET_DESTINATIONS = setOf(ROUTE_PATIENT_CASE, "search")
    }
}
