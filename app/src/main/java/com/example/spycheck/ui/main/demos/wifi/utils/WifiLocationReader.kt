package com.example.spycheck.ui.main.demos.wifi.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WifiLocationReader(private val context: Context) {

    suspend fun scanNearbyNetworks(): WifiScanResult = withContext(Dispatchers.IO) {
        val allNetworks = mutableListOf<WifiNetwork>()

        // Get current connected network
        val currentNetwork = getCurrentConnectedNetwork()
        if (currentNetwork != null) {
            allNetworks.add(currentNetwork)
        }

        // Get cached nearby networks
        val cachedNetworks = getCachedNearbyNetworks()
        cachedNetworks.forEach { network ->
            if (network.bssid != currentNetwork?.bssid) {
                allNetworks.add(network)
            }
        }

        WifiScanResult(
            networks = allNetworks,
            totalCount = allNetworks.size
        )
    }

    private fun getCurrentConnectedNetwork(): WifiNetwork? {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            ?: return null

        if (!wifiManager.isWifiEnabled) return null

        val connectionInfo: WifiInfo = try {
            wifiManager.connectionInfo
        } catch (e: SecurityException) {
            return null
        }

        val bssid = connectionInfo.bssid ?: return null
        if (bssid == "02:00:00:00:00:00") return null

        val ssid = connectionInfo.ssid?.removeSurrounding("\"") ?: "Unknown"
        val rssi = connectionInfo.rssi
        val frequency = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectionInfo.frequency
        } else {
            0
        }

        return WifiNetwork(
            ssid = ssid,
            bssid = bssid,
            signalStrength = rssi,
            frequency = frequency,
            isConnected = true
        )
    }

    private fun getCachedNearbyNetworks(): List<WifiNetwork> {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            ?: return emptyList()

        if (!wifiManager.isWifiEnabled) return emptyList()

        @Suppress("DEPRECATION")
        val scanResults = try {
            wifiManager.scanResults
        } catch (e: SecurityException) {
            return emptyList()
        }

        return scanResults.mapNotNull { result ->
            val ssid = result.SSID
            if (ssid.isBlank()) {
                null
            } else {
                WifiNetwork(
                    ssid = ssid,
                    bssid = result.BSSID,
                    signalStrength = result.level,
                    frequency = result.frequency,
                    isConnected = false
                )
            }
        }.sortedByDescending { it.signalStrength }
            .take(20)
    }

    fun getGoogleApiJsonFormat(networks: List<WifiNetwork>): String {
        val wifiAccessPoints = networks.take(10).joinToString(",\n    ") { network ->
            """
            {
              "macAddress": "${network.bssid}",
              "signalStrength": ${network.signalStrength},
              "signalToNoiseRatio": 0
            }
            """.trimIndent()
        }

        return """
{
  "considerIp": false,
  "wifiAccessPoints": [
    $wifiAccessPoints
  ]
}
        """.trimIndent()
    }

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasWifiPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.NEARBY_WIFI_DEVICES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Not needed on older versions
        }
    }
}