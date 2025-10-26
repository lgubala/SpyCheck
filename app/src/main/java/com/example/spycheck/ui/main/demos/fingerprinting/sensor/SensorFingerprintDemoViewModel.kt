package com.example.spycheck.ui.main.demos.fingerprinting.sensor

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spycheck.ui.main.demos.fingerprinting.sensor.utils.SensorFingerprintReader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SensorFingerprintData(
    val accelerometerBias: String = "",
    val gyroscopeBias: String = "",
    val magnetometerBias: String = "",
    val noisePattern: String = "",
    val calibrationErrors: String = "",
    val fingerprintHash: String = "",
    val analysisTime: Long = 0L
)

class SensorFingerprintDemoViewModel : ViewModel() {

    private var sensorReader: SensorFingerprintReader? = null

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _fingerprintData = MutableStateFlow<SensorFingerprintData?>(null)
    val fingerprintData: StateFlow<SensorFingerprintData?> = _fingerprintData.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun initialize(context: Context) {
        if (sensorReader == null) {
            sensorReader = SensorFingerprintReader(context)
        }
    }

    fun startAnalysis() {
        viewModelScope.launch {
            try {
                _isAnalyzing.value = true
                _error.value = null

                // Use the actual sensor reader to collect data
                val fingerprint = sensorReader?.collectSensorData()

                fingerprint?.let { fp ->
                    _fingerprintData.value = SensorFingerprintData(
                        accelerometerBias = "X: ${String.format("%.4f", fp.accelerometerBias.xBias)} m/s², " +
                                "Y: ${String.format("%.4f", fp.accelerometerBias.yBias)} m/s², " +
                                "Z: ${String.format("%.4f", fp.accelerometerBias.zBias)} m/s²",
                        gyroscopeBias = "X: ${String.format("%.4f", fp.gyroscopeBias.xDrift)} rad/s, " +
                                "Y: ${String.format("%.4f", fp.gyroscopeBias.yDrift)} rad/s, " +
                                "Z: ${String.format("%.4f", fp.gyroscopeBias.zDrift)} rad/s",
                        magnetometerBias = "X: ${String.format("%.1f", fp.magnetometerBias.xOffset)} μT, " +
                                "Y: ${String.format("%.1f", fp.magnetometerBias.yOffset)} μT, " +
                                "Z: ${String.format("%.1f", fp.magnetometerBias.zOffset)} μT",
                        noisePattern = "σ = ${String.format("%.4f", fp.accelerometerBias.magnitude)} m/s² RMS",
                        calibrationErrors = "±${String.format("%.1f", (fp.accelerometerBias.magnitude * 100))}% deviation from nominal",
                        fingerprintHash = fp.fingerprintId,
                        analysisTime = System.currentTimeMillis()
                    )
                }

            } catch (e: Exception) {
                _error.value = e.message ?: "Analysis failed"
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun stopAnalysis() {
        _isAnalyzing.value = false
    }

    fun clearResults() {
        _fingerprintData.value = null
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        stopAnalysis()
    }
}