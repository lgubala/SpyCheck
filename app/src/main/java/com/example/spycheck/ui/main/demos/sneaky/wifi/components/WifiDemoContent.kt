package com.example.spycheck.ui.main.demos.sneaky.wifi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.spycheck.R
import com.example.spycheck.ui.main.demos.sneaky.wifi.ApiProvider
import com.example.spycheck.ui.main.demos.sneaky.wifi.WifiDemoViewModel
import com.example.spycheck.ui.theme.DangerRed
import com.example.spycheck.ui.theme.SuccessGreen

/**
 * Demo content showing WiFi location tracking
 */
@Composable
fun WifiDemoContent(viewModel: WifiDemoViewModel) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Scan WiFi Networks Button
        Button(
            onClick = { viewModel.scanWifiNetworks() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isScanning
        ) {
            Icon(Icons.Default.Wifi, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                if (state.isScanning) stringResource(R.string.scanning_networks)
                else stringResource(R.string.scan_wifi_networks)
            )
        }

        // Error Message
        state.errorMessage?.let { error ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = DangerRed.copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        tint = DangerRed
                    )
                    Text(
                        text = error,
                        fontSize = 14.sp,
                        color = DangerRed
                    )
                }
            }
        }

        // Networks Found Display
        state.scanResult?.let { scanResult ->
            NetworksFoundCard(scanResult.totalCount)
            
            // Show network list
            NetworksList(networks = scanResult.networks)

            // API Location Section
            ApiLocationSection(viewModel = viewModel)
        }

        // Location Result Display
        state.locationResult?.let { result ->
            LocationResultCard(result = result, viewModel = viewModel)
        }
    }
}

@Composable
private fun NetworksFoundCard(totalCount: Int) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (totalCount > 0) DangerRed.copy(alpha = 0.15f) 
                           else Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (totalCount > 0) "⚠️" else "ℹ️", 
                    fontSize = 32.sp
                )
                Column {
                    Text(
                        text = if (totalCount > 0) {
                            stringResource(R.string.networks_found, totalCount)
                        } else {
                            stringResource(R.string.no_networks_found)
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (totalCount > 0) DangerRed else Color.White
                    )
                    if (totalCount > 0) {
                        Text(
                            text = "Each network reveals your location",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NetworksList(networks: List<com.example.spycheck.ui.main.demos.sneaky.wifi.utils.WifiNetwork>) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "📡 Detected Networks",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            networks.take(10).forEach { network ->
                NetworkItem(network)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (networks.size > 10) {
                Text(
                    text = "... and ${networks.size - 10} more",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun NetworkItem(network: com.example.spycheck.ui.main.demos.sneaky.wifi.utils.WifiNetwork) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF1A1A1A),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (network.isConnected) Icons.Default.WifiTethering 
                         else Icons.Default.Wifi,
            contentDescription = null,
            tint = if (network.isConnected) SuccessGreen else Color.White.copy(alpha = 0.6f),
            modifier = Modifier.size(20.dp)
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = network.ssid.ifEmpty { "<Hidden Network>" },
                fontSize = 14.sp,
                fontWeight = if (network.isConnected) FontWeight.Bold else FontWeight.Normal,
                color = Color.White
            )
            Text(
                text = "MAC: ${network.bssid}",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = Color.White.copy(alpha = 0.5f)
            )
        }

        // Signal strength indicator
        Text(
            text = getSignalStrengthText(network.signalStrength),
            fontSize = 12.sp,
            color = getSignalStrengthColor(network.signalStrength)
        )
    }
}

@Composable
private fun ApiLocationSection(viewModel: WifiDemoViewModel) {
    val state by viewModel.state.collectAsState()

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFBE0B).copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.wifi_api_example_title),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFFFFBE0B)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.wifi_api_example_description),
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // API Provider Selection
            Text(
                text = stringResource(R.string.wifi_api_select_provider),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ApiProviderChip(
                    text = "Mozilla",
                    selected = state.apiProvider == ApiProvider.MOZILLA,
                    onClick = { viewModel.setApiProvider(ApiProvider.MOZILLA) }
                )
                ApiProviderChip(
                    text = "Google",
                    selected = state.apiProvider == ApiProvider.GOOGLE,
                    onClick = { viewModel.setApiProvider(ApiProvider.GOOGLE) }
                )
                ApiProviderChip(
                    text = "Unwired",
                    selected = state.apiProvider == ApiProvider.UNWIRED,
                    onClick = { viewModel.setApiProvider(ApiProvider.UNWIRED) }
                )
            }

            // API Key input (if not Mozilla)
            if (state.apiProvider != ApiProvider.MOZILLA) {
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = state.apiKey,
                    onValueChange = { viewModel.setApiKey(it) },
                    label = { Text(stringResource(R.string.wifi_api_key_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFBE0B),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedLabelColor = Color(0xFFFFBE0B),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Locate Button
            Button(
                onClick = { viewModel.locateUserViaApi() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLocating && state.scanResult != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DangerRed
                )
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (state.isLocating) stringResource(R.string.wifi_locating)
                    else stringResource(R.string.wifi_locate_button)
                )
            }
        }
    }
}

@Composable
private fun ApiProviderChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (selected) Color(0xFFFFBE0B) else Color(0xFF2A2A2A),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Color.Black else Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun LocationResultCard(
    result: com.example.spycheck.ui.main.demos.sneaky.wifi.LocationResult,
    viewModel: WifiDemoViewModel
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = DangerRed.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "📍", fontSize = 32.sp)
                Column {
                    Text(
                        text = stringResource(R.string.wifi_location_found),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = DangerRed
                    )
                    Text(
                        text = "Your location was pinpointed using WiFi networks",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Coordinates
            DataItem(
                icon = Icons.Default.LocationOn,
                label = stringResource(R.string.wifi_coordinates_label),
                value = "${"%.6f".format(result.latitude)}, ${"%.6f".format(result.longitude)}",
                dangerous = true
            )

            result.accuracy?.let { accuracy ->
                Spacer(modifier = Modifier.height(8.dp))
                DataItem(
                    icon = Icons.Default.MyLocation,
                    label = "Accuracy",
                    value = stringResource(R.string.wifi_accuracy_label, accuracy.toInt())
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            DataItem(
                icon = Icons.Default.Cloud,
                label = "API Provider",
                value = result.provider.name
            )

            // Map preview
            Spacer(modifier = Modifier.height(16.dp))
            
            AsyncImage(
                model = viewModel.getStaticMapUrl(result),
                contentDescription = stringResource(R.string.wifi_map_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = DangerRed.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "⚠️ Apps can do this silently in the background without your knowledge!",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = DangerRed,
                    modifier = Modifier.padding(10.dp),
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
private fun DataItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    dangerous: Boolean = false
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (dangerous) DangerRed else Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(20.dp)
        )
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = if (dangerous) FontWeight.Bold else FontWeight.Normal,
                color = if (dangerous) DangerRed else Color.White
            )
        }
    }
}

private fun getSignalStrengthText(strength: Int): String {
    return when {
        strength >= -50 -> "●●●●"
        strength >= -60 -> "●●●○"
        strength >= -70 -> "●●○○"
        strength >= -80 -> "●○○○"
        else -> "○○○○"
    }
}

private fun getSignalStrengthColor(strength: Int): Color {
    return when {
        strength >= -50 -> SuccessGreen
        strength >= -70 -> Color(0xFFFFBE0B)
        else -> DangerRed
    }
}
