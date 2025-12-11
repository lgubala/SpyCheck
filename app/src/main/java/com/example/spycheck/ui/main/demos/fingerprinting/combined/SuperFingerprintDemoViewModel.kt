package com.example.spycheck.ui.main.demos.fingerprinting.combined

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spycheck.DeviceFingerprintReader
import com.example.spycheck.SuperFingerprintCombiner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SuperFingerprintData(
    val confidence: String = "",
    val uniqueness: String = "",
    val trackingResistance: String = "",
    val persistence: String = "",
    val activeComponents: Int = 0,
    val totalComponents: Int = 6,
    val fingerprintHash: String = "",
    val combinedFactors: List<String> = emptyList(),
    val analysisTime: Long = 0L
)

class SuperFingerprintDemoViewModel : ViewModel() {

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _fingerprintData = MutableStateFlow<SuperFingerprintData?>(null)
    val fingerprintData: StateFlow<SuperFingerprintData?> = _fingerprintData.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var context: Context? = null

    fun initialize(context: Context) {
        this.context = context  // STORE IT
    }



    fun startAnalysis() {
        viewModelScope.launch {
            try {
                _isAnalyzing.value = true
                _error.value = null

                val ctx = context ?: return@launch

                kotlinx.coroutines.delay(3500)

                // ACTUALLY COLLECT REAL FINGERPRINTS
                val deviceReader = DeviceFingerprintReader(ctx)
                val deviceFp = deviceReader.generateDeviceFingerprint()

                val sensorReader = com.example.spycheck.ui.main.demos.fingerprinting.sensor.utils.SensorFingerprintReader(ctx)
                val sensorFp = sensorReader.collectSensorData()

                // Battery, Audio, Network, Performance would go here
                // For now, at least use device + sensor

                val superFingerprint = SuperFingerprintCombiner.combineFingerprints(
                    device = deviceFp,
                    sensor = sensorFp,
                    battery = null,
                    audio = null,
                    network = null,
                    performance = null
                )

                _fingerprintData.value = SuperFingerprintData(
                    confidence = "${superFingerprint.confidenceScore}%",
                    uniqueness = superFingerprint.globalUniqueness,
                    trackingResistance = superFingerprint.trackingResistance,
                    persistence = superFingerprint.persistenceLevel,
                    activeComponents = superFingerprint.componentFingerprints.componentsAnalyzed,
                    totalComponents = 6,
                    fingerprintHash = superFingerprint.superFingerprintId,
                    combinedFactors = superFingerprint.combinedFactors,
                    analysisTime = System.currentTimeMillis()
                )

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