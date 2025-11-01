package com.example.spycheck.ui.main.demos.fingerprinting.combined.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spycheck.R
import com.example.spycheck.ui.main.demos.fingerprinting.combined.SuperFingerprintDemoViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SuperFingerprintDemoContent(viewModel: SuperFingerprintDemoViewModel) {
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
                    Icon(Icons.Default.Fingerprint, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.fp_combined_analyze_button))
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
                    Text(stringResource(R.string.fp_combined_analyzing))
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
                    Text(stringResource(R.string.fp_super_content_clear))
                }
            }
        }

        // Analysis Status
        if (isAnalyzing) {
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
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = colorResource(R.color.danger_red)
                    )
                    Column {
                        Text(
                            text = stringResource(R.string.fp_combined_analyzing),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = colorResource(R.color.danger_red)
                        )
                        Text(
                            text = stringResource(R.string.fp_super_content_analyzing_desc),
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
                            text = stringResource(R.string.fp_combined_error_title),
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
                    containerColor = colorResource(R.color.danger_red).copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.fp_super_content_results_title_with_icon, stringResource(R.string.fp_combined_results_title)),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = colorResource(R.color.danger_red)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.fp_super_content_analyzed_time, formatTimestamp(data.analysisTime)),
                        fontSize = 12.sp,
                        color = colorResource(R.color.text_primary).copy(alpha = 0.6f)
                    )
                }
            }

            // Warning Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.danger_red).copy(alpha = 0.25f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = stringResource(R.string.fp_super_content_ultimate_warning),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = colorResource(R.color.danger_red)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.fp_super_content_ultimate_desc),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = colorResource(R.color.text_primary).copy(alpha = 0.9f)
                    )
                }
            }

            // Scores
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ScoreCard(
                    label = stringResource(R.string.fp_combined_confidence),
                    value = data.confidence,
                    icon = stringResource(R.string.fp_super_content_icon_confidence),
                    color = colorResource(R.color.danger_red),
                    modifier = Modifier.weight(1f)
                )

                ScoreCard(
                    label = stringResource(R.string.fp_super_content_components_label, data.activeComponents, data.totalComponents),
                    value = stringResource(R.string.fp_super_content_components_value),
                    icon = stringResource(R.string.fp_super_content_icon_components),
                    color = colorResource(R.color.battery_result_color),
                    modifier = Modifier.weight(1f)
                )
            }

            // Uniqueness Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.warning).copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(R.string.fp_super_content_icon_uniqueness), fontSize = 32.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = data.uniqueness,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.warning),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.fp_combined_uniqueness),
                        fontSize = 12.sp,
                        color = colorResource(R.color.text_primary).copy(alpha = 0.7f)
                    )
                }
            }

            // Tracking Resistance
            DetailCard(
                label = stringResource(R.string.fp_super_content_tracking_resistance),
                value = data.trackingResistance,
                icon = stringResource(R.string.fp_super_content_icon_resistance),
                color = colorResource(R.color.danger_red)
            )

            // Persistence
            DetailCard(
                label = stringResource(R.string.fp_super_content_persistence_level),
                value = data.persistence,
                icon = stringResource(R.string.fp_super_content_icon_persistence),
                color = colorResource(R.color.super_persistence_color)
            )

            // Ultimate Tracking Hash
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
                        Text(text = stringResource(R.string.fp_super_content_icon_skull), fontSize = 20.sp)
                        Text(
                            text = stringResource(R.string.fp_combined_fingerprint_hash),
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
                        text = stringResource(R.string.fp_super_content_hash_description),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.danger_red),
                        lineHeight = 18.sp
                    )
                }
            }

            // Combined Factors
            if (data.combinedFactors.isNotEmpty()) {
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
                            text = stringResource(R.string.fp_super_content_combined_factors),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = colorResource(R.color.warning)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        data.combinedFactors.forEach { factor ->
                            Text(
                                text = factor,
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                color = colorResource(R.color.text_primary).copy(alpha = 0.9f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            // Final Warning
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.danger_red).copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = stringResource(R.string.fp_combined_warning_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = colorResource(R.color.danger_red)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.fp_combined_warning_desc),
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
private fun ScoreCard(
    label: String,
    value: String,
    icon: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = colorResource(R.color.text_primary).copy(alpha = 0.7f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun DetailCard(
    label: String,
    value: String,
    icon: String,
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
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 28.sp)

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = colorResource(R.color.text_primary).copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    lineHeight = 20.sp
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