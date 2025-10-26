package com.example.spycheck.ui.main.demos.sneaky.wifi

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spycheck.ui.main.demos.sneaky.wifi.utils.ApiLocationService
import com.example.spycheck.ui.main.demos.sneaky.wifi.utils.WifiLocationReader
import com.example.spycheck.ui.main.demos.sneaky.wifi.utils.WifiScanResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WifiDemoState(
    val hasLocationPermission: Boolean = false,
    val hasWifiPermission: Boolean = false,
    val scanResult: WifiScanResult? = null,
    val isScanning: Boolean = false,
    val apiProvider: ApiProvider = ApiProvider.MOZILLA,
    val apiKey: String = "",
    val locationResult: LocationResult? = null,
    val isLocating: Boolean = false,
    val errorMessage: String? = null
)

enum class ApiProvider {
    GOOGLE,
    MOZILLA,
    UNWIRED
}

data class LocationResult(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Double? = null,
    val provider: ApiProvider
)

class WifiDemoViewModel : ViewModel() {
    
    private val _state = MutableStateFlow(WifiDemoState())
    val state: StateFlow<WifiDemoState> = _state.asStateFlow()

    private var wifiReader: WifiLocationReader? = null
    private val apiLocationService = ApiLocationService()

    // Check permissions when screen loads
    fun checkPermissions(context: Context) {
        val hasLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasWifi = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.NEARBY_WIFI_DEVICES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Not needed on older Android versions
        }

        _state.update { 
            it.copy(
                hasLocationPermission = hasLocation, 
                hasWifiPermission = hasWifi
            ) 
        }

        // Initialize reader if we have permission
        if (hasLocation) {
            wifiReader = WifiLocationReader(context)
            
            // Auto-scan if both permissions are granted
            if (hasWifi || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                scanWifiNetworks()
            }
        }
    }

    fun onLocationPermissionResult(granted: Boolean) {
        _state.update { it.copy(hasLocationPermission = granted) }
    }

    fun onWifiPermissionResult(granted: Boolean) {
        _state.update { it.copy(hasWifiPermission = granted) }
    }

    fun scanWifiNetworks() {
        if (wifiReader == null) return
        
        _state.update { it.copy(isScanning = true, errorMessage = null) }
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = wifiReader?.scanNearbyNetworks()
                _state.update { 
                    it.copy(
                        scanResult = result, 
                        isScanning = false
                    ) 
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isScanning = false,
                        errorMessage = "Error scanning WiFi: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun setApiProvider(provider: ApiProvider) {
        _state.update { it.copy(apiProvider = provider) }
    }

    fun setApiKey(key: String) {
        _state.update { it.copy(apiKey = key) }
    }

    fun locateUserViaApi() {
        val currentState = _state.value
        
        if (currentState.scanResult == null || currentState.scanResult.networks.isEmpty()) {
            _state.update { it.copy(errorMessage = "Please scan WiFi networks first") }
            return
        }

        if (currentState.apiProvider != ApiProvider.MOZILLA && currentState.apiKey.isEmpty()) {
            _state.update { it.copy(errorMessage = "Please enter an API key") }
            return
        }

        _state.update { 
            it.copy(
                isLocating = true, 
                locationResult = null, 
                errorMessage = null
            ) 
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = apiLocationService.locateViaApi(
                    provider = currentState.apiProvider,
                    networks = currentState.scanResult.networks,
                    apiKey = currentState.apiKey
                )

                result.onSuccess { location ->
                    _state.update { 
                        it.copy(
                            locationResult = location, 
                            isLocating = false
                        ) 
                    }
                }.onFailure { error ->
                    _state.update { 
                        it.copy(
                            isLocating = false,
                            errorMessage = "Location error: ${error.message}"
                        ) 
                    }
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isLocating = false,
                        errorMessage = "Error: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun getStaticMapUrl(result: LocationResult): String {
        val lat = result.latitude
        val lng = result.longitude
        return "https://maps.googleapis.com/maps/api/staticmap?" +
                "center=$lat,$lng&zoom=15&size=800x400&markers=color:red%7C$lat,$lng"
    }

    fun getApiJsonFormat(): String {
        return state.value.scanResult?.let { scanResult ->
            wifiReader?.getGoogleApiJsonFormat(scanResult.networks)
        } ?: ""
    }

    fun needsWifiPermission() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }
}
