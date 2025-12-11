package com.example.spycheck.ui.main.demos.sneaky.keystroke

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spycheck.ui.main.demos.sneaky.keystroke.utils.KeystrokeMatch
import com.example.spycheck.ui.main.demos.sneaky.keystroke.utils.KeystrokeInferenceReader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class KeystrokePhase {
    INTRO,
    CALIBRATION_READY,
    CALIBRATING,
    CALIBRATION_DONE,
    TEST_READY,
    TESTING,
    RESULTS
}

class KeystrokeDemoViewModel : ViewModel() {

    private lateinit var reader: KeystrokeInferenceReader

    private val _phase = MutableStateFlow(KeystrokePhase.INTRO)
    val phase: StateFlow<KeystrokePhase> = _phase.asStateFlow()

    private val _calibrationText = MutableStateFlow("")
    val calibrationText: StateFlow<String> = _calibrationText.asStateFlow()

    private val _testText = MutableStateFlow("")
    val testText: StateFlow<String> = _testText.asStateFlow()

    private val _detectedKeys = MutableStateFlow<List<KeystrokeMatch>>(emptyList())
    val detectedKeys: StateFlow<List<KeystrokeMatch>> = _detectedKeys.asStateFlow()

    private val _sensorData = MutableStateFlow<Triple<Float, Float, Float>?>(null)
    val sensorData: StateFlow<Triple<Float, Float, Float>?> = _sensorData.asStateFlow()

    fun initialize(context: Context) {
        reader = KeystrokeInferenceReader(context)
        viewModelScope.launch {
            reader.detectedKeys.collect { keys ->
                _detectedKeys.value = keys
            }
        }
        viewModelScope.launch {
            reader.currentSensorData.collect { data ->
                _sensorData.value = data
            }
        }
    }

    fun setPhase(newPhase: KeystrokePhase) {
        _phase.value = newPhase
    }

    fun updateCalibrationText(text: String) {
        _calibrationText.value = text
    }

    fun updateTestText(text: String) {
        _testText.value = text
    }

    fun startCalibration() {
        reader.startCalibration("the quick brown fox")
    }

    fun finishCalibration(userText: String) {
        reader.processCalibration(userText)
        reader.stopMonitoring()
    }

    fun startTest() {
        reader.startTest()
    }

    fun stopMonitoring() {
        reader.stopMonitoring()
    }

    override fun onCleared() {
        super.onCleared()
        if (::reader.isInitialized) {
            reader.stopMonitoring()
        }
    }
}