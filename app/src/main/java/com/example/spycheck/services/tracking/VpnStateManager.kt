package com.example.spycheck.services.tracking

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Singleton to manage VPN state across the app
 */
object VpnStateManager {
    private val _isVpnRunning = MutableStateFlow(false)
    val isVpnRunning = _isVpnRunning.asStateFlow()

    private val _isOverlayRunning = MutableStateFlow(false)
    val isOverlayRunning = _isOverlayRunning.asStateFlow()

    fun setVpnRunning(running: Boolean) {
        _isVpnRunning.value = running
    }

    fun setOverlayRunning(running: Boolean) {
        _isOverlayRunning.value = running
    }

    fun isMonitoring(): Boolean {
        return _isVpnRunning.value && _isOverlayRunning.value
    }
}