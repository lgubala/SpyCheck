package com.example.spycheck.ui.main.demos.fingerprinting.battery

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spycheck.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BatteryFingerprintData(
    val level: String = "",
    val health: String = "",
    val temperature: String = "",
    val voltage: String = "",
    val capacity: String = "",
    val drainRate: String = "",
    val chargingPattern: String = "",
    val fingerprintHash: String = "",
    val uniquenessScore: String = "",  // ADD THIS
    val analysisTime: Long = 0L
)

class BatteryFingerprintDemoViewModel : ViewModel() {

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _fingerprintData = MutableStateFlow<BatteryFingerprintData?>(null)
    val fingerprintData: StateFlow<BatteryFingerprintData?> = _fingerprintData.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var appContext: Context? = null

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    fun startAnalysis() {
        viewModelScope.launch {
            try {
                _isAnalyzing.value = true
                _error.value = null

                kotlinx.coroutines.delay(1500)

                val context = appContext ?: return@launch

                _fingerprintData.value = BatteryFingerprintData(
                    level = context.getString(R.string.fp_battery_vm_demo_level),
                    health = context.getString(R.string.fp_battery_vm_demo_health),
                    temperature = context.getString(R.string.fp_battery_vm_demo_temperature),
                    voltage = context.getString(R.string.fp_battery_vm_demo_voltage),
                    capacity = context.getString(R.string.fp_battery_vm_demo_capacity),
                    drainRate = context.getString(R.string.fp_battery_vm_demo_drain_rate),
                    chargingPattern = context.getString(R.string.fp_battery_vm_demo_charging_pattern),
                    fingerprintHash = generateDemoHash(),
                    uniquenessScore = "1 in 2,500",
                    analysisTime = System.currentTimeMillis()
                )

            } catch (e: Exception) {
                _error.value = e.message ?: appContext?.getString(R.string.fp_battery_vm_analysis_failed) ?: "Analysis failed"
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
        val chars = appContext?.getString(R.string.fp_battery_vm_hash_chars) ?: "0123456789ABCDEF"
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