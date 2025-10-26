package com.example.spycheck.ui.main.demos.fingerprinting.combined.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spycheck.SuperFingerprint

@Composable
fun SuperFingerprintSection(
    superFingerprint: SuperFingerprint
) {
    // Pulsing animation for warning
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

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
                    text = "🧬",
                    fontSize = 32.sp
                )
                Column {
                    Text(
                        text = "SUPER FINGERPRINT",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6B6B)
                    )
                    Text(
                        text = "Combined tracking profile",
                        fontSize = 12.sp,
                        color = Color(0xFFFFBE0B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Warning banner
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFF6B6B).copy(alpha = 0.2f * pulseAlpha)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "🚨 THIS IS YOUR REAL TRACKING ID",
                        color = Color(0xFFFF6B6B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Companies combine ALL these fingerprints to track you across apps, websites, and devices. This is what they see.",
                        color = Color.White,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Super Fingerprint ID
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF9B59B6).copy(alpha = 0.2f)
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
                        text = "YOUR SUPER FINGERPRINT",
                        color = Color(0xFF9B59B6),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "#${superFingerprint.superFingerprintId.take(16).uppercase()}",
                        color = Color(0xFF4ECDC4),
                        fontSize = 16.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Global Uniqueness:",
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                    Text(
                        text = superFingerprint.globalUniqueness,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Component breakdown
            SuperFingerprintComponents(superFingerprint.componentFingerprints)

            Spacer(modifier = Modifier.height(16.dp))

            // Confidence & Tracking metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricCard(
                    emoji = "🎯",
                    value = "${superFingerprint.confidenceScore}%",
                    label = "Confidence",
                    color = when {
                        superFingerprint.confidenceScore >= 90 -> Color(0xFFFF6B6B)
                        superFingerprint.confidenceScore >= 70 -> Color(0xFFFFBE0B)
                        else -> Color(0xFF4ECDC4)
                    }
                )

                MetricCard(
                    emoji = "🛡️",
                    value = when {
                        superFingerprint.trackingResistance.contains("IMPOSSIBLE") -> "0%"
                        superFingerprint.trackingResistance.contains("Very Hard") -> "10%"
                        superFingerprint.trackingResistance.contains("Hard") -> "30%"
                        else -> "50%"
                    },
                    label = "Can Avoid",
                    color = Color(0xFFFF6B6B)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tracking resistance
            SuperFingerprintDetailCard(
                emoji = "🛡️",
                title = "Tracking Resistance",
                description = superFingerprint.trackingResistance,
                color = Color(0xFFFF6B6B)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Persistence level
            SuperFingerprintDetailCard(
                emoji = "⏱️",
                title = "Persistence Level",
                description = superFingerprint.persistenceLevel,
                color = Color(0xFFFFBE0B)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Cross-platform tracking
            SuperFingerprintDetailCard(
                emoji = "🌐",
                title = "Cross-Platform Tracking",
                description = if (superFingerprint.crossPlatformTracking)
                    "YES - You can be tracked across web browsers, mobile apps, and different devices"
                else
                    "Limited - Some cross-platform tracking possible",
                color = if (superFingerprint.crossPlatformTracking) Color(0xFFFF6B6B) else Color(0xFF4ECDC4)
            )

            // Combined factors
            if (superFingerprint.combinedFactors.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A1A1A)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "🔗 Combined Tracking Vectors:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        superFingerprint.combinedFactors.forEach { factor ->
                            Text(
                                text = factor,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.padding(vertical = 3.dp),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SuperFingerprintComponents(components: com.example.spycheck.ComponentFingerprints) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "📊 Components (${components.componentsAnalyzed}/6 analyzed)",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            ComponentRow("📱 Device", components.deviceId)
            ComponentRow("🎯 Sensor", components.sensorId)
            ComponentRow("🔋 Battery", components.batteryId)
            ComponentRow("🎤 Audio", components.audioId)
            ComponentRow("🌐 Network", components.networkId)
            ComponentRow("🚀 Performance", components.performanceId)
        }
    }
}

@Composable
private fun ComponentRow(label: String, id: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 11.sp
        )
        if (id != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "✓",
                    color = Color(0xFF4ECDC4),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "#${id.take(8).uppercase()}",
                    color = Color(0xFF4ECDC4),
                    fontSize = 10.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
        } else {
            Text(
                text = "Not analyzed",
                color = Color.Gray,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun MetricCard(emoji: String, value: String, label: String, color: Color) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = color,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = Color.Gray,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun SuperFingerprintDetailCard(
    emoji: String,
    title: String,
    description: String,
    color: Color
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = emoji, fontSize = 20.sp)
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    lineHeight = 15.sp
                )
            }
        }
    }
}