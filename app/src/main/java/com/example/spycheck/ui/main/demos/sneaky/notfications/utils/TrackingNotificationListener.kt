package com.example.spycheck.ui.main.demos.sneaky.notifications.utils

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Data class representing a captured notification
 */
data class CapturedNotification(
    val id: Int,
    val packageName: String,
    val appName: String,
    val title: String?,
    val text: String?,
    val timestamp: Long,
    val isSensitive: Boolean // Banking, messages, etc.
)

/**
 * Notification listener service that captures all notifications
 * This demonstrates how malicious apps can steal notification data
 */
class TrackingNotificationListener : NotificationListenerService() {

    companion object {
        private val _notifications = MutableStateFlow<List<CapturedNotification>>(emptyList())
        val notifications: StateFlow<List<CapturedNotification>> = _notifications.asStateFlow()

        private val _isServiceRunning = MutableStateFlow(false)
        val isServiceRunning: StateFlow<Boolean> = _isServiceRunning.asStateFlow()

        fun clearNotifications() {
            android.util.Log.d("NotificationListener", "Clearing all notifications")
            _notifications.value = emptyList()
        }
    }

    override fun onCreate() {
        super.onCreate()
        android.util.Log.d("NotificationListener", "Service created")
        _isServiceRunning.value = true
    }

    override fun onDestroy() {
        super.onDestroy()
        android.util.Log.d("NotificationListener", "Service destroyed")
        _isServiceRunning.value = false
        clearNotifications()
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        android.util.Log.d("NotificationListener", "========== LISTENER CONNECTED ==========")
        _isServiceRunning.value = true

        // Load existing notifications
        loadExistingNotifications()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        android.util.Log.d("NotificationListener", "Listener disconnected")
        _isServiceRunning.value = false
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        android.util.Log.d("NotificationListener", "New notification from: ${sbn.packageName}")

        val captured = captureNotification(sbn)
        if (captured != null && shouldIncludeNotification(captured)) {
            val current = _notifications.value.toMutableList()
            current.add(0, captured) // Add to beginning

            // Keep max 20 notifications
            if (current.size > 20) {
                current.removeAt(current.size - 1)
            }

            _notifications.value = current
            android.util.Log.d("NotificationListener", "Captured: ${captured.appName} - ${captured.title}")
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Optionally remove from our list when user dismisses
    }

    private fun loadExistingNotifications() {
        try {
            val active = activeNotifications ?: return
            android.util.Log.d("NotificationListener", "Loading ${active.size} existing notifications")

            val captured = active.mapNotNull { captureNotification(it) }
                .filter { shouldIncludeNotification(it) }
                .take(20)
                .sortedByDescending { it.timestamp }

            _notifications.value = captured
            android.util.Log.d("NotificationListener", "Loaded ${captured.size} relevant notifications")
        } catch (e: Exception) {
            android.util.Log.e("NotificationListener", "Error loading notifications", e)
        }
    }

    private fun captureNotification(sbn: StatusBarNotification): CapturedNotification? {
        return try {
            val notification = sbn.notification ?: return null
            val extras = notification.extras

            val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
            val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()

            // Get app name
            val appName = try {
                packageManager.getApplicationLabel(
                    packageManager.getApplicationInfo(sbn.packageName, 0)
                ).toString()
            } catch (e: Exception) {
                sbn.packageName
            }

            CapturedNotification(
                id = sbn.id,
                packageName = sbn.packageName,
                appName = appName,
                title = title,
                text = text,
                timestamp = sbn.postTime,
                isSensitive = isSensitiveNotification(sbn.packageName, title, text)
            )
        } catch (e: Exception) {
            android.util.Log.e("NotificationListener", "Error capturing notification", e)
            null
        }
    }

    private fun shouldIncludeNotification(notification: CapturedNotification): Boolean {
        // Don't show our own notifications
        if (notification.packageName == packageName) {
            android.util.Log.d("NotificationListener", "Filtered: ${notification.packageName} - own package")
            return false
        }

        // Must have either title or text
        if (notification.title.isNullOrBlank() && notification.text.isNullOrBlank()) {
            android.util.Log.d("NotificationListener", "Filtered: ${notification.packageName} - no title/text")
            return false
        }

        android.util.Log.d("NotificationListener", "INCLUDED: ${notification.appName} - ${notification.packageName}")
        return true
    }


    private fun isSensitiveNotification(packageName: String, title: String?, text: String?): Boolean {
        // List of known personal/sensitive app packages
        val personalAppPackages = setOf(
            // Email
            "com.google.android.gm", // Gmail
            "com.microsoft.office.outlook", // Outlook
            "com.yahoo.mobile.client.android.mail", // Yahoo Mail
            "com.android.email",

            // Messaging
            "com.whatsapp", // WhatsApp
            "org.thoughtcrime.securesms", // Signal
            "org.telegram.messenger", // Telegram
            "com.facebook.orca", // Messenger
            "com.viber.voip", // Viber
            "jp.naver.line.android", // LINE
            "com.skype.raider", // Skype
            "com.snapchat.android", // Snapchat
            "com.discord", // Discord
            "com.slack", // Slack

            // Social Media
            "com.instagram.android", // Instagram
            "com.facebook.katana", // Facebook
            "com.twitter.android", // Twitter
            "com.linkedin.android", // LinkedIn
            "com.reddit.frontpage", // Reddit
            "com.zhiliaoapp.musically", // TikTok

            // Banking & Finance
            "com.paypal.android.p2pmobile", // PayPal
            "com.venmo", // Venmo
            "com.chase.sig.android", // Chase
            "com.bankofamerica.mobile", // Bank of America
            "com.wellsfargo.mobile.android", // Wells Fargo
            "com.revolut.revolut", // Revolut
            "com.coinbase.android", // Coinbase

            // Dating
            "com.tinder", // Tinder
            "com.bumble.app", // Bumble
            "co.hinge.app", // Hinge
            "com.match.android", // Match

            // Shopping & Delivery
            "com.amazon.mShop.android.shopping", // Amazon
            "com.ubercab", // Uber
            "com.ubercab.eats", // Uber Eats
            "com.grubhub.android", // GrubHub
            "com.doordash.android", // DoorDash

            // Health & Medical
            "com.google.android.apps.fitness", // Google Fit
            "com.myfitnesspal.android", // MyFitnessPal
            "com.headspace.android", // Headspace
        )

        // Check if package is in personal apps list
        if (personalAppPackages.contains(packageName)) {
            return true
        }

        // Check for sensitive keywords in content
        val sensitiveKeywords = listOf(
            // Banking/Finance keywords
            "otp", "code", "verification", "bank", "payment", "card", "transaction",
            "security code", "passcode", "pin", "cvv", "account", "balance",

            // Personal communication
            "message", "chat", "replied", "sent you", "tagged you",

            // Private information
            "password", "login", "sign in", "verify", "confirm",

            // Medical/Health
            "appointment", "prescription", "doctor", "health", "medical"
        )

        val packageLower = packageName.lowercase()
        val titleLower = title?.lowercase() ?: ""
        val textLower = text?.lowercase() ?: ""
        val combined = "$packageLower $titleLower $textLower"

        return sensitiveKeywords.any { keyword ->
            combined.contains(keyword)
        }
    }
}
