package com.example.spycheck.ui.main.demos.fingerprinting.device.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spycheck.R
import com.example.spycheck.ui.main.demos.fingerprinting.device.DeviceFingerprintDemoViewModel
import com.example.spycheck.ui.theme.DangerRed
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DeviceFingerprintDemoContent(viewModel: DeviceFingerprintDemoViewModel) {
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
                        containerColor = DangerRed
                    )
                ) {
                    Icon(Icons.Default.PhoneAndroid, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.fp_device_analyze_button))
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
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(stringResource(R.string.fp_device_analyzing))
                }
            } else {
                Button(
                    onClick = { viewModel.startAnalysis() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DangerRed
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
                    Text("Clear")
                }
            }
        }

        // Analysis Status
        if (isAnalyzing) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF4ECDC4).copy(alpha = 0.15f)
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
                        color = Color(0xFF4ECDC4)
                    )
                    Column {
                        Text(
                            text = stringResource(R.string.fp_device_analyzing),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF4ECDC4)
                        )
                        Text(
                            text = "Collecting device hardware and software characteristics...",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Error Display
        error?.let { errorMessage ->
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
                        tint = DangerRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = stringResource(R.string.fp_device_error_title),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = DangerRed
                        )
                        Text(
                            text = errorMessage,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
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
                    containerColor = Color(0xFF4ECDC4).copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "📱 " + stringResource(R.string.fp_device_results_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF4ECDC4)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Analyzed: ${formatTimestamp(data.analysisTime)}",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            // Fingerprint Details
            FingerprintDetailCard(
                label = stringResource(R.string.fp_device_model),
                value = data.model,
                icon = "📱"
            )

            FingerprintDetailCard(
                label = stringResource(R.string.fp_device_manufacturer),
                value = data.manufacturer,
                icon = "🏭"
            )

            FingerprintDetailCard(
                label = stringResource(R.string.fp_device_android_version),
                value = data.androidVersion,
                icon = "🤖"
            )

            FingerprintDetailCard(
                label = stringResource(R.string.fp_device_screen_size),
                value = data.screenSize,
                icon = "📐"
            )

            FingerprintDetailCard(
                label = stringResource(R.string.fp_device_screen_density),
                value = data.screenDensity,
                icon = "🔬"
            )

            FingerprintDetailCard(
                label = stringResource(R.string.fp_device_cpu),
                value = data.cpu,
                icon = "⚙️"
            )

            FingerprintDetailCard(
                label = stringResource(R.string.fp_device_memory),
                value = data.memory,
                icon = "💾"
            )

            FingerprintDetailCard(
                label = stringResource(R.string.fp_device_storage),
                value = data.storage,
                icon = "💿"
            )

            FingerprintDetailCard(
                label = stringResource(R.string.fp_device_sensors),
                value = data.sensors,
                icon = "📡"
            )

            // Uniqueness Score
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFBE0B).copy(alpha = 0.15f)
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
                        Text(text = "🎯", fontSize = 20.sp)
                        Text(
                            text = stringResource(R.string.fp_device_uniqueness),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFFFFBE0B)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = data.uniqueness,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFFFBE0B)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Your device is unique among millions. Only 0.03% share similar characteristics.",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        lineHeight = 18.sp
                    )
                }
            }

            // Unique Hash
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = DangerRed.copy(alpha = 0.15f)
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
                        Text(text = "🔑", fontSize = 20.sp)
                        Text(
                            text = stringResource(R.string.fp_device_fingerprint_hash),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = DangerRed
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = Color.Black.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = data.fingerprintHash,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White,
                            modifier = Modifier.padding(12.dp),
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "This ID follows you everywhere until you buy a new phone.",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        lineHeight = 18.sp
                    )
                }
            }

            // What This Reveals
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
                        text = stringResource(R.string.fp_device_reveals_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFFFFBE0B)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    listOf(
                        R.string.fp_device_reveal1,
                        R.string.fp_device_reveal2,
                        R.string.fp_device_reveal3,
                        R.string.fp_device_reveal4,
                        R.string.fp_device_reveal5,
                        R.string.fp_device_reveal6
                    ).forEach { stringRes ->
                        Text(
                            text = "• " + stringResource(stringRes),
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // Warning
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = DangerRed.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = stringResource(R.string.fp_device_warning_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = DangerRed
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.fp_device_warning_desc),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color.White.copy(alpha = 0.9f)
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
            containerColor = Color(0xFF2A2A2A)
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
                    color = Color.White.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White
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