package com.example.spycheck.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.spycheck.R
import com.example.spycheck.ui.main.demos.exif.ExifDemoScreen
import com.example.spycheck.ui.main.model.DemoRepository

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier, startDestination: String) {
    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.SneakyStuff.route) {
            val demos = DemoRepository.getSneakyStuffDemos()
            ListScreen(
                titleRes = R.string.sneaky_stuff,
                icon = Screen.SneakyStuff.icon,
                introRes = R.string.sneaky_stuff_intro,
                details = demos,
                onDemoClick = { demoId ->
                    navController.navigate("demo_detail/$demoId")
                }
            )
        }
        composable(Screen.Fingerprint.route) {
            val demos = DemoRepository.getFingerprintDemos()
            ListScreen(
                titleRes = R.string.fingerprint,
                icon = Screen.Fingerprint.icon,
                introRes = R.string.fingerprint_intro,
                details = demos,
                onDemoClick = { demoId ->
                    navController.navigate("demo_detail/$demoId")
                }
            )
        }
        composable(Screen.History.route) {
            HistoryScreen()
        }

        // Detail screen with optional interactive demo button
        composable(
            route = "demo_detail/{demoId}",
            arguments = listOf(navArgument("demoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val demoId = backStackEntry.arguments?.getString("demoId")
            val demo = (DemoRepository.getSneakyStuffDemos() + DemoRepository.getFingerprintDemos()).find { it.id == demoId }
            if (demo != null) {
                DetailScreen(
                    detail = demo,
                    onBack = { navController.popBackStack() },
                    onStartDemo = if (demo.hasInteractiveDemo) {
                        { navController.navigate("demo_interactive/$demoId") }
                    } else null
                )
            }
        }

        // NEW: Interactive demo screen
        composable(
            route = "demo_interactive/{demoId}",
            arguments = listOf(navArgument("demoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val demoId = backStackEntry.arguments?.getString("demoId")
            when (demoId) {
                "exif_gps" -> ExifDemoScreen()
                // Add more interactive demos here in the future
            }
        }
    }
}