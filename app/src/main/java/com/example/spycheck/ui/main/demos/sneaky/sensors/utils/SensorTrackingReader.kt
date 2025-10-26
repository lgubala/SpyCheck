package com.example.spycheck.ui.main.demos.sneaky.sensors.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sqrt

data class MovementData(
    val stepCount: Int,
    val direction: String,
    val directionDegrees: Float,
    val distanceMeters: Float,
    val currentActivity: String,
    val movementIntensity: Float
)

data class MovementHistoryPoint(
    val timestamp: Long,
    val intensity: Float,
    val steps: Int
)

sealed class SensorTrackingState {
    object NoPermission : SensorTrackingState()
    object PermissionGranted : SensorTrackingState()
    object Disabled : SensorTrackingState()
}

class SensorTrackingReader(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private var stepSensor: Sensor? = null
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null

    private var initialStepCount: Int? = null
    private var currentStepCount = 0

    private val _movementData = MutableStateFlow<MovementData?>(null)
    val movementData: StateFlow<MovementData?> = _movementData.asStateFlow()

    private val _movementHistory = MutableStateFlow<List<MovementHistoryPoint>>(emptyList())
    val movementHistory: StateFlow<List<MovementHistoryPoint>> = _movementHistory.asStateFlow()

    private val _trackingState = MutableStateFlow<SensorTrackingState>(SensorTrackingState.NoPermission)
    val trackingState: StateFlow<SensorTrackingState> = _trackingState.asStateFlow()

    private var gravityValues = FloatArray(3)
    private var magneticValues = FloatArray(3)
    private var lastUpdateTime = System.currentTimeMillis()

    init {
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        android.util.Log.d("SensorTrackingReader", "Sensors available: Step=${stepSensor != null}, Accel=${accelerometer != null}, Mag=${magnetometer != null}")
    }

    fun hasActivityPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // No permission needed on Android 9 and below
        }
    }

    fun startTracking() {
        if (!hasActivityPermission()) {
            android.util.Log.e("SensorTrackingReader", "No permission to track")
            _trackingState.value = SensorTrackingState.NoPermission
            return
        }

        android.util.Log.d("SensorTrackingReader", "========== STARTING SENSOR TRACKING ==========")

        _trackingState.value = SensorTrackingState.PermissionGranted
        initialStepCount = null
        currentStepCount = 0
        lastUpdateTime = System.currentTimeMillis()

        // Register sensors
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            android.util.Log.d("SensorTrackingReader", "Step sensor registered")
        }

        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            android.util.Log.d("SensorTrackingReader", "Accelerometer registered")
        }

        magnetometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            android.util.Log.d("SensorTrackingReader", "Magnetometer registered")
        }
    }

    fun stopTracking() {
        android.util.Log.d("SensorTrackingReader", "========== STOPPING SENSOR TRACKING ==========")
        sensorManager.unregisterListener(this)
        _trackingState.value = SensorTrackingState.Disabled
    }

    fun clearAllData() {
        android.util.Log.d("SensorTrackingReader", "Clearing all tracking data")
        initialStepCount = null
        currentStepCount = 0
        _movementData.value = null
        _movementHistory.value = emptyList()
        _trackingState.value = SensorTrackingState.NoPermission
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> handleStepCounter(event)
            Sensor.TYPE_ACCELEROMETER -> handleAccelerometer(event)
            Sensor.TYPE_MAGNETIC_FIELD -> handleMagnetometer(event)
        }

        updateMovementData()
    }

    private fun handleStepCounter(event: SensorEvent) {
        val totalSteps = event.values[0].toInt()

        if (initialStepCount == null) {
            initialStepCount = totalSteps
            android.util.Log.d("SensorTrackingReader", "Initial step count: $totalSteps")
        }

        currentStepCount = totalSteps - (initialStepCount ?: totalSteps)
        android.util.Log.d("SensorTrackingReader", "Steps since start: $currentStepCount")
    }

    private fun handleAccelerometer(event: SensorEvent) {
        gravityValues = event.values.clone()
    }

    private fun handleMagnetometer(event: SensorEvent) {
        magneticValues = event.values.clone()
    }

    private fun updateMovementData() {
        val currentTime = System.currentTimeMillis()

        // Only update every 1 second
        if (currentTime - lastUpdateTime < 1000) return

        lastUpdateTime = currentTime

        // Calculate direction from compass
        val direction = calculateDirection()

        // Calculate movement intensity from accelerometer
        val intensity = calculateMovementIntensity()

        // Determine activity
        val activity = determineActivity(intensity)

        // Calculate distance (average step = 0.75m)
        val distance = currentStepCount * 0.75f

        val data = MovementData(
            stepCount = currentStepCount,
            direction = direction.first,
            directionDegrees = direction.second,
            distanceMeters = distance,
            currentActivity = activity,
            movementIntensity = intensity
        )

        _movementData.value = data

        // Add to history (keep last 60 points = 1 minute)
        val history = _movementHistory.value.toMutableList()
        history.add(MovementHistoryPoint(currentTime, intensity, currentStepCount))
        if (history.size > 60) {
            history.removeAt(0)
        }
        _movementHistory.value = history

        android.util.Log.d("SensorTrackingReader", "Updated: steps=$currentStepCount, dir=${direction.first}, activity=$activity")
    }

    private fun calculateDirection(): Pair<String, Float> {
        if (gravityValues.all { it == 0f } || magneticValues.all { it == 0f }) {
            return Pair("Unknown", 0f)
        }

        val rotationMatrix = FloatArray(9)
        val success = SensorManager.getRotationMatrix(rotationMatrix, null, gravityValues, magneticValues)

        if (!success) {
            return Pair("Unknown", 0f)
        }

        val orientation = FloatArray(3)
        SensorManager.getOrientation(rotationMatrix, orientation)

        var degrees = Math.toDegrees(orientation[0].toDouble()).toFloat()
        if (degrees < 0) degrees += 360f

        val direction = when {
            degrees < 22.5 || degrees >= 337.5 -> "North"
            degrees < 67.5 -> "Northeast"
            degrees < 112.5 -> "East"
            degrees < 157.5 -> "Southeast"
            degrees < 202.5 -> "South"
            degrees < 247.5 -> "Southwest"
            degrees < 292.5 -> "West"
            else -> "Northwest"
        }

        return Pair(direction, degrees)
    }

    private fun calculateMovementIntensity(): Float {
        if (gravityValues.all { it == 0f }) return 0f

        // Calculate magnitude of acceleration
        val x = gravityValues[0]
        val y = gravityValues[1]
        val z = gravityValues[2]

        val magnitude = sqrt(x * x + y * y + z * z)

        // Normalize (subtract gravity ~9.8 m/s²)
        return (magnitude - SensorManager.GRAVITY_EARTH).coerceAtLeast(0f)
    }

    private fun determineActivity(intensity: Float): String {
        return when {
            intensity < 0.5f -> "Still"
            intensity < 2.0f -> "Walking"
            intensity < 4.0f -> "Running"
            else -> "High Activity"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for our purposes
    }
}