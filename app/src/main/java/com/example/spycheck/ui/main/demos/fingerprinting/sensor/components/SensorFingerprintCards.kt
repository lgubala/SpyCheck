package com.example.spycheck.ui.main.demos.fingerprinting.sensor.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import com.example.spycheck.ui.main.demos.fingerprinting.sensor.utils.SensorAvailability

import com.example.spycheck.ui.main.demos.fingerprinting.sensor.utils.SensorFingerprint


@Composable
fun SensorFingerprintSection(
    fingerprint: SensorFingerprint?,
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
                    text = "🎯",
                    fontSize = 32.sp
                )
                Column {
                    Text(
                        text = "Sensor Fingerprint",
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
                // Pre-analysis education
                SensorEducationContent(
                    onAnalyze = onAnalyze,
                    isAnalyzing = isAnalyzing,
                    progress = progress,
                    statusMessage = statusMessage
                )
            } else {
                // Show results
                SensorResultsContent(fingerprint = fingerprint)
            }
        }
    }
}

@Composable
private fun SensorEducationContent(
    onAnalyze: () -> Unit,
    isAnalyzing: Boolean,
    progress: Int,
    statusMessage: String
) {
    Column {
        Text(
            text = "🔬 Manufacturing Imperfections",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFFFFBE0B)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Every phone's sensors have microscopic manufacturing flaws that create a unique pattern:",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.8f),
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        val examples = listOf(
            "📐 Accelerometer: Tiny bias when \"still\" (±0.2 m/s²)",
            "🌀 Gyroscope: Drift pattern (±0.05 rad/s)",
            "🧭 Magnetometer: Calibration offset (unique to each device)"
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
                text = "💡 Real Example: Two identical iPhone 14s sitting on a table will show DIFFERENT sensor readings. These differences persist forever and identify YOUR specific device.",
                fontSize = 11.sp,
                color = Color.White,
                modifier = Modifier.padding(12.dp),
                lineHeight = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Analyze button or progress
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
                    text = "🔍 Analyze My Sensors",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        } else {
            // Progress indicator
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
private fun SensorResultsContent(fingerprint: SensorFingerprint) {
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
                    text = "Sensor ID: #${fingerprint.fingerprintId.take(12).uppercase()}",
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

        // Accelerometer Bias
        if (fingerprint.accelerometerBias.biasSignature != "N/A") {
            SensorDetailCard(
                emoji = "📐",
                title = "Accelerometer Bias",
                signature = fingerprint.accelerometerBias.biasSignature,
                description = fingerprint.accelerometerBias.description,
                details = listOf(
                    "X-axis: ${String.format("%.4f", fingerprint.accelerometerBias.xBias)} m/s²",
                    "Y-axis: ${String.format("%.4f", fingerprint.accelerometerBias.yBias)} m/s²",
                    "Z-axis: ${String.format("%.4f", fingerprint.accelerometerBias.zBias)} m/s²",
                    "Magnitude: ${String.format("%.4f", fingerprint.accelerometerBias.magnitude)} m/s²"
                ),
                color = Color(0xFF4ECDC4)
            )
        }

        // Gyroscope Drift
        if (fingerprint.gyroscopeBias.driftSignature != "N/A") {
            SensorDetailCard(
                emoji = "🌀",
                title = "Gyroscope Drift",
                signature = fingerprint.gyroscopeBias.driftSignature,
                description = fingerprint.gyroscopeBias.description,
                details = listOf(
                    "X-drift: ${String.format("%.5f", fingerprint.gyroscopeBias.xDrift)} rad/s",
                    "Y-drift: ${String.format("%.5f", fingerprint.gyroscopeBias.yDrift)} rad/s",
                    "Z-drift: ${String.format("%.5f", fingerprint.gyroscopeBias.zDrift)} rad/s",
                    "Total: ${String.format("%.5f", fingerprint.gyroscopeBias.totalDrift)} rad/s"
                ),
                color = Color(0xFFFF6B6B)
            )
        }

        // Magnetometer Offset
        if (fingerprint.magnetometerBias.offsetSignature != "N/A") {
            SensorDetailCard(
                emoji = "🧭",
                title = "Magnetometer Offset",
                signature = fingerprint.magnetometerBias.offsetSignature,
                description = fingerprint.magnetometerBias.description,
                details = listOf(
                    "X-offset: ${String.format("%.2f", fingerprint.magnetometerBias.xOffset)} μT",
                    "Y-offset: ${String.format("%.2f", fingerprint.magnetometerBias.yOffset)} μT",
                    "Z-offset: ${String.format("%.2f", fingerprint.magnetometerBias.zOffset)} μT",
                    "Strength: ${String.format("%.2f", fingerprint.magnetometerBias.strength)} μT"
                ),
                color = Color(0xFFFFBE0B)
            )
        }

        // Sensor Availability
        SensorAvailabilityCard(fingerprint.sensorAvailability)

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
private fun SensorDetailCard(
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

@Composable
private fun SensorAvailabilityCard(availability: SensorAvailability) {
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
                text = "📊 Available Sensors (${availability.totalSensors} total)",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SensorBadge("📐", "Accel", availability.hasAccelerometer)
                SensorBadge("🌀", "Gyro", availability.hasGyroscope)
                SensorBadge("🧭", "Mag", availability.hasMagnetometer)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SensorBadge("👣", "Steps", availability.hasStepCounter)
                SensorBadge("👋", "Proximity", availability.hasProximitySensor)
                SensorBadge("💡", "Light", availability.hasLightSensor)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Signature: ${availability.sensorSignature.uppercase()}",
                fontSize = 10.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SensorBadge(emoji: String, label: String, available: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    if (available) Color(0xFF4ECDC4).copy(alpha = 0.2f)
                    else Color(0xFF3A3A3A),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = 18.sp,
                modifier = Modifier.alpha(if (available) 1f else 0.3f)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            color = if (available) Color.White else Color.Gray
        )
        Text(
            text = if (available) "✓" else "✗",
            fontSize = 10.sp,
            color = if (available) Color(0xFF4ECDC4) else Color.Gray
        )
    }
}

// Educational cards for sensor fingerprinting

@Composable
fun SensorFingerprintExplanationCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4ECDC4).copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "🤔 What Are Sensor Imperfections?",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "🏭 The Manufacturing Process:",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFFFFBE0B)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "When factories make phone sensors, they can't be PERFECTLY precise. Every sensor has microscopic differences:",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            val imperfections = listOf(
                "📐 Accelerometer: Tiny crystal misalignment (±0.0001mm)",
                "🌀 Gyroscope: MEMS structure variations (nanometer scale)",
                "🧭 Magnetometer: Magnetic material inconsistencies"
            )

            imperfections.forEach { imperfection ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "•", color = Color(0xFF4ECDC4), fontSize = 12.sp)
                    Text(
                        text = imperfection,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2A2A2A)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "💡 Think of it like fingerprints:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF4ECDC4)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Just like no two people have identical fingerprints, no two phones have identical sensor patterns - even if they're the same model made on the same day!",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SensorTrackingRealWorldCard() {
    var expanded by remember { mutableStateOf(false) }

    Card(
        onClick = { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFF6B6B).copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎭 Real-World Sensor Tracking",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFFFF6B6B)
                )
                Text(
                    text = if (expanded) "▼" else "▶",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 20.sp
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SensorRealWorldScenario(
                        number = "1",
                        title = "Tom's \"Anonymous\" Browsing",
                        story = "Tom uses Tor Browser and VPN for anonymous browsing. He never logs in. But websites STILL recognize him because his sensor fingerprint hasn't changed!",
                        impact = "Research shows sensor fingerprints can track users across 99.5% of browsing sessions, even with all privacy tools enabled."
                    )

                    SensorRealWorldScenario(
                        number = "2",
                        title = "Bank Fraud Detection Gone Wrong",
                        story = "Maria sold her old phone. The buyer used it for fraud. Police tracked the sensor fingerprint to Maria's new phone (same account). She got investigated even though she was innocent.",
                        impact = "Sensor fingerprints persist across factory resets and can link your old and new devices together."
                    )

                    SensorRealWorldScenario(
                        number = "3",
                        title = "Ad Network Cross-Device Tracking",
                        story = "Alex browses adult content on his personal phone. Same ad network detects his sensor fingerprint on his work tablet. Now his coworkers see embarrassing targeted ads.",
                        impact = "Companies link all your devices through sensor fingerprints - phone, tablet, smartwatch - building a complete profile."
                    )

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF2A2A2A)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "⚠️ CRITICAL: Sensor fingerprinting works even when:\n• All permissions denied\n• VPN active\n• Private browsing\n• Factory reset\n• Different accounts\n\nYour hardware doesn't lie.",
                            fontSize = 11.sp,
                            color = Color(0xFFFFBE0B),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(12.dp),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            if (!expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "👆 Tap to see 3 real examples",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SensorRealWorldScenario(
    number: String,
    title: String,
    story: String,
    impact: String
) {
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
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            Color(0xFFFF6B6B),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = number,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = story,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f),
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF4ECDC4).copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "💡",
                        fontSize = 12.sp
                    )
                    Text(
                        text = impact,
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SensorTestItYourselfCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4ECDC4).copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "🧪 Test Sensor Persistence",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF4ECDC4)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Prove sensor fingerprints are permanent:",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            SensorExperimentCard(
                number = "1",
                title = "The Factory Reset Test",
                steps = listOf(
                    "Screenshot your Sensor ID (#A7F2...)",
                    "Clear ALL app data",
                    "Factory reset your phone",
                    "Reinstall this app",
                    "Analyze sensors again"
                ),
                why = "SAME ID! Hardware imperfections don't change."
            )

            Spacer(modifier = Modifier.height(12.dp))

            SensorExperimentCard(
                number = "2",
                title = "The Stillness Test",
                steps = listOf(
                    "Place phone on flat table",
                    "Analyze sensors (note the readings)",
                    "Leave for 5 minutes without touching",
                    "Analyze again"
                ),
                why = "Same biases! Proves it's hardware, not movement."
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2A2A2A)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "💡 Advanced: Compare your sensor ID with a friend's phone of the SAME MODEL. You'll see different IDs - proving every device is unique!",
                    fontSize = 11.sp,
                    color = Color(0xFFFFBE0B),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun SensorExperimentCard(
    number: String,
    title: String,
    steps: List<String>,
    why: String
) {
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(
                            Color(0xFF4ECDC4),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = number,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            steps.forEachIndexed { index, step ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${index + 1}.",
                        fontSize = 11.sp,
                        color = Color(0xFF4ECDC4),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = step,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFBE0B).copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "📌", fontSize = 12.sp)
                    Text(
                        text = "Why: $why",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}