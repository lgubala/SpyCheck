package com.example.spycheck.ui.main.demos.fingerprinting.battery.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spycheck.R
import com.example.spycheck.ui.main.demos.fingerprinting.battery.BatteryFingerprintDemoViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BatteryFingerprintDemoContent(viewModel: BatteryFingerprintDemoViewModel) {
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val fingerprintData by viewModel.fingerprintData.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Control Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!isAnalyzing && fingerprintData == null) {
                Button(
                    onClick = { viewModel.startAnalysis() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.danger_red)
                    )
                ) {
                    Icon(Icons.Default.BatteryChargingFull, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.fp_battery_analyze_button))
                }
            } else if (isAnalyzing) {
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = colorResource(R.color.white)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(stringResource(R.string.fp_battery_analyzing))
                }
            } else {
                Button(
                    onClick = { viewModel.startAnalysis() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.danger_red)
                    )
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.fp_refresh))
                }

                OutlinedButton(
                    onClick = { viewModel.clearResults() },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.fp_battery_content_clear))
                }
            }
        }

        // Analysis Status
        if (isAnalyzing) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.warning).copy(alpha = 0.15f)
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
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = colorResource(R.color.warning)
                    )
                    Column {
                        Text(
                            text = stringResource(R.string.fp_battery_analyzing),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = colorResource(R.color.warning)
                        )
                        Text(
                            text = stringResource(R.string.fp_battery_content_analyzing_desc),
                            fontSize = 14.sp,
                            color = colorResource(R.color.text_primary).copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Error Display
        error?.let { errorMessage ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.danger_red).copy(alpha = 0.15f)
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
                        tint = colorResource(R.color.danger_red),
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = stringResource(R.string.fp_battery_error_title),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = colorResource(R.color.danger_red)
                        )
                        Text(
                            text = errorMessage,
                            fontSize = 14.sp,
                            color = colorResource(R.color.text_primary).copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Fingerprint Results
        fingerprintData?.let { data ->
            // Title
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.warning).copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.fp_battery_content_results_title_with_icon, stringResource(R.string.fp_battery_results_title)),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = colorResource(R.color.warning)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.fp_battery_content_analyzed_time, formatTimestamp(data.analysisTime)),
                        fontSize = 12.sp,
                        color = colorResource(R.color.text_primary).copy(alpha = 0.6f)
                    )
                }
            }

            // Fingerprint Details
            FingerprintDetailCard(
                label = stringResource(R.string.fp_battery_level),
                value = data.level,
                icon = stringResource(R.string.fp_battery_content_icon_level)
            )

            FingerprintDetailCard(
                label = stringResource(R.string.fp_battery_health),
                value = data.health,
                icon = stringResource(R.string.fp_battery_content_icon_health)
            )

            FingerprintDetailCard(
                label = stringResource(R.string.fp_battery_temperature),
                value = data.temperature,
                icon = stringResource(R.string.fp_battery_content_icon_temperature)
            )

            FingerprintDetailCard(
                label = stringResource(R.string.fp_battery_voltage),
                value = data.voltage,
                icon = stringResource(R.string.fp_battery_content_icon_voltage)
            )

            FingerprintDetailCard(
                label = stringResource(R.string.fp_battery_capacity),
                value = data.capacity,
                icon = stringResource(R.string.fp_battery_content_icon_capacity)
            )

            FingerprintDetailCard(
                label = stringResource(R.string.fp_battery_drain_rate),
                value = data.drainRate,
                icon = stringResource(R.string.fp_battery_content_icon_drain)
            )

            FingerprintDetailCard(
                label = stringResource(R.string.fp_battery_charging_pattern),
                value = data.chargingPattern,
                icon = stringResource(R.string.fp_battery_content_icon_charging)
            )

            // Unique Hash
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.danger_red).copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(R.string.fp_battery_content_icon_target), fontSize = 20.sp)
                        Text(
                            text = stringResource(R.string.fp_battery_fingerprint_hash),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = colorResource(R.color.danger_red)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = colorResource(R.color.black).copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = data.fingerprintHash,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = colorResource(R.color.white),
                            modifier = Modifier.padding(12.dp),
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.fp_battery_content_hash_description),
                        fontSize = 12.sp,
                        color = colorResource(R.color.text_primary).copy(alpha = 0.7f),
                        lineHeight = 18.sp
                    )
                }
            }

            // What This Reveals
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.warning).copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = stringResource(R.string.fp_battery_reveals_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = colorResource(R.color.warning)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    listOf(
                        R.string.fp_battery_reveal1,
                        R.string.fp_battery_reveal2,
                        R.string.fp_battery_reveal3,
                        R.string.fp_battery_reveal4,
                        R.string.fp_battery_reveal5,
                        R.string.fp_battery_reveal6
                    ).forEach { stringRes ->
                        Text(
                            text = stringResource(R.string.fp_battery_content_bullet_point, stringResource(stringRes)),
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            color = colorResource(R.color.text_primary).copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // Warning
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.danger_red).copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = stringResource(R.string.fp_battery_warning_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = colorResource(R.color.danger_red)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.fp_battery_warning_desc),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = colorResource(R.color.text_primary).copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

@Composable
private fun FingerprintDetailCard(
    label: String,
    value: String,
    icon: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.card_background)
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
            Text(text = icon, fontSize = 24.sp)

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = colorResource(R.color.text_primary).copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = colorResource(R.color.white)
                )
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60000 -> "Just now"
        diff < 3600000 -> "${diff / 60000}m ago"
        else -> SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(timestamp))
    }
}