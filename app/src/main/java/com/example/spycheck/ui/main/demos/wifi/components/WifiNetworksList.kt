package com.example.spycheck.ui.main.demos.wifi.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.spycheck.R
import com.example.spycheck.ui.main.demos.wifi.utils.WifiScanResult
import com.example.spycheck.ui.main.demos.wifi.utils.WifiNetwork
import com.example.spycheck.ui.theme.SuccessGreen
import com.example.spycheck.ui.theme.WarningAmber

@Composable
fun WifiNetworksList(
    scanResult: WifiScanResult,
    onScan: () -> Unit,
    isScanning: Boolean,
    allPermissionsGranted: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        // Scan button
        if (allPermissionsGranted) {
            Button(
                onClick = onScan,
                enabled = !isScanning,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isScanning) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Text(stringResource(R.string.scanning_wifi))
                } else {
                    Text(stringResource(R.string.scan_wifi_button))
                }
            }
        }

        // Always show list once data exists (no reveal button)
        if (scanResult.networks.isNotEmpty()) {
            Text(
                text = stringResource(R.string.wifi_networks_found, scanResult.totalCount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SuccessGreen
            )

            Text(
                text = stringResource(R.string.apps_can_see),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            scanResult.networks.forEachIndexed { index, network ->
                WifiNetworkCard(
                    network = network,
                    index = index
                )
            }
        }
    }
}

@Composable
fun WifiNetworkCard(
    network: WifiNetwork,
    index: Int
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (network.isConnected) {
                        stringResource(R.string.wifi_connected_network)
                    } else {
                        stringResource(R.string.wifi_nearby_network, index + 1)
                    },
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (network.isConnected) SuccessGreen else WarningAmber
                )
            }

            WifiInfoRow(
                label = stringResource(R.string.wifi_ssid_label),
                value = network.ssid
            )

            WifiInfoRow(
                label = stringResource(R.string.wifi_bssid_label),
                value = network.bssid,
                isMonospace = true
            )

            Text(
                text = stringResource(R.string.explain_wifi_mac),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            WifiInfoRow(
                label = stringResource(R.string.wifi_signal_label),
                value = "${network.signalStrength} dBm"
            )

            if (network.frequency > 0) {
                WifiInfoRow(
                    label = stringResource(R.string.wifi_frequency_label),
                    value = "${network.frequency} MHz"
                )
            }
        }
    }
}

@Composable
fun WifiInfoRow(
    label: String,
    value: String,
    isMonospace: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = if (isMonospace) {
                MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace)
            } else {
                MaterialTheme.typography.bodyMedium
            },
            fontWeight = FontWeight.Medium
        )
    }
}
