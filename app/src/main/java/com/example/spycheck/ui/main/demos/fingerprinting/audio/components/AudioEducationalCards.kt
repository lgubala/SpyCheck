package com.example.spycheck.ui.main.demos.fingerprinting.audio.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spycheck.R

@Composable
fun AudioFingerprintExplanationCard() {
    val context = LocalContext.current
    val factors = context.resources.getStringArray(R.array.audio_factors)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.info_blue).copy(alpha = 0.15f)
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
                text = stringResource(R.string.audio_explanation_title),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = colorResource(R.color.white)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.audio_unique_system),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = colorResource(R.color.warning_yellow)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.audio_unique_signature),
                fontSize = 12.sp,
                color = colorResource(R.color.white).copy(alpha = 0.8f),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            factors.forEach { factor ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = stringResource(R.string.bullet_point), color = colorResource(R.color.info_blue), fontSize = 12.sp)
                    Text(
                        text = factor,
                        fontSize = 11.sp,
                        color = colorResource(R.color.white).copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.card_background)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.audio_real_example),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = colorResource(R.color.info_blue)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.audio_real_example_text),
                        fontSize = 11.sp,
                        color = colorResource(R.color.white).copy(alpha = 0.9f),
                        lineHeight = 16.sp
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
                    text = stringResource(R.string.audio_no_permissions_warning),
                    fontSize = 11.sp,
                    color = colorResource(R.color.warning_yellow),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun AudioTrackingRealWorldCard() {
    var expanded by remember { mutableStateOf(false) }

    Card(
        onClick = { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.danger_red).copy(alpha = 0.2f)
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
                    text = stringResource(R.string.audio_real_world_tracking),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colorResource(R.color.danger_red)
                )
                Text(
                    text = if (expanded) stringResource(R.string.expand_icon_down) else stringResource(R.string.expand_icon_right),
                    color = colorResource(R.color.white).copy(alpha = 0.5f),
                    fontSize = 20.sp
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AudioRealWorldScenario(
                        number = stringResource(R.string.number_1),
                        title = stringResource(R.string.audio_scenario_1_title),
                        story = stringResource(R.string.audio_scenario_1_story),
                        impact = stringResource(R.string.audio_scenario_1_impact)
                    )

                    AudioRealWorldScenario(
                        number = stringResource(R.string.number_2),
                        title = stringResource(R.string.audio_scenario_2_title),
                        story = stringResource(R.string.audio_scenario_2_story),
                        impact = stringResource(R.string.audio_scenario_2_impact)
                    )

                    AudioRealWorldScenario(
                        number = stringResource(R.string.number_3),
                        title = stringResource(R.string.audio_scenario_3_title),
                        story = stringResource(R.string.audio_scenario_3_story),
                        impact = stringResource(R.string.audio_scenario_3_impact)
                    )

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = colorResource(R.color.card_background)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.audio_critical_warning),
                            fontSize = 11.sp,
                            color = colorResource(R.color.warning_yellow),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(12.dp),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AudioRealWorldScenario(
    number: String,
    title: String,
    story: String,
    impact: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.card_background)
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
                        .size(32.dp)
                        .background(colorResource(R.color.danger_red), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = number,
                        color = colorResource(R.color.white),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = colorResource(R.color.white)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = story,
                fontSize = 11.sp,
                color = colorResource(R.color.white).copy(alpha = 0.8f),
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.info_blue).copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = stringResource(R.string.lightbulb_icon), fontSize = 12.sp)
                    Text(
                        text = impact,
                        fontSize = 10.sp,
                        color = colorResource(R.color.white).copy(alpha = 0.9f),
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AudioTestItYourselfCard() {
    val context = LocalContext.current
    val consistencySteps = context.resources.getStringArray(R.array.audio_consistency_steps)
    val headphoneSteps = context.resources.getStringArray(R.array.audio_headphone_steps)
    val browserSteps = context.resources.getStringArray(R.array.audio_browser_steps)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.info_blue).copy(alpha = 0.2f)
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
                text = stringResource(R.string.audio_test_persistence),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = colorResource(R.color.info_blue)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.audio_prove_consistent),
                fontSize = 12.sp,
                color = colorResource(R.color.white).copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            AudioExperimentCard(
                number = stringResource(R.string.number_1),
                title = stringResource(R.string.audio_consistency_test),
                steps = consistencySteps.toList(),
                why = stringResource(R.string.audio_consistency_why)
            )

            Spacer(modifier = Modifier.height(12.dp))

            AudioExperimentCard(
                number = stringResource(R.string.number_2),
                title = stringResource(R.string.audio_headphone_test),
                steps = headphoneSteps.toList(),
                why = stringResource(R.string.audio_headphone_why)
            )

            Spacer(modifier = Modifier.height(12.dp))

            AudioExperimentCard(
                number = stringResource(R.string.number_3),
                title = stringResource(R.string.audio_browser_test),
                steps = browserSteps.toList(),
                why = stringResource(R.string.audio_browser_why)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.card_background)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.audio_advanced_tip),
                    fontSize = 11.sp,
                    color = colorResource(R.color.warning_yellow),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun AudioExperimentCard(
    number: String,
    title: String,
    steps: List<String>,
    why: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.card_background)
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
                        .background(colorResource(R.color.info_blue), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = number,
                        color = colorResource(R.color.black),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = colorResource(R.color.white)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            steps.forEachIndexed { index, step ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.step_number, index + 1),
                        fontSize = 11.sp,
                        color = colorResource(R.color.info_blue),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = step,
                        fontSize = 11.sp,
                        color = colorResource(R.color.white).copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.warning_yellow).copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.plug_icon), fontSize = 12.sp)
                    Text(
                        text = stringResource(R.string.audio_why_format, why),
                        fontSize = 10.sp,
                        color = colorResource(R.color.white).copy(alpha = 0.9f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}