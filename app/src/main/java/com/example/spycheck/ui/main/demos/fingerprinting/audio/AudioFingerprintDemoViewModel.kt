package com.example.spycheck.ui.main.demos.fingerprinting.audio

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AudioFingerprintData(
    val frequencyResponse: String = "",
    val harmonicDistortion: String = "",
    val noiseFloor: String = "",
    val phaseResponse: String = "",
    val fingerprintHash: String = "",
    val analysisTime: Long = 0L
)

class AudioFingerprintDemoViewModel : ViewModel() {

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _fingerprintData = MutableStateFlow<AudioFingerprintData?>(null)
    val fingerprintData: StateFlow<AudioFingerprintData?> = _fingerprintData.asStateFlow()

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

                kotlinx.coroutines.delay(2000)

                _fingerprintData.value = AudioFingerprintData(
                    frequencyResponse = "0x4F2A89B1",
                    harmonicDistortion = "0.0234%",
                    noiseFloor = "-96.3 dB",
                    phaseResponse = "±3.2° @ 1kHz",
                    fingerprintHash = generateDemoHash(),
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