package com.example.spycheck.ui.main.demos.fingerprinting.sensor.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Sensor Fingerprinting - NO PERMISSION REQUIRED!
 *
 * How it works:
 * Every phone's sensors (accelerometer, gyroscope, magnetometer) have tiny
 * manufacturing imperfections. These create a unique "bias pattern" that can
 * identify your specific device.
 *
 * Real-world example:
 * Two identical iPhone 14s will have DIFFERENT sensor readings when sitting
 * completely still on a table. These differences are consistent and unique.
 */

data class SensorFingerprint(
    val fingerprintId: String,
    val uniquenessScore: String,
    val accelerometerBias: AccelerometerBias,
    val gyroscopeBias: GyroscopeBias,
    val magnetometerBias: MagnetometerBias,
    val sensorAvailability: SensorAvailability,
    val uniquenessFactors: List<String>
)

data class AccelerometerBias(
    val xBias: Float,
    val yBias: Float,
    val zBias: Float,
    val magnitude: Float,
    val biasSignature: String,
    val description: String
)

data class GyroscopeBias(
    val xDrift: Float,
    val yDrift: Float,
    val zDrift: Float,
    val totalDrift: Float,
    val driftSignature: String,
    val description: String
)

data class MagnetometerBias(
    val xOffset: Float,
    val yOffset: Float,
    val zOffset: Float,
    val strength: Float,
    val offsetSignature: String,
    val description: String
)

data class SensorAvailability(
    val hasAccelerometer: Boolean,
    val hasGyroscope: Boolean,
    val hasMagnetometer: Boolean,
    val hasStepCounter: Boolean,
    val hasProximitySensor: Boolean,
    val hasLightSensor: Boolean,
    val totalSensors: Int,
    val sensorSignature: String
)

class SensorFingerprintReader(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    // Sensor data collection
    private val accelerometerReadings = mutableListOf<FloatArray>()
    private val gyroscopeReadings = mutableListOf<FloatArray>()
    private val magnetometerReadings = mutableListOf<FloatArray>()

    private var isCollecting = false

    // State flows for real-time updates
    private val _collectionProgress = MutableStateFlow(0)
    val collectionProgress: StateFlow<Int> = _collectionProgress.asStateFlow()

    private val _statusMessage = MutableStateFlow("Ready to analyze sensors")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    /**
     * Collect sensor data to create fingerprint
     * This takes about 3-5 seconds to gather enough readings
     */
    suspend fun collectSensorData(): SensorFingerprint {
        _statusMessage.value = "🔍 Detecting sensors..."
        _collectionProgress.value = 10

        // Register all sensors
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        // Clear previous readings
        accelerometerReadings.clear()
        gyroscopeReadings.clear()
        magnetometerReadings.clear()

        isCollecting = true

        // Register listeners with GAME delay instead of FASTEST (doesn't require special permission)
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        magnetometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }

        _statusMessage.value = "📊 Collecting sensor readings..."
        _collectionProgress.value = 30

        // Collect for 3 seconds
        var elapsed = 0
        while (elapsed < 3000 && isCollecting) {
            delay(100)
            elapsed += 100
            _collectionProgress.value = 30 + (elapsed / 3000f * 40).toInt()
        }

        _statusMessage.value = "🧮 Analyzing bias patterns..."
        _collectionProgress.value = 80

        // Unregister listeners
        sensorManager.unregisterListener(this)
        isCollecting = false

        delay(500) // Dramatic pause for UX

        _statusMessage.value = "✅ Fingerprint generated!"
        _collectionProgress.value = 100

        // Analyze the data
        return analyzeSensorData()
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (!isCollecting) return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                if (accelerometerReadings.size < 100) {
                    accelerometerReadings.add(event.values.clone())
                }
            }
            Sensor.TYPE_GYROSCOPE -> {
                if (gyroscopeReadings.size < 100) {
                    gyroscopeReadings.add(event.values.clone())
                }
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                if (magnetometerReadings.size < 100) {
                    magnetometerReadings.add(event.values.clone())
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for fingerprinting
    }

    private fun analyzeSensorData(): SensorFingerprint {
        // Analyze accelerometer bias
        val accelBias = if (accelerometerReadings.isNotEmpty()) {
            analyzeAccelerometerBias()
        } else {
            AccelerometerBias(0f, 0f, 0f, 0f, "N/A", "No accelerometer available")
        }

        // Analyze gyroscope drift
        val gyroBias = if (gyroscopeReadings.isNotEmpty()) {
            analyzeGyroscopeBias()
        } else {
            GyroscopeBias(0f, 0f, 0f, 0f, "N/A", "No gyroscope available")
        }

        // Analyze magnetometer offset
        val magBias = if (magnetometerReadings.isNotEmpty()) {
            analyzeMagnetometerBias()
        } else {
            MagnetometerBias(0f, 0f, 0f, 0f, "N/A", "No magnetometer available")
        }

        // Get sensor availability
        val sensorAvailability = getSensorAvailability()

        // Generate combined fingerprint ID
        val fingerprintData = buildString {
            append(accelBias.biasSignature)
            append(gyroBias.driftSignature)
            append(magBias.offsetSignature)
            append(sensorAvailability.sensorSignature)
        }
        val fingerprintId = hashString(fingerprintData)

        // Calculate uniqueness
        val uniquenessScore = calculateSensorUniqueness(accelBias, gyroBias, magBias)

        // Identify uniqueness factors
        val uniquenessFactors = identifySensorUniquenessFactors(accelBias, gyroBias, magBias)

        return SensorFingerprint(
            fingerprintId = fingerprintId,
            uniquenessScore = uniquenessScore,
            accelerometerBias = accelBias,
            gyroscopeBias = gyroBias,
            magnetometerBias = magBias,
            sensorAvailability = sensorAvailability,
            uniquenessFactors = uniquenessFactors
        )
    }

    private fun analyzeAccelerometerBias(): AccelerometerBias {
        // Calculate average bias when phone is still
        // In perfect world: X=0, Y=0, Z=9.8 (gravity)
        // Reality: Each phone has unique deviations

        val avgX = accelerometerReadings.map { it[0] }.average().toFloat()
        val avgY = accelerometerReadings.map { it[1] }.average().toFloat()
        val avgZ = accelerometerReadings.map { it[2] }.average().toFloat()

        // Calculate bias (deviation from expected)
        val xBias = avgX // Should be ~0
        val yBias = avgY // Should be ~0
        val zBias = avgZ - 9.8f // Should be ~9.8 (gravity)

        val magnitude = sqrt(xBias * xBias + yBias * yBias + zBias * zBias)

        val biasSignature = hashString("$xBias:$yBias:$zBias").take(8)

        val description = when {
            magnitude < 0.1f -> "Very low bias (newer phone)"
            magnitude < 0.3f -> "Normal bias pattern"
            magnitude < 0.5f -> "High bias (older phone or damaged)"
            else -> "Extreme bias (possible calibration issue)"
        }

        return AccelerometerBias(
            xBias = xBias,
            yBias = yBias,
            zBias = zBias,
            magnitude = magnitude,
            biasSignature = biasSignature,
            description = description
        )
    }

    private fun analyzeGyroscopeBias(): GyroscopeBias {
        // Gyroscope should read 0,0,0 when phone is still
        // But manufacturing imperfections cause "drift"

        val avgX = gyroscopeReadings.map { it[0] }.average().toFloat()
        val avgY = gyroscopeReadings.map { it[1] }.average().toFloat()
        val avgZ = gyroscopeReadings.map { it[2] }.average().toFloat()

        val totalDrift = sqrt(avgX * avgX + avgY * avgY + avgZ * avgZ)

        val driftSignature = hashString("$avgX:$avgY:$avgZ").take(8)

        val description = when {
            totalDrift < 0.01f -> "Minimal drift (high-quality sensor)"
            totalDrift < 0.05f -> "Normal drift pattern"
            totalDrift < 0.1f -> "Noticeable drift (budget sensor)"
            else -> "High drift (sensor degradation)"
        }

        return GyroscopeBias(
            xDrift = avgX,
            yDrift = avgY,
            zDrift = avgZ,
            totalDrift = totalDrift,
            driftSignature = driftSignature,
            description = description
        )
    }

    private fun analyzeMagnetometerBias(): MagnetometerBias {
        // Magnetometer measures magnetic field
        // Each phone's internal components create unique interference

        val avgX = magnetometerReadings.map { it[0] }.average().toFloat()
        val avgY = magnetometerReadings.map { it[1] }.average().toFloat()
        val avgZ = magnetometerReadings.map { it[2] }.average().toFloat()

        val strength = sqrt(avgX * avgX + avgY * avgY + avgZ * avgZ)

        val offsetSignature = hashString("$avgX:$avgY:$avgZ").take(8)

        val description = when {
            strength < 30f -> "Weak field (possible interference)"
            strength < 60f -> "Normal magnetic field"
            strength > 100f -> "Strong field (near magnets?)"
            else -> "Typical magnetic pattern"
        }

        return MagnetometerBias(
            xOffset = avgX,
            yOffset = avgY,
            zOffset = avgZ,
            strength = strength,
            offsetSignature = offsetSignature,
            description = description
        )
    }

    private fun getSensorAvailability(): SensorAvailability {
        val allSensors = sensorManager.getSensorList(Sensor.TYPE_ALL)

        val hasAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null
        val hasGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null
        val hasMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null
        val hasStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null
        val hasProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null
        val hasLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null

        val sensorPattern = buildString {
            append(if (hasAccelerometer) "A" else "-")
            append(if (hasGyroscope) "G" else "-")
            append(if (hasMagnetometer) "M" else "-")
            append(if (hasStepCounter) "S" else "-")
            append(if (hasProximity) "P" else "-")
            append(if (hasLight) "L" else "-")
        }

        val sensorSignature = hashString(sensorPattern + allSensors.size).take(6)

        return SensorAvailability(
            hasAccelerometer = hasAccelerometer,
            hasGyroscope = hasGyroscope,
            hasMagnetometer = hasMagnetometer,
            hasStepCounter = hasStepCounter,
            hasProximitySensor = hasProximity,
            hasLightSensor = hasLight,
            totalSensors = allSensors.size,
            sensorSignature = sensorSignature
        )
    }

    private fun calculateSensorUniqueness(
        accel: AccelerometerBias,
        gyro: GyroscopeBias,
        mag: MagnetometerBias
    ): String {
        var uniqueness = 1.0

        // Accelerometer bias contributes heavily
        if (accel.biasSignature != "N/A") {
            uniqueness *= 50000 // Very unique
        }

        // Gyroscope drift adds more uniqueness
        if (gyro.driftSignature != "N/A") {
            uniqueness *= 30000
        }

        // Magnetometer pattern adds final layer
        if (mag.offsetSignature != "N/A") {
            uniqueness *= 20000
        }

        val finalScore = uniqueness.toLong()

        return when {
            finalScore > 1_000_000_000 -> "1 in ${finalScore / 1_000_000_000} billion"
            finalScore > 1_000_000 -> "1 in ${finalScore / 1_000_000} million"
            finalScore > 1_000 -> "1 in ${finalScore / 1_000}K"
            else -> "1 in $finalScore"
        }
    }

    private fun identifySensorUniquenessFactors(
        accel: AccelerometerBias,
        gyro: GyroscopeBias,
        mag: MagnetometerBias
    ): List<String> {
        val factors = mutableListOf<String>()

        if (accel.magnitude > 0.3f) {
            factors.add("High accelerometer bias increases uniqueness")
        }

        if (gyro.totalDrift > 0.05f) {
            factors.add("Significant gyroscope drift (older device marker)")
        }

        if (mag.strength > 100f || mag.strength < 30f) {
            factors.add("Unusual magnetic field pattern")
        }

        if (abs(accel.xBias) > 0.2f || abs(accel.yBias) > 0.2f) {
            factors.add("Asymmetric accelerometer calibration")
        }

        if (factors.isEmpty()) {
            factors.add("Well-calibrated sensors (still uniquely identifiable)")
        }

        return factors
    }

    fun cleanup() {
        sensorManager.unregisterListener(this)
        isCollecting = false
    }

    private fun hashString(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}