package com.example.spycheck.ui.main.demos.sneaky.sensors

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spycheck.ui.main.demos.sneaky.sensors.utils.MovementData
import com.example.spycheck.ui.main.demos.sneaky.sensors.utils.MovementHistoryPoint
import com.example.spycheck.ui.main.demos.sneaky.sensors.utils.SensorTrackingReader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SensorTrackingDemoViewModel : ViewModel() {

    private lateinit var reader: SensorTrackingReader
    private var context: Context? = null

    private val _hasPermission = MutableStateFlow(false)
    val hasPermission: StateFlow<Boolean> = _hasPermission.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private val _movementData = MutableStateFlow<MovementData?>(null)
    val movementData: StateFlow<MovementData?> = _movementData.asStateFlow()

    private val _movementHistory = MutableStateFlow<List<MovementHistoryPoint>>(emptyList())
    val movementHistory: StateFlow<List<MovementHistoryPoint>> = _movementHistory.asStateFlow()

    fun checkPermission(context: Context) {
        this.context = context // Store context for later use

        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // No permission needed on Android 9 and below
        }

        _hasPermission.value = hasPermission

        // Initialize reader if permission granted
        if (hasPermission && !::reader.isInitialized) {
            reader = SensorTrackingReader(context)
            observeSensorData()
        }
    }

    fun updatePermission(granted: Boolean) {
        _hasPermission.value = granted

        // Initialize reader when permission is granted
        if (granted && !::reader.isInitialized && context != null) {
            reader = SensorTrackingReader(context!!)
            observeSensorData()
        }
    }

    private fun observeSensorData() {
        viewModelScope.launch {
            reader.movementData.collect { data ->
                _movementData.value = data
            }
        }
        viewModelScope.launch {
            reader.movementHistory.collect { history ->
                _movementHistory.value = history
            }
        }
    }

    fun startTracking() {
        if (::reader.isInitialized) {
            reader.startTracking()
            _isTracking.value = true
        }
    }

    fun stopTracking() {
        if (::reader.isInitialized) {
            reader.stopTracking()
            _isTracking.value = false
        }
    }

    fun clearData() {
        if (::reader.isInitialized) {
            reader.clearAllData()
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (::reader.isInitialized) {
            reader.stopTracking()
        }
    }
}