package com.example.spycheck.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Simple manager for storing user preferences
 */
object PreferencesManager {
    private const val PREFS_NAME = "spycheck_preferences"
    private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"

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
}