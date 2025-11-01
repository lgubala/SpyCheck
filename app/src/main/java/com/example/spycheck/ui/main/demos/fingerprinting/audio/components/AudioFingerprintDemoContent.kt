package com.example.spycheck.ui.main.demos.fingerprinting.audio.components

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
import com.example.spycheck.ui.main.demos.fingerprinting.audio.AudioFingerprintDemoViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Demo content showing audio fingerprinting
 */
@Composable
fun AudioFingerprintDemoContent(viewModel: AudioFingerprintDemoViewModel) {
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
                    Icon(Icons.Default.Mic, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.fp_audio_analyze_button))
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
                    Text(stringResource(R.string.fp_audio_analyzing))
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
                    Text(stringResource(R.string.fp_audio_content_clear))
                }
            }
        }

        // Analysis Status
        if (isAnalyzing) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.audio_analyzing_bg)
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
                        color = colorResource(R.color.audio_analyzing_color)
                    )
                    Column {
                        Text(
                            text = stringResource(R.string.fp_audio_analyzing),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = colorResource(R.color.audio_analyzing_color)
                        )
                        Text(
                            text = stringResource(R.string.fp_audio_content_analyzing_desc),
                            fontSize = 14.sp,
                            color = colorResource(R.color.white).copy(alpha = 0.8f)
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
                            text = stringResource(R.string.fp_audio_error_title),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = colorResource(R.color.danger_red)
                        )
                        Text(
                            text = errorMessage,
                            fontSize = 14.sp,
                            color = colorResource(R.color.white).copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Fingerprint Results
        fingerprintData?.let { data ->
            val context = LocalContext.current

            // Title
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.audio_analyzing_bg)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.fp_audio_content_results_title_with_icon, stringResource(R.string.fp_audio_results_title)),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = colorResource(R.color.audio_analyzing_color)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.fp_audio_content_analyzed_time, formatTimestamp(context, data.analysisTime)),
                        fontSize = 12.sp,
                        color = colorResource(R.color.white).copy(alpha = 0.6f)
                    )
                }
            }

            // Fingerprint Details
            FingerprintDetailCard(
                label = stringResource(R.string.fp_audio_frequency_response),
                value = data.frequencyResponse,
                icon = stringResource(R.string.fp_audio_content_icon_frequency)
            )

            FingerprintDetailCard(
                label = stringResource(R.string.fp_audio_harmonic_distortion),
                value = data.harmonicDistortion,
                icon = stringResource(R.string.fp_audio_content_icon_harmonic)
            )

            FingerprintDetailCard(
                label = stringResource(R.string.fp_audio_noise_floor),
                value = data.noiseFloor,
                icon = stringResource(R.string.fp_audio_content_icon_noise)
            )

            FingerprintDetailCard(
                label = stringResource(R.string.fp_audio_phase_response),
                value = data.phaseResponse,
                icon = stringResource(R.string.fp_audio_content_icon_phase)
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
                        Text(text = stringResource(R.string.fp_audio_content_icon_target), fontSize = 20.sp)
                        Text(
                            text = stringResource(R.string.fp_audio_fingerprint_hash),
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
                        text = stringResource(R.string.fp_audio_content_hash_description),
                        fontSize = 12.sp,
                        color = colorResource(R.color.white).copy(alpha = 0.7f),
                        lineHeight = 18.sp
                    )
                }
            }

            // What This Reveals
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.warning_yellow).copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = stringResource(R.string.fp_audio_reveals_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = colorResource(R.color.warning_yellow)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    listOf(
                        R.string.fp_audio_reveal1,
                        R.string.fp_audio_reveal2,
                        R.string.fp_audio_reveal3,
                        R.string.fp_audio_reveal4,
                        R.string.fp_audio_reveal5,
                        R.string.fp_audio_reveal6
                    ).forEach { stringRes ->
                        Text(
                            text = stringResource(R.string.fp_audio_content_bullet_point, stringResource(stringRes)),
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            color = colorResource(R.color.white).copy(alpha = 0.9f)
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
                        text = stringResource(R.string.fp_audio_warning_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = colorResource(R.color.danger_red)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.fp_audio_warning_desc),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = colorResource(R.color.white).copy(alpha = 0.9f)
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
                    color = colorResource(R.color.white).copy(alpha = 0.6f)
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

private fun formatTimestamp(context: android.content.Context, timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60000 -> context.getString(R.string.fp_audio_content_time_just_now)
        diff < 3600000 -> context.getString(R.string.fp_audio_content_time_minutes_ago, diff / 60000)
        else -> SimpleDateFormat(
            context.getString(R.string.fp_audio_content_time_format),
            Locale.getDefault()
        ).format(Date(timestamp))
    }
}