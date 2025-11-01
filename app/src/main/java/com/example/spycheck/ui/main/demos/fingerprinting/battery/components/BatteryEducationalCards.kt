package com.example.spycheck.ui.main.demos.fingerprinting.battery.components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spycheck.R

@Composable
fun BatteryFingerprintExplanationCard() {
    val context = LocalContext.current
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
                text = stringResource(R.string.fp_battery_edu_what_is),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = colorResource(R.color.text_primary)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.fp_battery_edu_every_battery),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = colorResource(R.color.warning)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.fp_battery_edu_identical_phones),
                fontSize = 12.sp,
                color = colorResource(R.color.text_primary).copy(alpha = 0.8f),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            val factors = context.resources.getStringArray(R.array.fp_battery_edu_factors)

            factors.forEach { factor ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = stringResource(R.string.fp_battery_edu_bullet), color = colorResource(R.color.info_blue), fontSize = 12.sp)
                    Text(
                        text = factor,
                        fontSize = 11.sp,
                        color = colorResource(R.color.text_primary).copy(alpha = 0.7f)
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
                        text = stringResource(R.string.fp_battery_edu_real_example),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = colorResource(R.color.info_blue)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.fp_battery_edu_example_text),
                        fontSize = 11.sp,
                        color = colorResource(R.color.text_primary).copy(alpha = 0.9f),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun BatteryTrackingRealWorldCard() {
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
                    text = stringResource(R.string.fp_battery_edu_real_world),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colorResource(R.color.danger_red)
                )
                Text(
                    text = if (expanded) stringResource(R.string.fp_battery_edu_arrow_down) else stringResource(R.string.fp_battery_edu_arrow_right),
                    color = colorResource(R.color.text_primary).copy(alpha = 0.5f),
                    fontSize = 20.sp
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    BatteryRealWorldScenario(
                        number = stringResource(R.string.fp_battery_edu_scenario_1_num),
                        title = stringResource(R.string.fp_battery_edu_scenario_1_title),
                        story = stringResource(R.string.fp_battery_edu_scenario_1_story),
                        impact = stringResource(R.string.fp_battery_edu_scenario_1_impact)
                    )

                    BatteryRealWorldScenario(
                        number = stringResource(R.string.fp_battery_edu_scenario_2_num),
                        title = stringResource(R.string.fp_battery_edu_scenario_2_title),
                        story = stringResource(R.string.fp_battery_edu_scenario_2_story),
                        impact = stringResource(R.string.fp_battery_edu_scenario_2_impact)
                    )

                    BatteryRealWorldScenario(
                        number = stringResource(R.string.fp_battery_edu_scenario_3_num),
                        title = stringResource(R.string.fp_battery_edu_scenario_3_title),
                        story = stringResource(R.string.fp_battery_edu_scenario_3_story),
                        impact = stringResource(R.string.fp_battery_edu_scenario_3_impact)
                    )

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = colorResource(R.color.card_background)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.fp_battery_edu_critical),
                            fontSize = 11.sp,
                            color = colorResource(R.color.warning),
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
                    text = stringResource(R.string.fp_battery_edu_tap_to_see),
                    fontSize = 11.sp,
                    color = colorResource(R.color.text_primary).copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun BatteryRealWorldScenario(
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
                        .size(28.dp)
                        .background(colorResource(R.color.danger_red), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = number,
                        color = colorResource(R.color.white),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = colorResource(R.color.text_primary)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = story,
                fontSize = 12.sp,
                color = colorResource(R.color.text_primary).copy(alpha = 0.8f),
                lineHeight = 17.sp
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
                    Text(text = stringResource(R.string.fp_battery_edu_lightbulb), fontSize = 12.sp)
                    Text(
                        text = impact,
                        fontSize = 10.sp,
                        color = colorResource(R.color.text_primary).copy(alpha = 0.9f),
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun BatteryTestItYourselfCard() {
    val context = LocalContext.current
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
                text = stringResource(R.string.fp_battery_edu_test_persistence),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = colorResource(R.color.info_blue)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.fp_battery_edu_prove_permanent),
                fontSize = 12.sp,
                color = colorResource(R.color.text_primary).copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            BatteryExperimentCard(
                number = stringResource(R.string.fp_battery_edu_test_1_num),
                title = stringResource(R.string.fp_battery_edu_test_1_title),
                steps = context.resources.getStringArray(R.array.fp_battery_edu_test_1_steps).toList(),
                why = stringResource(R.string.fp_battery_edu_test_1_why)
            )

            Spacer(modifier = Modifier.height(12.dp))

            BatteryExperimentCard(
                number = stringResource(R.string.fp_battery_edu_test_2_num),
                title = stringResource(R.string.fp_battery_edu_test_2_title),
                steps = context.resources.getStringArray(R.array.fp_battery_edu_test_2_steps).toList(),
                why = stringResource(R.string.fp_battery_edu_test_2_why)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.card_background)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.fp_battery_edu_advanced),
                    fontSize = 11.sp,
                    color = colorResource(R.color.warning),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun BatteryExperimentCard(
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
                    color = colorResource(R.color.text_primary)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            steps.forEachIndexed { index, step ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.fp_battery_edu_step_number, index + 1),
                        fontSize = 11.sp,
                        color = colorResource(R.color.info_blue),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = step,
                        fontSize = 11.sp,
                        color = colorResource(R.color.text_primary).copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.warning).copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.fp_battery_edu_plug), fontSize = 12.sp)
                    Text(
                        text = stringResource(R.string.fp_battery_edu_why_format, why),
                        fontSize = 10.sp,
                        color = colorResource(R.color.text_primary).copy(alpha = 0.9f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}