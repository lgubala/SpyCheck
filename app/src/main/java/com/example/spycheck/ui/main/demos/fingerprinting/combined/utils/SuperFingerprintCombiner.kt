package com.example.spycheck

import android.content.Context
import com.example.spycheck.ui.main.demos.fingerprinting.sensor.utils.SensorFingerprint
import java.security.MessageDigest

/**
 * Super Fingerprint - Combines ALL 6 fingerprinting methods
 *
 * This is what real tracking companies do - they combine multiple
 * fingerprinting techniques to create an EXTREMELY unique identifier
 * that persists across:
 * - VPN usage
 * - Private browsing
 * - App uninstalls
 * - Factory resets (if you reinstall same apps)
 * - Different accounts
 */

data class SuperFingerprint(
    val superFingerprintId: String,
    val globalUniqueness: String,
    val componentFingerprints: ComponentFingerprints,
    val confidenceScore: Int, // 0-100, how confident this ID is
    val trackingResistance: String, // How hard it is to avoid this fingerprint
    val persistenceLevel: String, // How long this fingerprint lasts
    val crossPlatformTracking: Boolean, // Can this track across web/mobile
    val combinedFactors: List<String>
)

data class ComponentFingerprints(
    val deviceId: String?,
    val sensorId: String?,
    val batteryId: String?,
    val audioId: String?,
    val networkId: String?,
    val performanceId: String?,
    val componentsAnalyzed: Int,
    val totalComponents: Int
)

class SuperFingerprintCombiner(private val context: Context) {

    fun combineFingerprints(
        device: DeviceFingerprint?,
        sensor: SensorFingerprint?,
        battery: BatteryFingerprint?,
        audio: AudioFingerprint?,
        network: NetworkFingerprint?,
        performance: PerformanceFingerprint?
    ): SuperFingerprint {

        val components = listOfNotNull(
            device?.fingerprintId,
            sensor?.fingerprintId,
            battery?.fingerprintId,
            audio?.fingerprintId,
            network?.fingerprintId,
            performance?.fingerprintId
        )

        val componentsAnalyzed = components.size

        // Combine all IDs into one super ID
        val superFingerprintId = if (components.isNotEmpty()) {
            hashString(components.joinToString(":"))
        } else {
            context.getString(R.string.fp_super_combiner_no_data)
        }

        // Calculate global uniqueness (multiply all individual uniqueness scores)
        val globalUniqueness = calculateGlobalUniqueness(
            device, sensor, battery, audio, network, performance
        )

        // Calculate confidence score
        val confidenceScore = calculateConfidenceScore(componentsAnalyzed)

        // Determine tracking resistance level
        val trackingResistance = determineTrackingResistance(componentsAnalyzed)

        // Determine persistence level
        val persistenceLevel = determinePersistence(device, sensor, battery)

        // Check if can track cross-platform
        val crossPlatformTracking = device != null && audio != null && performance != null

        // Identify combined factors
        val combinedFactors = identifyCombinedFactors(
            device, sensor, battery, audio, network, performance
        )

        return SuperFingerprint(
            superFingerprintId = superFingerprintId,
            globalUniqueness = globalUniqueness,
            componentFingerprints = ComponentFingerprints(
                deviceId = device?.fingerprintId,
                sensorId = sensor?.fingerprintId,
                batteryId = battery?.fingerprintId,
                audioId = audio?.fingerprintId,
                networkId = network?.fingerprintId,
                performanceId = performance?.fingerprintId,
                componentsAnalyzed = componentsAnalyzed,
                totalComponents = 6
            ),
            confidenceScore = confidenceScore,
            trackingResistance = trackingResistance,
            persistenceLevel = persistenceLevel,
            crossPlatformTracking = crossPlatformTracking,
            combinedFactors = combinedFactors
        )
    }

    private fun calculateGlobalUniqueness(
        device: DeviceFingerprint?,
        sensor: SensorFingerprint?,
        battery: BatteryFingerprint?,
        audio: AudioFingerprint?,
        network: NetworkFingerprint?,
        performance: PerformanceFingerprint?
    ): String {
        // Extract numbers from uniqueness scores and multiply them
        var totalUniqueness = 1L

        device?.let { totalUniqueness *= extractUniquenessNumber(it.uniquenessScore) }
        sensor?.let { totalUniqueness *= extractUniquenessNumber(it.uniquenessScore) }
        battery?.let { totalUniqueness *= extractUniquenessNumber(it.uniquenessScore) }
        audio?.let { totalUniqueness *= extractUniquenessNumber(it.uniquenessScore) }
        network?.let { totalUniqueness *= extractUniquenessNumber(it.uniquenessScore) }
        performance?.let { totalUniqueness *= extractUniquenessNumber(it.uniquenessScore) }

        return when {
            totalUniqueness > 1_000_000_000_000_000 -> context.getString(R.string.fp_super_combiner_quintillions)
            totalUniqueness > 1_000_000_000_000 -> context.getString(R.string.fp_super_combiner_trillion, totalUniqueness / 1_000_000_000_000)
            totalUniqueness > 1_000_000_000 -> context.getString(R.string.fp_super_combiner_billion, totalUniqueness / 1_000_000_000)
            totalUniqueness > 1_000_000 -> context.getString(R.string.fp_super_combiner_million, totalUniqueness / 1_000_000)
            else -> context.getString(R.string.fp_super_combiner_k, totalUniqueness / 1_000)
        }
    }

    private fun extractUniquenessNumber(uniquenessScore: String): Long {
        // Extract number from strings like "1 in 50 million" or "1 in 5K"
        val parts = uniquenessScore.split(" ")
        if (parts.size < 3) return 1000L

        val numberPart = parts[2].replace(",", "")
        return when {
            numberPart.contains("million", ignoreCase = true) -> {
                numberPart.replace("million", "", ignoreCase = true).trim().toLongOrNull()?.times(1_000_000L) ?: 1000L
            }
            numberPart.contains("billion", ignoreCase = true) -> {
                numberPart.replace("billion", "", ignoreCase = true).trim().toLongOrNull()?.times(1_000_000_000L) ?: 1000L
            }
            numberPart.contains("K", ignoreCase = true) -> {
                numberPart.replace("K", "", ignoreCase = true).trim().toLongOrNull()?.times(1000L) ?: 1000L
            }
            else -> numberPart.toLongOrNull() ?: 1000L
        }
    }

    private fun calculateConfidenceScore(componentsAnalyzed: Int): Int {
        // More components = higher confidence
        return when (componentsAnalyzed) {
            0 -> 0
            1 -> 30
            2 -> 50
            3 -> 70
            4 -> 85
            5 -> 95
            6 -> 99 // Almost certain identification with all 6!
            else -> 99
        }
    }

    private fun determineTrackingResistance(componentsAnalyzed: Int): String {
        return when (componentsAnalyzed) {
            0 -> context.getString(R.string.fp_super_combiner_resistance_unknown)
            1 -> context.getString(R.string.fp_super_combiner_resistance_very_easy)
            2 -> context.getString(R.string.fp_super_combiner_resistance_easy)
            3 -> context.getString(R.string.fp_super_combiner_resistance_moderate)
            4 -> context.getString(R.string.fp_super_combiner_resistance_hard)
            5 -> context.getString(R.string.fp_super_combiner_resistance_very_hard)
            6 -> context.getString(R.string.fp_super_combiner_resistance_impossible)
            else -> context.getString(R.string.fp_super_combiner_resistance_impossible_default)
        }
    }

    private fun determinePersistence(
        device: DeviceFingerprint?,
        sensor: SensorFingerprint?,
        battery: BatteryFingerprint?
    ): String {
        val hasHardwareFingerprints = sensor != null || battery != null
        val hasDeviceFingerprint = device != null

        return when {
            hasHardwareFingerprints && hasDeviceFingerprint ->
                context.getString(R.string.fp_super_combiner_persistence_permanent)
            hasHardwareFingerprints ->
                context.getString(R.string.fp_super_combiner_persistence_long_term)
            hasDeviceFingerprint ->
                context.getString(R.string.fp_super_combiner_persistence_medium_term)
            else ->
                context.getString(R.string.fp_super_combiner_persistence_unknown)
        }
    }

    private fun identifyCombinedFactors(
        device: DeviceFingerprint?,
        sensor: SensorFingerprint?,
        battery: BatteryFingerprint?,
        audio: AudioFingerprint?,
        network: NetworkFingerprint?,
        performance: PerformanceFingerprint?
    ): List<String> {
        val factors = mutableListOf<String>()

        if (device != null && sensor != null) {
            factors.add(context.getString(R.string.fp_super_combiner_factor_device_sensor))
        }

        if (battery != null && performance != null) {
            factors.add(context.getString(R.string.fp_super_combiner_factor_battery_performance))
        }

        if (network != null && device != null) {
            factors.add(context.getString(R.string.fp_super_combiner_factor_network_device))
        }

        if (audio != null && performance != null) {
            factors.add(context.getString(R.string.fp_super_combiner_factor_audio_performance))
        }

        if (device != null) {
            factors.add(context.getString(R.string.fp_super_combiner_factor_apps))
        }

        if (sensor != null) {
            factors.add(context.getString(R.string.fp_super_combiner_factor_sensor))
        }

        if (battery != null) {
            factors.add(context.getString(R.string.fp_super_combiner_factor_battery))
        }

        if (network != null) {
            factors.add(context.getString(R.string.fp_super_combiner_factor_network))
        }

        val componentCount = listOf(device, sensor, battery, audio, network, performance).count { it != null }

        if (componentCount >= 4) {
            factors.add(context.getString(R.string.fp_super_combiner_critical_warning, componentCount))
            factors.add(context.getString(R.string.fp_super_combiner_critical_browsers))
            factors.add(context.getString(R.string.fp_super_combiner_critical_accounts))
            factors.add(context.getString(R.string.fp_super_combiner_critical_vpn))
            factors.add(context.getString(R.string.fp_super_combiner_critical_private))
            factors.add(context.getString(R.string.fp_super_combiner_critical_reinstalls))
        }

        return factors
    }

    private fun hashString(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}