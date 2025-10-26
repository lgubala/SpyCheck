package com.example.spycheck.ui.main.demos.sneaky.notifications.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings

/**
 * Helper for notification listener access and permissions
 */
object NotificationAccessHelper {

    /**
     * Check if notification listener permission is granted
     */
    fun isNotificationAccessGranted(context: Context): Boolean {
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        )

        if (enabledListeners.isNullOrEmpty()) {
            return false
        }

        val packageName = context.packageName
        val listeners = enabledListeners.split(":")

        return listeners.any { it.contains(packageName) }
    }

    /**
     * Open notification listener settings
     */
    fun openNotificationListenerSettings(context: Context) {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /**
     * Get component name for our notification listener
     */
    fun getListenerComponentName(context: Context): ComponentName {
        return ComponentName(context, TrackingNotificationListener::class.java)
    }
}
