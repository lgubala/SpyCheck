package com.example.spycheck.ui.main.demos.fingerprinting.device

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spycheck.DeviceFingerprintReader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DeviceFingerprintData(
    val model: String = "",
    val manufacturer: String = "",
    val androidVersion: String = "",
    val screenSize: String = "",
    val screenDensity: String = "",
    val cpu: String = "",
    val memory: String = "",
    val storage: String = "",
    val sensors: String = "",
    val fingerprintHash: String = "",
    val uniqueness: String = "",
    val analysisTime: Long = 0L
)

class DeviceFingerprintDemoViewModel : ViewModel() {

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _fingerprintData = MutableStateFlow<DeviceFingerprintData?>(null)
    val fingerprintData: StateFlow<DeviceFingerprintData?> = _fingerprintData.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var context: Context? = null  // Make it nullable

    fun initialize(context: Context) {
        this.context = context
    }

    fun startAnalysis() {
        viewModelScope.launch {
            try {
                _isAnalyzing.value = true
                _error.value = null

                // Check if context is initialized
                val ctx = context
                if (ctx == null) {
                    _error.value = "Context not initialized"
                    _isAnalyzing.value = false
                    return@launch
                }

                kotlinx.coroutines.delay(1200)

                // USE THE REAL READER
                val reader = DeviceFingerprintReader(ctx)
                val realFingerprint = reader.generateDeviceFingerprint()

                _fingerprintData.value = DeviceFingerprintData(
                    model = realFingerprint.deviceInfo.model,
                    manufacturer = realFingerprint.deviceInfo.manufacturer,
                    androidVersion = "Android ${realFingerprint.deviceInfo.androidVersion} (API ${realFingerprint.deviceInfo.apiLevel})",
                    screenSize = "${realFingerprint.screenMetrics.widthPixels} x ${realFingerprint.screenMetrics.heightPixels} px",
                    screenDensity = "${realFingerprint.screenMetrics.densityDpi} dpi",
                    cpu = "${realFingerprint.hardwareInfo.cpuCores} cores",
                    memory = realFingerprint.hardwareInfo.totalRam,
                    storage = realFingerprint.hardwareInfo.totalStorage,
                    sensors = "${realFingerprint.uniquenessFactors.size} factors",
                    fingerprintHash = realFingerprint.fingerprintId,
                    uniqueness = realFingerprint.uniquenessScore,
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

    private fun generateDemoHash(): String {
        val chars = "0123456789ABCDEF"
        return buildString {
            repeat(64) {
                append(chars.random())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopAnalysis()
    }
}