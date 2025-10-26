package com.example.spycheck.ui.main.demos.sneaky.sensors.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spycheck.R
import com.example.spycheck.ui.main.demos.sneaky.sensors.SensorTrackingDemoViewModel

@Composable
fun SensorTrackingDemoContent(viewModel: SensorTrackingDemoViewModel) {
    val isTracking by viewModel.isTracking.collectAsState()
    val movementData by viewModel.movementData.collectAsState()
    val movementHistory by viewModel.movementHistory.collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Control Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!isTracking) {
                Button(
                    onClick = { viewModel.startTracking() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF06FFA5)
                    )
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.sensor_tracking_start))
                }
            } else {
                Button(
                    onClick = { viewModel.stopTracking() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6B6B)
                    )
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.sensor_tracking_stop))
                }
            }

            OutlinedButton(
                onClick = { viewModel.clearData() },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.sensor_tracking_clear))
            }
        }

        // Status Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isTracking) Color(0xFF06FFA5).copy(alpha = 0.15f)
                else Color(0xFF2A2A2A)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.DirectionsWalk,
                    contentDescription = null,
                    tint = if (isTracking) Color(0xFF06FFA5) else Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isTracking)
                            stringResource(R.string.sensor_tracking_active)
                        else
                            stringResource(R.string.sensor_tracking_inactive),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (isTracking) Color(0xFF06FFA5) else Color.White
                    )
                    Text(
                        text = if (isTracking)
                            stringResource(R.string.sensor_tracking_move_around)
                        else
                            stringResource(R.string.sensor_tracking_press_start),
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Movement Data Display
        if (movementData != null) {
            MovementDataCard(movementData!!)
        }

        // Instructions
        if (!isTracking && movementData == null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF4ECDC4).copy(alpha = 0.15f)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = stringResource(R.string.sensor_tracking_how_to),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4ECDC4)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.sensor_tracking_instructions),
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Privacy Warning
        if (movementData != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFF6B6B).copy(alpha = 0.15f)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = stringResource(R.string.sensor_tracking_privacy_warning),
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun MovementDataCard(data: com.example.spycheck.ui.main.demos.sneaky.sensors.utils.MovementData) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.sensor_tracking_detected_movement),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Divider(color = Color.White.copy(alpha = 0.1f))

            // Steps
            DataRow(
                label = stringResource(R.string.sensor_tracking_steps),
                value = data.stepCount.toString()
            )

            // Distance
            DataRow(
                label = stringResource(R.string.sensor_tracking_distance),
                value = stringResource(R.string.sensor_tracking_meters, data.distanceMeters)
            )

            // Direction
            DataRow(
                label = stringResource(R.string.sensor_tracking_direction),
                value = stringResource(R.string.sensor_tracking_direction_value,
                    data.direction, data.directionDegrees)
            )

            // Activity
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = when (data.currentActivity) {
                        "Running" -> Color(0xFFFF6B6B).copy(alpha = 0.2f)
                        "Walking" -> Color(0xFF4ECDC4).copy(alpha = 0.2f)
                        else -> Color(0xFF9D4EDD).copy(alpha = 0.2f)
                    }
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.sensor_tracking_current_activity),
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = data.currentActivity,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Intensity Bar
            Column {
                Text(
                    text = stringResource(R.string.sensor_tracking_movement_intensity),
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = (data.movementIntensity / 20f).coerceIn(0f, 1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = when {
                        data.movementIntensity > 15f -> Color(0xFFFF6B6B)
                        data.movementIntensity > 8f -> Color(0xFFFFBE0B)
                        else -> Color(0xFF4ECDC4)
                    },
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
            }
        }
    }
}

@Composable
private fun DataRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}