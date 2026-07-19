package com.kairos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kairos.core.model.ThemeMode
import com.kairos.core.repository.SettingsRepository
import com.kairos.core.theme.KairosTheme
import com.kairos.navigation.KairosNavHost
import com.kairos.navigation.ROUTE_PATIENT_CASE
import com.kairos.navigation.TopLevelDestination
import com.kairos.ui.BottomBar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(0, 0),
            navigationBarStyle = SystemBarStyle.auto(0, 0),
        )
        setContent {
            val settings by settingsRepository.observeSettings()
                .collectAsStateWithLifecycle(
                    initialValue = com.kairos.core.model.AppSettings()
                )

            val darkTheme = when (settings.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            KairosTheme(darkTheme = darkTheme) {
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
                            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
                    )
                }
            }
        }
    }

    companion object {
        const val EXTRA_WIDGET_DESTINATION = "com.kairos.widget.DESTINATION"

        // Whitelist: the activity is exported, so never navigate to arbitrary extras
        private val ALLOWED_WIDGET_DESTINATIONS = setOf(ROUTE_PATIENT_CASE, "search")
    }
}
