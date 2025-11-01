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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spycheck.BatteryFingerprint
import com.example.spycheck.R

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
                    text = stringResource(R.string.fp_battery_section_icon),
                    fontSize = 32.sp
                )
                Column {
                    Text(
                        text = stringResource(R.string.fp_battery_section_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.text_primary)
                    )
                    Text(
                        text = stringResource(R.string.fp_battery_section_no_permission),
                        fontSize = 12.sp,
                        color = colorResource(R.color.info_blue)
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
    val context = LocalContext.current
    Column {
        Text(
            text = stringResource(R.string.fp_battery_section_aging_patterns),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = colorResource(R.color.warning)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.fp_battery_section_every_battery),
            fontSize = 12.sp,
            color = colorResource(R.color.text_primary).copy(alpha = 0.8f),
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        val examples = context.resources.getStringArray(R.array.fp_battery_section_examples)

        examples.forEach { example ->
            Row(
                modifier = Modifier.padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = stringResource(R.string.fp_battery_section_bullet),
                    color = colorResource(R.color.info_blue),
                    fontSize = 12.sp
                )
                Text(
                    text = example,
                    fontSize = 11.sp,
                    color = colorResource(R.color.text_primary).copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = colorResource(R.color.danger_red).copy(alpha = 0.2f)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(R.string.fp_battery_section_real_example),
                fontSize = 11.sp,
                color = colorResource(R.color.white),
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
                    containerColor = colorResource(R.color.info_blue)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.fp_battery_section_analyze_button),
                    color = colorResource(R.color.black),
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
                    color = colorResource(R.color.info_blue),
                    trackColor = colorResource(R.color.battery_progress_track)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = statusMessage,
                    fontSize = 12.sp,
                    color = colorResource(R.color.info_blue),
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
                containerColor = colorResource(R.color.battery_result_bg)
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
                    text = stringResource(R.string.fp_battery_section_battery_id, fingerprint.fingerprintId.take(12).uppercase()),
                    fontSize = 12.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    color = colorResource(R.color.battery_result_color)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.fp_battery_section_uniqueness, fingerprint.uniquenessScore),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.white)
                )
            }
        }

        // Battery Health
        BatteryDetailCard(
            emoji = stringResource(R.string.fp_battery_section_icon_health),
            title = stringResource(R.string.fp_battery_section_health_title),
            signature = fingerprint.batteryHealth.healthSignature,
            description = fingerprint.batteryHealth.healthStatus,
            details = listOf(
                stringResource(R.string.fp_battery_section_health_percentage, fingerprint.batteryHealth.healthPercentage),
                stringResource(R.string.fp_battery_section_technology, fingerprint.batteryHealth.technology),
                stringResource(R.string.fp_battery_section_estimated_age, fingerprint.batteryHealth.ageEstimate),
                stringResource(R.string.fp_battery_section_charge_cycles, fingerprint.batteryHealth.cycleEstimate)
            ),
            color = colorResource(R.color.battery_codec_color)
        )

        // Charging Behavior
        BatteryDetailCard(
            emoji = stringResource(R.string.fp_battery_section_icon_charging),
            title = stringResource(R.string.fp_battery_section_charging_title),
            signature = fingerprint.chargingBehavior.behaviorSignature,
            description = fingerprint.chargingBehavior.chargingSpeed,
            details = listOf(
                stringResource(R.string.fp_battery_section_current_level, fingerprint.chargingBehavior.currentChargeLevel),
                stringResource(R.string.fp_battery_section_status, if (fingerprint.chargingBehavior.isCharging) stringResource(R.string.fp_battery_section_charging) else stringResource(R.string.fp_battery_section_discharging)),
                stringResource(R.string.fp_battery_section_source, fingerprint.chargingBehavior.chargingSource),
                stringResource(R.string.fp_battery_section_voltage_value, fingerprint.chargingBehavior.voltage),
                stringResource(R.string.fp_battery_section_current_value, fingerprint.chargingBehavior.current / 1000)
            ),
            color = colorResource(R.color.warning)
        )

        // Capacity Profile
        BatteryDetailCard(
            emoji = stringResource(R.string.fp_battery_section_icon_capacity),
            title = stringResource(R.string.fp_battery_section_capacity_title),
            signature = fingerprint.capacityProfile.capacitySignature,
            description = fingerprint.capacityProfile.degradationLevel,
            details = listOf(
                stringResource(R.string.fp_battery_section_degradation, fingerprint.capacityProfile.degradationLevel),
                stringResource(R.string.fp_battery_section_estimated_cycles, fingerprint.capacityProfile.estimatedCycleCount)
            ),
            color = colorResource(R.color.danger_red)
        )

        // Temperature Profile
        BatteryDetailCard(
            emoji = stringResource(R.string.fp_battery_section_icon_temperature),
            title = stringResource(R.string.fp_battery_section_temp_title),
            signature = fingerprint.temperatureProfile.tempSignature,
            description = fingerprint.temperatureProfile.temperatureStatus,
            details = listOf(
                stringResource(R.string.fp_battery_section_current_temp, String.format("%.1f", fingerprint.temperatureProfile.currentTemp)),
                stringResource(R.string.fp_battery_section_temp_status, fingerprint.temperatureProfile.temperatureStatus),
                stringResource(R.string.fp_battery_section_thermal, fingerprint.temperatureProfile.thermalBehavior)
            ),
            color = colorResource(R.color.info_blue)
        )

        // Uniqueness Factors
        if (fingerprint.uniquenessFactors.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.card_background)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.fp_battery_section_unique_factors),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = colorResource(R.color.white)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    fingerprint.uniquenessFactors.forEach { factor ->
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = stringResource(R.string.fp_battery_section_bullet), color = colorResource(R.color.info_blue), fontSize = 11.sp)
                            Text(
                                text = factor,
                                fontSize = 11.sp,
                                color = colorResource(R.color.text_primary).copy(alpha = 0.8f)
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
                        color = colorResource(R.color.white)
                    )
                    Text(
                        text = stringResource(R.string.fp_battery_section_id_label, signature.uppercase()),
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
                color = colorResource(R.color.text_primary).copy(alpha = 0.7f),
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(8.dp))

            details.forEach { detail ->
                Text(
                    text = detail,
                    fontSize = 10.sp,
                    color = colorResource(R.color.text_primary).copy(alpha = 0.6f),
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}