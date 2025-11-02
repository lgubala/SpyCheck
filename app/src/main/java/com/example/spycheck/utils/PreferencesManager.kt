package com.example.spycheck.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Simple manager for storing user preferences
 */
object PreferencesManager {
    private const val PREFS_NAME = "spycheck_preferences"
    private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    private const val KEY_THEME_MODE = "theme_mode"

    // Theme mode values
    const val THEME_MODE_SYSTEM = 0
    const val THEME_MODE_LIGHT = 1
    const val THEME_MODE_DARK = 2

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Check if user has completed onboarding (clicked "Don't show again")
     */
    fun hasCompletedOnboarding(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    /**
     * Mark onboarding as completed
     */
    fun setOnboardingCompleted(context: Context, completed: Boolean = true) {
        getPrefs(context).edit()
            .putBoolean(KEY_ONBOARDING_COMPLETED, completed)
            .apply()
    }

    /**
     * Reset onboarding (for testing purposes)
     */
    fun resetOnboarding(context: Context) {
        setOnboardingCompleted(context, false)
    }

    /**
     * Get the current theme mode
     * @return THEME_MODE_SYSTEM, THEME_MODE_LIGHT, or THEME_MODE_DARK
     */
    fun getThemeMode(context: Context): Int {
        return getPrefs(context).getInt(KEY_THEME_MODE, THEME_MODE_DARK) // Default to DARK
    }

    /**
     * Set the theme mode
     * @param mode THEME_MODE_SYSTEM, THEME_MODE_LIGHT, or THEME_MODE_DARK
     */
    fun setThemeMode(context: Context, mode: Int) {
        getPrefs(context).edit()
            .putInt(KEY_THEME_MODE, mode)
            .apply()
    }
}