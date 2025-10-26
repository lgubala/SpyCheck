// WifiLocationReader.kt - Replace lines 102, 151, 164, 191 - Change WifiNetworkInfo to WifiNetwork

package com.example.spycheck.ui.main.demos.sneaky.wifi.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat

class WifiLocationReader(private val context: Context) {
    private val TAG = "WifiLocationReader"

    suspend fun scanNearbyNetworks(): WifiScanResult {
        Log.d(TAG, "=== Scanning WiFi Networks (Hacker Mode) ===")
        checkPermissions()

        val allNetworks = mutableListOf<WifiNetwork>()

        val currentNetwork = getCurrentConnectedNetwork(context)
        if (currentNetwork != null) {
            allNetworks.add(
                WifiNetwork(
                    ssid = "${currentNetwork.ssid} ⭐ CONNECTED",
                    bssid = currentNetwork.bssid,
                    signalStrength = currentNetwork.signalStrength,
                    frequency = currentNetwork.frequency,
                    isConnected = true
                )
            )
            Log.d(TAG, "✅ Added current connected network")
        }

        val cachedNetworks = getCachedNearbyNetworks(context)
        cachedNetworks.forEach { network ->
            if (network.bssid != currentNetwork?.bssid) {
                allNetworks.add(network)
            }
        }

        return WifiScanResult(networks = allNetworks, totalCount = allNetworks.size)
    }

    private fun checkPermissions() {
        val fineLocation = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        Log.d(TAG, "ACCESS_FINE_LOCATION: $fineLocation")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val nearbyWifi = hasPermission(Manifest.permission.NEARBY_WIFI_DEVICES)
            Log.d(TAG, "NEARBY_WIFI_DEVICES: $nearbyWifi")
        }
    }

    private fun getCurrentConnectedNetwork(context: Context): WifiNetwork? {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        if (wifiManager == null || !wifiManager.isWifiEnabled) return null

        val connectionInfo: WifiInfo = try {
            wifiManager.connectionInfo
        } catch (e: SecurityException) {
            return null
        }

        val bssid = connectionInfo.bssid
        val ssid = connectionInfo.ssid?.removeSurrounding("\"") ?: "Unknown"
        val rssi = connectionInfo.rssi
        val frequency = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectionInfo.frequency
        } else {
            0
        }

        if (bssid == null || bssid == "02:00:00:00:00:00") return null

        return WifiNetwork(
            ssid = ssid,
            bssid = bssid,
            signalStrength = rssi,
            frequency = frequency,
            isConnected = true
        )
    }

    private fun getCachedNearbyNetworks(context: Context): List<WifiNetwork> {
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
            if (result.SSID.isBlank()) null else WifiNetwork(
                ssid = result.SSID,
                bssid = result.BSSID,
                signalStrength = result.level,
                frequency = result.frequency,
                isConnected = false
            )
        }.sortedByDescending { it.signalStrength }.take(20)
    }

    fun getGoogleApiJsonFormat(networks: List<WifiNetwork>): String {
        val wifiAccessPoints = networks.take(10).joinToString(",\n    ") { network ->
            """{"macAddress": "${network.bssid}","signalStrength": ${network.signalStrength},"signalToNoiseRatio": 0}""".trimIndent()
        }
        return """{"considerIp": false,"wifiAccessPoints": [$wifiAccessPoints]}""".trimIndent()
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}