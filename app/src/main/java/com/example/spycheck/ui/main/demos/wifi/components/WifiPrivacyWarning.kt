package com.example.spycheck.ui.main.demos.wifi.components

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spycheck.R
import com.example.spycheck.ui.theme.DangerRed

@Composable
fun WifiPrivacyWarning() {
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(containerColor = DangerRed.copy(alpha = 0.15f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.wifi_privacy_title),
                style = MaterialTheme.typography.titleMedium,
                color = DangerRed
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.wifi_privacy_description),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
            ) {
                Text(stringResource(R.string.open_settings_button))
            }
        }
    }
}
