package com.example.spycheck.ui.main.demos.wifi

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.spycheck.ui.main.demos.wifi.utils.ApiLocationService
import com.example.spycheck.ui.main.demos.wifi.utils.WifiLocationReader
import com.example.spycheck.ui.main.demos.wifi.utils.WifiScanResult
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
    val isRevealed: Boolean = false,
    val apiProvider: ApiProvider = ApiProvider.MOZILLA,
    val apiKey: String = "",
    val locationResult: LocationResult? = null,
    val isLocating: Boolean = false
)

class WifiDemoViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(WifiDemoState())
    val state: StateFlow<WifiDemoState> = _state.asStateFlow()

    private val wifiReader = WifiLocationReader(application)
    private val apiLocationService = ApiLocationService()

    init {
        checkPermissions()
    }

    private fun checkPermissions() {
        val hasLocation = ContextCompat.checkSelfPermission(
            getApplication(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasWifi = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.NEARBY_WIFI_DEVICES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        _state.update { it.copy(
            hasLocationPermission = hasLocation,
            hasWifiPermission = hasWifi
        )}
    }

    fun onLocationPermissionResult(granted: Boolean) {
        _state.update { it.copy(hasLocationPermission = granted) }
    }

    fun onWifiPermissionResult(granted: Boolean) {
        _state.update { it.copy(hasWifiPermission = granted) }
    }

    fun scanWifiNetworks() {
        _state.update { it.copy(isScanning = true) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = wifiReader.scanNearbyNetworks()
                _state.update { it.copy(
                    scanResult = result,
                    isScanning = false
                )}
            } catch (e: Exception) {
                _state.update { it.copy(isScanning = false) }
                showToast("Error scanning WiFi networks: ${e.message}")
            }
        }
    }

    fun revealNetworks() {
        _state.update { it.copy(isRevealed = true) }
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
            showToast("Please scan WiFi networks first")
            return
        }

        if (currentState.apiProvider != ApiProvider.MOZILLA && currentState.apiKey.isEmpty()) {
            showToast("Please enter an API key")
            return
        }

        _state.update { it.copy(isLocating = true, locationResult = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = apiLocationService.locateViaApi(
                    provider = currentState.apiProvider,
                    networks = currentState.scanResult.networks,
                    apiKey = currentState.apiKey
                )

                result.onSuccess { location ->
                    _state.update { it.copy(
                        locationResult = location,
                        isLocating = false
                    )}
                    showToast("Location found!")
                }.onFailure { error ->
                    _state.update { it.copy(isLocating = false) }
                    showToast("Location error: ${error.message}")
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLocating = false) }
                showToast("Error: ${e.message}")
            }
        }
    }

    fun getStaticMapUrl(result: LocationResult): String {
        val lat = result.latitude
        val lng = result.longitude

        // Google Static Maps API (works without key for low usage)
        return "https://maps.googleapis.com/maps/api/staticmap?" +
                "center=$lat,$lng&" +
                "zoom=15&" +
                "size=800x400&" +
                "markers=color:red%7C$lat,$lng"
    }

    fun getApiJsonFormat(): String {
        return state.value.scanResult?.let {
            wifiReader.getGoogleApiJsonFormat(it.networks)
        } ?: ""
    }

    fun recheckPermissions() {
        checkPermissions()
    }

    fun needsWifiPermission(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }

    private fun showToast(message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
        }
    }
}