package com.example.spycheck.ui.main.demos.wifi

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spycheck.R
import com.example.spycheck.ui.main.demos.wifi.components.*
import com.example.spycheck.ui.main.demos.wifi.utils.WifiScanResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiDemoScreen(
    onBackClick: () -> Unit,
    viewModel: WifiDemoViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onLocationPermissionResult(granted)
    }

    val wifiPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onWifiPermissionResult(granted)
    }

    LaunchedEffect(Unit) {
        viewModel.recheckPermissions()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.wifi_demo_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.wifi_demo_description),
                style = MaterialTheme.typography.bodyMedium
            )

            // DEBUG: Show permission status
            Text(
                text = "Debug: Location=${state.hasLocationPermission}, WiFi=${state.hasWifiPermission}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )

            // Permission buttons (shows when permissions are missing)
            if (!state.hasLocationPermission || (viewModel.needsWifiPermission() && !state.hasWifiPermission)) {
                WifiPermissionButtons(
                    hasLocationPermission = state.hasLocationPermission,
                    hasWifiPermission = state.hasWifiPermission,
                    needsWifiPermission = viewModel.needsWifiPermission(),
                    onRequestLocation = {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    },
                    onRequestWifi = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            wifiPermissionLauncher.launch(Manifest.permission.NEARBY_WIFI_DEVICES)
                        }
                    }
                )
            }

            // Networks list with scan button (shows when location permission is granted)
            if (state.hasLocationPermission) {
                WifiNetworksList(
                    scanResult = state.scanResult ?: WifiScanResult(emptyList(), 0),
                    isRevealed = state.isRevealed,
                    onReveal = { viewModel.revealNetworks() },
                    onScan = { viewModel.scanWifiNetworks() },
                    isScanning = state.isScanning,
                    allPermissionsGranted = state.hasLocationPermission && state.hasWifiPermission,
                    modifier = Modifier.fillMaxWidth()
                )

                // Only show additional content when revealed
                if (state.isRevealed && state.scanResult != null) {
                    WifiInfoSection(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    WifiApiExample(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxWidth()
                    )

                    WifiPrivacyWarning()
                }
            }
        }
    }
}