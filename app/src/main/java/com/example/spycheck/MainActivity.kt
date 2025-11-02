package com.example.spycheck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import com.example.spycheck.ui.main.MainScreen
import com.example.spycheck.ui.onboarding.OnboardingScreen
import com.example.spycheck.ui.theme.SpyCheckTheme
import com.example.spycheck.utils.PreferencesManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var currentThemeMode by remember {
                mutableIntStateOf(PreferencesManager.getThemeMode(this))
            }

            val useDarkTheme = when (currentThemeMode) {
                PreferencesManager.THEME_MODE_LIGHT -> false
                PreferencesManager.THEME_MODE_DARK -> true
                else -> isSystemInDarkTheme()
            }

            SpyCheckTheme(darkTheme = useDarkTheme) {
                var showOnboarding by remember {
                    mutableStateOf(!PreferencesManager.hasCompletedOnboarding(this))
                }

                if (showOnboarding) {
                    OnboardingScreen(
                        onComplete = { dontShowAgain ->
                            if (dontShowAgain) {
                                PreferencesManager.setOnboardingCompleted(this)
                            }
                            showOnboarding = false
                        }
                    )
                } else {
                    MainScreen(
                        currentThemeMode = currentThemeMode,
                        onThemeModeChanged = { newMode ->
                            PreferencesManager.setThemeMode(this, newMode)
                            currentThemeMode = newMode
                        }
                    )
                }
            }
        }
    }
}