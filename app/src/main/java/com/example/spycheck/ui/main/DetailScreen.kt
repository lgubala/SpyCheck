package com.example.spycheck.ui.main

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spycheck.R
import com.example.spycheck.ui.main.demos.exif.ExifDemoViewModel
import com.example.spycheck.ui.main.demos.exif.components.*
import com.example.spycheck.ui.main.demos.wifi.WifiDemoViewModel
import com.example.spycheck.ui.main.demos.wifi.components.*
import com.example.spycheck.ui.main.demos.wifi.utils.WifiScanResult
import com.example.spycheck.ui.main.model.Detail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    detail: Detail,
    onBack: () -> Unit,
    onStartDemo: (() -> Unit)? = null
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = detail.title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Common info text
            Text(
                text = stringResource(id = detail.longDescription),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.real_life_examples),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            detail.realLifeExamples.forEach {
                Text(
                    text = "• ${stringResource(id = it)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (detail.id) {

                // ---------------- EXIF DEMO ----------------
                "exif_gps" -> {
                    val viewModel: ExifDemoViewModel = viewModel()
                    val state by viewModel.state.collectAsState()

                    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Manifest.permission.READ_MEDIA_IMAGES
                    } else {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    }

                    val launcher = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { granted ->
                        viewModel.onPermissionResult(granted)
                    }

                    InfoSection()
                    Spacer(Modifier.height(16.dp))
                    PermissionButton(
                        hasPermission = state.hasPermission,
                        onRequest = { launcher.launch(permission) }
                    )

                    if (state.hasPermission) {
                        Spacer(Modifier.height(16.dp))
                        when {
                            state.isLoading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            state.photoData != null -> {
                                PhotoPreview(state.photoData!!)
                                Spacer(Modifier.height(16.dp))
                                ExifInfoCard(state.photoData!!)
                            }
                            else -> Text("No photos found in your gallery.")
                        }

                        Spacer(Modifier.height(16.dp))
                        PermissionWarning()
                    }
                }

                // ---------------- WIFI DEMO ----------------
                "wifi" -> {
                    val viewModel: WifiDemoViewModel = viewModel()
                    val state by viewModel.state.collectAsState()

                    val locationLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { granted -> viewModel.onLocationPermissionResult(granted) }

                    val wifiLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { granted -> viewModel.onWifiPermissionResult(granted) }

                    LaunchedEffect(Unit) { viewModel.recheckPermissions() }

                    Text(
                        text = stringResource(R.string.wifi_demo_description),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(12.dp))

                    if (!state.hasLocationPermission ||
                        (viewModel.needsWifiPermission() && !state.hasWifiPermission)
                    ) {
                        WifiPermissionButtons(
                            hasLocationPermission = state.hasLocationPermission,
                            hasWifiPermission = state.hasWifiPermission,
                            needsWifiPermission = viewModel.needsWifiPermission(),
                            onRequestLocation = {
                                locationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            },
                            onRequestWifi = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    wifiLauncher.launch(Manifest.permission.NEARBY_WIFI_DEVICES)
                                }
                            }
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    if (state.hasLocationPermission) {
                        WifiNetworksList(
                            scanResult = state.scanResult ?: WifiScanResult(emptyList(), 0),
                            onScan = { viewModel.scanWifiNetworks() },
                            isScanning = state.isScanning,
                            allPermissionsGranted = state.hasLocationPermission && state.hasWifiPermission,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (state.scanResult != null) {
                            WifiInfoSection(viewModel = viewModel)
                            HorizontalDivider(Modifier.padding(vertical = 8.dp))
                            WifiApiExample(viewModel = viewModel)
                            WifiPrivacyWarning()
                        }
                    }
                }

                // ---------------- OTHER DEMOS ----------------
                else -> {
                    if (detail.hasInteractiveDemo && onStartDemo != null) {
                        Button(
                            onClick = onStartDemo,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🔬 Try Interactive Demo")
                        }
                    }
                }
            }
        }
    }
}
