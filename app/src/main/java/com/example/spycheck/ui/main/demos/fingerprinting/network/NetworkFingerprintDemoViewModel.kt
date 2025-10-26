package com.example.spycheck.ui.main.demos.fingerprinting.network

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NetworkFingerprintData(
    val connectionType: String = "",
    val ipAddress: String = "",
    val latency: String = "",
    val mtuSize: String = "",
    val tcpWindow: String = "",
    val dnsServers: String = "",
    val routing: String = "",
    val fingerprintHash: String = "",
    val analysisTime: Long = 0L
)

class NetworkFingerprintDemoViewModel : ViewModel() {

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _fingerprintData = MutableStateFlow<NetworkFingerprintData?>(null)
    val fingerprintData: StateFlow<NetworkFingerprintData?> = _fingerprintData.asStateFlow()

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

                kotlinx.coroutines.delay(1800)

                _fingerprintData.value = NetworkFingerprintData(
                    connectionType = "WiFi (802.11ac)",
                    ipAddress = "192.168.xxx.xxx",
                    latency = "12-18ms (avg 14.3ms)",
                    mtuSize = "1500 bytes",
                    tcpWindow = "65535 bytes",
                    dnsServers = "8.8.8.8, 8.8.4.4",
                    routing = "3 hops to gateway",
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