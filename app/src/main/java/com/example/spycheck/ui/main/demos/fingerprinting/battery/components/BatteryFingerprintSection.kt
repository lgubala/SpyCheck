package com.example.spycheck.ui.main.demos.fingerprinting.battery.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spycheck.BatteryFingerprint

@Composable
fun BatteryFingerprintSection(
    fingerprint: BatteryFingerprint?,
    onAnalyze: () -> Unit,
    isAnalyzing: Boolean,
    progress: Int,
    statusMessage: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔋",
                    fontSize = 32.sp
                )
                Column {
                    Text(
                        text = "Battery Fingerprint",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "NO permission required",
                        fontSize = 12.sp,
                        color = Color(0xFF4ECDC4)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (fingerprint == null) {
                BatteryEducationContent(
                    onAnalyze = onAnalyze,
                    isAnalyzing = isAnalyzing,
                    progress = progress,
                    statusMessage = statusMessage
                )
            } else {
                BatteryResultsContent(fingerprint = fingerprint)
            }
        }
    }
}

@Composable
private fun BatteryEducationContent(
    onAnalyze: () -> Unit,
    isAnalyzing: Boolean,
    progress: Int,
    statusMessage: String
) {
    Column {
        Text(
            text = "⚡ Battery Aging Patterns",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFFFFBE0B)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Every battery ages uniquely based on:",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.8f),
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        val examples = listOf(
            "🔋 Charge cycle count (how many times charged)",
            "🌡️ Temperature exposure patterns",
            "⚡ Charging speed preferences (fast vs slow)",
            "📉 Capacity degradation level (battery health)"
        )

        examples.forEach { example ->
            Row(
                modifier = Modifier.padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "•",
                    color = Color(0xFF4ECDC4),
                    fontSize = 12.sp
                )
                Text(
                    text = example,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFF6B6B).copy(alpha = 0.2f)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "💡 Real Example: Two identical phones bought on the same day will have different battery signatures within weeks due to different charging habits!",
                fontSize = 11.sp,
                color = Color.White,
                modifier = Modifier.padding(12.dp),
                lineHeight = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isAnalyzing) {
            Button(
                onClick = onAnalyze,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4ECDC4)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "🔋 Analyze My Battery",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Color(0xFF4ECDC4),
                    trackColor = Color(0xFF3A3A3A)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = statusMessage,
                    fontSize = 12.sp,
                    color = Color(0xFF4ECDC4),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun BatteryResultsContent(fingerprint: BatteryFingerprint) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Fingerprint ID & Uniqueness
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF9B59B6).copy(alpha = 0.15f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Battery ID: #${fingerprint.fingerprintId.take(12).uppercase()}",
                    fontSize = 12.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    color = Color(0xFF9B59B6)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Uniqueness: ${fingerprint.uniquenessScore}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Battery Health
        BatteryDetailCard(
            emoji = "💚",
            title = "Battery Health",
            signature = fingerprint.batteryHealth.healthSignature,
            description = fingerprint.batteryHealth.healthStatus,
            details = listOf(
                "Health: ${fingerprint.batteryHealth.healthPercentage}%",
                "Technology: ${fingerprint.batteryHealth.technology}",
                "Estimated Age: ${fingerprint.batteryHealth.ageEstimate}",
                "Charge Cycles: ${fingerprint.batteryHealth.cycleEstimate}"
            ),
            color = Color(0xFF2ECC71)
        )

        // Charging Behavior
        BatteryDetailCard(
            emoji = "⚡",
            title = "Charging Behavior",
            signature = fingerprint.chargingBehavior.behaviorSignature,
            description = fingerprint.chargingBehavior.chargingSpeed,
            details = listOf(
                "Current Level: ${fingerprint.chargingBehavior.currentChargeLevel}%",
                "Status: ${if (fingerprint.chargingBehavior.isCharging) "Charging" else "Discharging"}",
                "Source: ${fingerprint.chargingBehavior.chargingSource}",
                "Voltage: ${fingerprint.chargingBehavior.voltage} mV",
                "Current: ${fingerprint.chargingBehavior.current / 1000} mA"
            ),
            color = Color(0xFFFFBE0B)
        )

        // Capacity Profile
        BatteryDetailCard(
            emoji = "📊",
            title = "Capacity Profile",
            signature = fingerprint.capacityProfile.capacitySignature,
            description = fingerprint.capacityProfile.degradationLevel,
            details = listOf(
                "Degradation: ${fingerprint.capacityProfile.degradationLevel}",
                "Estimated Cycles: ${fingerprint.capacityProfile.estimatedCycleCount}"
            ),
            color = Color(0xFFFF6B6B)
        )

        // Temperature Profile
        BatteryDetailCard(
            emoji = "🌡️",
            title = "Temperature Profile",
            signature = fingerprint.temperatureProfile.tempSignature,
            description = fingerprint.temperatureProfile.temperatureStatus,
            details = listOf(
                "Current: ${String.format("%.1f", fingerprint.temperatureProfile.currentTemp)}°C",
                "Status: ${fingerprint.temperatureProfile.temperatureStatus}",
                "Thermal: ${fingerprint.temperatureProfile.thermalBehavior}"
            ),
            color = Color(0xFF4ECDC4)
        )

        // Uniqueness Factors
        if (fingerprint.uniquenessFactors.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2A2A2A)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "🎯 What Makes You Unique:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    fingerprint.uniquenessFactors.forEach { factor ->
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "•", color = Color(0xFF4ECDC4), fontSize = 11.sp)
                            Text(
                                text = factor,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BatteryDetailCard(
    emoji: String,
    title: String,
    signature: String,
    description: String,
    details: List<String>,
    color: Color
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
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
                Text(text = emoji, fontSize = 20.sp)
                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                    Text(
                        text = "ID: #${signature.uppercase()}",
                        fontSize = 10.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        color = color
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.7f),
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(8.dp))

            details.forEach { detail ->
                Text(
                    text = detail,
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}