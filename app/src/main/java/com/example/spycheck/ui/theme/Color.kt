package com.example.spycheck.ui.theme

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.spycheck.R

// Helper function to get color from resources
@Composable
fun colorResource(id: Int): Color {
    val context = LocalContext.current
    return Color(ContextCompat.getColor(context, id))
}

// Extension function for non-Composable contexts
fun Context.getColorCompat(id: Int): Color {
    return Color(ContextCompat.getColor(this, id))
}

// Composable color accessors that load from colors.xml
object AppColors {
    val Crimson: Color
        @Composable get() = colorResource(R.color.primary)

    val CrimsonDark: Color
        @Composable get() = colorResource(R.color.primary_dark)

    val IcyBlue: Color
        @Composable get() = colorResource(R.color.accent)

    val Amber: Color
        @Composable get() = colorResource(R.color.warning)

    val LightGreen: Color
        @Composable get() = colorResource(R.color.success)

    val BackgroundDark: Color
        @Composable get() = colorResource(R.color.background_dark)

    val SurfaceDark: Color
        @Composable get() = colorResource(R.color.surface_dark)

    val TextPrimary: Color
        @Composable get() = colorResource(R.color.text_primary)

    val TextSecondary: Color
        @Composable get() = colorResource(R.color.text_secondary)

    // Demo-specific colors (aliases for convenience)
    val DangerRed: Color
        @Composable get() = colorResource(R.color.danger_red)

    val SuccessGreen: Color
        @Composable get() = colorResource(R.color.success_green)

    val WarningAmber: Color
        @Composable get() = colorResource(R.color.warning)
}

// For backwards compatibility and Theme.kt usage
// These are loaded once at app startup (non-composable context)
val Crimson = Color(0xFFFF5A5F)
val CrimsonDark = Color(0xFFE63946)
val IcyBlue = Color(0xFF8ECAE6)
val Amber = Color(0xFFFFB703)
val LightGreen = Color(0xFF90EE90)

val BackgroundDark = Color(0xFF0F0F0F)
val SurfaceDark = Color(0xFF1A1A1A)
val BackgroundLight = Color(0xFFF8F8F8)
val SurfaceLight = Color(0xFFFFFFFF)

val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFB0B0B0)
val TextPrimaryLight = Color(0xFF1A1A1A)
val TextSecondaryLight = Color(0xFF666666)

val DangerRed = Color(0xFFFF6B6B)
val SuccessGreen = Color(0xFF4ECDC4)
val WarningAmber = Amber

// NEW: Permission Card Colors - Dark Mode
val PermissionGrantedBgDark = Color(0x33FF5A5F)
val PermissionGrantedTitleDark = Color(0xFFFF5A5F)
val PermissionWarningTextDark = Color(0xFF90EE90)
val MonitoringActiveBgDark = Color(0x33FF5A5F)
val MonitoringActiveTextDark = Color(0xFFFF5A5F)
val WarningAmberTextDark = Color(0xFFFFB703)

// NEW: Permission Card Colors - Light Mode
val PermissionGrantedBgLight = Color(0xFF75F6CF)
val PermissionGrantedTitleLight = Color(0xFFE63946)
val PermissionWarningTextLight = Color(0xFF006400)
val MonitoringActiveBgLight = Color(0xFFFFE5E5)
val MonitoringActiveTextLight = Color(0xFFE63946)
val WarningAmberTextLight = Color(0xFF8B6000)