package com.example.spycheck.ui.main.demos.sneaky.wifi.utils

import android.net.Uri

data class WifiNetwork(
    val ssid: String,
    val bssid: String,
    val signalStrength: Int,
    val frequency: Int = 0,
    val isConnected: Boolean = false
)

data class WifiScanResult(
    val networks: List<WifiNetwork>,
    val totalCount: Int
)

