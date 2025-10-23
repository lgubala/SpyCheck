package com.example.spycheck.ui.main.demos.wifi.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.spycheck.R
import com.example.spycheck.ui.theme.DangerRed

@Composable
fun WifiPermissionButtons(
    hasLocationPermission: Boolean,
    hasWifiPermission: Boolean,
    needsWifiPermission: Boolean,
    onRequestLocation: () -> Unit,
    onRequestWifi: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        if (!hasLocationPermission) {
            Button(
                onClick = onRequestLocation,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
            ) {
                Text(stringResource(R.string.grant_location_permission))
            }
        }

        if (needsWifiPermission && !hasWifiPermission) {
            Button(
                onClick = onRequestWifi,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
            ) {
                Text(stringResource(R.string.grant_wifi_permission))
            }
        }
    }
}