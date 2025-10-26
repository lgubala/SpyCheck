package com.example.spycheck.ui.main.demos.fingerprinting.device

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun initialize(context: Context) {
        // Initialization if needed
    }

    fun startAnalysis() {
        viewModelScope.launch {
            try {
                _isAnalyzing.value = true
                _error.value = null

                kotlinx.coroutines.delay(1200)

                _fingerprintData.value = DeviceFingerprintData(
                    model = android.os.Build.MODEL,
                    manufacturer = android.os.Build.MANUFACTURER,
                    androidVersion = "Android ${android.os.Build.VERSION.RELEASE} (API ${android.os.Build.VERSION.SDK_INT})",
                    screenSize = "1440 x 3200 px",
                    screenDensity = "560 dpi (xxxhdpi)",
                    cpu = android.os.Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown",
                    memory = "12 GB RAM",
                    storage = "256 GB",
                    sensors = "15 sensors detected",
                    fingerprintHash = generateDemoHash(),
                    uniqueness = "99.97%",
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