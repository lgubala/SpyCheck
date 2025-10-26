package com.example.spycheck.ui.main.demos.fingerprinting

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spycheck.R

/**
 * Home screen for Fingerprinting section
 * Shows clear explanations of device fingerprinting methods
 */
@Composable
fun FingerprintingHomeScreen(
    onDemoClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header explanation
        item {
            HeaderCard()
        }

        // Demo cards
        items(getFingerprintingDemos()) { demo ->
            FingerprintDemoCard(
                demo = demo,
                onClick = { onDemoClick(demo.id) }
            )
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HeaderCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF9D4EDD).copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.fingerprinting_home_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.fingerprinting_home_intro),
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = Color(0xFF2A2A2A),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.fingerprinting_home_subtitle),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF9D4EDD),
                    modifier = Modifier.padding(12.dp),
                    lineHeight = 19.sp
                )
            }
        }
    }
}

@Composable
private fun FingerprintDemoCard(
    demo: FingerprintDemo,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon
            Surface(
                color = demo.color.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Box(
                    contentAlignment = androidx.compose.ui.Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = demo.icon,
                        contentDescription = null,
                        tint = demo.color,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(demo.title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(demo.description),
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

private data class FingerprintDemo(
    val id: String,
    val title: Int,
    val description: Int,
    val icon: ImageVector,
    val color: Color
)

private fun getFingerprintingDemos(): List<FingerprintDemo> {
    return listOf(
        FingerprintDemo(
            id = "device",
            title = R.string.fp_device_title,
            description = R.string.fp_device_short_desc,
            icon = Icons.Default.PhoneAndroid,
            color = Color(0xFF4ECDC4)
        ),
        FingerprintDemo(
            id = "sensor",
            title = R.string.fp_sensor_title,
            description = R.string.fp_sensor_short_desc,
            icon = Icons.Default.Sensors,
            color = Color(0xFF06FFA5)
        ),
        FingerprintDemo(
            id = "battery",
            title = R.string.fp_battery_title,
            description = R.string.fp_battery_short_desc,
            icon = Icons.Default.BatteryChargingFull,
            color = Color(0xFFFFBE0B)
        ),
        FingerprintDemo(
            id = "audio",
            title = R.string.fp_audio_title,
            description = R.string.fp_audio_short_desc,
            icon = Icons.Default.Mic,
            color = Color(0xFFFF6B6B)
        ),
        FingerprintDemo(
            id = "network",
            title = R.string.fp_network_title,
            description = R.string.fp_network_short_desc,
            icon = Icons.Default.Wifi,
            color = Color(0xFF9D4EDD)
        ),
        FingerprintDemo(
            id = "performance",
            title = R.string.fp_performance_title,
            description = R.string.fp_performance_short_desc,
            icon = Icons.Default.Speed,
            color = Color(0xFFFF9E00)
        ),
        FingerprintDemo(
            id = "combined",
            title = R.string.fp_combined_title,
            description = R.string.fp_combined_short_desc,
            icon = Icons.Default.Fingerprint,
            color = Color(0xFFFF6B6B)
        )
    )
}