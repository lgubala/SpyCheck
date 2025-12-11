package com.example.spycheck.ui.main.demos.sneaky.keystroke.utils
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sqrt
/**

Keystroke Inference - Guess what user types using ONLY motion sensors
NO keyboard permission required!

How it works:


Calibration: User types known text, we record sensor patterns




Test: User types PIN/password, we match patterns to calibration




Result: Show which keys were pressed based on sensor data
 */



data class KeystrokePattern(
    val character: Char,
    val sensorX: Float,
    val sensorY: Float,
    val sensorZ: Float,
    val magnitude: Float,
    val timestamp: Long
)
data class KeystrokeMatch(
    val detectedChar: Char,
    val confidence: Int, // 0-100
    val sensorX: Float,
    val sensorY: Float,
    val topMatches: List<Pair<Char, Int>> // Top 3 matches

)
class KeystrokeInferenceReader(private val context: Context) : SensorEventListener {

    private var smoothedX = 0f
    private var smoothedY = 0f
    private var smoothedZ = 0f
    private val SMOOTHING_FACTOR = 0.3f // Lower = smoother

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    // Calibration data: character -> sensor pattern
    private val calibrationData = mutableMapOf<Char, MutableList<KeystrokePattern>>()

    // Real-time keystroke detection
    private val detectedKeystrokes = mutableListOf<KeystrokeMatch>()

    private var isMonitoring = false
    private var lastKeystrokeTime = 0L
    private val MIN_KEYSTROKE_DELAY = 100L // Min 100ms between keystrokes

    // State flows
    private val _calibrationProgress = MutableStateFlow(0)
    val calibrationProgress: StateFlow<Int> = _calibrationProgress.asStateFlow()

    private val _detectedKeys = MutableStateFlow<List<KeystrokeMatch>>(emptyList())
    val detectedKeys: StateFlow<List<KeystrokeMatch>> = _detectedKeys.asStateFlow()

    private val _currentSensorData = MutableStateFlow<Triple<Float, Float, Float>?>(null)
    val currentSensorData: StateFlow<Triple<Float, Float, Float>?> =
        _currentSensorData.asStateFlow()

    /**
     * Start calibration - record sensor data for known text
     */
    fun startCalibration(expectedText: String) {
        calibrationData.clear()
        detectedKeystrokes.clear()
        _detectedKeys.value = emptyList()

        // Pre-populate expected characters
        expectedText.toLowerCase().forEach { char ->
            if (char != ' ') {
                calibrationData[char] = mutableListOf()
            }
        }

        startMonitoring()
    }

    /**
     * Start test mode - detect keystrokes and match to calibration
     */
    fun startTest() {
        detectedKeystrokes.clear()
        _detectedKeys.value = emptyList()
        startMonitoring()
    }

    /**
     * Stop monitoring sensors
     */
    fun stopMonitoring() {
        isMonitoring = false
        sensorManager.unregisterListener(this)
    }

    /**
     * Process calibration input - match detected patterns to user's typed text
     */
    fun processCalibration(actualText: String) {
        val patterns = detectedKeystrokes
        val text = actualText.toLowerCase().replace(" ", "")

        android.util.Log.d("KeystrokeInference", "Processing calibration: $actualText")
        android.util.Log.d(
            "KeystrokeInference",
            "Detected ${patterns.size} keystrokes for ${text.length} characters"
        )

        // Match detected patterns to actual characters
        text.forEachIndexed { index, char ->
            if (index < patterns.size) {
                val pattern = patterns[index]
                calibrationData[char]?.add(
                    KeystrokePattern(
                        character = char,
                        sensorX = pattern.sensorX,
                        sensorY = pattern.sensorY,
                        sensorZ = 0f,
                        magnitude = sqrt(pattern.sensorX * pattern.sensorX + pattern.sensorY * pattern.sensorY),
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        }

        val calibratedChars = calibrationData.filter { it.value.isNotEmpty() }.size
        android.util.Log.d("KeystrokeInference", "Calibrated $calibratedChars characters")
    }

    /**
     * Get current calibration status
     */
    fun getCalibrationStatus(): Pair<Int, Int> {
        val total = calibrationData.size
        val calibrated = calibrationData.filter { it.value.isNotEmpty() }.size
        return Pair(calibrated, total)
    }

    /**
     * Check if calibration is complete
     */
    fun isCalibrationComplete(): Boolean {
        return calibrationData.all { it.value.isNotEmpty() }
    }

    private fun startMonitoring() {
        isMonitoring = true
        lastKeystrokeTime = 0L
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (!isMonitoring) return
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        // Apply smoothing to make values readable
        smoothedX = smoothedX + SMOOTHING_FACTOR * (x - smoothedX)
        smoothedY = smoothedY + SMOOTHING_FACTOR * (y - smoothedY)
        smoothedZ = smoothedZ + SMOOTHING_FACTOR * (z - smoothedZ)

        // Update display with smoothed, rounded values
        _currentSensorData.value = Triple(
            (smoothedX * 10).toInt() / 10f,  // Round to 1 decimal
            (smoothedY * 10).toInt() / 10f,
            (smoothedZ * 10).toInt() / 10f
        )

        val magnitude = sqrt(smoothedX * smoothedX + smoothedY * smoothedY + smoothedZ * smoothedZ)
        val currentTime = System.currentTimeMillis()

        // Keep threshold at 10f
        if (magnitude > 10f && currentTime - lastKeystrokeTime > MIN_KEYSTROKE_DELAY) {
            lastKeystrokeTime = currentTime

            android.util.Log.d(
                "KeystrokeInference",
                "Keystroke detected! X=$smoothedX, Y=$smoothedY, magnitude=$magnitude"
            )

            if (calibrationData.isEmpty()) {
                val dummyMatch = KeystrokeMatch(
                    detectedChar = '?',
                    confidence = 0,
                    sensorX = smoothedX,
                    sensorY = smoothedY,
                    topMatches = emptyList()
                )
                detectedKeystrokes.add(dummyMatch)
            } else {
                val match = findBestMatch(smoothedX, smoothedY)
                if (match != null) {
                    detectedKeystrokes.add(match)
                    _detectedKeys.value = detectedKeystrokes.toList()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed
    }

    /**
     * Find best matching character from calibration data
     */
    private fun findBestMatch(x: Float, y: Float): KeystrokeMatch? {
        if (calibrationData.isEmpty()) return null

        val matches = mutableListOf<Pair<Char, Int>>()

        calibrationData.forEach { (char, patterns) ->
            if (patterns.isEmpty()) return@forEach

            // Average calibration pattern for this character
            val avgX = patterns.map { it.sensorX }.average().toFloat()
            val avgY = patterns.map { it.sensorY }.average().toFloat()

            // Calculate similarity (inverse of distance)
            val distance = sqrt((x - avgX) * (x - avgX) + (y - avgY) * (y - avgY))
            val similarity = (100 / (1 + distance)).toInt().coerceIn(0, 100)

            matches.add(Pair(char, similarity))
        }

        // Sort by confidence
        matches.sortByDescending { it.second }

        val topMatch = matches.firstOrNull() ?: return null
        val top3 = matches.take(3)

        return KeystrokeMatch(
            detectedChar = topMatch.first,
            confidence = topMatch.second,
            sensorX = x,
            sensorY = y,
            topMatches = top3
        )
    }

    fun cleanup() {
        stopMonitoring()
        calibrationData.clear()
        detectedKeystrokes.clear()
    }
}