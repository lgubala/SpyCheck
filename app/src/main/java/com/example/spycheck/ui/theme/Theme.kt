package com.example.spycheck.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// --- Dark Theme Color Scheme ---
private val DarkColorScheme = darkColorScheme(
    primary = Crimson,
    onPrimary = Color.White,
    secondary = IcyBlue,
    onSecondary = Color.Black,
    tertiary = Amber,
    background = BackgroundDark,
    surface = SurfaceDark,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = CrimsonDark,
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = TextSecondary,
    // NEW: Custom colors for permission cards
    primaryContainer = PermissionGrantedBgDark,
    onPrimaryContainer = PermissionGrantedTitleDark,
    secondaryContainer = MonitoringActiveBgDark,
    onSecondaryContainer = MonitoringActiveTextDark,
    tertiaryContainer = PermissionWarningTextDark,
    onTertiaryContainer = WarningAmberTextDark
)

// --- Light Theme Color Scheme ---
private val LightColorScheme = lightColorScheme(
    primary = CrimsonDark,
    onPrimary = Color.White,
    secondary = IcyBlue,
    onSecondary = Color.Black,
    tertiary = Amber,
    background = BackgroundLight,
    surface = SurfaceLight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    error = Crimson,
    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariant = TextSecondaryLight,
    // NEW: Custom colors for permission cards
    primaryContainer = PermissionGrantedBgLight,
    onPrimaryContainer = PermissionGrantedTitleLight,
    secondaryContainer = MonitoringActiveBgLight,
    onSecondaryContainer = MonitoringActiveTextLight,
    tertiaryContainer = PermissionWarningTextLight,
    onTertiaryContainer = WarningAmberTextLight
)

@Composable
fun SpyCheckTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // we'll disable dynamic colors to preserve your brand
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}