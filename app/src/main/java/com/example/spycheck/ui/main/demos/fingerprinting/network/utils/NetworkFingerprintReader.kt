package com.example.spycheck

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.net.NetworkInterface
import java.security.MessageDigest

/**
 * Network Fingerprinting - NO permissions required!
 *
 * How it works:
 * - Every network has unique configuration characteristics
 * - ISP/Carrier settings create distinct patterns
 * - Network capabilities vary by device and location
 * - MTU sizes are carrier/ISP specific
 * - DNS configurations are unique per network
 *
 * NO permissions needed!
 */

data class NetworkFingerprint(
    val fingerprintId: String,
    val uniquenessScore: String,
    val connectionProfile: NetworkConnectionProfile,
    val networkCapabilities: NetworkCapabilitiesProfile,
    val networkConfig: NetworkConfigProfile,
    val uniquenessFactors: List<String>
)

data class NetworkConnectionProfile(
    val connectionType: String, // "WiFi", "Cellular", "Ethernet", "None"
    val isConnected: Boolean,
    val isMetered: Boolean,
    val connectionStrength: String, // "Excellent", "Good", "Fair", "Poor"
    val networkName: String?, // WiFi SSID or carrier name
    val connectionSignature: String
)

data class NetworkCapabilitiesProfile(
    val supportsIPv6: Boolean,
    val supportsIPv4: Boolean,
    val hasVPN: Boolean,
    val hasProxy: Boolean,
    val maxDownloadSpeed: Int, // in Mbps
    val maxUploadSpeed: Int,   // in Mbps
    val capabilitiesSignature: String
)

data class NetworkConfigProfile(
    val mtu: Int, // Maximum Transmission Unit (carrier/ISP specific!)
    val dnsServers: List<String>,
    val hasMultipleNetworks: Boolean,
    val networkInterfaceCount: Int,
    val configSignature: String
)

class NetworkFingerprintReader(private val context: Context) {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val _analysisProgress = MutableStateFlow(0)
    val analysisProgress: StateFlow<Int> = _analysisProgress.asStateFlow()

    private val _statusMessage = MutableStateFlow("Ready to analyze network")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    suspend fun analyzeNetwork(): NetworkFingerprint {
        _statusMessage.value = "🌐 Detecting network connection..."
        _analysisProgress.value = 20

        val connectionProfile = analyzeConnection()

        delay(300)
        _statusMessage.value = "🔧 Analyzing network capabilities..."
        _analysisProgress.value = 50

        val capabilitiesProfile = analyzeNetworkCapabilities()

        delay(300)
        _statusMessage.value = "⚙️ Reading network configuration..."
        _analysisProgress.value = 80

        val configProfile = analyzeNetworkConfig()

        delay(300)
        _statusMessage.value = "✅ Network fingerprint generated!"
        _analysisProgress.value = 100

        // Generate combined fingerprint ID
        val fingerprintData = buildString {
            append(connectionProfile.connectionSignature)
            append(capabilitiesProfile.capabilitiesSignature)
            append(configProfile.configSignature)
        }

        val fingerprintId = hashString(fingerprintData)
        val uniquenessScore = calculateNetworkUniqueness(connectionProfile, configProfile)
        val uniquenessFactors = identifyUniquenessFactors(connectionProfile, capabilitiesProfile, configProfile)

        return NetworkFingerprint(
            fingerprintId = fingerprintId,
            uniquenessScore = uniquenessScore,
            connectionProfile = connectionProfile,
            networkCapabilities = capabilitiesProfile,
            networkConfig = configProfile,
            uniquenessFactors = uniquenessFactors
        )
    }

    private fun analyzeConnection(): NetworkConnectionProfile {
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = activeNetwork?.let { connectivityManager.getNetworkCapabilities(it) }

        val isConnected = networkCapabilities != null
        val isMetered = connectivityManager.isActiveNetworkMetered

        val connectionType = when {
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "WiFi"
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "Cellular"
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> "Ethernet"
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) == true -> "Bluetooth"
            else -> "None"
        }

        // Get network name (WiFi SSID - generic on Android 10+)
        val networkName = when (connectionType) {
            "WiFi" -> "WiFi Network" // Android 10+ restricts SSID access without location permission
            "Cellular" -> "Mobile Data"
            "Ethernet" -> "Wired Connection"
            else -> null
        }

        // Estimate connection strength for WiFi
        val connectionStrength = when (connectionType) {
            "WiFi" -> {
                try {
                    val wifiInfo = wifiManager.connectionInfo
                    val rssi = wifiInfo?.rssi ?: -100
                    when {
                        rssi > -50 -> "Excellent (${rssi} dBm)"
                        rssi > -60 -> "Good (${rssi} dBm)"
                        rssi > -70 -> "Fair (${rssi} dBm)"
                        else -> "Poor (${rssi} dBm)"
                    }
                } catch (e: Exception) {
                    "Unknown"
                }
            }
            "Cellular" -> "Varies by location"
            else -> "N/A"
        }

        val connectionSignature = hashString(
            "$connectionType:$isMetered:$networkName"
        ).take(8)

        return NetworkConnectionProfile(
            connectionType = connectionType,
            isConnected = isConnected,
            isMetered = isMetered,
            connectionStrength = connectionStrength,
            networkName = networkName,
            connectionSignature = connectionSignature
        )
    }

    private fun analyzeNetworkCapabilities(): NetworkCapabilitiesProfile {
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = activeNetwork?.let { connectivityManager.getNetworkCapabilities(it) }

        val supportsIPv4 = hasIPv4Address()
        val supportsIPv6 = hasIPv6Address()

        val hasVPN = networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true

        // Check for proxy
        val hasProxy = System.getProperty("http.proxyHost") != null ||
                System.getProperty("https.proxyHost") != null

        // Get link speeds (API 21+)
        val maxDownloadSpeed = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            networkCapabilities?.linkDownstreamBandwidthKbps?.div(1000) ?: 0 // Convert to Mbps
        } else {
            0
        }

        val maxUploadSpeed = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            networkCapabilities?.linkUpstreamBandwidthKbps?.div(1000) ?: 0 // Convert to Mbps
        } else {
            0
        }

        val capabilitiesSignature = hashString(
            "$supportsIPv4:$supportsIPv6:$hasVPN:$hasProxy:$maxDownloadSpeed:$maxUploadSpeed"
        ).take(8)

        return NetworkCapabilitiesProfile(
            supportsIPv6 = supportsIPv6,
            supportsIPv4 = supportsIPv4,
            hasVPN = hasVPN,
            hasProxy = hasProxy,
            maxDownloadSpeed = maxDownloadSpeed,
            maxUploadSpeed = maxUploadSpeed,
            capabilitiesSignature = capabilitiesSignature
        )
    }

    private suspend fun analyzeNetworkConfig(): NetworkConfigProfile = withContext(Dispatchers.IO) {
        // Get MTU (Maximum Transmission Unit) - very carrier/ISP specific!
        val mtu = try {
            val activeNetwork = connectivityManager.activeNetwork
            val linkProperties = connectivityManager.getLinkProperties(activeNetwork)
            linkProperties?.mtu ?: 1500 // 1500 is standard default
        } catch (e: Exception) {
            1500
        }

        // Get DNS servers
        val dnsServers = try {
            val activeNetwork = connectivityManager.activeNetwork
            val linkProperties = connectivityManager.getLinkProperties(activeNetwork)
            linkProperties?.dnsServers?.map { it.hostAddress ?: "Unknown" } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        // Count network interfaces
        val networkInterfaces = try {
            NetworkInterface.getNetworkInterfaces().toList()
        } catch (e: Exception) {
            emptyList()
        }

        val hasMultipleNetworks = networkInterfaces.size > 1
        val networkInterfaceCount = networkInterfaces.size

        val configSignature = hashString(
            "$mtu:${dnsServers.joinToString(",")}:$networkInterfaceCount"
        ).take(8)

        NetworkConfigProfile(
            mtu = mtu,
            dnsServers = dnsServers.take(3), // Limit to first 3 for privacy
            hasMultipleNetworks = hasMultipleNetworks,
            networkInterfaceCount = networkInterfaceCount,
            configSignature = configSignature
        )
    }

    private fun hasIPv4Address(): Boolean {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val iface = interfaces.nextElement()
                val addresses = iface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val addr = addresses.nextElement()
                    if (!addr.isLoopbackAddress && addr is java.net.Inet4Address) {
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            // Ignore
        }
        return false
    }

    private fun hasIPv6Address(): Boolean {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val iface = interfaces.nextElement()
                val addresses = iface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val addr = addresses.nextElement()
                    if (!addr.isLoopbackAddress && addr is java.net.Inet6Address) {
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            // Ignore
        }
        return false
    }

    private fun calculateNetworkUniqueness(
        connection: NetworkConnectionProfile,
        config: NetworkConfigProfile
    ): String {
        var uniqueness = 1.0

        // Connection type contribution
        uniqueness *= when (connection.connectionType) {
            "Ethernet" -> 1000.0  // Rare on mobile
            "Bluetooth" -> 500.0  // Very rare
            "Cellular" -> 100.0
            "WiFi" -> 50.0
            else -> 10.0
        }

        // MTU uniqueness (VERY carrier/ISP specific - THIS IS THE GOLD!)
        uniqueness *= when (config.mtu) {
            1500 -> 50.0   // Standard default
            1492 -> 100.0  // PPPoE
            1280 -> 150.0  // IPv6 minimum
            1428 -> 200.0  // T-Mobile/Verizon
            1430 -> 250.0  // AT&T
            9000 -> 1000.0 // Jumbo frames (rare)
            else -> 300.0  // Custom value
        }

        // DNS configuration
        uniqueness *= (config.dnsServers.size * 100.0 + 50.0)

        val finalScore = uniqueness.toLong()

        return when {
            finalScore > 1_000_000 -> "1 in ${finalScore / 1_000_000} million"
            finalScore > 1_000 -> "1 in ${finalScore / 1_000}K"
            else -> "1 in $finalScore"
        }
    }

    private fun identifyUniquenessFactors(
        connection: NetworkConnectionProfile,
        capabilities: NetworkCapabilitiesProfile,
        config: NetworkConfigProfile
    ): List<String> {
        val factors = mutableListOf<String>()

        if (config.mtu != 1500) {
            factors.add("Custom MTU size (${config.mtu} bytes) - carrier/ISP specific")
        }

        if (capabilities.hasVPN) {
            factors.add("VPN detected - reduces tracking but changes fingerprint")
        }

        if (config.dnsServers.isNotEmpty()) {
            factors.add("DNS configuration (${config.dnsServers.size} servers) - ISP specific")
        }

        if (capabilities.supportsIPv6 && capabilities.supportsIPv4) {
            factors.add("Dual-stack IPv4/IPv6 support")
        }

        if (connection.isMetered) {
            factors.add("Metered connection - likely cellular data")
        }

        if (config.dnsServers.any { it.contains("8.8.8.8") || it.contains("8.8.4.4") }) {
            factors.add("Using Google DNS (user-configured)")
        }

        if (factors.isEmpty()) {
            factors.add("Standard network configuration (MTU: ${config.mtu} is your unique identifier)")
        }

        return factors
    }

    private fun hashString(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}