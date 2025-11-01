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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spycheck.R
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
            containerColor = colorResource(R.color.card_background)
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
                    text = stringResource(R.string.fp_super_section_icon),
                    fontSize = 32.sp
                )
                Column {
                    Text(
                        text = stringResource(R.string.fp_super_section_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.danger_red)
                    )
                    Text(
                        text = stringResource(R.string.fp_super_section_subtitle),
                        fontSize = 12.sp,
                        color = colorResource(R.color.warning)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Warning banner
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.danger_red).copy(alpha = 0.2f * pulseAlpha)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.fp_super_section_warning_title),
                        color = colorResource(R.color.danger_red),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.fp_super_section_warning_desc),
                        color = colorResource(R.color.white),
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
                    containerColor = colorResource(R.color.battery_result_color).copy(alpha = 0.2f)
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
                        text = stringResource(R.string.fp_super_section_your_super_fp),
                        color = colorResource(R.color.battery_result_color),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.fp_super_section_fp_id, superFingerprint.superFingerprintId.take(16).uppercase()),
                        color = colorResource(R.color.info_blue),
                        fontSize = 16.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.fp_super_section_global_uniqueness),
                        color = colorResource(R.color.text_secondary),
                        fontSize = 11.sp
                    )
                    Text(
                        text = superFingerprint.globalUniqueness,
                        color = colorResource(R.color.white),
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
                    emoji = stringResource(R.string.fp_super_section_icon_confidence),
                    value = stringResource(R.string.fp_super_section_confidence_value, superFingerprint.confidenceScore),
                    label = stringResource(R.string.fp_super_section_confidence_label),
                    color = when {
                        superFingerprint.confidenceScore >= 90 -> colorResource(R.color.danger_red)
                        superFingerprint.confidenceScore >= 70 -> colorResource(R.color.warning)
                        else -> colorResource(R.color.info_blue)
                    }
                )

                MetricCard(
                    emoji = stringResource(R.string.fp_super_section_icon_resistance),
                    value = when {
                        superFingerprint.trackingResistance.contains("IMPOSSIBLE") -> stringResource(R.string.fp_super_section_avoid_0)
                        superFingerprint.trackingResistance.contains("Very Hard") -> stringResource(R.string.fp_super_section_avoid_10)
                        superFingerprint.trackingResistance.contains("Hard") -> stringResource(R.string.fp_super_section_avoid_30)
                        else -> stringResource(R.string.fp_super_section_avoid_50)
                    },
                    label = stringResource(R.string.fp_super_section_can_avoid_label),
                    color = colorResource(R.color.danger_red)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tracking resistance
            SuperFingerprintDetailCard(
                emoji = stringResource(R.string.fp_super_section_icon_resistance),
                title = stringResource(R.string.fp_super_section_tracking_resistance),
                description = superFingerprint.trackingResistance,
                color = colorResource(R.color.danger_red)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Persistence level
            SuperFingerprintDetailCard(
                emoji = stringResource(R.string.fp_super_section_icon_persistence),
                title = stringResource(R.string.fp_super_section_persistence_level),
                description = superFingerprint.persistenceLevel,
                color = colorResource(R.color.warning)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Cross-platform tracking
            SuperFingerprintDetailCard(
                emoji = stringResource(R.string.fp_super_section_icon_cross_platform),
                title = stringResource(R.string.fp_super_section_cross_platform_title),
                description = if (superFingerprint.crossPlatformTracking)
                    stringResource(R.string.fp_super_section_cross_platform_yes)
                else
                    stringResource(R.string.fp_super_section_cross_platform_limited),
                color = if (superFingerprint.crossPlatformTracking) colorResource(R.color.danger_red) else colorResource(R.color.info_blue)
            )

            // Combined factors
            if (superFingerprint.combinedFactors.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(R.color.surface_dark)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.fp_super_section_combined_vectors),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = colorResource(R.color.white)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        superFingerprint.combinedFactors.forEach { factor ->
                            Text(
                                text = factor,
                                fontSize = 11.sp,
                                color = colorResource(R.color.white).copy(alpha = 0.85f),
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
            containerColor = colorResource(R.color.surface_dark)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.fp_super_section_components, components.componentsAnalyzed),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = colorResource(R.color.white)
            )

            Spacer(modifier = Modifier.height(12.dp))

            ComponentRow(stringResource(R.string.fp_super_section_component_device), components.deviceId)
            ComponentRow(stringResource(R.string.fp_super_section_component_sensor), components.sensorId)
            ComponentRow(stringResource(R.string.fp_super_section_component_battery), components.batteryId)
            ComponentRow(stringResource(R.string.fp_super_section_component_audio), components.audioId)
            ComponentRow(stringResource(R.string.fp_super_section_component_network), components.networkId)
            ComponentRow(stringResource(R.string.fp_super_section_component_performance), components.performanceId)
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
            color = colorResource(R.color.white).copy(alpha = 0.7f),
            fontSize = 11.sp
        )
        if (id != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = stringResource(R.string.fp_super_section_checkmark),
                    color = colorResource(R.color.info_blue),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.fp_super_section_component_id, id.take(8).uppercase()),
                    color = colorResource(R.color.info_blue),
                    fontSize = 10.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
        } else {
            Text(
                text = stringResource(R.string.fp_super_section_not_analyzed),
                color = colorResource(R.color.text_secondary),
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
                color = colorResource(R.color.text_secondary),
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
                    color = colorResource(R.color.white)
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = colorResource(R.color.white).copy(alpha = 0.8f),
                    lineHeight = 15.sp
                )
            }
        }
    }
}