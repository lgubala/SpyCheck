package com.example.spycheck.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.spycheck.R
import com.example.spycheck.ui.main.demos.sneaky.SneakyStuffHomeScreen
import com.example.spycheck.ui.main.demos.sneaky.exif.ExifDemoScreen
import com.example.spycheck.ui.main.demos.sneaky.wifi.WifiDemoScreen
import com.example.spycheck.ui.main.demos.sneaky.clipboard.ClipboardDemoScreen
import com.example.spycheck.ui.main.demos.sneaky.keystroke.KeystrokeDemoScreen
import com.example.spycheck.ui.main.demos.sneaky.notifications.NotificationDemoScreen
import com.example.spycheck.ui.main.demos.sneaky.sensors.SensorTrackingDemoScreen
import com.example.spycheck.ui.main.demos.sneaky.usage_stats.UsageStatsDemoScreen
import com.example.spycheck.ui.main.model.DemoRepository
import com.example.spycheck.ui.main.demos.fingerprinting.FingerprintingHomeScreen
import com.example.spycheck.ui.main.demos.fingerprinting.audio.AudioFingerprintDemoScreen
import com.example.spycheck.ui.main.demos.fingerprinting.battery.BatteryFingerprintDemoScreen
import com.example.spycheck.ui.main.demos.fingerprinting.device.DeviceFingerprintDemoScreen
import com.example.spycheck.ui.main.demos.fingerprinting.network.NetworkFingerprintDemoScreen
import com.example.spycheck.ui.main.demos.fingerprinting.performance.PerformanceFingerprintDemoScreen
import com.example.spycheck.ui.main.demos.fingerprinting.sensor.SensorFingerprintDemoScreen
import com.example.spycheck.ui.main.demos.fingerprinting.combined.SuperFingerprintDemoScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String,
    currentThemeMode: Int,
    onThemeModeChanged: (Int) -> Unit
) {
    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {
        composable(Screen.Home.route) {
            HomeScreen(
                currentThemeMode = currentThemeMode,
                onThemeModeChanged = onThemeModeChanged
            )
        }

        // Sneaky Stuff Home Screen
        composable(Screen.SneakyStuff.route) {
            SneakyStuffHomeScreen(
                onDemoClick = { demoId ->
                    navController.navigate("sneaky_demo/$demoId")
                }
            )
        }

        // Fingerprint Home Screen
        composable(Screen.Fingerprint.route) {
            FingerprintingHomeScreen(
                onDemoClick = { demoId ->
                    navController.navigate("fingerprint_demo/$demoId")
                }
            )
        }

        composable(Screen.History.route) { HistoryScreen() }

        // NEW: Sneaky demos route
        composable(
            route = "sneaky_demo/{demoId}",
            arguments = listOf(navArgument("demoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val demoId = backStackEntry.arguments?.getString("demoId")

            when (demoId) {
                "exif" -> ExifDemoScreen(
                    onBack = { navController.popBackStack() }
                )
                "wifi" -> WifiDemoScreen(
                    onBack = { navController.popBackStack() }
                )
                "clipboard" -> ClipboardDemoScreen(
                    onBack = { navController.popBackStack() }
                )
                "notifications" -> NotificationDemoScreen(
                    onBack = { navController.popBackStack() }
                )
                "keystroke" -> KeystrokeDemoScreen(
                    onBack = { navController.popBackStack() }
                )
                "sensors" -> SensorTrackingDemoScreen(
                    onBack = { navController.popBackStack() }
                )
                "usage_stats" -> UsageStatsDemoScreen(
                    onBack = { navController.popBackStack() }
                )
                else -> {
                    // For demos not yet migrated, show old DetailScreen
                    val demo = DemoRepository.getSneakyStuffDemos().find { it.id == demoId }
                    if (demo != null) {
                        DetailScreen(
                            detail = demo,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }

        // OLD: Detail screen for fingerprinting demos (keep for now)
        composable(
            route = "demo_detail/{demoId}",
            arguments = listOf(navArgument("demoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val demoId = backStackEntry.arguments?.getString("demoId")
            val demo = DemoRepository.getFingerprintDemos().find { it.id == demoId }

            if (demo != null) {
                DetailScreen(
                    detail = demo,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // Fingerprinting demos route
        composable(
            route = "fingerprint_demo/{demoId}",
            arguments = listOf(navArgument("demoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val demoId = backStackEntry.arguments?.getString("demoId")

            when (demoId) {
                "audio" -> AudioFingerprintDemoScreen(
                    onBack = { navController.popBackStack() }
                )
                "battery" -> BatteryFingerprintDemoScreen(
                    onBack = { navController.popBackStack() }
                )
                "device" -> DeviceFingerprintDemoScreen(
                    onBack = { navController.popBackStack() }
                )
                "network" -> NetworkFingerprintDemoScreen(
                    onBack = { navController.popBackStack() }
                )
                "performance" -> PerformanceFingerprintDemoScreen(
                    onBack = { navController.popBackStack() }
                )
                "sensor" -> SensorFingerprintDemoScreen(
                    onBack = { navController.popBackStack() }
                )
                "combined" -> SuperFingerprintDemoScreen(
                    onBack = { navController.popBackStack() }
                )
                else -> {
                    // Fallback for unknown fingerprinting demos
                    val demo = DemoRepository.getFingerprintDemos().find { it.id == demoId }
                    if (demo != null) {
                        DetailScreen(
                            detail = demo,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}