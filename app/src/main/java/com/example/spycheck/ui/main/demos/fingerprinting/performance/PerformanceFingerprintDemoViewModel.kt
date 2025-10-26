package com.example.spycheck.ui.main.demos.fingerprinting.performance

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PerformanceFingerprintData(
    val cpuScore: String = "",
    val gpuScore: String = "",
    val memoryScore: String = "",
    val renderTime: String = "",
    val computePattern: String = "",
    val throttleBehavior: String = "",
    val fingerprintHash: String = "",
    val analysisTime: Long = 0L
)

class PerformanceFingerprintDemoViewModel : ViewModel() {

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _fingerprintData = MutableStateFlow<PerformanceFingerprintData?>(null)
    val fingerprintData: StateFlow<PerformanceFingerprintData?> = _fingerprintData.asStateFlow()

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

                kotlinx.coroutines.delay(2500)

                _fingerprintData.value = PerformanceFingerprintData(
                    cpuScore = "8,347 points",
                    gpuScore = "12,934 points",
                    memoryScore = "6,821 MB/s",
                    renderTime = "16.7ms @ 60fps",
                    computePattern = "Parallel: ${Runtime.getRuntime().availableProcessors()} cores",
                    throttleBehavior = "Throttles at 42°C",
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