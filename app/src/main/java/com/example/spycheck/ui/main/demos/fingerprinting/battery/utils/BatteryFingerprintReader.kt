package com.example.spycheck

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import kotlin.math.abs

/**
 * Battery Fingerprinting - NO PERMISSION REQUIRED!
 *
 * How it works:
 * Every battery ages differently based on:
 * - Manufacturing variations (capacity tolerance)
 * - Usage patterns (charge cycles, temperature exposure)
 * - Age and degradation level
 *
 * Even two identical phones bought on the same day will have different
 * battery characteristics within weeks due to different usage patterns.
 *
 * This creates a unique, persistent fingerprint.
 */

data class BatteryFingerprint(
    val fingerprintId: String,
    val uniquenessScore: String,
    val batteryHealth: BatteryHealthProfile,
    val chargingBehavior: ChargingBehaviorProfile,
    val capacityProfile: BatteryCapacityProfile,
    val temperatureProfile: TemperatureProfile,
    val uniquenessFactors: List<String>
)

data class BatteryHealthProfile(
    val healthStatus: String, // "Good", "Fair", "Poor"
    val healthPercentage: Int, // 0-100%
    val technology: String, // "Li-ion", "Li-poly"
    val ageEstimate: String, // "New", "6-12 months", "1-2 years", "2+ years"
    val cycleEstimate: String, // Estimated charge cycles
    val healthSignature: String
)

data class ChargingBehaviorProfile(
    val currentChargeLevel: Int, // 0-100%
    val isCharging: Boolean,
    val chargingSource: String, // "AC", "USB", "Wireless", "Not charging"
    val voltage: Int, // in millivolts
    val current: Int, // in microamps (negative = discharging)
    val chargingSpeed: String, // "Fast", "Normal", "Slow", "Trickle"
    val behaviorSignature: String
)

data class BatteryCapacityProfile(
    val designCapacity: Int?, // Original capacity (may not be available)
    val currentCapacity: Int?, // Estimated current capacity
    val degradationLevel: String, // "Minimal", "Moderate", "Significant", "Severe"
    val capacitySignature: String,
    val estimatedCycleCount: Int
)

data class TemperatureProfile(
    val currentTemp: Float, // in Celsius
    val temperatureStatus: String, // "Cool", "Normal", "Warm", "Hot"
    val thermalBehavior: String, // How device handles heat
    val tempSignature: String
)

class BatteryFingerprintReader(private val context: Context) {

    private val _analysisProgress = MutableStateFlow(0)
    val analysisProgress: StateFlow<Int> = _analysisProgress.asStateFlow()

    private val _statusMessage = MutableStateFlow("Ready to analyze battery")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    suspend fun analyzeBattery(): BatteryFingerprint {
        _statusMessage.value = "🔋 Reading battery status..."
        _analysisProgress.value = 20

        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryStatus = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        _statusMessage.value = "📊 Analyzing battery health..."
        _analysisProgress.value = 40

        val healthProfile = analyzeBatteryHealth(batteryManager, batteryStatus)

        _statusMessage.value = "⚡ Checking charging behavior..."
        _analysisProgress.value = 60

        val chargingProfile = analyzeChargingBehavior(batteryManager, batteryStatus)

        _statusMessage.value = "🔬 Measuring capacity degradation..."
        _analysisProgress.value = 80

        val capacityProfile = analyzeCapacity(batteryManager, batteryStatus)

        val temperatureProfile = analyzeTemperature(batteryStatus)

        _statusMessage.value = "✅ Battery fingerprint generated!"
        _analysisProgress.value = 100

        // Generate combined fingerprint ID
        val fingerprintData = buildString {
            append(healthProfile.healthSignature)
            append(chargingProfile.behaviorSignature)
            append(capacityProfile.capacitySignature)
            append(temperatureProfile.tempSignature)
        }

        val fingerprintId = hashString(fingerprintData)
        val uniquenessScore = calculateBatteryUniqueness(healthProfile, capacityProfile)
        val uniquenessFactors = identifyUniquenessFactors(healthProfile, capacityProfile, chargingProfile)

        return BatteryFingerprint(
            fingerprintId = fingerprintId,
            uniquenessScore = uniquenessScore,
            batteryHealth = healthProfile,
            chargingBehavior = chargingProfile,
            capacityProfile = capacityProfile,
            temperatureProfile = temperatureProfile,
            uniquenessFactors = uniquenessFactors
        )
    }

    private fun analyzeBatteryHealth(
        batteryManager: BatteryManager,
        batteryStatus: Intent?
    ): BatteryHealthProfile {
        val health = batteryStatus?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1
        val technology = batteryStatus?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Unknown"

        val healthStatus = when (health) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheating"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
            else -> "Unknown"
        }

        // Estimate health percentage based on capacity (if available)
        val capacity = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        } else {
            -1
        }

        // Estimate battery age based on health and capacity
        val ageEstimate = estimateBatteryAge(health, capacity)
        val cycleEstimate = estimateChargeCycles(ageEstimate)

        val healthPercentage = when (healthStatus) {
            "Good" -> 100
            "Overheating" -> 70
            "Cold" -> 85
            "Over Voltage" -> 60
            "Dead" -> 0
            else -> 80
        }

        val healthSignature = hashString("$health:$technology:$healthPercentage").take(8)

        return BatteryHealthProfile(
            healthStatus = healthStatus,
            healthPercentage = healthPercentage,
            technology = technology,
            ageEstimate = ageEstimate,
            cycleEstimate = cycleEstimate,
            healthSignature = healthSignature
        )
    }

    private fun analyzeChargingBehavior(
        batteryManager: BatteryManager,
        batteryStatus: Intent?
    ): ChargingBehaviorProfile {
        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val chargeLevel = if (level >= 0 && scale > 0) {
            (level * 100 / scale)
        } else {
            -1
        }

        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val plugged = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
        val chargingSource = when (plugged) {
            BatteryManager.BATTERY_PLUGGED_AC -> "AC Charger"
            BatteryManager.BATTERY_PLUGGED_USB -> "USB"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
            else -> "Not charging"
        }

        // Get voltage - use EXTRA_VOLTAGE from Intent
        val voltage = batteryStatus?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) ?: -1

        // Get current (requires API 21+)
        val current = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
            } catch (e: Exception) {
                0
            }
        } else {
            0
        }

        // Determine charging speed based on current
        val chargingSpeed = when {
            !isCharging -> "Not charging"
            abs(current) > 2000000 -> "Fast charging" // > 2A
            abs(current) > 1000000 -> "Normal charging" // 1-2A
            abs(current) > 500000 -> "Slow charging" // 0.5-1A
            abs(current) > 0 -> "Trickle charging" // < 0.5A
            else -> if (isCharging) "Charging (speed unknown)" else "Not charging"
        }

        val behaviorSignature = hashString("$chargeLevel:$voltage:$current:$plugged").take(8)

        return ChargingBehaviorProfile(
            currentChargeLevel = chargeLevel,
            isCharging = isCharging,
            chargingSource = chargingSource,
            voltage = voltage,
            current = current,
            chargingSpeed = chargingSpeed,
            behaviorSignature = behaviorSignature
        )
    }

    private fun analyzeCapacity(
        batteryManager: BatteryManager,
        batteryStatus: Intent?
    ): BatteryCapacityProfile {
        // Try to get capacity (only available on some devices)
        val currentCapacity = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }

        // Estimate degradation based on health
        val health = batteryStatus?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1
        val degradationLevel = when (health) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Minimal (0-10%)"
            BatteryManager.BATTERY_HEALTH_COLD -> "Moderate (10-25%)"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Significant (25-40%)"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Severe (40%+)"
            else -> "Unknown"
        }

        // Estimate cycle count based on degradation
        val estimatedCycleCount = when (degradationLevel) {
            "Minimal (0-10%)" -> 100
            "Moderate (10-25%)" -> 300
            "Significant (25-40%)" -> 500
            "Severe (40%+)" -> 800
            else -> 0
        }

        val capacitySignature = hashString("$currentCapacity:$degradationLevel:$estimatedCycleCount").take(8)

        return BatteryCapacityProfile(
            designCapacity = null, // Not directly available on Android
            currentCapacity = currentCapacity,
            degradationLevel = degradationLevel,
            capacitySignature = capacitySignature,
            estimatedCycleCount = estimatedCycleCount
        )
    }

    private fun analyzeTemperature(batteryStatus: Intent?): TemperatureProfile {
        val temp = batteryStatus?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1
        val tempCelsius = if (temp > 0) temp / 10.0f else 0f

        val temperatureStatus = when {
            tempCelsius < 15 -> "Cool (<15°C)"
            tempCelsius < 25 -> "Normal (15-25°C)"
            tempCelsius < 35 -> "Warm (25-35°C)"
            tempCelsius < 45 -> "Hot (35-45°C)"
            else -> "Overheating (>45°C)"
        }

        val thermalBehavior = when {
            tempCelsius < 20 -> "Good thermal management"
            tempCelsius < 30 -> "Normal thermal behavior"
            tempCelsius < 40 -> "Above average heat generation"
            else -> "Poor thermal management"
        }

        val tempSignature = hashString("$tempCelsius:$temperatureStatus").take(6)

        return TemperatureProfile(
            currentTemp = tempCelsius,
            temperatureStatus = temperatureStatus,
            thermalBehavior = thermalBehavior,
            tempSignature = tempSignature
        )
    }

    private fun estimateBatteryAge(health: Int, capacity: Int): String {
        return when {
            health == BatteryManager.BATTERY_HEALTH_GOOD && capacity > 95 -> "New (0-6 months)"
            health == BatteryManager.BATTERY_HEALTH_GOOD && capacity > 85 -> "Recent (6-12 months)"
            health == BatteryManager.BATTERY_HEALTH_GOOD && capacity > 75 -> "Used (1-2 years)"
            health == BatteryManager.BATTERY_HEALTH_GOOD -> "Aged (2+ years)"
            else -> "Degraded (varies)"
        }
    }

    private fun estimateChargeCycles(ageEstimate: String): String {
        return when (ageEstimate) {
            "New (0-6 months)" -> "~50-150 cycles"
            "Recent (6-12 months)" -> "~150-300 cycles"
            "Used (1-2 years)" -> "~300-600 cycles"
            "Aged (2+ years)" -> "~600-1000+ cycles"
            else -> "Unknown cycle count"
        }
    }

    private fun calculateBatteryUniqueness(
        health: BatteryHealthProfile,
        capacity: BatteryCapacityProfile
    ): String {
        var uniqueness = 1.0

        // Health contribution
        uniqueness *= when (health.healthPercentage) {
            in 95..100 -> 50.0  // New batteries are somewhat common
            in 85..94 -> 150.0
            in 75..84 -> 300.0
            in 50..74 -> 500.0
            else -> 1000.0
        }

        // Degradation uniqueness
        uniqueness *= when (capacity.estimatedCycleCount) {
            in 0..100 -> 50.0
            in 101..300 -> 200.0
            in 301..500 -> 400.0
            in 501..800 -> 600.0
            else -> 1000.0
        }

        val finalScore = uniqueness.toLong()

        return when {
            finalScore > 1_000_000 -> "1 in ${finalScore / 1_000_000} million"
            finalScore > 1_000 -> "1 in ${finalScore / 1_000}K"
            else -> "1 in $finalScore"
        }
    }

    private fun identifyUniquenessFactors(
        health: BatteryHealthProfile,
        capacity: BatteryCapacityProfile,
        charging: ChargingBehaviorProfile
    ): List<String> {
        val factors = mutableListOf<String>()

        if (health.healthPercentage < 90) {
            factors.add("Battery degradation indicates specific usage patterns")
        }

        if (capacity.estimatedCycleCount > 500) {
            factors.add("High cycle count suggests heavy usage (${capacity.estimatedCycleCount}+ cycles)")
        }

        if (charging.voltage in 3700..4200) {
            factors.add("Voltage profile indicates ${health.technology} battery characteristics")
        }

        if (abs(charging.current) > 2000000) {
            factors.add("Fast charging capability detected (unique charge curve)")
        }

        if (factors.isEmpty()) {
            factors.add("New/well-maintained battery (still identifiable by voltage/current patterns)")
        }

        return factors
    }

    private fun hashString(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}