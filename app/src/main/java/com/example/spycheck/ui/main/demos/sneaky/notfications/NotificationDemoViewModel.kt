package com.example.spycheck.ui.main.demos.sneaky.notifications

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.spycheck.ui.main.demos.sneaky.notifications.utils.NotificationAccessHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for Notification Stealing Demo
 * Manages notification access permission state
 */
class NotificationDemoViewModel : ViewModel() {

    private val _hasPermission = MutableStateFlow(false)
    val hasPermission: StateFlow<Boolean> = _hasPermission.asStateFlow()

    /**
     * Check if notification access permission is granted
     */
    fun checkPermission(context: Context) {
        _hasPermission.value = NotificationAccessHelper.isNotificationAccessGranted(context)
    }

    /**
     * Update permission state (called when returning from settings)
     */
    fun updatePermission(granted: Boolean) {
        _hasPermission.value = granted
    }
}
